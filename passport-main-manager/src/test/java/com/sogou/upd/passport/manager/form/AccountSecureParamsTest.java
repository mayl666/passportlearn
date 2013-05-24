package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午1:35 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountSecureParamsTest extends AbstractJUnit4SpringContextTests {
    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String CLIENT_ID = "999";
    private static final String CLIENT_ID_NULL = null;
    private static final String CLIENT_ID_EMPTY = "";
    private static final String CLIENT_ID_WRONG = "99A";

    @Test
    public void testValidateParams() {
        // 测试@NotBlank的设置，以及@Min用在String类型上是否能限制为数字格式
        AccountSecureParams params = new AccountSecureParams();
        params.setPassport_id(PASSPORT_ID);
        params.setClient_id(CLIENT_ID);
        String validateResult = ControllerHelperForTest.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            System.out.println("测试失败：" + validateResult);
        } else {
            System.out.println("测试成功！");
        }

        params.setClient_id(CLIENT_ID_NULL);
        validateResult = ControllerHelperForTest.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            System.out.println("测试成功：" + validateResult);
        } else {
            System.out.println("测试失败！");
        }

        params.setClient_id(CLIENT_ID_EMPTY);
        validateResult = ControllerHelperForTest.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            System.out.println("测试成功：" + validateResult);
        } else {
            System.out.println("测试失败！");
        }

        params.setClient_id(CLIENT_ID_WRONG);
        validateResult = ControllerHelperForTest.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            System.out.println("测试成功：" + validateResult);
        } else {
            System.out.println("测试失败！");
        }
    }


}

