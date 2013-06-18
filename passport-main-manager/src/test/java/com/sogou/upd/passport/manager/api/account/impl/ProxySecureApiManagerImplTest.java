package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 上午11:26
 */
public class ProxySecureApiManagerImplTest  extends BaseTest {

    @Autowired
    private SecureApiManager proxySecureApiManager;

    @Test
    public void testUpdatePwd() throws Exception {
        UpdatePwdApiParams updatePwdApiParams=new UpdatePwdApiParams();
        updatePwdApiParams.setUserid(userid);
        updatePwdApiParams.setModifyip(modifyIp);
        updatePwdApiParams.setPassword("testtest");
        updatePwdApiParams.setNewpassword("testtest1");
        Result result= proxySecureApiManager.updatePwd(updatePwdApiParams);
        System.out.println(result.toString());
    }

    @Test
    public void testUpdateQues() throws Exception {
        UpdateQuesApiParams updateQuesApiParams=new UpdateQuesApiParams();
        updateQuesApiParams.setPassword("testtest1");
        updateQuesApiParams.setModifyip(modifyIp);
        updateQuesApiParams.setNewanswer("测试答案");
        updateQuesApiParams.setNewquestion("测试问题");
        updateQuesApiParams.setUserid(userid);
        Result result= proxySecureApiManager.updateQues(updateQuesApiParams);
        System.out.println(result.toString());

    }

    public void testGetUserSecureInfo() throws Exception {

    }

    public void testResetPasswordByQues() throws Exception {

    }
}
