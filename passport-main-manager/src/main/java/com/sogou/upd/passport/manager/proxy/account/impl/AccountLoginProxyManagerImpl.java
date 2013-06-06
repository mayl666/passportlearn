package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.manager.form.proxy.account.AuthUserParameters;
import com.sogou.upd.passport.manager.proxy.ProxyBaseManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.AccountLoginProxyManager;
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
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.AUTH_USER,"info");
        requestModelXml.addParams(authUserParameters);
        return this.execute(requestModelXml);
    }
}
