package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountRoamManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountRoamInfo;
import com.sogou.upd.passport.service.account.AccountRoamService;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;

/**
 * 支持搜狗域、搜狐域、第三方账号漫游manager
 * User: chengang
 * Date: 14-7-29
 * Time: 上午10:28
 */
public class AccountRoamManagerImpl implements AccountRoamManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRoamManagerImpl.class);

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRoamService accountRoamService;

    /**
     * 支持：搜狗域、搜狐域、第三方账号 3类账号漫游
     * <p/>
     * 不支持：外域、手机账号漫游
     * <p/>
     * 账号策略： 因支持漫游测试阶段，搜狐并没有停掉漫游，所以需要做兼容逻辑处理。
     * <p/>
     * (1)对于漫游过来的手机、外域邮箱账号、直接清掉cookie
     * (2)账号在sg不存在:
     * 1、对搜狗域账号、第三方账号、直接清除掉cookie
     * 2、搜狐域账号、初始化Account、AccountInfo
     * <p/>
     * <p/>
     * 签名数据存储
     * key:sgId
     * value: version:xxxx|userid:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)|ip:xxxx(用户真实ip)
     *
     * @param response
     * @param sgLogin    搜狗是否登录
     * @param sgLgUserId 搜狗登录用户userid
     * @param r_key      签名信息
     * @param ru         调整地址
     * @param clientId   应用id
     * @return
     * @throws ServiceException
     */
    @Override
    public Result webRoam(HttpServletResponse response, boolean sgLogin, String sgLgUserId, String r_key, String ru, int clientId) throws ServiceException {
        Result result = new APIResultSupport(false);
        String roamPassportId = null;
        String createIp = null;
        try {
            //检查签名正确性
            //TODO 验证 r_key



            //根据sgId 取出存储在缓存的漫游用户信息
            AccountRoamInfo accountRoamInfo = accountRoamService.getAccountRoamInfoBySgId(r_key);
            if (accountRoamInfo != null) {
                roamPassportId = accountRoamInfo.getUserId();
                createIp = accountRoamInfo.getRequestIp();
            } else {
                //漫游用户信息取不到 返回对应状态码的Result
                result.setCode(ErrorUtil.ERR_CODE_ROAM_INFO_NOT_EXIST);
                return result;
            }

            //判断漫游用户与目前搜狗登录的用户是否一致、如果搜狗已登录、以搜狗为准
            if (!Strings.isNullOrEmpty(sgLgUserId)) {
                if (sgLogin && !sgLgUserId.equalsIgnoreCase(roamPassportId)) {
                    result.setSuccess(true);
                    return result;
                }
            }

            //获取用户账号类型
            AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(roamPassportId);

            //对于漫游过来的手机、外域账号、直接清除cookie
            // TODO 搜狐切断漫游后去除清cookie逻辑
            if (accountDomain == AccountDomainEnum.PHONE || accountDomain == AccountDomainEnum.OTHER) {
                clearCookie(response);
                result.setSuccess(true);
                return result;
            }


            //判断账号是否存在
            Account account = accountService.queryAccountByPassportId(roamPassportId);
            if (account == null) {
                //若账号不存在、搜狗域、第三方账号 清cookie
                if (accountDomain == AccountDomainEnum.SOGOU || accountDomain == AccountDomainEnum.THIRD) {
                    //搜狗域、第三方账号在搜狗不存在 记录Log
                    LOGGER.info("roam account sg not exist. userId:{},accountDomain:{}", roamPassportId, accountDomain.getValue());

                    //清cookie
                    clearCookie(response);

                    //返回result
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
                //若搜狐域账号、初始化一条无密码的搜狐域Account
                if (accountDomain == AccountDomainEnum.SOHU) {
                    Account insertSoHuAccount = accountService.initialAccount(roamPassportId, null, false, createIp, AccountTypeEnum.SOHU.getValue());
                    if (insertSoHuAccount == null) {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                        return result;
                    }
                }
            }

            //漫游用户在搜狗未登录、设置搜狗登录状态
            if (!sgLogin) {
                cookieManager.setCookie(response, roamPassportId, clientId, createIp, ru, (int) DateAndNumTimesConstant.TWO_WEEKS);
            }
        } catch (Exception e) {
            LOGGER.error("SupportAccountRoam error. roamPassportId:{},r_key:{}", roamPassportId, r_key, e);
            throw new ServiceException(e);
        }
        result.setDefaultModel("userId", roamPassportId);
        result.setDefaultModel("createIp", createIp);
        result.setSuccess(true);
        return result;
    }


    /**
     * 清除cookie
     *
     * @param response
     */
    private void clearCookie(HttpServletResponse response) {
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINF);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPRDIG);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PASSPORT);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINFO);
    }
}
