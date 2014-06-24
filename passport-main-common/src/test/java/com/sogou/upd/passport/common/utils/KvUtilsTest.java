package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.User;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-24 Time: 下午6:43 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = {"classpath:spring-config-jredis-test.xml"})
public class KvUtilsTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private KvUtils kvUtils;

    @Autowired
    private CoreKvUtils coreKvUtils;

    @Test
    public void testKvUtil() {

        for (int i = 0; i < 1; i++) {
            KvUser user = newUser(i);
            kvUtils.pushObjectWithMaxLen("TEST", user, 10);
            kvUtils.pushWithMaxLen("TEST1", "VALUE" + i, 10);
        }
        List<KvUser> list = kvUtils.getList("TEST", KvUser.class);
        List<String> list1 = kvUtils.getList("TEST1");
        for (KvUser user : list) {
            System.out.println(user.getAge() + ";" + new Date(user.getTime()));
        }
        for (int i = 0; i < list1.size(); i++) {
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


    @Deprecated
    @Test
    public void testCoreKvUtil() {
        String kv_key = "13008/account_token/chengang@sogou-inc.com_1_1";
        String kv_value = "test_core_kv";
        coreKvUtils.set(kv_key, kv_value);

        String kv_data = coreKvUtils.getObject(kv_key, String.class);
        System.out.println(kv_data);
        Assert.assertNotNull(kv_data);


        for (int i = 0; i < 2; i++) {
            KvUser user = newUser(i);
            kvUtils.pushObjectWithMaxLen("TEST", user, 10);
            kvUtils.pushWithMaxLen("TEST1", "VALUE" + i, 10);
        }
        List<KvUser> list = kvUtils.getList("TEST", KvUser.class);
        List<String> list1 = kvUtils.getList("TEST1");
        for (KvUser user : list) {
            System.out.println(user.getAge() + ";" + new Date(user.getTime()));
        }
        for (int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i));
        }
        System.out.println(kvUtils.top("TEST", KvUser.class).getAge());
        System.out.println(kvUtils.top("TEST", KvUser.class).getAge());
        System.out.println(kvUtils.top("TEST", User.class) == null);

    }
}
