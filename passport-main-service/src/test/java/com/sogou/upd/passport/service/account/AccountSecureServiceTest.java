package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.model.account.ActionRecord;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-22 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountSecureServiceTest extends AbstractJUnit4SpringContextTests {
    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final int CLIENT_ID = 999;

    @Autowired
    AccountSecureService accountSecureService;

    @Test
    public void testGetAndSetSecureCodeResetPwd() {
        System.out.println("测试一");
        String secureCode = accountSecureService.getSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID);
        System.out.println("Secure Code is: " + secureCode);
        boolean checkRes = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID, secureCode);
        boolean checkRandom = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID, "ABC");
        System.out.println("验证结果是：" + checkRes + " _ " + checkRandom);
        checkRes = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID, secureCode);
        checkRandom = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID, "ABC");
        System.out.println("二次验证结果是：" + checkRes + " _ " + checkRandom);

    }

    @Test
    public void testGetAndSetSecureCode() {
        System.out.println("测试三");
        String secureCode = accountSecureService.getSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID);
        System.out.println("Secure Code is: " + secureCode);
        boolean checkRes = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID,
                                                                        secureCode);
        boolean checkRandom = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID,
                                                                             CLIENT_ID,
                                                                             secureCode);
        System.out.println("验证结果是：" + checkRes + " _ " + checkRandom);
        checkRes = accountSecureService.checkSecureCodeResetPwd(PASSPORT_ID, CLIENT_ID, secureCode);
        checkRandom = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID,
                                                                     secureCode);
        System.out.println("二次验证结果是：" + checkRes + " _ " + checkRandom);

    }

    @Test
    public void testGetAndSetSecureCodeMod() {
        System.out.println("测试二");
        String secureCode = accountSecureService.getSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID);
        System.out.println("Secure Code is: " + secureCode);
        boolean checkRes = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID,
                                                                          secureCode);
        boolean checkRandom = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID,
                                                                             CLIENT_ID, "ABC");
        System.out.println("验证结果是：" + checkRes + " _ " + checkRandom);
        checkRes = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID,
                                                                  secureCode);
        checkRandom = accountSecureService.checkSecureCodeModSecInfo(PASSPORT_ID, CLIENT_ID,
                                                                     "ABC");
        System.out.println("二次验证结果是：" + checkRes + " _ " + checkRandom);

    }

    @Test
    public void testGetAndSetRecords() throws Exception{
        for (int i=0; i<20; i++) {
            accountSecureService.setActionRecord(PASSPORT_ID, CLIENT_ID,
                    AccountModuleEnum.RESETPWD, "127.0.0.1 - " + i, null);
        }

        List<ActionRecord>
                list = accountSecureService.getActionRecords(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.RESETPWD);
        List<ActionRecord>
                list1 = accountSecureService.getActionRecords(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.REGISTER);
        Iterator it = list.iterator();
        System.out.println("List: ");
        while (it.hasNext()) {
            System.out.println(new ObjectMapper().writeValueAsString(it.next()));
        }
        if (list1 == null) {
            return;
        }
        it = list1.iterator();
        System.out.println("List1: ");
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    private ActionRecord newRecord() {
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setUserId(PASSPORT_ID);
        actionRecord.setClientId(CLIENT_ID);
        actionRecord.setAction(AccountModuleEnum.RESETPWD);
        actionRecord.setIp("127.0.0.1");
        actionRecord.setDate(System.currentTimeMillis());
        return actionRecord;
    }
}
