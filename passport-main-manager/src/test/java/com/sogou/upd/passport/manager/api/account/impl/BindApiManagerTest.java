package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.service.account.AccountService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:19
 * To change this template use File | Settings | File Templates.
 */
public class BindApiManagerTest extends BaseTest {

    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private BindApiManager bindApiManager;

    private static final String mobile = mobile_2;
    private static final String mobile_passportId = userid_sohu;
    private static final String exist_mobile = "18978941658";
    private static final String new_bind_mobile = "15986484867";
    private static final String binded_mobile = "13621009174";
    private static final String wrong_mobile = "1360x0x0x01";
    private static final String no_mobile = "13601010101";

    @Test
    public void testGetPassportId(){
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile("13071155730");
        Result resultSH = proxyBindApiManager.getPassportIdByMobile(params);
        System.out.println(resultSH.toString());
    }

    /**
     * 手机号已注册或已绑定的情况下---根据手机号获取passportId
     */
    @Test
    public void testGetPassportIdByMobile() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(mobile);
        Result resultSH = proxyBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSH = JacksonJsonMapperUtil.getMapper().readValue(resultSH.toString(), APIResultForm.class);
        Assert.assertEquals("0", formSH.getStatus());
        Assert.assertEquals(mobile_passportId, formSH.getData().get("userid"));
        Assert.assertEquals("操作成功", formSH.getStatusText());
        Result resultSG = sgBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
        Assert.assertTrue(formSH.equals(formSG));
        //如果比较失败，可打开下面的结果输出对比找原因
//        System.out.println("-----------------结果如下---------------");
//        System.out.println("搜狐结果：" + resultSH);
//        System.out.println("搜狗结果：" + resultSG);
    }

    /**
     * 手机号不存在的情况下---根据手机号获取passportId
     */
    @Test
    public void testGetPassportIdByMobileNoMobile() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(no_mobile);
        Result resultSH = proxyBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSH = JacksonJsonMapperUtil.getMapper().readValue(resultSH.toString(), APIResultForm.class);
        Assert.assertEquals(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND, formSH.getStatus());
        Assert.assertEquals(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND), formSH.getStatusText());
        Result resultSG = sgBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
        Assert.assertTrue(formSH.equals(formSG));
        //如果比较失败，可打开下面的结果输出对比找原因
//        System.out.println("-----------------结果如下---------------");
//        System.out.println("搜狐结果：" + resultSH);
//        System.out.println("搜狗结果：" + resultSG);
    }

    @Test
    public void testBindMobile() {
        //账号不存在
        String accountNotExistStr = "{\"data\":{},\"statusText\":\"账号不存在\",\"status\":\"20205\"}";
        Result accountNotExistExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
        Result accountNotExistActualResult = bindApiManager.bindMobile("shopengzhi@sogou.com", new_bind_mobile);
        Assert.assertEquals(accountNotExistExpectedResult.toString(), accountNotExistActualResult.toString());
        //手机号已绑定或已注册
        String mobileNotBind = "{\"data\":{},\"statusText\":\"绑定密保手机失败\",\"status\":\"20289\"}";
        Result mobileNotBindExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
        Result mobileNotBindActualResult = bindApiManager.bindMobile(mobile_userid_sogou, exist_mobile);
        Assert.assertEquals(mobileNotBindExpectedResult.toString(), mobileNotBindActualResult.toString());
        //账号已绑定手机号
        String accountBinded = "{\"data\":{},\"statusText\":\"绑定密保手机失败\",\"status\":\"20289\"}";
        Result accountBindedExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
        Result accountBindedActualResult = bindApiManager.bindMobile(mobile_userid_sogou, new_bind_mobile);
        Assert.assertEquals(accountBindedExpectedResult.toString(), accountBindedActualResult.toString());
        //解绑
        accountService.deleteOrUnbindMobile(binded_mobile);
        //sogou账号首次绑定成功
        String bindMobileStr = "{\"data\":{},\"statusText\":\"操作成功\",\"status\":\"0\"}";
        Result bindMobileExpectedResult = new APIResultSupport(true,"0", "操作成功");
        Result bindMobileActualResult = bindApiManager.bindMobile(mobile_userid_sogou, binded_mobile);
        Assert.assertEquals(bindMobileExpectedResult.toString(), bindMobileActualResult.toString());
        //外域账号首次绑定成功
//        proxyBindApiManager.unBindMobile("13624598765");
//        String waiyuBindMobileStr = "{\"data\":{},\"statusText\":\"操作成功\",\"status\":\"0\"}";
//        Result waiyuBindMobileExpectedResult = new APIResultSupport(true,"0", "操作成功");
//        Result waiyuBindMobileActualResult = bindApiManager.bindMobile(userid_email, "13624598765");
//        Assert.assertEquals(waiyuBindMobileExpectedResult.toString(), waiyuBindMobileActualResult.toString());
//        proxyBindApiManager.unBindMobile("13624598765");
    }

    @Test
    public void testModifyBindMobile() {
        //账号不存在
        String accountNotExistStr = "{\"data\":{},\"statusText\":\"账号不存在\",\"status\":\"20205\"}";
        Result accountNotExistExpectedResult = new APIResultSupport(false, ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
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
