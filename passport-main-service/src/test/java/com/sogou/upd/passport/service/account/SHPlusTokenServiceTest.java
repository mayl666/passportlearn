package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
public class SHPlusTokenServiceTest extends BaseTest {

    @Autowired
    private SHPlusTokenService shPlusTokenService;

    @Test
    public void testVerifyShPlusRefreshToken(){
        String passportId = "shipengzhi1986@sogou.com";
        int clientId = 1065;
        String instanceId = "112950932";
        String refreshToken = "43399244b170cef4264a0cac9f53cd495eb4157f2aee5c86d3734528c93136d8";
        shPlusTokenService.verifyShPlusRefreshToken(passportId,clientId,instanceId,refreshToken);
    }
}
