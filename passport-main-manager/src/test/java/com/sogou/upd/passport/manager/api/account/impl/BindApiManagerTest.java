package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:19
 * To change this template use File | Settings | File Templates.
 */
public class BindApiManagerTest extends BaseTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private BindApiManager bindApiManager;

    private static final String mobile = mobile_2;
    private static final String mobile_passportId = userid_phone;
    private static final String exist_mobile = "18978941658";
    private static final String new_bind_mobile = "15986484867";
    private static final String binded_mobile = "13621009174";
    private static final String wrong_mobile = "1360x0x0x01";
    private static final String no_mobile = "13601010101";


    @Test
    public void testBindMobile() {
        //账号不存在
        String accountNotExistStr = "{\"data\":{},\"statusText\":\"账号不存在\",\"status\":\"20205\"}";
        Result accountNotExistExpectedResult = new APIResultSupport(false, ErrorUtil.INVALID_ACCOUNT);
        Account account = accountService.queryNormalAccount("shopengzhi@sogou.com");
        Result accountNotExistActualResult = bindApiManager.bindMobile("shopengzhi@sogou.com", new_bind_mobile, account);
        Assert.assertEquals(accountNotExistExpectedResult.toString(), accountNotExistActualResult.toString());
        //手机号已绑定或已注册
        String mobileNotBind = "{\"data\":{},\"statusText\":\"绑定密保手机失败\",\"status\":\"20289\"}";
        Result mobileNotBindExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
        account = accountService.queryNormalAccount(mobile_userid_sogou);
        Result mobileNotBindActualResult = bindApiManager.bindMobile(mobile_userid_sogou, exist_mobile, account);
        Assert.assertEquals(mobileNotBindExpectedResult.toString(), mobileNotBindActualResult.toString());
        //账号已绑定手机号
        String accountBinded = "{\"data\":{},\"statusText\":\"绑定密保手机失败\",\"status\":\"20289\"}";
        Result accountBindedExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
        account = accountService.queryNormalAccount(mobile_userid_sogou);
        Result accountBindedActualResult = bindApiManager.bindMobile(mobile_userid_sogou, new_bind_mobile, account);
        Assert.assertEquals(accountBindedExpectedResult.toString(), accountBindedActualResult.toString());
        //解绑
        accountService.deleteOrUnbindMobile(binded_mobile);
        //sogou账号首次绑定成功
        String bindMobileStr = "{\"data\":{},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        Result bindMobileExpectedResult = new APIResultSupport(true, "0", "操作成功");
        account = accountService.queryNormalAccount(mobile_userid_sogou);
        Result bindMobileActualResult = bindApiManager.bindMobile(mobile_userid_sogou, binded_mobile, account);
        Assert.assertEquals(bindMobileExpectedResult.toString(), bindMobileActualResult.toString());
    }

    @Test
    public void testModifyBindMobile() {
        //账号不存在
        String accountNotExistStr = "{\"data\":{},\"statusText\":\"账号不存在\",\"status\":\"20205\"}";
        Result accountNotExistExpectedResult = new APIResultSupport(false, ErrorUtil.INVALID_ACCOUNT);
        Result accountNotExistActualResult = bindApiManager.modifyBindMobile("shopengzhi@sogou.com", new_bind_mobile);
        Assert.assertEquals(accountNotExistExpectedResult.toString(), accountNotExistActualResult.toString());
        //手机号已绑定或已注册
        String mobileNotBind = "{\"data\":{},\"statusText\":\"绑定密保手机失败\",\"status\":\"20289\"}";
        Result mobileNotBindExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
        Result mobileNotBindActualResult = bindApiManager.modifyBindMobile(mobile_userid_sogou, exist_mobile);
        Assert.assertEquals(mobileNotBindExpectedResult.toString(), mobileNotBindActualResult.toString());
        //修改密保手机
        String accountBinded = "{\"data\":{},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        Result accountBindedExpectedResult = new APIResultSupport(true, "0", "操作成功");
        Result accountBindedActualResult = bindApiManager.modifyBindMobile(mobile_userid_sogou, new_bind_mobile);
        Assert.assertEquals(accountBindedExpectedResult.toString(), accountBindedActualResult.toString());
        //恢复初始值
        String accountRenewBinded = "{\"data\":{},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        Result accountRenewBindedExpectedResult = new APIResultSupport(true, "0", "操作成功");
        Result accountRenewBindedActualResult = bindApiManager.modifyBindMobile(mobile_userid_sogou, binded_mobile);
        Assert.assertEquals(accountRenewBindedExpectedResult.toString(), accountRenewBindedActualResult.toString());
    }

}
