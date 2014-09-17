package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.utils.ReflectUtil;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;
import com.sogou.upd.passport.service.account.dataobject.WapActiveEmailDO;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;
import com.sogou.upd.passport.service.account.impl.EmailSenderServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-26
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public class EmailSenderServiceTest extends BaseTest {
    @Autowired
    private AccountSecureService accountSecureService;

    @Test
    public void testBuildActiveUrl() throws Exception {
        String passportId = mobile_userid_sogou;
        int clientId = 1120;
        String flag = String.valueOf(System.currentTimeMillis());
        String token = accountSecureService.getSecureCodeRandom(flag);
        String ru = CommonConstant.DEFAULT_INDEX_URL + "?token=" + token + "&id=" + flag + "&username=" + passportId;
        String toEmail = "shipengzhi1986@126.com";
        String scode = SecureCodeGenerator.generatorSecureCode(passportId, clientId);
        // web端绑定邮箱发激活链接
        ActiveEmailDO webActiveEmailDO = new ActiveEmailDO(passportId, clientId, ru, AccountModuleEnum.SECURE, toEmail, true);
        String webExpectedUrl = CommonConstant.DEFAULT_INDEX_URL + "/web/security/checkemail?username=" + passportId + "&client_id=" + clientId + "&scode=" + scode + "&ru=" + URLEncoder.encode(ru, "UTF-8");
        String webActualUrl = (String) ReflectUtil.invoke(new EmailSenderServiceImpl(), "buildActiveUrl", new Class[]{ActiveEmailDO.class, String.class},
                new Object[]{webActiveEmailDO, scode});
        Assert.assertEquals(webExpectedUrl, webActualUrl);
        // wap端绑定邮箱发激活链接
        String skin = "red";
        String v = "5";
        ActiveEmailDO wapActiveEmailDO = new WapActiveEmailDO(passportId, clientId, ru, AccountModuleEnum.RESETPWD, toEmail, false, skin, v);
        String wapExpectedUrl = CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap/findpwd/checkemail?username=" + passportId + "&client_id=" + clientId + "&scode=" + scode + "&v=" + v + "&skin=" + skin + "&ru=" + URLEncoder.encode(ru, "UTF-8");
        String wapActualUrl = (String) ReflectUtil.invoke(new EmailSenderServiceImpl(), "buildActiveUrl", new Class[]{ActiveEmailDO.class, String.class},
                new Object[]{wapActiveEmailDO, scode});
        Assert.assertEquals(wapExpectedUrl, wapActualUrl);
    }
}
