package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.model.problem.ProblemType;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 12-11-22 Time: 下午6:26 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis-test.xml"})
public class JredisTest extends AbstractJUnit4SpringContextTests {
    private static final String TEST_KEY = "TEST_REDIS_KEY";
    private static final String TEST_SUB_KEY = "TEST_REDIS_SUB_KEY";

    @Inject
    private RedisUtils redisUtils;

    @Test
    public void test() {
        try {
//            redisUtils.set("aaaaa", "bbbb");
            redisUtils.hPut("dsdsds","aaa","bbba");
//        redisUtils.expire("aaaaa",10);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Test
    public void testIncrement() throws Exception {
        try {
            // 测试不存在某个键，能否increment
            redisUtils.delete(TEST_KEY);
            redisUtils.increment(TEST_KEY);
            System.out.println("value1:"+redisUtils.get(TEST_KEY));
            redisUtils.increment(TEST_KEY);
            System.out.println("value2:"+redisUtils.get(TEST_KEY));
            String value = redisUtils.get(TEST_KEY);

            Assert.assertTrue(value != null && "1".equals(value));

            redisUtils.delete(TEST_KEY);
            redisUtils.hIncrBy(TEST_KEY, TEST_SUB_KEY);
            String sub_value = redisUtils.hGet(TEST_KEY, TEST_SUB_KEY);
            Assert.assertTrue(sub_value != null && "1".equals(value));

            redisUtils.delete(TEST_KEY);
            redisUtils.hPut(TEST_KEY, TEST_SUB_KEY + "SUFFIX", "abc");
            redisUtils.hIncrBy(TEST_KEY, TEST_SUB_KEY);
            sub_value = redisUtils.hGet(TEST_KEY, TEST_SUB_KEY);
            Assert.assertTrue(sub_value != null && "1".equals(value));

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testlGet() throws Exception {
        try {
            List<ProblemType> list = new ArrayList<ProblemType>();
            ProblemType problemType1 = new ProblemType();
            problemType1.setId(1);
            problemType1.setTypeName("类型1");
            list.add(problemType1);

            ProblemType problemType2 = new ProblemType();
            problemType2.setId(2);
            problemType2.setTypeName("类型2");
            list.add(problemType2);

//            redisUtils.lPutAllObject("key1",list);
//
//            List<ProblemType> result = redisUtils.lGetAll("key1",ProblemType.class);
//            for (ProblemType problemType:list){
//              System.out.println("id:"+problemType.getId()+",typename:"+problemType.getTypeName());
//            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}
