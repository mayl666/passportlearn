package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-30
 * Time: 下午7:55
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class AccountBaseInfoServiceTest extends BaseTest {

    @Autowired
    private AccountBaseInfoService accountBaseInfoService;

    private String passportId = "shipengzhi@qq.sohu.com";
    private String uniqname_1 = "shipengzhi";
    private String avatar_1 = "http://sucimg.itc.cn/avatarimg/100054944_1384153927685_c175";

    @Test
    public void testInitConnectAccountBaseInfo() {
        ConnectUserInfoVO connectUserInfoVO = new ConnectUserInfoVO();
        connectUserInfoVO.setNickname(uniqname_1);
        connectUserInfoVO.setAvatarLarge(avatar_1);

        AccountBaseInfo success = accountBaseInfoService.initConnectAccountBaseInfo(passportId, connectUserInfoVO);
//        Assert.assertTrue(success);

    }


}
