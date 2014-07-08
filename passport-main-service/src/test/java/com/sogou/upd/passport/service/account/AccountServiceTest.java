package com.sogou.upd.passport.service.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import junit.framework.Assert;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA. User: liuling Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
public class AccountServiceTest extends BaseTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    private static final String MOBILE = "13545210241";
    private static final String PASSWORD = "liuling8";
    private static final String SPZ_PASSPORTID = "shipengzhi1986@sogou.com";
    private static final String exist_mobile = "18978941658";
    private static final String new_bind_mobile = "15986484867";
    private static final String binded_mobile = "13621009174";
    private static final
    String PASSPORT_ID = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final String IP = "127.0.0.1";
    private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();

    /**
     * 测试初始化非第三方用户账号
     */
    @Test
    public void testInitialAccount() throws Exception {
        Account account = accountService.initialAccount(MOBILE, PASSWORD, true, IP, PROVIDER);
        if (account != null) {
            System.out.println("插入account表成功...");
        } else {
            System.out.println("插入account表不成功!!!");
        }
    }

    /**
     * 测试根据用户名获取Account对象
     */
    @Test
    public void testQueryAccountByPassportId() {

        String passportId = "null";


        System.out.println("============strip2Empty:" + StringUtils.stripToEmpty(passportId));
        System.out.println("============strip2NUll:" + StringUtils.stripToNull(passportId));

        if (Strings.isNullOrEmpty(passportId)) {
            System.out.println("check passportId is null");
        }

        Account account = accountService.queryAccountByPassportId(passportId);
        if (account == null) {
            System.out.println("获取不成功!!!");
        } else {
            System.out.println("获取成功..");
        }
    }

    /**
     * 测试验证账号的有效性，是否为正常用户
     */
    @Test
    public void testVerifyAccountVaild() {
        Account account = accountService.queryNormalAccount(PASSPORT_ID);
        if (account != null) {
            System.out.println("用户存在...");
        } else {
            System.out.println("用户不存在!!!");
        }
    }

    /**
     * 测试验证用户名密码是否正确
     */
    @Test
    public void testVerifyUserPwdVaild() {
//        Result result = accountService.verifyUserPwdVaild(MOBILE, PASSWORD, true);
//        if (result.isSuccess()) {
//            System.out.println("正确...");
//        } else {
//            System.out.println("不正确!!!");
//        }
    }

    @Test
    public void testCheckResetPassport() {
        String storedPwd = "GdIRFYky$tjgErloRiudSgGv4Jw1wh1";
        String pwd = "e0be6aca";
        String pwdMD5 = DigestUtils.md5Hex(pwd.getBytes());
        if (storedPwd.equals(pwdMD5)) {
            System.out.println(" pwd equals ");
        }
        System.out.println(" pwd not equals ");
    }


    /**
     * 测试重置密码
     */
    @Test
    public void testResetPassword() {
        Account account = accountService.queryNormalAccount(PASSPORT_ID);
        boolean flag = accountService.resetPassword(account, PASSWORD, true);
        if (flag != false) {
            System.out.println("重置成功...");
        } else {
            System.out.println("重置失败!!!");
        }
    }

    /**
     * 测试首次绑定手机
     */
    @Test
    public void testBindMobile() {
        //初始值shipengzhi1986@sogou.com绑定13621009174
        Account account = accountService.queryAccountByPassportId(SPZ_PASSPORTID);
        //已注册手机无法绑定
        boolean isBinded = accountService.bindMobile(account, exist_mobile);
        Assert.assertTrue(!isBinded);
        //账号已绑定手机，无法再绑
        boolean isModifyBind = accountService.bindMobile(account, new_bind_mobile);
        Assert.assertTrue(!isModifyBind);
        //删除绑定
        boolean isUnbind = accountService.deleteOrUnbindMobile(binded_mobile);
        Assert.assertTrue(isUnbind);
        //异常情况，account写成功，但mapping没写成功
        account.setMobile(binded_mobile);
        boolean isAbnormalBind = accountService.bindMobile(account, binded_mobile);
        Assert.assertTrue(!isAbnormalBind);
        //首次绑定
        account.setMobile(null);
        boolean isBind = accountService.bindMobile(account, binded_mobile);
        Assert.assertTrue(isBind);
    }

    /**
     * 测试修改绑定手机
     */
    @Test
    public void testModifyBindMobile() {
        //初始值shipengzhi1986@sogou.com绑定13621009174
        Account account = accountService.queryAccountByPassportId(SPZ_PASSPORTID);
        //已注册手机无法修改绑定
        boolean isBinded = accountService.modifyBindMobile(account, exist_mobile);
        Assert.assertTrue(!isBinded);
        //账号未绑定手机，无法修改绑定
        account.setMobile(null);
        boolean isModifyBind = accountService.modifyBindMobile(account, new_bind_mobile);
        Assert.assertTrue(!isModifyBind);
        account.setMobile(binded_mobile);
        //修改绑定
        boolean isBind = accountService.modifyBindMobile(account, new_bind_mobile);
        Assert.assertTrue(isBind);
        //异常情况，上一次绑定失败，写account成功，写mapping失败
        mobilePassportMappingService.deleteMobilePassportMapping(new_bind_mobile);
        boolean isAbnormalBind = accountService.modifyBindMobile(account, binded_mobile);
        Assert.assertTrue(!isAbnormalBind);
        //恢复初始值
        mobilePassportMappingService.initialMobilePassportMapping(new_bind_mobile, SPZ_PASSPORTID);
        boolean isRenewBind = accountService.modifyBindMobile(account, binded_mobile);
        Assert.assertTrue(isRenewBind);
    }

    @Test
    public void testFixData() {
        String passportId = "wangqingemail@sohu.com";
        Account account = accountService.queryAccountByPassportId(passportId);


        //初始化 王卿测试账号
        //INSERT INTO account_07 (passport_id,PASSWORD,mobile,reg_time,reg_ip,flag,passwordtype,account_type,uniqname,avatar)VALUE
        // ('wangqingemail@sohu.com',NULL,NULL,'1395990989000','10.1.99.33','1','0','9','KeSyren1234',NULL);


        /**
         * {birthday=1969-03-28, createip=61.135.151.250, status=0, userid=wangqingemail@sohu.com,
         *  personalid=410811198901100105, uniqname=自由的青的夏天, city=450101, createtime=2013-07-18 15:10:51, username=希希,
         *  flag=1, email=wangqing3127@163.com, province=450000, gender=1, mobile=}
         */
        if (account != null) {
            System.out.println("cache has wangqingemail@sohu.com" + JsonUtil.obj2Json(account));
        }
        /**
         * {"id":0,"password":null,
         * "mobile":null,
         * "flag":1,
         * "accountType":9,
         * "passportId":"wangqingemail@sohu.com",
         * "uniqname":"KeSyren1234",
         * "avatar":null,
         * "passwordtype":0,
         * "regTime":1395990989000,
         * "regIp":"10.1.99.33"}
         */
    }


}
