package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.ResetPasswordBySecQuesApiParams;
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
    private SecureApiManager proxySecureApiManager;


    @Test
    public void testUpdatePwd() throws Exception {
        //修改密码
        UpdatePwdApiParams updatePwdApiParams=new UpdatePwdApiParams();
        updatePwdApiParams.setUserid(passportId);
        updatePwdApiParams.setPassword(password+"aaa");
        updatePwdApiParams.setModifyip(modifyIp);
        updatePwdApiParams.setNewpassword("testtest2");
        Result result= proxySecureApiManager.updatePwd(updatePwdApiParams);
//        Assert.assertTrue(result.isSuccess());
        System.out.println(result.toString());

        //再将密码改回来
        updatePwdApiParams=new UpdatePwdApiParams();
        updatePwdApiParams.setUserid(passportId);
        updatePwdApiParams.setPassword("testtest2");
        updatePwdApiParams.setModifyip(modifyIp);
        updatePwdApiParams.setNewpassword(password);
        Result result1= proxySecureApiManager.updatePwd(updatePwdApiParams);
//        Assert.assertTrue(result.isSuccess());
        System.out.println(result1.toString());
    }

    @Test
    public void testUpdateQues() throws Exception {
        UpdateQuesApiParams updateQuesApiParams=new UpdateQuesApiParams();
        updateQuesApiParams.setModifyip(modifyIp);
        updateQuesApiParams.setUserid(passportId);
        updateQuesApiParams.setPassword(password);
        updateQuesApiParams.setNewanswer("测试答案1");
        updateQuesApiParams.setNewquestion("测试问题1");
        Result result= proxySecureApiManager.updateQues(updateQuesApiParams);
        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetUserSecureInfo(){
        GetSecureInfoApiParams getSecureInfoApiParams =new GetSecureInfoApiParams();
        getSecureInfoApiParams.setUserid("lg-coder@sohu.com");
        Result result= proxySecureApiManager.getUserSecureInfo(getSecureInfoApiParams);
        System.out.println(result.toString());
//        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testCreateCode() throws Exception {
        long ct=System.currentTimeMillis();
        String passportId="lg-coder@sohu.com";
        String code= passportId+"1001"+"c3425ddc98da66f51628ee6a59eb08cb784d610c"+ct;
        code= Coder.encryptMD5(code);
        String url="http://127.0.0.1:8080/internal/secure/info?userid="+passportId+"&client_id=1001&code="+code+"&ct="+ct;
        System.out.println(url);
    }


    @Test
    public void testResetPasswordByQues(){
        ResetPasswordBySecQuesApiParams resetPasswordBySecQuesApiParams=new ResetPasswordBySecQuesApiParams();
        resetPasswordBySecQuesApiParams.setUserid(passportId);
        resetPasswordBySecQuesApiParams.setAnswer(answer);
        resetPasswordBySecQuesApiParams.setNewpassword(password);
        resetPasswordBySecQuesApiParams.setModifyip(modifyIp);
        Result result= proxySecureApiManager.resetPasswordByQues(resetPasswordBySecQuesApiParams);
        System.out.println(result.toString());
    }
}
