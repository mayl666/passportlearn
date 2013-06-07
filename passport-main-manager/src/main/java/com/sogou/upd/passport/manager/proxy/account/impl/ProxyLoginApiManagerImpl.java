package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.manager.proxy.BaseProxyManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
import com.sogou.upd.passport.manager.proxy.account.LoginApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 代理搜狐Passport的登录实现
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component
public class ProxyLoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {

    @Override
    public  Map<String,Object> authUser(AuthUserApiParams authUserParameters) {
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.AUTH_USER,"info");
        requestModelXml.addParams(authUserParameters);
        return this.execute(requestModelXml);
    }
}
