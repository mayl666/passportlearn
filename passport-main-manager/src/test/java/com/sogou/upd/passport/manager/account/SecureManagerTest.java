package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
public class SecureManagerTest extends BaseTest {
    @Autowired
    private SecureManager secureManager;

    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final int CLIENT_ID = 1120;
    private static final String NEW_MOBILE = "18511531063";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "hujunfei1986@126.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    @Test
    public void testSendMobileCode() throws Exception {
        Result result;
        result = secureManager.sendMobileCode(NEW_MOBILE, CLIENT_ID, AccountModuleEnum.REGISTER);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testAll() {

    }

    /* ------------------------------------修改密保Begin------------------------------------ */
    public void testModify() {

    }

    public void testModifyEmail() {

    }

    /* ------------------------------------修改密保End------------------------------------ */

    /* ------------------------------------修改密保End------------------------------------ */

    public void testResetPwd() {

    }

    public void testResetPwdByEmail() {

    }

    @Test
    public void testActionRecord() {
        for (int i = 0; i < 15; i++) {
            secureManager.logActionRecord(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN, "202.106.180." + (i + 1), null);
        }

        Result
                result = secureManager.queryActionRecords(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN);
        System.out.println(result.toString());
    }

    /* ------------------------------------修改密保End------------------------------------ */

}
