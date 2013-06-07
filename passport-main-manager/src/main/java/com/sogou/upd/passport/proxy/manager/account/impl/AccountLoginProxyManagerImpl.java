package com.sogou.upd.passport.proxy.manager.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.proxy.manager.from.login.AuthUserParameters;
import com.sogou.upd.passport.proxy.manager.ProxyBaseManager;
import com.sogou.upd.passport.proxy.manager.SHPPUrlConstant;
import com.sogou.upd.passport.proxy.manager.account.AccountLoginProxyManager;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component
public class AccountLoginProxyManagerImpl extends ProxyBaseManager implements AccountLoginProxyManager {


    @Override
    public  Map<String,Object> authUser(AuthUserParameters authUserParameters) {
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.AUTH_USER,SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(authUserParameters);
        return this.execute(requestModelXml);
    }
}
