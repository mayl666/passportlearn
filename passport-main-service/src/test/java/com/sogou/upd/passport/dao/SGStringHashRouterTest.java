package com.sogou.upd.passport.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.FileUtil;
import com.sogou.upd.passport.dao.dal.routing.SGStringHashRouter;
import junit.framework.TestCase;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-2-10
 * Time: 下午11:48
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class SGStringHashRouterTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SGStringHashRouterTest.class);

    private static SGStringHashRouter router;

    static {
        router = new SGStringHashRouter("id", "account_{0}", 32);
    }

    /**
     * 测试sg-string-hash计算是否正确
     */
    @Test
    public void testRoute() {
        router = new SGStringHashRouter("id", "account_{0}", 32);
        String id5 = "wsyangxy@sogou.com"; //account_24
        System.out.println(router.doRoute(id5));
    }


    @Test
    public void testMobileRoute() {
        router = new SGStringHashRouter("id", "mobile_passportid_mapping_{0}", 32);

        String mobile_flag_0 = "13661512835";
//        String mobile_flag_1 = "13703211617";
        String mobile_table_name = router.doRoute(mobile_flag_0);
//        String mobile_table_name_1 = router.doRoute(mobile_flag_1);
        System.out.println("mobile_table_name:" + mobile_table_name);
//        System.out.println("mobile_table_name_1:" + mobile_table_name_1);
        System.out.println(router.doRoute("13778507392"));

    }


    @Test
    public void testUpmShard() {
        router = new SGStringHashRouter("id", "uniqname_passportid_mapping_{0}", 32);
        LOGGER.info("u_p_m shard." + router.doRoute("簩龖"));
    }


    @Ignore
    @Test
    public void testModuleShard() {
        String userid = "gang.chen0505@gmail.com";
//        String userid1 = "nanajiaozixian22@sogou.com";
        String userid1 = "wpv5@sogou.com";
        int shardCount = 2;
        int aimCount = 0;

        String useridHash = DigestUtils.md5Hex(userid1);
        int tempInt = Integer.parseInt(useridHash.substring(0, 2), 16);
        int shardValue = tempInt % shardCount;

//        if (shardValue == aimCount) {
        System.out.println("===== module shard result :" + shardValue);
//        }

       /* Map<String, String> shardMap = Maps.newHashMap();
//        String file = "D:\\项目\\module替换\\test_module_shard.sql";
        String file = "D:\\项目\\module替换\\bingna_test.txt";
        String line;
        Path dataPath = Paths.get(file);
        try (BufferedReader reader = Files.newBufferedReader(dataPath, Charset.defaultCharset())) {
            while ((line = reader.readLine()) != null) {
                int tempShard = Integer.parseInt(DigestUtils.md5Hex(line).substring(0, 2), 16);
                shardMap.put(line, String.valueOf(tempShard % shardCount));
            }
            FileUtil.storeFileMap2Local("D:\\项目\\module替换\\shard_bingna_test_2.txt", shardMap);
        } catch (Exception e) {
            LOGGER.error("testModulesShard error.", e);
        }*/


    }


}
