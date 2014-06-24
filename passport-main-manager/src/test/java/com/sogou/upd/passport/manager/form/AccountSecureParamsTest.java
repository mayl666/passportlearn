package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午1:35 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountSecureParamsTest extends AbstractJUnit4SpringContextTests {
    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String CLIENT_ID = "999";
    private static final String CLIENT_ID_NULL = null;
    private static final String CLIENT_ID_EMPTY = "";
    private static final String CLIENT_ID_WRONG = "99A";

    class AccountParamsTest {
        @NotBlank(message = "账号不允许为空!")
        private String passport_id;
        @NotBlank
        @Min(0)
        private String client_id;
        private String couldBeNull;

        public String getCouldBeNull() {
            return couldBeNull;
        }

        public void setCouldBeNull(String couldBeNull) {
            this.couldBeNull = couldBeNull;
        }

        public String getPassport_id() {
            return passport_id;
        }

        public void setPassport_id(String passport_id) {
            this.passport_id = passport_id;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }
    }

    @Test
    public void testValidateParams() {
        // 测试@NotBlank的设置，以及@Min用在String类型上是否能限制为数字格式
        AccountParamsTest params = new AccountParamsTest();
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

