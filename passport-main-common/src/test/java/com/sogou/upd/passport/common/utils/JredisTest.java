package com.sogou.upd.passport.common.utils;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 12-11-22 Time: 下午6:26 To change this template use
 * File | Settings | File Templates.
 */
//@Ignore
@ContextConfiguration(locations = {"classpath:spring-config-jredis-test.xml"})
public class JredisTest extends AbstractJUnit4SpringContextTests {
    private static final String TEST_KEY = "TEST_REDIS_KEY";
    private static final String TEST_SUB_KEY = "TEST_REDIS_SUB_KEY";

    @Inject
    private DBShardRedisUtils dbShardRedisUtils;

    @Test
    public void test() {
        try {
//            String key="/internal/account/authuser";
//            String appId="1100";
//            getLimitedTimes(key,appId);
//            dbShardRedisUtils.setString("1112", "111231");

            System.err.println(dbShardRedisUtils.get("SP.MOBILE:PASSPORTID_"));

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
