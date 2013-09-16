package com.sogou.upd.passport.manager.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:18
 * To change this template use File | Settings | File Templates.
 */
public class OAuth2ResourceFactory {

    private static Logger log = LoggerFactory.getLogger(OAuth2ResourceFactory.class);

    @Autowired
    private SHPlusTokenService shPlusTokenService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;

    public static Result getResource(PCOAuth2ResourceParams params, AppConfig appConfig) {
        OAuth2ResourceFactory factory = new OAuth2ResourceFactory();
        String clientSecret = appConfig.getClientSecret();
        return factory.resource(params, clientSecret);
    }

    public Result resource(PCOAuth2ResourceParams params, String clientSecret) {
        Result result = new OAuthResultSupport(false);
        int clientId = params.getClient_id();
        String instanceId = params.getInstance_id();
        try {
            if (!verifyAccessToken(clientId, clientSecret, instanceId, params.getAccess_token())) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            String resourceType = params.getResource_type();
            //校验accessToken
            if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_COOKIE)) {

            } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {

            } else {
                result.setCode(ErrorUtil.INVALID_RESOURCE_TYPE);
                return result;
            }
        } catch (Exception e) {
            log.error("Obtain OAuth2 Resource Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /*
     * 验证accessToken有效性并且返回passportId
     */
    private boolean verifyAccessToken(int clientId, String clientSecret, String instanceId, String accessToken) throws Exception {
        try {
            String passportId = TokenDecrypt.decryptPcToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                boolean isRightPcRToken = pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken);
                if (!isRightPcRToken) {
                    Map userInfoMap = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_FULL_USERINFO);
                    if (userInfoMap != null) {
                        String result = (String) userInfoMap.get("result");
                        if ("confirm".equals(result)) {
                            //TODO 更新头像,以后写到搜狗数据库里
                            String avatar = (String) userInfoMap.get("tiny_avatar");
                            if(ManagerHelper.isInvokeProxyApi(passportId)){

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Decrypt PC AccessToken error,accessToken:" + accessToken, e);
        }
        return false;
    }

}
