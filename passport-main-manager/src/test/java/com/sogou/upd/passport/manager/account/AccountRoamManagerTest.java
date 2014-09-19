package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-9-19
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class AccountRoamManagerTest extends BaseTest {
    @Autowired
    private AccountRoamManager accountRoamManager;

    @Test
    public void testGetUserIdByBrowerRoamToken() throws IOException {
        String cipherText = "lqz5+RmbnzcoX/Qz+OUJAelP5FG0Ko5mS+6VCjn2t1aXDctPDFzFWQk4XDpBB6JX2glhuAPbFMGt6QeB1nIhP7+a3HB7neUlz0BVg8M7X1StFLWDrzzrjVwFnICJu1h1TTX/gr9ss/a7zIgOzOgNzA9rRs37v+O/zmWe9xdQMKRGQqUTWfw7dXjxliqk0/D5DFOk/yT2ntM0i82hmQDjPNpmbSTekEUezKeUupEkqUxTyJQG/cgXZVkv5keg2blW00TJn3MJb6mLkO3JLihpMgsTnZ+WKT+MG7AKgCM1+hDQMXmeV53eE/WUWo5mkN3r8Dfba3Ikj+QuDrZxeU6T3A==";
        String passportId = accountRoamManager.getUserIdByBrowerRoamToken(cipherText);
        System.out.println("passportId:"+passportId);
    }
}
