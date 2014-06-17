package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:28
 */
//@Ignore
public class ProxyUserInfoApiManagerImplTest extends BaseTest {

    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Test
    public void testGetUserInfo2() throws Exception {
        GetUserInfoApiparams getUserInfoApiParams=new GetUserInfoApiparams();
        getUserInfoApiParams.setUserid("ljyf_136615@sogou.com");
        getUserInfoApiParams.setFields("usertype,createip,birthday,gender,createip,createtime,"
                +
                "personalid,personalidflag,sec_mobile,sec_email,province," +
                "city,createtime,sec_ques,avatarurl,regappid");
//        getUserInfoApiParams.setFields("uniqname");
        Result result= proxyUserInfoApiManager.getUserInfo(getUserInfoApiParams);
        System.out.println(result);
    }

    @Test
    public void testGetUserInfo() throws Exception {
        GetUserInfoApiparams getUserInfoApiParams = new GetUserInfoApiparams();
        getUserInfoApiParams.setUserid("573582495@qq.com");
        //获取昵称
//        getUserInfoApiParams.setUserid("pqmagic20061@sohu.com");
//        getUserInfoApiParams.setFields("uniqname");
//        Result result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiParams);
//        System.out.println("nick result:" + result);
        //获取图片
        getUserInfoApiParams.setFields("sec_mobile,sec_email,uniqname,avatarurl");
//        getUserInfoApiParams.setImagesize("180");
        Result result1 = proxyUserInfoApiManager.getUserInfo(getUserInfoApiParams);
        System.out.println(result1.toString());
//        String bindMobile = (String) result1.getModels().get("sec_mobile");
//        String bindEmail =(String)result1.getModels().get("sec_email");
//
//        String imgurl="";
//        String avaterMap =  result1.getModels().get("avatarurl").toString();
//        if(!StringUtils.isEmpty(avaterMap)){
//
//            Map map = (Map)result1.getModels().get("avatarurl");
//            imgurl =(String)map.get("img_180");
//        }
//
//        System.out.println("bindMobile:"+bindMobile);
//        System.out.println("bindEmail:"+bindEmail);
//        System.out.println("avatar result:" + result1);
    }

    @Test
    public void testUpdateUserInfo() {
        UpdateUserInfoApiParams updateUserInfoApiParams = new UpdateUserInfoApiParams();
        updateUserInfoApiParams.setUserid(userid);
        updateUserInfoApiParams.setGender("2");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1988);
        updateUserInfoApiParams.setBirthday("2012-01-01");
//        updateUserInfoApiParams.setProvince(530000);
//        updateUserInfoApiParams.setCity(532401);
        updateUserInfoApiParams.setUniqname("111");
        updateUserInfoApiParams.setModifyip(modifyIp);
        Result result = proxyUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        System.out.println(result);
    }

    @Test
    public void testCheckUniqName() {
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();

//        String name="%E4%B8%AD%E6%96%87323212";
        String name = "汽车驾驶模拟器1946在搜狐";
        updateUserUniqnameApiParams.setUniqname(name);
        Result result = proxyUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        System.out.println("result输出结果为:" + result.toString());
    }

    @Test
    public void testDefaultPhoto() {
//       Result photoResult = proxyUserInfoApiManager.obtainPhoto(Integer.toString(1044), "30,50,180");
//       System.out.println(photoResult.toString());

    }

}
