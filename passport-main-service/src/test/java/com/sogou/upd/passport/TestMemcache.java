package com.sogou.upd.passport;

import com.sogou.upd.passport.common.utils.MemcacheUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-8-21
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-memcache.xml"})
public class TestMemcache  extends AbstractJUnit4SpringContextTests {

    @Inject
    private MemcacheUtils memUtils;

    @Test
    public void test() throws Exception {
        System.out.println("##############"+memUtils.buildMemcachedClient().get("tinkame700@sogou.com|1044|37318746"));

    }
}
