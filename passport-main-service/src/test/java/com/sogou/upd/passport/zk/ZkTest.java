package com.sogou.upd.passport.zk;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.RedisUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-11-1
 * Time: 上午10:33
 */
@Ignore
public class ZkTest extends BaseTest {

    @Autowired
    private RedisUtils redisUtils;

    @Ignore
    public void testA() {
        while (true) {
            try {
                String key = "aaa" + new Random().nextInt(1000);
                redisUtils.set(key, "1");
                Thread.sleep(1);
                System.out.println("10000----------");
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
