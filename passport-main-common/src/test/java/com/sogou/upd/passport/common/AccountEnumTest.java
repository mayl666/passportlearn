package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 下午4:40 To change this template use
 * File | Settings | File Templates.
 */

public class AccountEnumTest extends TestCase {

    @Test
    public void testAccountModule() {
        AccountModuleEnum module = AccountModuleEnum.REGISTER;
        Assert.assertTrue(module == AccountModuleEnum.REGISTER);
        Assert.assertTrue(module != AccountModuleEnum.LOGIN);
        Assert.assertTrue(module.getDirect().equals(AccountModuleEnum.REGISTER.getDirect()));
        Assert.assertTrue(module.getDirect().equals("register"));
        Assert.assertTrue(!module.getDirect().equals(AccountModuleEnum.LOGIN.getDirect()));
        Assert.assertTrue(!module.getDirect().equals("login"));

        // 测试是否能直接与字符串作连接操作
        String joinStr = module + " can join strings";
        System.out.println(joinStr);
    }

    @Test
    public void testAccountDomain() {
        AccountDomainEnum domain = AccountDomainEnum.INDIVID;
        System.out.println(domain.toString());
        System.out.println(domain);
        System.out.println(AccountDomainEnum.INDIVID);
        assertTrue(domain == AccountDomainEnum.INDIVID);
        assertTrue(domain.equals(AccountDomainEnum.INDIVID));
        assertTrue(domain != AccountDomainEnum.SOGOU);
        assertTrue(!domain.equals(AccountDomainEnum.SOGOU));
    }
}
