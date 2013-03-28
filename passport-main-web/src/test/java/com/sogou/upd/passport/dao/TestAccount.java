package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-25
 * Time: 下午2:04
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
public class TestAccount extends AbstractJUnit4SpringContextTests {

    @Inject
    private AccountService accountService;

    @Inject
    private AppConfigService appConfigService;

    /**
     * 测试手机账号是否存在
     */
    @Test
    public void testCheckIsRegisterAccount() {
        String mobile = "13565090053";
        String passwd = "liuling";
        String passportId = PassportIDGenerator.generator(mobile, AccountTypeEnum.PHONE.getValue());
        Account account = new Account();
        account.setMobile(mobile);
        account.setPasswd(passwd);
        account.setPassportId(passportId);
        boolean as = false;
        try {
            System.out.println("-----开始查询------");
            as = accountService.checkIsRegisterAccount(account);
            System.out.println(as);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (as == true) {
            System.out.println("用户没有注册过，允许注册！");
        } else {
            System.out.println("用户已经注册过，不允许再次注册！");
        }
    }

    /**
     * 测试用户正式注册
     */
    @Test
    public void testUserRegister() {
        String mobile = "13545210241";
        String passwd = "liuling";
        String ip = "192.168.0.1";
        Account account = null;
        try {
            account = accountService.initialAccount(mobile, passwd, ip, AccountTypeEnum.PHONE.getValue());
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (account != null) {
            System.out.println(account.getPasswd());
        } else {
            System.out.println("");
        }

    }

    /**
     * 测试初始化用户状态信息
     */
    @Test
    public void testInitialAccountAuth() {
        long id = 13;
        String passportId = "13545210241@sohu.com";
        int clientId = 1004;
        AccountAuth accountAuth = null;
        try {
            accountAuth = accountService.initialAccountAuth(id, passportId, clientId);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(accountAuth != null){
            System.out.println(accountAuth.getAccessValidTime());
        }else{
            System.out.println("");
        }

    }

    /**
     * 测试更新用户状态表
     */
    @Test
    public void testUpdateAccountAuth() {
        String accessToken = "123456";
        String refreshToken = "jfdroe";
        long accessValidTime = 30;
        long refreshValidTime = 30;
        AccountAuth aa = new AccountAuth();
        aa.setId(1);
        aa.setUserId(3);
        aa.setRefreshToken(refreshToken);
        aa.setAccessToken(accessToken);
        aa.setAccessValidTime(accessValidTime);
        aa.setRefreshValidTime(refreshValidTime);
        int row = accountService.updateAccountAuth(aa);
        System.out.println(row);
    }

    /**
     * 测试根据passportId获取手机号码
     */
    @Test
    public void testGetMobileByPassportId() {
        String passportId = "13520069535@sohu.com";
        String mobile = accountService.getMobileByPassportId(passportId);
        System.out.println(mobile);
    }

    /**
     * 测试根据clientId获取配置信息
     */
    @Test
    public void testGetAppConfigByClientId() {
        int clientId = 1004;
        AppConfig appConfig = appConfigService.getAppConfig(clientId);
        if (appConfig != null) {
            System.out.println(appConfig.getSmsText());
        }

    }

    /**
     *测试根据account表的主键ID获取passportId
     */
    @Test
    public void testGetPassportIdByUserId(){
        long userId = 21;
        String passportId = accountService.getPassportIdByUserId(userId);
        if(passportId != null){
            System.out.println(passportId);
        } else {
            System.out.println("");
        }
    }

    /**
     *测试根据passportId获取account表的主键ID
     */
    @Test
    public void testGetUserIdByPassportId(){
        String passportId = "13621009174@sohu.com";
        long userId = accountService.getUserIdByPassportId(passportId);
        if(userId != 0){
            System.out.println(userId);
        } else {
            System.out.println("零！！！");
        }
    }
}
