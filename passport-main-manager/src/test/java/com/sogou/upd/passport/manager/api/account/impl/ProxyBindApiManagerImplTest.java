package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午2:02
 */
public class ProxyBindApiManagerImplTest extends BaseTest {

    @Autowired
    private BindApiManager proxyBindApiManager;

//    @Test
//    public void testBindMobile(){
//        BindMobileApiParams bindMobileApiParams = new BindMobileApiParams();
//        bindMobileApiParams.setUserid(passportId);
//        bindMobileApiParams.setMobile("13940075348");
//        bindMobileApiParams.setClient_id(clientId);
//        Result result = proxyBindApiManager.bindMobile(bindMobileApiParams);
//        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
//
////        this.testUnbindMobile();
//    }
//
//
//    public void testUnbindMobile(){
//        BaseMoblieApiParams unBindMobileApiParams =new BaseMoblieApiParams();
//        unBindMobileApiParams.setMobile("18210193340");
//        unBindMobileApiParams.setClient_id(clientId);
//        Result result = proxyBindApiManager.updateBindMobile(unBindMobileApiParams);
//        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
//    }

    @Test
    public void testUpdataBindMobile(){
        UpdateBindMobileApiParams updateBindMobileApiParams=new UpdateBindMobileApiParams();
        updateBindMobileApiParams.setUserid(passportId);
        updateBindMobileApiParams.setOldMobile("18912987312");
        updateBindMobileApiParams.setNewMobile("13940075348");
        Result result = proxyBindApiManager.updateBindMobile(updateBindMobileApiParams);
        System.out.println(result.toString());
    }

    @Test
    public void testBindEmail(){
        BindEmailApiParams bindEmailApiParams=new BindEmailApiParams();
        bindEmailApiParams.setNewbindemail("411541129@qq.com");
        bindEmailApiParams.setOldbindemail("34310327@qq.com");
        bindEmailApiParams.setPassword(password);
        bindEmailApiParams.setClient_id(clientId);
        bindEmailApiParams.setUserid(passportId);
        bindEmailApiParams.setPwdtype(1);
        Result result = proxyBindApiManager.bindEmail(bindEmailApiParams);
        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetPassportIdFromMobile(){
        BaseMoblieApiParams baseMoblieApiParams=new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile("18612987312");
        Result result = proxyBindApiManager.getPassportIdFromMobile(baseMoblieApiParams);
        System.out.println(result.toString());
    }
}
