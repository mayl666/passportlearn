package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Map;

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
        GetUserInfoApiparams getUserInfoApiParams=new GetUserInfoApiparams();
//        getUserInfoApiParams.setUserid("pqmagic20061@sohu.com");

        getUserInfoApiParams.setUserid("1666643531@sina.sohu.com");

//        getUserInfoApiParams.setFields("uniqname");
        Result result= proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiParams);
        String userid = result.getModels().get("userid").toString();

        Result userInfoResult = new APIResultSupport(true);
        userInfoResult.setCode(result.getCode());
        userInfoResult.setMessage(result.getMessage());

        Map<String, Object> data = Maps.newHashMap();
        Map<String, Object> value_data = Maps.newHashMap();
        value_data.put("id","");
        value_data.put("birthday",result.getModels().get("birthday").toString());
        value_data.put("sex",result.getModels().get("gender").toString());
        value_data.put("nick",result.getModels().get("uniqname").toString());
        value_data.put("location",result.getModels().get("province").toString());
        value_data.put("headurl",result.getModels().get("avatarurl").toString());
        data.put("result",value_data);
        data.put("userid",userid);
        userInfoResult.setModels(data);

        System.out.println("############"+userInfoResult.toString());
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
        updateUserInfoApiParams.setUniqname("111");
        updateUserInfoApiParams.setModifyip(modifyIp);
        Result result= proxyUserInfoApiManagerImpl.updateUserInfo(updateUserInfoApiParams);
        System.out.println(result);
    }

    @Test
    public void testCheckUniqName(){
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();

//        String name="%E4%B8%AD%E6%96%87323212";
        String name="你看";
        updateUserUniqnameApiParams.setUniqname(name);
        Result result = proxyUserInfoApiManagerImpl.checkUniqName(updateUserUniqnameApiParams);
        System.out.println("result输出结果为:" + result.toString());
    }

}
