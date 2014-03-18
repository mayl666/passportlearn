package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.PCOAuth2RegManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.manager.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 下午7:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCOAuth2RegManagerImpl implements PCOAuth2RegManager {
    public static final Logger logger = LoggerFactory.getLogger(PCOAuth2RegManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PCAccountTokenService pcAccountService;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    SnamePassportMappingService snamePassportMappingService;

    @Override
    public Result isPcAccountNotExists(String username, boolean type) {
        Result result = new APIResultSupport(false);
        String sohuPassportId = snamePassportMappingService.queryPassportIdBySnameOrPhone(username);
        if (!Strings.isNullOrEmpty(sohuPassportId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            return result;
        }

        //如果sohu+库里没有，说明用户名肯定不会与老用户重复,再验证sohu库里有没有该用户
        if (type) {
            //手机号判断绑定账户
            BaseMobileApiParams params = new BaseMobileApiParams();
            params.setMobile(username);
            //TODO 目前及搜狗账号迁移完成，手机注册都需要查sohu库；全部账号迁移完成后，手机注册查sogou库，不需要查sohu库了
            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = proxyBindApiManager.getPassportIdByMobile(params);
            } else {
                result = sgBindApiManager.getPassportIdByMobile(params);
            }
            if (result.isSuccess()) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result;
            }
        } else {
            //个性账号注册
            username = username + "@sogou.com";
            CheckUserApiParams checkUserApiParams = buildProxyApiParams(username);
            //TODO 目前，查sohu库，搜狗账号迁移完成后，查sogou库
            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = proxyRegisterApiManager.checkUser(checkUserApiParams);
            } else {
                result = sgRegisterApiManager.checkUser(checkUserApiParams);
            }
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                return result;
            }
        }
        result.setSuccess(true);
        result.setMessage("账号可以注册");
        return result;
    }

    private CheckUserApiParams buildProxyApiParams(String username) {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username);
        return checkUserApiParams;
    }


    @Override
    public Result pcAccountRegister(PCOAuth2RegisterParams params, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        String username = null;
        int clientId = Integer.parseInt(params.getClient_id());
        username = params.getUsername().trim().toLowerCase();
        String password = params.getPassword();
        String captcha = params.getCaptcha();
        String ru = params.getRu();
        //判断是否是手机号注册
        if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
            username = username + "@sogou.com";
        }
        //判断注册账号类型，sogou用户还是手机用户
        AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);
        switch (emailType) {
            case SOGOU://个性账号直接注册
                String token = params.getToken();
                //判断验证码
                if (!accountService.checkCaptchaCode(token, captcha)) {
                    logger.info("[pcAccountRegister captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captcha);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
                RegEmailApiParams regEmailApiParams = buildRegMailProxyApiParams(username, password, ip,
                        clientId, ru);
                if (ManagerHelper.isInvokeProxyApi(username)) {
                    result = proxyRegisterApiManager.regMailUser(regEmailApiParams);
                } else {
                    result = sgRegisterApiManager.regMailUser(regEmailApiParams);
                }
                break;
            case PHONE://手机号
                RegMobileCaptchaApiParams regMobileCaptchaApiParams = buildProxyApiParams(username, password, captcha, clientId, ip);
                if (ManagerHelper.isInvokeProxyApi(username)) {
                    result = proxyRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
                } else {
                    result = sgRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
                }
                break;
        }
        return result;
    }

    @Override
    public Result getPairToken(PcPairTokenParams pcPairTokenParams) {
        Result finalResult = new APIResultSupport(false);
        try {
            int clientId = Integer.parseInt(pcPairTokenParams.getAppid());
            String passportId = pcPairTokenParams.getUserid();
            String instanceId = pcPairTokenParams.getTs();
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            return getAccountToken(passportId, instanceId, appConfig);
        } catch (Exception e) {
            logger.error("getPairToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    private Result getAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        Result result = new APIResultSupport(false);
        AccountToken accountToken = pcAccountService.initialAccountToken(passportId, instanceId, appConfig); //该接口之前调用 initialOrUpdateAccountToken
        if (accountToken != null) {
            result.setSuccess(true);
            result.setDefaultModel(accountToken);
        } else {
            result.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
        }
        return result;
    }

    private RegEmailApiParams buildRegMailProxyApiParams(String username, String password, String ip, int clientId, String ru) {
        return new RegEmailApiParams(username, password, ip, clientId, ru);
    }


    private RegMobileCaptchaApiParams buildProxyApiParams(String mobile, String password, String captcha, int clientId, String ip) {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setMobile(mobile);
        regMobileCaptchaApiParams.setPassword(password);
        regMobileCaptchaApiParams.setCaptcha(captcha);
        regMobileCaptchaApiParams.setClient_id(clientId);
        regMobileCaptchaApiParams.setIp(ip);
        return regMobileCaptchaApiParams;
    }
}
