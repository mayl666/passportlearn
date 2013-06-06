package com.sogou.upd.passport.proxy.manager.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.proxy.account.AuthUserParameters;
import com.sogou.upd.passport.proxy.manager.ProxyBaseManager;
import com.sogou.upd.passport.proxy.manager.SHPPUrlConstant;
import com.sogou.upd.passport.proxy.manager.account.AccountLoginManager;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component
public class AccountLoginManagerImpl extends ProxyBaseManager implements AccountLoginManager  {
    @Override
    public  Map<String,Object> authUser(AuthUserParameters authUserParameters) {
        RequestModelXml requestModelXml=new RequestModelXml(SHPPUrlConstant.AUTH_USER,"");
        return this.execute(requestModelXml);
    }
}
