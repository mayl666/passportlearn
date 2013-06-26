package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.User;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Date;
import java.util.List;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-24 Time: 下午6:43 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis.xml"})
public class KvUtilsTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private KvUtils kvUtils;

    @Test
    public void testKvUtil() {

        for (int i=0; i<15; i++) {
            KvUser user = newUser(i);
            kvUtils.pushObjectWithMaxLen("TEST", user, 10);
            kvUtils.pushWithMaxLen("TEST1", "VALUE" + i, 10);
        }
        List<KvUser> list = kvUtils.getList("TEST", KvUser.class);
        List<String> list1 = kvUtils.getList("TEST1");
        for (KvUser user : list) {
            System.out.println(user.getAge() + ";" + new Date(user.getTime()));
        }
        for (int i=0; i<list1.size(); i++) {
            System.out.println(list1.get(i));
        }
        System.out.println(kvUtils.top("TEST", KvUser.class).getAge());
        System.out.println(kvUtils.top("TEST", KvUser.class).getAge());
        System.out.println(kvUtils.top("TEST", User.class) == null);
    }

    private KvUser newUser(int age) {
        KvUser user = new KvUser();
        user.setName("JERRY");
        user.setAge(age);
        user.setTime(System.currentTimeMillis());
        return user;
    }
}
