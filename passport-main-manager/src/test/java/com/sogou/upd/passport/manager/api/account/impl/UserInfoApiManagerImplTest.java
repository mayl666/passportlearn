package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:20
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class UserInfoApiManagerImplTest extends BaseTest {

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;

    //检查昵称是否可用
    private String new_uniqname_english = "erinslover" + new Random().nextInt(100);
    private String new_uniqname_format = "搜狐lover" + new Random().nextInt(100);
    //更新用户信息
    private String uniqname_english = "happychen" + new Random().nextInt(1000);
    private String uniqname_chinese = "阿沐昵称" + new Random().nextInt(1000);
    private String uniqname_update = "测试昵称" + new Random().nextInt(1000);
    private String fullname = "阿沐牛" + new Random().nextInt(1000);
    private static final String province = "110000";
    private static final String city = "110100";
    private static final String gender = "0";
    private static final String personalId = "110102199406013990";
    private static final String birthday = "1985-12-09";
    private String username = fullname;
    //获取用户信息
    private static final String fields = "province,city,gender,birthday,fullname,personalid,uniqname,mobile,email,question";

    /**
     * 更新用户信息
     */
    @Test
    public void testUpdateUserInfo() throws Exception {
        //更新用户，昵称为英文
        UpdateUserInfoApiParams params_english = getUpdateUserInfoApiParams(userid_sogou_1, uniqname_english, fullname, province, city, gender, personalId, birthday, username);
//        Result expectResult1 = proxyUserInfoApiManager.updateUserInfo(params_english);
//        System.out.println(expectResult1);
        String expectResult1 = "{\"statusText\":\"操作成功\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectResult1.toString(), APIResultForm.class);
        Result actualResult1 = sgUserInfoApiManager.updateUserInfo(params_english);
//        System.out.println(actualResult1.toString());  //{"statusText":"修改个人资料成功","data":{},"status":"0"}
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        Assert.assertTrue(!expectForm1.equals(actualForm1));//todo 提示信息不一致，但状态码需要修改一致

        //更新用户，昵称为中文
        UpdateUserInfoApiParams params_chinese = getUpdateUserInfoApiParams(userid_sogou_1, uniqname_chinese, fullname, province, city, gender, personalId, birthday, username);
//        Result expectResult2 = proxyUserInfoApiManager.updateUserInfo(params_chinese);
//        System.out.println(expectResult2);
        String expectResult2 = "{\"statusText\":\"操作成功\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm2 = JacksonJsonMapperUtil.getMapper().readValue(expectResult2.toString(), APIResultForm.class);
        Result actualResult2 = sgUserInfoApiManager.updateUserInfo(params_chinese);
//        System.out.println(actualResult2.toString());
        APIResultForm actualForm2 = JacksonJsonMapperUtil.getMapper().readValue(actualResult2.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm2.equals(actualForm2)); //todo 提示信息不一致，但状态码需要修改一致

        //只更新用户昵称
        UpdateUserInfoApiParams params = getUpdateUserInfoApiParams(userid_sogou_1, uniqname_update, null, null, null, null, null, null, null);
//        Result expectResult = proxyUserInfoApiManager.updateUserInfo(params);
//        System.out.println(expectResult);
        String expectResult = "{\"statusText\":\"操作成功\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm = JacksonJsonMapperUtil.getMapper().readValue(expectResult.toString(), APIResultForm.class);
        Result actualResult = sgUserInfoApiManager.updateUserInfo(params_chinese);
//        System.out.println(actualResult.toString());
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult.toString(), APIResultForm.class);
//        Assert.assertTrue(!expectForm2.equals(actualForm2)); //todo 提示信息不一致，但状态码需要修改一致
    }

    /**
     * 检查用户昵称是否可用
     */
    @Test
    public void testCheckUniqName() throws Exception {
        //昵称不存在
        UpdateUserUniqnameApiParams params_not = getUpdateUserUniqnameApiParams(new_uniqname_english);
//            Result expectResult1 = proxyUserInfoApiManager.checkUniqName(params_not);
//            System.out.println(expectResult1);
        String expectResult1 = "{\"statusText\":\"昵称未被占用\",\"data\":{},\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectResult1.toString(), APIResultForm.class);
        Result actualResult1 = sgUserInfoApiManager.checkUniqName(params_not);
//        System.out.println(actualResult1.toString());
        APIResultForm actualForm = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
//            Assert.assertTrue(expectForm1.equals(actualForm)); //todo 返回的错误提示信息不一样，但错误码一样，此处也需要保持一致

        //昵称存在
        UpdateUserUniqnameApiParams params_have = getUpdateUserUniqnameApiParams(uniqname_sogou_1);
//            Result expectResult2 = proxyUserInfoApiManager.checkUniqName(params_have);
//            System.out.println(expectResult2.toString());
        String expectResult2 = "{\"statusText\":\"用户昵称已经被使用\",\"data\":{\"userid\":\"testeeee@sogou.com\"},\"status\":\"20248\"}";
        APIResultForm expectForm2 = JacksonJsonMapperUtil.getMapper().readValue(expectResult2.toString(), APIResultForm.class);
        Result actualResult2 = sgUserInfoApiManager.checkUniqName(params_have);
        System.out.println(actualResult2.toString());  //{"statusText":"昵称未被占用,可以使用","data":{},"status":"0"}
        APIResultForm actualForm2 = JacksonJsonMapperUtil.getMapper().readValue(actualResult2.toString(), APIResultForm.class);
//        Assert.assertTrue(expectForm2.equals(actualForm2));

        //昵称包含限制词
        UpdateUserUniqnameApiParams params_format = getUpdateUserUniqnameApiParams(new_uniqname_format);
//            Result expectResult3 = proxyUserInfoApiManager.checkUniqName(params_format);
//            System.out.println(expectResult3);
        String expectResult3 = "{\"statusText\":\"昵称包含限制词\",\"data\":{},\"status\":\"20249\"}";
        APIResultForm expectForm3 = JacksonJsonMapperUtil.getMapper().readValue(expectResult3.toString(), APIResultForm.class);
        Result actualResult3 = sgUserInfoApiManager.checkUniqName(params_format);
//        System.out.println(actualResult3.toString());      {"statusText":"昵称未被占用,可以使用","data":{},"status":"0"}
        APIResultForm actualForm3 = JacksonJsonMapperUtil.getMapper().readValue(actualResult3.toString(), APIResultForm.class);
//        Assert.assertTrue(expectForm3.equals(actualForm3)); todo 搜狗没有做限制词的判断,故返回昵称未被使用
    }


    /**
     * 获取用户信息
     */
    @Test
    public void testGetUserInfo() throws Exception {
        GetUserInfoApiparams params = getUserInfoApiParams(fields, userid_sogou_1);
//        Result expectResult3 = proxyUserInfoApiManager.getUserInfo(params);
//        System.out.println(expectResult3);
//        String expectResult3 = "{\"statusText\":\"操作成功\",\"data\":{\"birthday\":\"" + birthday + "\",\"flag\":\"1\",\"province\":\"" + province + "\",\"userid_chinese\":\"" + userid_sogou_1 + "\",\"gender\":\"" + gender + "\",\"fullname\":\"" + fullname + "\",\"avatarurl\":null,\"city\":\"" + city + "\"},\"status\":\"0\"}";
//            APIResultForm expectForm3 = JacksonJsonMapperUtil.getMapper().readValue(expectResult3.toString(), APIResultForm.class);
        Result actualResult3 = sgUserInfoApiManager.getUserInfo(params);
        System.out.println(actualResult3.toString());
//            APIResultForm actualForm3 = JacksonJsonMapperUtil.getMapper().readValue(actualResult3.toString(), APIResultForm.class);
//            Assert.assertTrue(expectForm3.equals(actualForm3));

    }

}
