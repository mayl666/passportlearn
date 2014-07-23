package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-17
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class CommonManagerImplTest extends BaseTest {

    @Autowired
    private CommonManager commonManager;

    /**
     * 根据用户名获取主账号
     *
     * @throws Exception
     */
    @Test
    public void testGetPassportIdByUsername() throws Exception {
        //手机@sohu.com账号
        String actual1 = commonManager.getPassportIdByUsername(userid_phone);
        Assert.assertEquals(actual1, userid_phone);
        //外域账号
        String actual2 = commonManager.getPassportIdByUsername(userid_email);
        Assert.assertEquals(actual2, userid_email);
        //sogou账号
        String actual3 = commonManager.getPassportIdByUsername(userid_sogou_1);
        Assert.assertEquals(actual3, userid_sogou_1);
        //qq账号
        String actual4 = commonManager.getPassportIdByUsername(userid_connect);
        Assert.assertEquals(actual4, userid_connect);
        //手机账号
        String expect5 = "loveerin@sogou.com";
        String actual5 = commonManager.getPassportIdByUsername(mobile_1);
        Assert.assertEquals(expect5, actual5);
        //个性账号
        String expect6 = userid_invild + "@sogou.com";
        String actual6 = commonManager.getPassportIdByUsername(userid_invild);
        Assert.assertEquals(expect6, actual6);
    }

}
