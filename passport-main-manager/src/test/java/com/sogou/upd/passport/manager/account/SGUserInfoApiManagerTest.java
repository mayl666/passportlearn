package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-23
 * Time: 下午8:00
 * To change this template use File | Settings | File Templates.
 */
public class SGUserInfoApiManagerTest extends BaseTest {

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private AccountService accountService;

    /**
     * 检查昵称是否存在
     */
    @Test
    public void testCheckUniqname() {
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setClient_id(clientId);
        updateUserUniqnameApiParams.setUniqname(uniqname);
        Result result = sgUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        Assert.assertTrue(result.isSuccess());
    }

    /**
     * 更新用户个人资料
     *
     * @throws Exception
     */
    @Test
    public void testUpdateUserInfo() throws Exception {
        Account account = accountService.queryAccountByPassportId(userid_sogou);
        String passwordCrypt = PwdGenerator.generatorStoredPwd(password, true);
        if (account == null) {
            Account accountNew = accountService.initialAccount(userid_sogou, passwordCrypt, false, modifyIp, AccountTypeEnum.EMAIL.getValue());
            Assert.assertNotNull(accountNew);
        }
        UpdateUserInfoApiParams updateUserInfoApiParams = new UpdateUserInfoApiParams();
        updateUserInfoApiParams.setClient_id(clientId);
        updateUserInfoApiParams.setUserid(userid_sogou);
        updateUserInfoApiParams.setBirthday(birthday);
        updateUserInfoApiParams.setCity(city);
        updateUserInfoApiParams.setGender(gender);
        updateUserInfoApiParams.setFullname(fullname);
        updateUserInfoApiParams.setProvince(province);
        updateUserInfoApiParams.setPersonalId(personalid);
        updateUserInfoApiParams.setUniqname(new_uniqname);
        updateUserInfoApiParams.setModifyip(modifyIp);
        Result result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        Assert.assertTrue(result.isSuccess());
    }

    /**
     * 获取用户个人资料
     */
    @Test
    public void testGetUserInfo() {
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
        getUserInfoApiparams.setUserid(userid_sogou);
        getUserInfoApiparams.setClient_id(clientId);
        getUserInfoApiparams.setFields(fields);
        Result result = sgUserInfoApiManager.getUserInfo(getUserInfoApiparams);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(birthday, result.getModels().get("birthday"));
        Assert.assertEquals(province, result.getModels().get("province"));
        Assert.assertEquals(city, result.getModels().get("city"));
        Assert.assertEquals(gender, result.getModels().get("gender"));
        Assert.assertEquals(fullname, result.getModels().get("fullname"));
        Assert.assertEquals(personalid, result.getModels().get("personalid"));
        Assert.assertEquals(new_uniqname, result.getModels().get("uniqname"));
    }

}
