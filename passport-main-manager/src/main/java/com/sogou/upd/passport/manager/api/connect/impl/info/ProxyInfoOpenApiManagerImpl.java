package com.sogou.upd.passport.manager.api.connect.impl.info;

import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.InfoOpenApiManager;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:20
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyInfoOpenApiManager")
public class ProxyInfoOpenApiManagerImpl extends BaseProxyManager implements InfoOpenApiManager {

    @Override
    public Result addUserShareOrPic(BaseApiParams baseApiParams) {
//        RequestModelJSON requestModelJSON = new RequestModelJSON(SHPPUrlConstant.CONNECT_SHARE_PIC);
//        requestModelJSON.convertObjectToMap(baseApiParams);
//        return executeResult(requestModelJSON);
        return null;
    }

}
