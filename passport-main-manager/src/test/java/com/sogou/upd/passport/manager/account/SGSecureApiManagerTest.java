package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-24
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class SGSecureApiManagerTest extends BaseTest {

    @Autowired
    private SecureManager sgSecureApiManager;

    /**
     * 外域邮箱修改密码
     *
     * @throws Exception
     */
    @Test
    public void testUpdatePwd() throws Exception {
        UpdatePwdParameters updatePwdParameters = new UpdatePwdParameters();
        updatePwdParameters.setClient_id(String.valueOf(clientId));
        updatePwdParameters.setIp(modifyIp);
        updatePwdParameters.setPassword(password);
        updatePwdParameters.setNewpwd(new_password);
        updatePwdParameters.setPassport_id(userid_mail);
        Result result = sgSecureApiManager.resetWebPassword(updatePwdParameters, modifyIp);
        Assert.assertTrue(result.isSuccess());
    }
}
