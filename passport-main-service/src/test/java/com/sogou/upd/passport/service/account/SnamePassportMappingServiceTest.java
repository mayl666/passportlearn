package com.sogou.upd.passport.service.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: liuling Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class SnamePassportMappingServiceTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private SnamePassportMappingService snamePassportMappingService;


    // Account Test Constant
    public static final String PASSPORT_ID = "13600000000@sohu.com";
    public static final String SNAME = "sohuplus_name";
    public static final String SID = "11111111111";

    /**
     * 测试初始化非第三方用户账号
     */

    @Test
    public void queryPassportIdBySname() throws Exception {
        String res = snamePassportMappingService.queryPassportIdBySname(SNAME);
        if (Strings.isNullOrEmpty(res)) {
            System.out.println("插入account表成功...");
        } else {
            System.out.println("插入account表不成功!!!");
        }
    }

    @Test
    public void updateSnamePassportMapping() throws Exception {
        boolean res = snamePassportMappingService.updateSnamePassportMapping(SNAME,PASSPORT_ID);
        if (res) {
            System.out.println("插入account表成功...");
        } else {
            System.out.println("插入account表不成功!!!");
        }
    }

    @Test
    public void insertSnamePassportMapping() throws Exception {
        boolean res = snamePassportMappingService.insertSnamePassportMapping("11111112221122","new33333test23","tinkame7222241@sohu.com","");
        if (res) {
            System.out.println("插入account表成功...");
        } else {
            System.out.println("插入account表不成功!!!");
        }
    }


    @Test
    public void deleteSnamePassportMapping() throws Exception {
        boolean res = snamePassportMappingService.deleteSnamePassportMapping(SNAME);
        if (res) {
            System.out.println("插入account表成功...");
        } else {
            System.out.println("插入account表不成功!!!");
        }
    }
}
