package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.WebRoamDO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.TokenService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRoamManagerImpl.class);

    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    @Override
    public Result roamGo(String sLoginPassportId) {
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
                //安全启见、根据 r_key 清除 缓存中 漫游用户信息、仅供使用一次!
                tokenService.deleteWebRoamDoByToken(CacheConstant.CACHE_KEY_WEB_ROAM + r_key);
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

            //漫游用户在搜狗未登录、设置搜狗登录状态
            if (Strings.isNullOrEmpty(sgLgUserId)) {
                cookieManager.setCookie(response, roamPassportId, clientId, createIp, ru, (int) DateAndNumTimesConstant.TWO_WEEKS);
            }
        } catch (Exception e) {
            LOGGER.error("webRoam error. roamPassportId:{},r_key:{},ru:{}", new Object[]{roamPassportId, r_key, ru}, e);
            throw new ServiceException(e);
        }
        result.setSuccess(true);
        return result;
    }
}
