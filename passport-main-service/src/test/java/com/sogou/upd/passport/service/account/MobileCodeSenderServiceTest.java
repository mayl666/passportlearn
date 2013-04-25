package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

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

    private static final String MOBILE = "13545210241";
    private static final String SMSCODE = "13267";
    private static final int CLIENT_ID = 1001;
    private static final String CACHE_KEY = MOBILE + "_" + CLIENT_ID;

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;

    /**
     * 测试验证手机号码与验证码是否匹配
     */
    @Test
    public void testCheckSmsInfoFromCache() {
        boolean flag = mobileCodeSenderService.checkSmsInfoFromCache(MOBILE, SMSCODE, CLIENT_ID);
        if (flag) {
            System.out.println("匹配...");
        } else {
            System.out.println("不匹配!!!");
        }
    }


    /**
     * 测试缓存中是否有此号码
     */
    @Test
    public void testCheckCacheKeyIsExist() {
        boolean flag = mobileCodeSenderService.checkIsExistMobileCode(CACHE_KEY);
        if (flag) {
            System.out.println("存在...");
        } else {
            System.out.println("不存在!!!");
        }
    }

    /**
     * 测试注册成功后清除sms缓存信息
     */
    @Test
    public void testDeleteSmsCache() {
        boolean flag = mobileCodeSenderService.deleteSmsCache(MOBILE, CLIENT_ID);
        if (flag) {
            System.out.println("清除成功...");
        } else {
            System.out.println("清除失败!!!");
        }
    }

    /**
     * 测试手机验证码的获取与重发
     */
    @Test
    public void testHandleSendSms() {
        Map<String, Object> mapResult = null;
        mobileCodeSenderService.handleSendSms(MOBILE, CLIENT_ID);
        if (MapUtils.isNotEmpty(mapResult)) {
            System.out.println(mapResult.size());
        } else {
            System.out.println(ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND));
        }
    }

    /**
     * 测试重发验证码时更新缓存状态
     */
    @Test
    public void testUpdateSmsInfoByCacheKeyAndClientid() {
        Map<String, Object> mapResult = null;
        mobileCodeSenderService.updateSmsCacheInfo(CACHE_KEY, CLIENT_ID);
        if (MapUtils.isNotEmpty(mapResult)) {
            System.out.println(mapResult.size());
        } else {
            System.out.println(ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND));
        }
    }
}
