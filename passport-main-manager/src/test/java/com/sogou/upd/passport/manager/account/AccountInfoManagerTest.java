package com.sogou.upd.passport.manager.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
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
        AccountInfoParams aip1 = getAccountInfoParams(userid_sogou_1, uniqname_update, fullname_update, null, null, null, personalId, null, null);
        Result actualResult1 = accountInfoManager.updateUserInfo(aip1, modifyIp);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        String expectString1 = "{\"data\":{\"baseInfo\":{\"id\":483846,\"uniqname\":\"" + uniqname_update + "\",\"avatar\":null,\"passportId\":\"" + userid_sogou_1 + "\"}},\"statusText\":\"修改成功\",\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
        Assert.assertTrue(expectForm1.equals(actualForm1));
    }

    @Test
    public void testGetAccountInfo() throws IOException {
        ObtainAccountInfoParams oaip1 = getObtainAccountInfoParams(userid_sogou_1, fields);
        Result actualResult1 = accountInfoManager.getUserInfo(oaip1);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        String expectString1 = "{\"statusText\":\"操作成功\",\"data\":{\"username\":\"\",\"flag\":\"1\",\"province\":\"\",\"userid\":\"" + userid_sogou_1 + "\",\"uniqname\":\"" + uniqname_update + "\",\"avatarurl\":null,\"city\":\"\"},\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
//        Assert.assertTrue(expectForm1.equals(actualForm1));
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
