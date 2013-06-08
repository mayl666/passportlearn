package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.BindApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;
import junit.framework.Assert;
import org.junit.Test;

import javax.inject.Inject;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class ProxyBindApiManagerImplTest extends BaseTest {

    @Inject
    private BindApiManager bindApiManager;

    @Test
    public void testBindMobile(){
        BindMobileProxyParams bindMobileProxyParams = new BindMobileProxyParams();
        bindMobileProxyParams.setPassport_id(passportId);
        bindMobileProxyParams.setMobile("18210193340");
        bindMobileProxyParams.setClient_id(clientId);
        Result result = bindApiManager.bindMobile(bindMobileProxyParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());

        this.testUnbindMobile();
    }


    public void testUnbindMobile(){
        UnBindMobileProxyParams unBindMobileProxyParams=new UnBindMobileProxyParams();
        unBindMobileProxyParams.setMobile("18210193340");
        unBindMobileProxyParams.setClient_id(clientId);
        Result result = bindApiManager.unbindMobile(unBindMobileProxyParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testBindEmail(){
        BindEmailApiParams bindEmailApiParams=new BindEmailApiParams();
        bindEmailApiParams.setNewbindemail("411541129@qq.com");
        bindEmailApiParams.setOldbindemail("34310327@qq.com");
        bindEmailApiParams.setPassword(password);
        bindEmailApiParams.setClient_id(clientId);
        bindEmailApiParams.setPassport_id(passportId);
        Result result = bindApiManager.bindEmail(bindEmailApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetPassportIdFromMobile(){
        MobileBindPassportIdApiParams mobileBindPassportIdApiParams=new MobileBindPassportIdApiParams();
        mobileBindPassportIdApiParams.setMobile("18612987312");
        Result result = bindApiManager.getPassportIdFromMobile(mobileBindPassportIdApiParams);
        System.out.println(result.toString());
    }
}
