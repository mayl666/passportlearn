package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
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
    private UserInfoApiManager  proxyUserInfoApiManagerImpl;

    @Test
    public void testGetUserInfo() throws Exception {
        GetUserInfoApiparams getUserInfoApiparams=new GetUserInfoApiparams();
        getUserInfoApiparams.setUserid("13600001111@sohu.com");
        getUserInfoApiparams.setFields("usertype,createip,birthday,gender,createip,createtime,"
                +
                "personalid,personalidflag,sec_mobile,sec_email,province," +
                "city,createtime,sec_ques,avatarurl,regappid");
//        getUserInfoApiparams.setFields("gender");
        Result result= proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
        System.out.println(result);
    }

    @Test
    public void testUpdateUserInfo(){
        UpdateUserInfoApiParams updateUserInfoApiParams=new UpdateUserInfoApiParams();
        updateUserInfoApiParams.setUserid(userid);
        updateUserInfoApiParams.setGender("2");
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,1988);
        updateUserInfoApiParams.setBirthday(calendar.getTime());
        updateUserInfoApiParams.setProvince(530000);
        updateUserInfoApiParams.setCity(532401);
        updateUserInfoApiParams.setModifyip(modifyIp);
        Result result= proxyUserInfoApiManagerImpl.updateUserInfo(updateUserInfoApiParams);
        System.out.println(result);
    }
}
