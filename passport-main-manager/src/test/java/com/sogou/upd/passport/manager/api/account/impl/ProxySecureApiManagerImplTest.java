package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import com.sogou.upd.passport.service.account.AccountService;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 上午11:26
 */
@Ignore
public class ProxySecureApiManagerImplTest extends BaseTest {

    @Autowired
    private SecureApiManager proxySecureApiManager;
    @Autowired
    private SecureApiManager sgSecureApiManager;
    @Autowired
    private AccountService accountService;

    @Test
    public void testUpdatePwd() throws Exception {
        String passportId = "shipengzhi1986@sogou.com";
        int clientId = 1120;
        String oldPwd = "spz1986411";
        String newPwd = "spz2915871";
        String successStr = "{\"statusText\":\"操作成功\",\"status\":\"0\",\"data\":{}}";
        Result successExpectedResult = new APIResultSupport(true, "0", "操作成功");
        Result successActualResult = sgSecureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        Assert.assertEquals(successExpectedResult, successActualResult);

        String pwdErrorStr = "{\"statusText\":\"密码错误\",\"status\":\"20206\",\"data\":{}}";
        Result pwdErrorExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
        Result pwdErrorActualResult = sgSecureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        Assert.assertEquals(pwdErrorExpectedResult, pwdErrorActualResult);

        Result recoveryResult = sgSecureApiManager.updatePwd(passportId, clientId, newPwd, oldPwd, modifyIp);
        Result endResult = accountService.verifyUserPwdVaild(passportId, oldPwd, true);
        Assert.assertTrue(endResult.isSuccess());
    }

    @Test
    public void testUpdateQues() throws Exception {
        UpdateQuesApiParams updateQuesApiParams = new UpdateQuesApiParams();
        updateQuesApiParams.setPassword("testtest1");
        updateQuesApiParams.setModifyip(modifyIp);
        updateQuesApiParams.setNewanswer("测试答案");
        updateQuesApiParams.setNewquestion("测试问题");
        updateQuesApiParams.setUserid(userid);
        Result result = proxySecureApiManager.updateQues(updateQuesApiParams);
        System.out.println(result.toString());

    }

    public void testGetUserSecureInfo() throws Exception {

    }

    public void testResetPasswordByQues() throws Exception {

    }

}
