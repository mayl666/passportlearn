package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.SecureApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.GetUserSecureInfoApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.UpdateQuesApiParams;
import junit.framework.Assert;
import org.junit.Test;

import javax.inject.Inject;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午4:03
 */
public class ProxySecureApiManagerImplTest extends BaseTest {

    @Inject
    private SecureApiManager secureApiManager;


    @Test
    public void testUpdatePwd() throws Exception {
        //修改密码
        UpdatePwdApiParams updatePwdApiParams=new UpdatePwdApiParams();
        updatePwdApiParams.setUserid(passportId);
        updatePwdApiParams.setPassword(password+"aaa");
        updatePwdApiParams.setModifyip(modifyIp);
        updatePwdApiParams.setNewpassword("testtest2");
        Result result= secureApiManager.updatePwd(updatePwdApiParams);
//        Assert.assertTrue(result.isSuccess());
        System.out.println(result.toString());

//        //再将密码改回来
//        updatePwdApiParams=new UpdatePwdApiParams();
//        updatePwdApiParams.setUserid(passportId);
//        updatePwdApiParams.setPassword("testtest2");
//        updatePwdApiParams.setModifyip(modifyIp);
//        updatePwdApiParams.setNewpassword(password);
//        result= secureApiManager.updatePwd(updatePwdApiParams);
////        Assert.assertTrue(result.isSuccess());
//        System.out.println(result.toString());
    }

    @Test
    public void testUpdateQues() throws Exception {
        UpdateQuesApiParams updateQuesApiParams=new UpdateQuesApiParams();
        updateQuesApiParams.setModifyip(modifyIp);
        updateQuesApiParams.setUserid(passportId);
        updateQuesApiParams.setPassword(password);
        updateQuesApiParams.setNewanswer("测试答案");
        updateQuesApiParams.setNewquestion("测试问题");
        Result result= secureApiManager.updateQues(updateQuesApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetUserSecureInfo(){
        GetUserSecureInfoApiParams getUserSecureInfoApiParams=new GetUserSecureInfoApiParams();
        getUserSecureInfoApiParams.setUserid(passportId);
        Result result= secureApiManager.getUserSecureInfo(getUserSecureInfoApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }
}
