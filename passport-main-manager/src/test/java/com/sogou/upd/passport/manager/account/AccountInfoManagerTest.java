package com.sogou.upd.passport.manager.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
//@Ignore
public class AccountInfoManagerTest extends BaseTest {

    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

    private String uniqname_update = "测试昵称" + new Random().nextInt(1000);
    private String fullname_update = "测试全称" + new Random().nextInt(1000);
    private static final String personalId = "530101198309115642";

    @Test
    public void testUpdateAccountInfo() throws IOException {
        //搜狗账号修改昵称
        AccountInfoParams aip1 = getAccountInfoParams(userid_sogou_1, uniqname_update, fullname_update, null, null, null, personalId, null, null);
        Result actualResult1 = accountInfoManager.updateUserInfo(aip1, modifyIp);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        String expectString1 = "{\"data\":{},\"statusText\":\"修改成功\",\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
        Assert.assertTrue(expectForm1.equals(actualForm1));
        AccountInfoParams aip2 = getAccountInfoParams(userid_sogou_1, "阿沐测试01", "测试全称", null, null, null, personalId, null, null);
        Result actualResult2 = accountInfoManager.updateUserInfo(aip2, modifyIp);
    }

    @Test
    public void testGetAccountInfo() throws IOException {
        ObtainAccountInfoParams params = getObtainAccountInfoParams(userid_sogou_1, userinfo_all_fields);
        //搜狗账号获取全属性的个人资料
        String expectString1 = "{\"data\":{\"birthday\":\"1900-01-01\",\"username\":\"测试全称\",\"sec_ques\":\"测试问题\",\"userid\":\"liulingtest01@sogou.com\",\"province\":\"\",\"gender\":\"1\",\"sec_email\":\"\",\"sec_mobile\":\"\",\"uniqname\":\"阿沐测试01\",\"personalid\":\"530101198309115642\",\"city\":\"\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
        Result actualResult1 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm1.equals(actualForm1));
        //个性账号获取全属性的个人资料
        params.setUsername(userid_sogou_1_another);
        Result actualResult2 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm2 = JacksonJsonMapperUtil.getMapper().readValue(actualResult2.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm1.equals(actualForm2));
        //手机账号获取全属性的个人资料
        params.setUsername(userid_phone);
        String expectString3 = "{\"data\":{\"birthday\":\"1987-01-01\",\"username\":\"\",\"sec_ques\":\"\",\"userid\":\"13581695053@sohu.com\",\"province\":\"110000\",\"gender\":\"2\",\"sec_email\":\"\",\"sec_mobile\":\"13581695053\",\"uniqname\":\"13581695053\",\"personalid\":\"\",\"city\":\"110100\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        APIResultForm expectForm3 = JacksonJsonMapperUtil.getMapper().readValue(expectString3, APIResultForm.class);
        Result actualResult3 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm3 = JacksonJsonMapperUtil.getMapper().readValue(actualResult3.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm3.equals(actualForm3));
        //外域邮箱账号获取全属性的个人资料
        params.setUsername(userid_email);
        String expectString4 = "{\"data\":{\"birthday\":\"1900-01-01\",\"username\":\"\",\"sec_ques\":\"\",\"userid\":\"loveerin9460@163.com\",\"province\":\"\",\"gender\":\"1\",\"sec_email\":\"loveerin9460@163.com\",\"sec_mobile\":\"\",\"uniqname\":\"loveerin9460\",\"personalid\":\"\",\"city\":\"\"},\"status\":\"0\",\"statusText\":\"操作成功\"}";
        APIResultForm expectForm4 = JacksonJsonMapperUtil.getMapper().readValue(expectString4, APIResultForm.class);
        Result actualResult4 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm4 = JacksonJsonMapperUtil.getMapper().readValue(actualResult4.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm4.equals(actualForm4));
        //第三方账号获取全属性的个人资料，且account_info表里没记录
        params.setUsername(userid_connect);
        String expectString5 = "{\"data\":{\"userid\":\"CFF81AB013A94663D83FEC36AC117933@qq.sohu.com\",\"sec_mobile\":\"\",\"uniqname\":\"阿沐\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        APIResultForm expectForm5 = JacksonJsonMapperUtil.getMapper().readValue(expectString5, APIResultForm.class);
        Result actualResult5 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm5 = JacksonJsonMapperUtil.getMapper().readValue(actualResult5.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm5.equals(actualForm5));
        //不存在的账号获取全属性的个人资料，且account_info表里没记录
        params.setUsername(userid_invild);
        String expectString6 = "{\"data\":{},\"status\":\"20205\",\"statusText\":\"账号不存在\"}";
        APIResultForm expectForm6 = JacksonJsonMapperUtil.getMapper().readValue(expectString6, APIResultForm.class);
        Result actualResult6 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm6 = JacksonJsonMapperUtil.getMapper().readValue(actualResult6.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm6.equals(actualForm6));
        //搜狗账号获取不存在属性的个人资料
        params.setUsername(userid_sogou_1);
        params.setFields(userinfo_no_fields);
        String expectString7 = "{\"data\":{\"username\":\"测试全称\",\"userid\":\"liulingtest01@sogou.com\",\"uniqname\":\"阿沐测试01\"},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        APIResultForm expectForm7 = JacksonJsonMapperUtil.getMapper().readValue(expectString7, APIResultForm.class);
        Result actualResult7 = accountInfoManager.getUserInfo(params);
        APIResultForm actualForm7 = JacksonJsonMapperUtil.getMapper().readValue(actualResult7.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm7.equals(actualForm7));
    }

    @Test
    public void testGetUserNickNameAndAvatar() throws IOException {
        GetUserInfoApiparams params = new GetUserInfoApiparams();
        params.setClient_id(clientId);
        params.setFields("uniqname,avatarurl");
        //数据库里存在的搜狐域账号
        params.setUserid(userid_phone);
        String sohuAccountInfoExpect = "{\"data\":{\"img_30\":\"\",\"img_50\":\"\",\"img_180\":\"\",\"userid\":\"13581695053@sohu.com\",\"account\":{\"id\":0,\"password\":\"ybiqf2QI$ygztMKXtqLj8QUlWIUv1x0\",\"uniqname\":null,\"avatar\":null,\"accountType\":2,\"passportId\":\"13581695053@sohu.com\",\"flag\":1,\"mobile\":\"13581695053\",\"regIp\":\"10.129.192.121\",\"regTime\":1395671209000,\"passwordtype\":2},\"uniqname\":\"13581695053\",\"avatarurl\":\"\"},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expect1 = JacksonJsonMapperUtil.getMapper().readValue(sohuAccountInfoExpect, APIResultForm.class);
        String sohuAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual1 = JacksonJsonMapperUtil.getMapper().readValue(sohuAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect1, actual1);
        //数据库里不存在的搜狐域账号
        params.setUserid("spztest1@sohu.com");
        String noExistSohuAccountInfoExpect = "{\"statusText\":\"\",\"data\":{\"img_30\":\"\",\"img_50\":\"\",\"img_180\":\"\",\"userid\":\"spztest1@sohu.com\",\"account\":null,\"uniqname\":\"spztest1\",\"avatarurl\":\"\"},\"status\":\"0\"}";
        APIResultForm expect2 = JacksonJsonMapperUtil.getMapper().readValue(noExistSohuAccountInfoExpect, APIResultForm.class);
        String noExistSohuAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual2 = JacksonJsonMapperUtil.getMapper().readValue(noExistSohuAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect2, actual2);
        //数据库里存在的搜狗账号
        params.setUserid(userid_sogou_1);
        String sogouAccountInfoExpect = "{\"data\":{\"img_30\":\"http://img04.sogoucdn.com/app/a/100140006/Ti78RREtsRL63r64_1395717724516\",\"img_50\":\"http://img04.sogoucdn.com/app/a/100140007/Ti78RREtsRL63r64_1395717724516\",\"img_180\":\"http://img01.sogoucdn.com/app/a/100140008/Ti78RREtsRL63r64_1395717724516\",\"userid\":\"liulingtest01@sogou.com\",\"account\":{\"id\":0,\"password\":\"ol3WcQve$gdwqt9ybb9/IL/v5SA5e0.\",\"uniqname\":\"阿沐测试01\",\"avatar\":\"%s/app/a/%s/Ti78RREtsRL63r64_1395717724516\",\"accountType\":1,\"passportId\":\"liulingtest01@sogou.com\",\"flag\":1,\"mobile\":null,\"regIp\":\"10.129.192.121\",\"regTime\":1395717724000,\"passwordtype\":2},\"uniqname\":\"阿沐测试01\",\"avatarurl\":\"http://img04.sogoucdn.com/app/a/100140007/Ti78RREtsRL63r64_1395717724516\"},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expect3 = JacksonJsonMapperUtil.getMapper().readValue(sogouAccountInfoExpect, APIResultForm.class);
        String sogouAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual3 = JacksonJsonMapperUtil.getMapper().readValue(sogouAccountInfoActual, APIResultForm.class);
//        Assert.assertEquals(expect3, actual3);  //图片url cdn域名，每次生成的可能会变
        //数据库里不存在的搜狗账号
        params.setUserid("dafdsaggasd@sogou.com");
        String noExistSogouAccountInfoExpect = "{\"data\":{},\"status\":\"20205\",\"statusText\":\"账号不存在\"}";
        APIResultForm expect4 = JacksonJsonMapperUtil.getMapper().readValue(noExistSogouAccountInfoExpect, APIResultForm.class);
        String noExistSogouAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual4 = JacksonJsonMapperUtil.getMapper().readValue(noExistSogouAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect4, actual4);
        //数据库里不存在的第三方账号
        params.setUserid("dasfasdfasdfasdweq@qq.sohu.com");
        String noExistConnectAccountInfoExpect = "{\"data\":{},\"status\":\"20205\",\"statusText\":\"账号不存在\"}";
        APIResultForm expect5 = JacksonJsonMapperUtil.getMapper().readValue(noExistConnectAccountInfoExpect, APIResultForm.class);
        String noExistConnectAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual5 = JacksonJsonMapperUtil.getMapper().readValue(noExistConnectAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect5, actual5);
        //修改fields
        params.setUserid(userid_sogou_1);
        params.setFields("uniqname,sec_email,province");
        String diffFieldsAccountInfoExpect = "{\"data\":{\"userid\":\"liulingtest01@sogou.com\",\"account\":{\"id\":0,\"password\":\"ol3WcQve$gdwqt9ybb9/IL/v5SA5e0.\",\"uniqname\":\"阿沐测试01\",\"avatar\":\"%s/app/a/%s/Ti78RREtsRL63r64_1395717724516\",\"accountType\":8,\"passportId\":\"liulingtest01@sogou.com\",\"flag\":1,\"mobile\":null,\"regIp\":\"10.129.192.121\",\"regTime\":1395717724000,\"passwordtype\":2},\"uniqname\":\"阿沐测试01\"},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expect6 = JacksonJsonMapperUtil.getMapper().readValue(diffFieldsAccountInfoExpect, APIResultForm.class);
        String diffFieldsAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual6 = JacksonJsonMapperUtil.getMapper().readValue(diffFieldsAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect6, actual6);
    }

    @Test
    public void testGetUserUniqName(){
        int clientId = 1120;
        String expectStr;
        String actualStr;
        //=====================需要encode=============
        //搜狗账号
        expectStr = "%E9%98%BF%E6%B2%90%E6%B5%8B%E8%AF%9501";
        actualStr = accountInfoManager.getUniqName(userid_sogou_1, clientId, true);
        Assert.assertEquals(expectStr, actualStr);
        //个性账号
        expectStr = "%E9%98%BF%E6%B2%90%E6%B5%8B%E8%AF%9501";
        actualStr = accountInfoManager.getUniqName(userid_sogou_1_another, clientId, true);
        Assert.assertEquals(expectStr, actualStr);
        //外域邮箱账号
        expectStr = "loveerin9460";
        actualStr = accountInfoManager.getUniqName(userid_email, clientId, true);
        Assert.assertEquals(expectStr, actualStr);
        //第三方账号
        expectStr = "%E9%98%BF%E6%B2%90";
        actualStr = accountInfoManager.getUniqName(userid_connect, clientId, true);
        Assert.assertEquals(expectStr, actualStr);
        //=====================不需要encode=============
        //搜狗账号
        expectStr = "阿沐测试01";
        actualStr = accountInfoManager.getUniqName(userid_sogou_1, clientId, false);
        Assert.assertEquals(expectStr, actualStr);
        //外域邮箱账号
        expectStr = "loveerin9460";
        actualStr = accountInfoManager.getUniqName(userid_email, clientId, false);
        Assert.assertEquals(expectStr, actualStr);
        //第三方账号
        expectStr = "阿沐";
        actualStr = accountInfoManager.getUniqName(userid_connect, clientId, false);
        Assert.assertEquals(expectStr, actualStr);
        //接口对比
        expectStr = oAuth2ResourceManager.getUniqname(userid_email, clientId);
        actualStr = accountInfoManager.getUniqName(userid_email, clientId, false);
        Assert.assertEquals(expectStr, actualStr);
        expectStr = oAuth2ResourceManager.getUniqname(userid_connect, clientId);
        actualStr = accountInfoManager.getUniqName(userid_connect, clientId, false);
        Assert.assertEquals(expectStr, actualStr);
    }

    //构造更新用户信息参数
    protected AccountInfoParams getAccountInfoParams(String userid, String uniqname, String fullname, String province, String city, String gender, String personalId, String birthday, String username) {
        AccountInfoParams params = new AccountInfoParams();
        params.setUniqname(uniqname);
        params.setClient_id(String.valueOf(clientId));
        if (!Strings.isNullOrEmpty(userid)) params.setUsername(userid);
        if (!Strings.isNullOrEmpty(fullname)) params.setFullname(fullname);
        if (!Strings.isNullOrEmpty(personalId)) params.setPersonalid(personalId);
        if (!Strings.isNullOrEmpty(province)) params.setProvince(province);
        if (!Strings.isNullOrEmpty(city)) params.setCity(city);
        if (!Strings.isNullOrEmpty(gender)) params.setGender(gender);
        if (!Strings.isNullOrEmpty(birthday)) params.setBirthday(birthday);
        return params;
    }

    protected ObtainAccountInfoParams getObtainAccountInfoParams(String userid, String fields) {
        ObtainAccountInfoParams oaip = new ObtainAccountInfoParams();
        oaip.setUsername(userid);
        oaip.setClient_id(String.valueOf(clientId));
        oaip.setFields(fields);
        return oaip;
    }
}
