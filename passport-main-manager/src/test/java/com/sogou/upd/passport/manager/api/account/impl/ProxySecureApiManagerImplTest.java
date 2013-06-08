package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
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
        GetSecureInfoApiParams getSecureInfoApiParams =new GetSecureInfoApiParams();
        getSecureInfoApiParams.setUserid(passportId);
        Result result= secureApiManager.getUserSecureInfo(getSecureInfoApiParams);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testCreateCode() throws Exception {
        long ct=System.currentTimeMillis();
        String code= passportId+"1001"+"c3425ddc98da66f51628ee6a59eb08cb784d610c"+ct;
        code= Coder.encryptMD5(code);
        String url="http://127.0.0.1:8080/internal/secure/info?userid="+passportId+"&client_id=1001&code="+code+"&ct="+ct;
        System.out.println(url);
    }
}
