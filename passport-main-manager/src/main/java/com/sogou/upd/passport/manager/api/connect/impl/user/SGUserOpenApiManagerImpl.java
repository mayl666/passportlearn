package com.sogou.upd.passport.manager.api.connect.impl.user;

import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:21
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyUserOpenApiManager")
public class SGUserOpenApiManagerImpl extends BaseProxyManager implements UserOpenApiManager {

    @Override
    public Result getUserInfo(UserOpenApiParams userOpenApiParams) {
        RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.GET_OPEN_USER_INFO);
        requestModelJSON.addParams(userOpenApiParams);
        return executeResult(requestModelJSON);
    }

}
