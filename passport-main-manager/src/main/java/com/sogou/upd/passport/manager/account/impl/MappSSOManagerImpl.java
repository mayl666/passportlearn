package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.MappSSOManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.PackageNameSign;
import com.sogou.upd.passport.service.account.MappSSOService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午5:57
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MappSSOManagerImpl implements MappSSOManager {
    private static final Logger logger = LoggerFactory.getLogger(MappSSOManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private MappSSOService mappSSOService;

    @Autowired
    private SessionServerManager sessionServerManager;

    public Result checkAppPackageSign(int clientId, long ct, String packageSignEncrypt, String udid) {
        Result result = new APIResultSupport(false);
        try {
            //解密包签名
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }

            String clientSecret = appConfig.getClientSecret();
            String decryptResult = AES.decryptURLSafeString(packageSignEncrypt, clientSecret);

            //校验解密后的包签名信息
            PackageNameSign packageNameSign = mappSSOService.baseSSOAppInfoCheck(clientId, ct, decryptResult);
            if (null == packageNameSign) {
                logger.warn("baseSSOAppInfoCheck failed");
                result.setCode(ErrorUtil.ERR_CODE_SSO_APP_CHECK_FAILED);
                return result;
            }

            boolean checkSign = mappSSOService.checkSSOPackageSign(packageNameSign);
            if (!checkSign) {
                logger.warn("checkSSOPackageSign failed");
                result.setCode(ErrorUtil.ERR_CODE_SSO_APP_CHECK_FAILED);
                return result;
            }

            //生成token，存储，加密
            String ssoToken = mappSSOService.generateSSOToken(ct, packageNameSign.getPackageName(), udid);
            mappSSOService.saveSSOTokenToCache(ssoToken);
            String ssoTokenEncryped = mappSSOService.encryptSSOToken(ssoToken, clientSecret);

            //生成ST
            String serverSecret = appConfig.getServerSecret();
            String ssoTicket = mappSSOService.generateTicket(clientId, udid, ssoToken, serverSecret);

            //返回结果
            String verifyResult = ssoTokenEncryped + CommonConstant.SEPARATOR_1 + ssoTicket;
            result.setDefaultModel(LoginConstant.SSO_VOUCHER, verifyResult);
            result.setSuccess(true);
            result.setMessage("操作成功");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_SSO_APP_CHECK_FAILED);
            return result;

        }

        return result;
    }

    @Override
    public Result getOldSgid(int clientId, String stoken, String udid) {

        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            String serverSecret = appConfig.getServerSecret();

            //解密stoken，获取token
            String[] paramArray = stoken.split("\\" + CommonConstant.SEPARATOR_1);
            if (null == paramArray || paramArray.length < 2) {
                result.setCode(ErrorUtil.ERR_CODE_SSO_PARAM_INVALID);
                return result;
            }

            String appClientInfo = paramArray[0];
            String serviceTicket = paramArray[1];
            if (Strings.isNullOrEmpty(appClientInfo) || Strings.isNullOrEmpty(serviceTicket)) {
                result.setCode(ErrorUtil.ERR_CODE_SSO_PARAM_INVALID);
                return result;
            }

            // 解密st，查看redis中是否有token,删除token
            String token = mappSSOService.checkSSOTicket(serviceTicket, serverSecret);
            if (Strings.isNullOrEmpty(token)) {
                result.setCode(ErrorUtil.ERR_CODE_SSO_TOKEN_INVALID);
                return result;
            }

            //删除redis中的token，只能用一次
            mappSSOService.delSSOToken(token);

            //用token解密app-client info，校验app-client info,获取sgid
            String oldSgid = mappSSOService.getOldSgid(appClientInfo, token, udid, clientId);
            if (Strings.isNullOrEmpty(oldSgid)) {
                result.setCode(ErrorUtil.ERR_CODE_SSO_APP_CHECK_FAILED);
                return result;
            }

            //返回结果
            result.setDefaultModel(LoginConstant.SSO_OLD_SID, oldSgid);
            result.setDefaultModel(LoginConstant.SSO_TOKEN, token);
            result.setSuccess(true);
            result.setMessage("操作成功");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_SSO_FAILED);
            return result;
        }

        return result;

    }

}
