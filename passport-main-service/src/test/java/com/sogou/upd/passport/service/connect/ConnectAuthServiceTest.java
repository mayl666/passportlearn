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
    private final static String passportId = "001A6C38D281DECDEC5438F451945F05@qq.sohu.com";


    @Test
    public void testObtainConnectUserInfoFromSogou() {
        int provider = AccountTypeEnum.getAccountType(passportId).getValue();
        ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(passportId, provider, CommonConstant.APP_CONNECT_KEY);
        if (connectUserInfoVO != null) {
            System.out.println("----------------------结果如下-----------------");
            System.out.println(connectUserInfoVO.getNickname());
            System.out.println(connectUserInfoVO.getAvatarSmall());
            System.out.println(connectUserInfoVO.getAvatarMiddle());
            System.out.println(connectUserInfoVO.getAvatarLarge());
            System.out.println(connectUserInfoVO.getGender());
        } else {
            System.out.println("从搜狗获取用户信息失败");
        }


    }
}
