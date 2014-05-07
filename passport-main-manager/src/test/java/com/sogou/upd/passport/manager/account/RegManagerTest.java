package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-5-7
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class RegManagerTest extends BaseTest {

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegManager regManager;

    @Test
    public void testSendMobileRegCaptcha() {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(new_mobile);
        baseMoblieApiParams.setClient_id(1100);
        Result result = sgRegisterApiManager.sendMobileRegCaptcha(baseMoblieApiParams);
        Assert.assertTrue(result.isSuccess());
    }

    //wap手机注册，type=wap
    @Test
    public void testRegMobileUser() throws Exception {
        String mobile = "13500401112";//与下发短信接口的手机中与保持一致
        String captcha = "95096";//需要发送短信接口下发的手机验证码
        Result result = regManager.registerMobile(mobile, password, CommonConstant.SGPP_DEFAULT_CLIENTID, captcha, ConnectTypeEnum.WAP.toString());
        Assert.assertTrue(result.isSuccess());
        System.out.println(result.toString());
        System.out.println(result.getModels().get("sgid").toString());

    }
}
