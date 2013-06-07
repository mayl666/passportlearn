package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.BindApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileApiParams;
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
        BindMobileApiParams bindMobileApiParams = new BindMobileApiParams();
        bindMobileApiParams.setPassport_id(passportId);
        bindMobileApiParams.setMobile("18210193340");
        bindMobileApiParams.setClient_id(clientId);
        Result result = bindApiManager.bindMobile(bindMobileApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());

        this.testUnbindMobile();
    }


    public void testUnbindMobile(){
        BaseMoblieApiParams unBindMobileApiParams =new BaseMoblieApiParams();
        unBindMobileApiParams.setMobile("18210193340");
        unBindMobileApiParams.setClient_id(clientId);
        Result result = bindApiManager.unbindMobile(unBindMobileApiParams);
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
}
