package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-21
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class ProxyRegisterApiManagerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    //手机账号
    private final static String MOBILE = "13599695053";
    private static final int PROVIDER_PHONE = AccountTypeEnum.PHONE.getValue();
    private final static String PASSPORT_MOBILE = PassportIDGenerator.generator(MOBILE, PROVIDER_PHONE);
    //搜狗账号
    private final static String SOGOU = "liulingtestygtt@sogou.com";
    private static final int PROVIDER_SOGOU = AccountTypeEnum.EMAIL.getValue();
    private final static String PASSPORT_SOGOU = PassportIDGenerator.generator(SOGOU, PROVIDER_SOGOU);
    //邮箱账号
    private final static String MAIL = "liuling65659460@163.com";
    private static final int PROVIDER_MAIL = AccountTypeEnum.EMAIL.getValue();
    private final static String PASSPORT_MAIL = PassportIDGenerator.generator(MAIL, PROVIDER_MAIL);

    private final static int CLIENT_ID = CommonConstant.SGPP_DEFAULT_CLIENTID;
    private final static String CLIENT_KEY = CommonConstant.SGPP_DEFAULT_SERVER_SECRET;
    private final static String PASSWORD = "111111";
    private final static String CAPTHCA = "32815";
    private final static String IP = "127.0.0.1";


    @Test
    public void testCheckUser() {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setClient_id(CLIENT_ID);
        checkUserApiParams.setUserid(PASSPORT_MOBILE);
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(PASSPORT_MOBILE, CLIENT_ID, CLIENT_KEY, ct);
        checkUserApiParams.setCt(ct);
        checkUserApiParams.setCode(code);
        Result result = proxyRegisterApiManager.checkUser(checkUserApiParams);
        Assert.assertTrue(result.isSuccess());

    }


}
