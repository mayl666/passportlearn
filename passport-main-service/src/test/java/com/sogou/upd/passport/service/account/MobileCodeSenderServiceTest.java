package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class MobileCodeSenderServiceTest extends AbstractJUnit4SpringContextTests {

    private static final String MOBILE = "18511531063";
    private static final String SMSCODE = "27434";
    private static final int CLIENT_ID = 1120;
    private static final String CACHE_KEY = MOBILE + "_" + CLIENT_ID;

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;

    /**
     * 发送短信验证码
     */
    @Test
    public void testSendSmsToMobile() {
        Result result = mobileCodeSenderService.sendSmsCode(MOBILE, CLIENT_ID, AccountModuleEnum.REGISTER);
        Assert.isTrue(result.isSuccess());
    }

    /**
     * 测试验证手机号码与验证码是否匹配
     */
    @Test
    public void testCheckSmsInfoFromCache() {
        boolean flag = mobileCodeSenderService.checkSmsInfoFromCache(MOBILE, CLIENT_ID, AccountModuleEnum.REGISTER, SMSCODE);
        Assert.isTrue(flag);
    }


    /**
     * 测试缓存中是否有此号码
     */
    @Test
    public void testCheckCacheKeyIsExist() {
        boolean flag = mobileCodeSenderService.checkIsExistMobileCode(CACHE_KEY);
        Assert.isTrue(flag);
    }

    /**
     * 检测当日验证码输入错误次数是否超过上限
     */
    @Test
    public void testCheckLimitForSmsFail() {
        boolean flag = mobileCodeSenderService.checkLimitForSmsFail(MOBILE, CLIENT_ID, AccountModuleEnum.REGISTER);
        Assert.isTrue(flag);
    }

    /**
     * 测试注册成功后清除sms缓存信息
     */
    @Test
    public void testDeleteSmsCache() {
        boolean flag = mobileCodeSenderService.deleteSmsCache(MOBILE, CLIENT_ID);
        Assert.isTrue(flag);
    }
}
