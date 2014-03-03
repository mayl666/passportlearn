package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-3
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
public class ConnectAuthServiceTest extends BaseTest {
    @Autowired
    private ConnectAuthService connectAuthService;
    private final static String passportId = "0115BAD1B6B4E49A27824FCC2398A1BA@qq.sohu.com";


    @Test
    public void testObtainConnectUserInfoFromSogou() {
        int provider = AccountTypeEnum.getAccountType(passportId).getValue();
        ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfoFromSogou(passportId, provider, CommonConstant.APP_CONNECT_KEY);
        System.out.println(connectUserInfoVO);

    }
}
