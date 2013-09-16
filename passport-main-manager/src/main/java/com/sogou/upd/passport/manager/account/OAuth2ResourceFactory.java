package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
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

    public static Result getResource(PCOAuth2ResourceParams params, AppConfig appConfig) {
        Result result = new OAuthResultSupport(false);
        String resourceType = params.getResource_type();
        //校验accessToken

        if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_COOKIE)) {

        } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {

        } else {
           result.setCode(ErrorUtil.INVALID_RESOURCE_TYPE);
        }
        return result;
    }

    /*
     * 验证accessToken有效性并且返回passportId
     */
    private String queryPassportIdByToken(int clientId, String instanceId, String clientSecret, String accessToken) throws Exception {

//        boolean isRightPcRToken = pcAccountTokenService.verifyAccessToken(clientId, instanceId, clientSecret, accessToken);
//        if (!isRightPcRToken) {
//            String passportId = shPlusTokenService.verifyShPlusAccessToken(clientId, instanceId, accessToken);
//            return isRightSHRToken;
//        }
//        return true;
        return null;
    }

}
