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
@ContextConfiguration(locations = {"classpath:spring-config-memcache-test.xml"})
public class TestMemcache  extends AbstractJUnit4SpringContextTests {

    @Inject
    private MemcacheUtils aTokenMemUtils;

    @Test
    public void test() throws Exception {
        System.out.println("##############"+aTokenMemUtils.get("tinkame700@sogou.com|1044|37318746"));
    }

    @Test
    public void testset() throws Exception {
        String passportId="tinkame710@sogou.com";
        int clientId = 1044;
        String instanceId = "178068027";
        String key = buildTsKeyStr(passportId,clientId,instanceId);
        aTokenMemUtils.set(key,60, "OpPP841SOEL4C5cJlf4r0D4Fj74c5l");
    }
    private String buildTsKeyStr(String passportId, int clientId, String instanceId) {
        return passportId + "|" + clientId + "|" + instanceId;
    }
}
