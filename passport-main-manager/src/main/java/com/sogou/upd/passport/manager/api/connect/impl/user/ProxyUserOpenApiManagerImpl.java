package com.sogou.upd.passport.manager.api.connect.impl.user;

import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:21
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyUserOpenApiManager")
public class ProxyUserOpenApiManagerImpl extends BaseProxyManager implements UserOpenApiManager {

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.GET_OPEN_USER_INFO);
        requestModelJSON.addParams(userOpenApiParams);
        return executeResult(requestModelJSON);
    }

    @Override
    public Result handleObtainConnectUserInfo(int provider, String openid, String accessToken, int original) throws ServiceException, IOException, OAuthProblemException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
