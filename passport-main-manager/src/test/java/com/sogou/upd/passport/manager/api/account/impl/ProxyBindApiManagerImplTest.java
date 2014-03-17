package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
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

//    @Test
//    public void testBindMobile(){
//        BindMobileApiParams bindMobileApiParams = new BindMobileApiParams();
//        bindMobileApiParams.setUserid(userid);
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
//        BaseMobileApiParams unBindMobileApiParams =new BaseMobileApiParams();
//        unBindMobileApiParams.setMobile("18210193340");
//        unBindMobileApiParams.setClient_id(clientId);
//        Result result = proxyBindApiManager.updateBindMobile(unBindMobileApiParams);
//        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
//    }
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
//        BaseMobileApiParams baseMoblieApiParams = new BaseMobileApiParams();
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
        BaseMobileApiParams baseMobileApiParams =new BaseMobileApiParams();
        baseMobileApiParams.setMobile("18910872912");
        Result result = proxyBindApiManager.getPassportIdByMobile(baseMobileApiParams);
        System.out.println(result.toString());
    }
}
