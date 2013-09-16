package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

    public static Result getResource(PCOAuth2ResourceParams params, AppConfig appConfig) {
        OAuth2ResourceFactory factory = new OAuth2ResourceFactory();
        String clientSecret = appConfig.getClientSecret();
        return factory.resource(params, clientSecret);
    }

    public Result resource(PCOAuth2ResourceParams params, String clientSecret) {
        Result result = new OAuthResultSupport(false);
        int clientId = params.getClient_id();
        String instanceId = params.getInstance_id();
        String accessToken = params.getAccess_token();
        try {
            String passportId = TokenDecrypt.decryptPcToken(accessToken, clientSecret);
            //校验accessToken
            if (!pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken)) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            String resourceType = params.getResource_type();
            if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_COOKIE)) {
                result = oAuth2ResourceManager.getCookieValue(passportId);
            } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {
                result = oAuth2ResourceManager.getFullUserInfo(passportId);
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

}
