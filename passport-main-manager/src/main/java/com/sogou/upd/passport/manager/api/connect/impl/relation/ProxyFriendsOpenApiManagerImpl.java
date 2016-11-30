package com.sogou.upd.passport.manager.api.connect.impl.relation;

import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.FriendsOpenApiManager;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:27
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyFriendsOpenApiManager")
public class ProxyFriendsOpenApiManagerImpl extends BaseProxyManager implements FriendsOpenApiManager {

    @Override
    public Result getUserFriends(BaseApiParams baseApiParams) {
//        RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.GET_CONNECT_FRIENDS_INFO);
//        requestModelJSON.addParams(baseApiParams);
//        return executeResult(requestModelJSON);
        return null;
    }

}
