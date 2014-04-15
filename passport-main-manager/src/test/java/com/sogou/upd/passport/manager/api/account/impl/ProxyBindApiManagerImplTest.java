package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
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

    @Autowired
    private BaseProxyManager baseProxyManager;

    @Test
    public void testBindMobile(){
        BindMobileApiParams bindMobileApiParams = new BindMobileApiParams();
        bindMobileApiParams.setUserid(userid);
//        bindMobileApiParams.setMobile("13940075348");
        bindMobileApiParams.setClient_id(clientId);
        Result result = proxyBindApiManager.bindMobile("tinkame710@sogou.com","15210832767");
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());

//        this.testUnbindMobile();
    }
//
//
    @Test
    public void testUnbindMobile(){
        Result result = proxyBindApiManager.unBindMobile("15210832767");
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }
//
//    @Test
//    public void testUpdataBindMobile(){
//        UpdateBindMobileApiParams updateBindMobileApiParams=new UpdateBindMobileApiParams();
//        updateBindMobileApiParams.setUserid(userid);
//        updateBindMobileApiParams.setOldMobile("18912987312");
//        updateBindMobileApiParams.setNewMobile("13940075348");
//        Result result = proxyBindApiManager.updateBindMobile(updateBindMobileApiParams);
//        System.out.println(result.toString());
//    }

//    @Test
//    public void testUnbindMobile(){
//        String mobile="13940075348";
//        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.UNBING_MOBILE, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
//        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
//        baseMoblieApiParams.setMobile(mobile);
//        requestModelXml.addParams(baseMoblieApiParams);
//        Result result= baseProxyManager.executeResult(requestModelXml,mobile);
//        System.out.println(result.toString());
//    }
//
//
//    @Test
//    public void testSendCaptchaUnbind(){
//        SendCaptchaApiParams sendCaptchaApiParams=new SendCaptchaApiParams();
//        sendCaptchaApiParams.setType(4);
//        sendCaptchaApiParams.setMobile("15210832767");
//        Result result = proxyBindApiManager.sendCaptcha(sendCaptchaApiParams);
//        System.out.println(result.toString());
//    }
//
//    @Test
//    public void testSendCaptchaBind(){
//        SendCaptchaApiParams sendCaptchaApiParams=new SendCaptchaApiParams();
//        sendCaptchaApiParams.setType(3);
//        sendCaptchaApiParams.setMobile("18210696900");
//        Result result = proxyBindApiManager.sendCaptcha(sendCaptchaApiParams);
//        System.out.println(result.toString());
//    }
//
//    @Test
//    public void testBindMobile(){
//        BindMobileApiParams bindMobileApiParams=new BindMobileApiParams();
//        bindMobileApiParams.setNewMobile("18210696900");
//        bindMobileApiParams.setNewCaptcha("1454");
//        bindMobileApiParams.setOldCaptcha("1621");
//        bindMobileApiParams.setOldMobile("15210832767");
//        bindMobileApiParams.setUserid(userid);
//        Result result = proxyBindApiManager.bindMobile(bindMobileApiParams);
//        System.out.println(result.toString());
//    }



    @Test
    public void testBindEmail() throws Exception {
        BindEmailApiParams bindEmailApiParams=new BindEmailApiParams();
        bindEmailApiParams.setNewbindemail("411541129@qq.com");
        bindEmailApiParams.setOldbindemail("34310327@qq.com");
        bindEmailApiParams.setPassword(Coder.encryptMD5(password));
        bindEmailApiParams.setClient_id(clientId);
        bindEmailApiParams.setUserid(userid);
        bindEmailApiParams.setPwdtype(1);
        Result result = proxyBindApiManager.bindEmail(bindEmailApiParams);
        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetPassportIdFromMobile(){
        BaseMoblieApiParams baseMoblieApiParams=new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile("13621050796");
        Result result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
        System.out.println("result:"+result.toString());
    }
}
