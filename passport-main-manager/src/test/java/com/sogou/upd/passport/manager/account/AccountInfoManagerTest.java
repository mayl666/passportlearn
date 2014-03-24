package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
public class AccountInfoManagerTest extends BaseTest {
    @Autowired
    private AccountInfoManager accountInfoManager;


    @Test
    public void testUpload() {
        File file = new File("d:/1.jpg");
        try {
            Result result = accountInfoManager.uploadImg(IOUtils.toByteArray(new FileInputStream(file)), "tinkame700@sogou.com", "0");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testObtainImage() {
        Result result = accountInfoManager.obtainPhoto("tinkame700@sogou.com", "50,180");
        System.out.println("result:" + result.getModels().get("180"));
    }

    /**
     * 获取已存在用户的个人资料
     */
    @Test
    public void testGetUserInfo() {
        String fields = "province,city,gender,birthday,fullname,personalid";
        ObtainAccountInfoParams obtainAccountInfoParams = new ObtainAccountInfoParams();
        obtainAccountInfoParams.setFields(fields);
        obtainAccountInfoParams.setClient_id(String.valueOf(clientId));
        obtainAccountInfoParams.setUsername(userid_sogou);
        Result result = accountInfoManager.getUserInfo(obtainAccountInfoParams);
        Assert.assertTrue(result.isSuccess());
    }

    /**
     * 获取不存在用户的个人资料
     */
    @Test
    public void testGetUserInfoWithout() {
        String fields = "province,city,gender,birthday,fullname,personalid";
        ObtainAccountInfoParams obtainAccountInfoParams = new ObtainAccountInfoParams();
        obtainAccountInfoParams.setFields(fields);
        obtainAccountInfoParams.setClient_id(String.valueOf(clientId));
        obtainAccountInfoParams.setUsername(userid);
        Result result = accountInfoManager.getUserInfo(obtainAccountInfoParams);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT,result.getCode());
    }
}
