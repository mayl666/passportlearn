package com.sogou.upd.passport.util;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-8-29
 * Time: 下午7:29
 */

@Ignore
public class RedisUtilTest extends BaseTest {

    private static final String cache_key_blacklist = "cache_module_blacklist";

    @Autowired
    private RedisUtils redisUtils;

    @Ignore
    @Test
    public void testRedisUtilGetList() {

        //填充数据
        /*for (int i = 0; i < 10; i++) {
            redisUtils.lPush(cache_key_blacklist, "chengang_test_" + i);
        }*/

        List<String> lists = redisUtils.getList(cache_key_blacklist);
        System.out.println(" testRedisUtilGetList " + JsonUtil.obj2Json(lists));

        long expireTime = (System.currentTimeMillis() / 1000) + 300;

        StringBuffer resultText = new StringBuffer("0 0 10").append("\r\n");
        for (String userid : lists) {
            resultText.append(userid).append(" ").append(expireTime).append("\r\n");
        }

        System.out.println("==========================");
        System.out.println(resultText.toString());
        System.out.println("==========================");

    }
}
