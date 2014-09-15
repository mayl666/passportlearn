package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Base64Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.PcRoamTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.WebRoamDO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.TokenService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游manager
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:28
 */
@Component
public class AccountRoamManagerImpl implements AccountRoamManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountRoamManagerImpl.class);
    public static final int TIME_LIMIT = 60 * 60 * 24 * 1000;

    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

    @Override
    public Result createRoamKey(String sLoginPassportId) {
        Result result = new APIResultSupport(false);
        if (Strings.isNullOrEmpty(sLoginPassportId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result;
        }
        String r_key = tokenService.saveWebRoamToken(sLoginPassportId);
        if (!Strings.isNullOrEmpty(r_key)) {
            result.setSuccess(true);
            result.setDefaultModel("r_key", r_key);
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
        }
        return result;
    }

    @Override
    public Result pcRoamGo(String type, String s) {
        Result result = new APIResultSupport(false);
        String passportId = "";
        // 验证桌面端登录态，解析passportId
        if (PcRoamTypeEnum.iec.getValue().equals(type)) {
            // TODO
        } else if (PcRoamTypeEnum.iet.getValue().equals(type)) {
            // TODO
        } else if (PcRoamTypeEnum.pinyint.getValue().equals(type)) {
            passportId = getUserIdByPinyinRoamToken(s);
        } else {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage("type类型不支持");
            return result;
        }
        if (Strings.isNullOrEmpty(passportId)) {
            result.setCode(ErrorUtil.ERR_CODE_RSA_DECRYPT);
            return result;
        }
        // 生成登录标识
        String r_key = tokenService.saveWebRoamToken(passportId);
        if (Strings.isNullOrEmpty(r_key)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result;
        }
        // 验证账号是否存在，并获取用户信息
        Account account = accountService.queryNormalAccount(passportId);
        if (account == null) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result;
        } else {
            String uniqname = Strings.isNullOrEmpty(account.getUniqname()) ? passportId : account.getUniqname();
            result.setDefaultModel("uniqname", uniqname);
            result.setDefaultModel("userid", passportId);
            result.setDefaultModel("r_key", r_key);
        }
    return result;
}

    @Override
    public Result webRoam(HttpServletResponse response, String sgLgUserId, String r_key, String ru, String createIp, int clientId) throws ServiceException {
        Result result = new APIResultSupport(false);
        String roamPassportId = null;
        try {
            //检查签名正确性
            //根据r_key 取出存储在缓存的漫游用户信息
            WebRoamDO webRoamDO = tokenService.getWebRoamDOByToken(r_key);
            if (webRoamDO != null) {
                roamPassportId = webRoamDO.getPassportId();
                result.setDefaultModel("userId", roamPassportId);
            } else {
                //漫游用户信息取不到 返回对应状态码的Result
                result.setCode(ErrorUtil.ERR_CODE_ROAM_INFO_NOT_EXIST);
                return result;
            }

            //判断漫游用户与目前搜狗登录的用户是否一致、如果搜狗已登录、以搜狗为准
            if (!Strings.isNullOrEmpty(sgLgUserId) && !sgLgUserId.equalsIgnoreCase(roamPassportId)) {
                result.setSuccess(true);
                return result;
            }

            //获取用户账号类型
            AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(roamPassportId);

            //判断账号是否存在
            Account account = accountService.queryAccountByPassportId(roamPassportId);
            if (account == null) {
                //标注 20140806 对于在sg 数据库不存在的第三方账号、为减少对数据的影响、仍然支持种cookie、此处对于不存在的第三方账号后续要处理。
                if (accountDomain == AccountDomainEnum.SOGOU) {
                    //返回result
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
                //若搜狐域账号、初始化一条无密码的搜狐域Account
                if (accountDomain == AccountDomainEnum.SOHU) {
                    if (!accountService.initSOHUAccount(roamPassportId, createIp)) {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                        return result;
                    }
                }
            } else if (accountDomain == AccountDomainEnum.PHONE || accountDomain == AccountDomainEnum.OTHER) {
                result.setSuccess(true);
                return result;
            }

            //漫游用户在搜狗未登录、设置搜狗登录状态 //TODO module替换
            if (Strings.isNullOrEmpty(sgLgUserId)) {
//                cookieManager.setCookie(response, roamPassportId, clientId, createIp, ru, (int) DateAndNumTimesConstant.TWO_WEEKS);

                CookieApiParams cookieApiParams = new CookieApiParams();
                cookieApiParams.setUserid(roamPassportId);
                cookieApiParams.setClient_id(clientId);
                cookieApiParams.setRu(ru);
                cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
                cookieApiParams.setPersistentcookie(String.valueOf(1));
                cookieApiParams.setIp(createIp);
                cookieApiParams.setMaxAge((int) DateAndNumTimesConstant.TWO_WEEKS);
                cookieApiParams.setCreateAndSet(CommonConstant.CREATE_COOKIE_AND_SET);

                if (account != null) {
                    if (!Strings.isNullOrEmpty(account.getUniqname())) {
                        cookieApiParams.setUniqname(account.getUniqname());
                    } else {
                        if (!Strings.isNullOrEmpty(roamPassportId)) {
                            if (StringUtils.contains(roamPassportId, "@")) {
                                cookieApiParams.setUniqname(StringUtils.substring(roamPassportId, 0, roamPassportId.indexOf("@")));
                            } else {
                                cookieApiParams.setUniqname(roamPassportId);
                            }
                        }
                    }
                }
                cookieManager.createCookie(response, cookieApiParams);
            }
        } catch (Exception e) {
            logger.error("webRoam error. roamPassportId:{},r_key:{},ru:{}", new Object[]{roamPassportId, r_key, ru}, e);
            throw new ServiceException(e);
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public String getUserIdByPinyinRoamToken(String cipherText) {
        String clearText;
        try {
            clearText = RSA.decryptByPrivateKey(Base64Coder.decode(cipherText), TokenGenerator.PRIVATE_KEY);
        } catch (Exception e) {
            logger.error("decrypt error, cipherText:" + cipherText, e);
            return null;
        }
        if (!Strings.isNullOrEmpty(clearText)) {
            String[] textArray = clearText.split("\\|");
            if (textArray.length == 4) { //数据组成： userid|clientId|token|timestamp
                //判断时间有效性
                long timeStamp = Long.parseLong(textArray[3]);
                if (Math.abs(timeStamp - System.currentTimeMillis()) > TIME_LIMIT) {
                    logger.error("time expired, text:" + clearText + " current:" + System.currentTimeMillis());
                    return null;
                }
                //判断用户名是否和token取得的一致
                Result getUserIdResult = oAuth2ResourceManager.getPassportIdByToken(textArray[2], Integer.parseInt(textArray[1]));
                if (getUserIdResult.isSuccess()) {
                    String passportId = (String) getUserIdResult.getDefaultModel();
                    if (!Strings.isNullOrEmpty(passportId) && passportId.equals(textArray[0])) { //解密后的token得到userid，需要和传入的userid一样，保证安全及toke有效性。
                        return textArray[0];
                    }
                } else {
                    logger.error("can't get token, text:" + clearText);
                    return null;
                }
            } else {
                //长度不对。
                logger.error("text to array  length error, expect 4, text:" + clearText);
                return null;
            }
        } else {
            logger.error("clearText is empty cipherText:" + cipherText);
            return null;
        }
        return null;
    }
}
