package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:28
 */
public class ProxyUserInfoApiManagerImplTest extends BaseTest {

    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;

    @Test
    public void testGetUserInfo() throws Exception {
        GetUserInfoApiparams getUserInfoApiParams = new GetUserInfoApiparams();
        getUserInfoApiParams.setUserid("pqmagic20061@sohu.com");
        //获取昵称
        getUserInfoApiParams.setFields("uniqname");
        Result result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiParams);
        System.out.println("nick result:" + result);
        //获取图片
        getUserInfoApiParams.setFields("sec_email,uniqname,avatarurl");
        getUserInfoApiParams.setImagesize("180,55");
        Result result1 = proxyUserInfoApiManager.getUserInfo(getUserInfoApiParams);
        System.out.println("avatar result:" + result1);
    }

    @Test
    public void testUpdateUserInfo() {
        UpdateUserInfoApiParams updateUserInfoApiParams = new UpdateUserInfoApiParams();
        updateUserInfoApiParams.setUserid(userid);
        updateUserInfoApiParams.setGender("2");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1988);
        updateUserInfoApiParams.setBirthday(calendar.getTime());
        updateUserInfoApiParams.setProvince(530000);
        updateUserInfoApiParams.setCity(532401);
        updateUserInfoApiParams.setUniqname("111");
        updateUserInfoApiParams.setModifyip(modifyIp);
        Result result = proxyUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        System.out.println(result);
    }

    @Test
    public void testCheckUniqName() {
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();

//        String name="%E4%B8%AD%E6%96%87323212";
        String name = "你看";
        updateUserUniqnameApiParams.setUniqname(name);
        Result result = proxyUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        System.out.println("result输出结果为:" + result.toString());
    }

}
