package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.SohuPasswordType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.service.account.AccountService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-18
 * Time: 上午11:26
 */
//@Ignore
public class SecureApiManagerImplTest extends BaseTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SecureApiManager secureApiManager;

    @Test
    public void testUpdatePwd() throws Exception {
        String passportId = "shipengzhi1986@sogou.com";
        int clientId = 1120;
        String oldPwd = "spz1986411";
        String newPwd = "spz2915871";
        String successStr = "{\"statusText\":\"操作成功\",\"status\":\"0\",\"data\":{}}";
        Result successExpectedResult = new APIResultSupport(true, "0", "操作成功");
        Result successActualResult = secureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        Assert.assertEquals(successExpectedResult.toString(), successActualResult.toString());

        String pwdErrorStr = "{\"statusText\":\"密码错误\",\"status\":\"20206\",\"data\":{}}";
        Result pwdErrorExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
        Result pwdErrorActualResult = secureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        Assert.assertEquals(pwdErrorExpectedResult.toString(), pwdErrorActualResult.toString());

        Result recoveryResult = secureApiManager.updatePwd(passportId, clientId, newPwd, oldPwd, modifyIp);
        Result endResult = accountService.verifyUserPwdVaild(passportId, oldPwd, true, SohuPasswordType.TEXT);
        Assert.assertTrue(endResult.isSuccess());
    }

    /**
     * 用户存在且密码正确
     *
     * @throws Exception
     */
    @Test
    public void testUpdateQues_0() throws Exception {
        String userId = "test255@sogou.com";
        String password = "111111";
        String newQues = "测试答案2";
        String newAnswer = "测试问题2";
        Result expectResult = secureApiManager.updateQues(userId, clientId, password, newQues, newAnswer, modifyIp);
        System.out.println(expectResult.toString());
    }

    /**
     * 用户存在,密码错误
     *
     * @throws Exception
     */
    @Test
    public void testUpdateQues_20206() throws Exception {
        String userId = "test255@sogou.com";
        String password = "123456";
        String newQues = "测试问题";
        String newAnswer = "测试答案";
        Result expectResult = secureApiManager.updateQues(userId, clientId, password, newQues, newAnswer, modifyIp);
        System.out.println(expectResult.toString());
    }

    /**
     * 测试密保答案
     *
     * @throws Exception
     */
    @Test
    public void testAnswer() throws Exception {
        //中文4个字符，英文2个，特殊字符1个
        String new_answer = "我是一只小白┰┱┡┏兔，住在海边的，山上";
        boolean flag = true;
        int length = new_answer.getBytes(Charset.forName("GBK")).length;
        if (length < 5 || length > 48) {
            flag = false;
        }
        Assert.assertTrue(flag);
    }

}
