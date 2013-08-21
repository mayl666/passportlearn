package com.sogou.upd.passport;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.sogou.upd.passport.common.utils.MemcacheUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void test(){
//        System.out.println();
        memUtils.getrTokenMaster() ;
 /*       String[] servers = { "10.10.71.26:11213"};
        Integer[] weights = { 5};
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);
        pool.setWeights(weights);
        pool.setFailover(true);
        pool.setInitConn(10);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();

        MemCachedClient mcc = new MemCachedClient();
        String a = "hello1";
        mcc.set("xxx", a);
        System.out.println(mcc.get("xxx"));*/

    }
}
