package com.sogou.upd.passport.service.account;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.CoreKvUtils;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-30
 * Time: 上午12:08
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class PCAccountTokenServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private TokenRedisUtils tokenRedisUtils;
    @Autowired
    private CoreKvUtils coreKvUtils;

    private static final String INSTANCE_ID = "856416207";
    private static final int CLIENT_ID = 1044;
    private static final String PASSPORT_ID = "shipengzhi1986@sogou.com";
    private AppConfig appConfig;

    @Before
    public void init() {
        appConfig = appConfigService.queryAppConfigByClientId(CLIENT_ID);
    }

    @Test
    public void testInitialAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.initialAccountToken(PASSPORT_ID, INSTANCE_ID, appConfig);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testupdateAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.updateAccountToken(PASSPORT_ID, INSTANCE_ID, appConfig);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testQueryAccountToken() {
        try {
            AccountToken accountToken = pcAccountTokenService.queryAccountToken(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
            Assert.assertTrue(accountToken != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testBatchRemovieAccountToken() {
        List<String> instanceIdList = Lists.newLinkedList();
        String coreKvKeyStr = CacheConstant.CORE_KV_PREFIX_PASSPROTID_TOKEN + PASSPORT_ID;
        coreKvUtils.delete(coreKvKeyStr);
        for (int i = 0; i < 50; i++) {
            String instanceId = RandomStringUtils.randomNumeric(6);
            instanceIdList.add(instanceId);
            AccountToken accountToken = pcAccountTokenService.initialAccountToken(PASSPORT_ID, instanceId, appConfig);
        }
        long start = System.currentTimeMillis();
        pcAccountTokenService.batchRemoveAccountToken(PASSPORT_ID, false);
        long end = System.currentTimeMillis();
        System.out.println("time:" + (end - start) + "ms");
        for (String str : instanceIdList) {
            String redisKey = PASSPORT_ID + "_" + CLIENT_ID + "_" + str;
            String redisToken = tokenRedisUtils.get(redisKey);

            String coreKvKey = CacheConstant.CORE_KV_PREFIX_PASSPROTID_TOKEN + "_" + PASSPORT_ID + "_" + CLIENT_ID + "_" + str;
            String kvToken = coreKvUtils.get(coreKvKey);
            //如果是异步批量删除则不能使用此断言
            Assert.assertTrue(Strings.isNullOrEmpty(redisToken) && Strings.isNullOrEmpty(kvToken));
        }
        coreKvUtils.delete(coreKvKeyStr);
    }

}
