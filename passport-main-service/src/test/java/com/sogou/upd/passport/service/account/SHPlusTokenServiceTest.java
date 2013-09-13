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
        String refreshToken = "7d91b7c1e68ab8ff51c252dd02611b4b5ffb542542144c521625123b339b54c6";
        shPlusTokenService.verifyShPlusRefreshToken(passportId,clientId,instanceId,refreshToken);
    }
}
