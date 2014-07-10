package com.sogou.upd.passport.manager.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import junit.framework.Assert;
import org.codehaus.jackson.JsonParseException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
//@Ignore
public class AccountInfoManagerTest extends BaseTest {

    @Autowired
    private AccountInfoManager accountInfoManager;

    private String uniqname_update = "测试昵称" + new Random().nextInt(1000);
    private String fullname_update = "测试全称" + new Random().nextInt(1000);
    private static final String fields = "province,city,fullname,personalid,uniqname";
    private static final String personalId = "530101198309115642";

    @Test
    public void testUpdateAccountInfo() throws IOException {
        //搜狗账号修改昵称
//        AccountInfoParams aip1 = getAccountInfoParams(userid_sogou_1, uniqname_update, fullname_update, null, null, null, personalId, null, null);
//        Result actualResult1 = accountInfoManager.updateUserInfo(aip1, modifyIp);
//        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
//        String expectString1 = "{\"data\":{\"baseInfo\":{\"id\":483846,\"uniqname\":\"" + uniqname_update + "\",\"avatar\":null,\"passportId\":\"" + userid_sogou_1 + "\"}},\"statusText\":\"修改成功\",\"status\":\"0\"}";
//        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
//        Assert.assertTrue(expectForm1.equals(actualForm1));
//        System.out.println(expectString1);
    }

    @Test
    public void testGetAccountInfo() throws IOException {
//        ObtainAccountInfoParams oaip1 = getObtainAccountInfoParams(userid_sogou_1, fields);
//        Result actualResult1 = accountInfoManager.getUserInfo(oaip1);
//        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
//        String expectString1 = "{\"statusText\":\"操作成功\",\"data\":{\"username\":\"\",\"flag\":\"1\",\"province\":\"\",\"userid\":\"" + userid_sogou_1 + "\",\"uniqname\":\"" + uniqname_update + "\",\"avatarurl\":null,\"city\":\"\"},\"status\":\"0\"}";
//        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
//        Assert.assertTrue(expectForm1.equals(actualForm1));
    }

    @Test
    public void testGetUserNickNameAndAvatar() throws IOException {
        GetUserInfoApiparams params = new GetUserInfoApiparams();
        params.setClient_id(clientId);
        params.setFields("uniqname,avatarurl");
        //数据库里存在的搜狐域账号
        params.setUserid(userid_sohu);
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
        String diffFieldsAccountInfoExpect = "{\"data\":{\"userid\":\"liulingtest01@sogou.com\",\"account\":{\"id\":0,\"password\":\"ol3WcQve$gdwqt9ybb9/IL/v5SA5e0.\",\"uniqname\":\"阿沐测试01\",\"avatar\":\"%s/app/a/%s/Ti78RREtsRL63r64_1395717724516\",\"accountType\":1,\"passportId\":\"liulingtest01@sogou.com\",\"flag\":1,\"mobile\":null,\"regIp\":\"10.129.192.121\",\"regTime\":1395717724000,\"passwordtype\":2},\"uniqname\":\"阿沐测试01\"},\"status\":\"0\",\"statusText\":\"\"}";
        APIResultForm expect6 = JacksonJsonMapperUtil.getMapper().readValue(diffFieldsAccountInfoExpect, APIResultForm.class);
        String diffFieldsAccountInfoActual = accountInfoManager.getUserNickNameAndAvatar(params).toString();
        APIResultForm actual6 = JacksonJsonMapperUtil.getMapper().readValue(diffFieldsAccountInfoActual, APIResultForm.class);
        Assert.assertEquals(expect6, actual6);
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
