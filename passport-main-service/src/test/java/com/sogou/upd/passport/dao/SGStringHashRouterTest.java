package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.dao.dal.routing.SGStringHashRouter;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

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
//        String id = "056B15F99925016562B24E2070AE7AF5@qq.sohu.com";
//        String id = "happychen09031@163.com";
//        String id = "0C7E1BC4094CACCBDFEFC60BDDC598BD@qq.sohu.com";
//        String id = "15BF5B2E00BCB4A7E8AD31E59480FF8C@qq.sohu.com";
//        String id = "B5008F7D2813EDCCE03BCE3EA6DC7FFA@qq.sohu.com";
        String id = "912086925@qq.com";    // account_03
//        String id = "wangqingemail@sohu.com";
//        String id = "lkmydn@126.com";   //       account_14
//        String id = "yangking2010@gmail.com";   // account_12
//        String id = "wangqingtest009@sogou.com";   // account_07
//        String id = "wangqingemail@sohu.com";   //      account_07

        String id1 = "nanajiaozixian@sogou.com";     // account_13
        String id2 = "F65D19C8B0948244AB0A1CCB5EC792B4@qq.sohu.com";   // account_19
        String id3 = "18600548420@sohu.com";  // account_20
        String id4 = "598284375@qq.com";    // account_14

        String id5 = "wangqingdata13@sohu.com"; //account_24
        String id6 = "464534647@qq.com"; //
//        System.out.println(router.doRoute(id));
//        System.out.println(router.doRoute(id1));
//        System.out.println(router.doRoute(id2));
//        System.out.println(router.doRoute(id3));
//        System.out.println(router.doRoute(id4));
        System.out.println(router.doRoute(id5));
        System.out.println(router.doRoute(id6));
//        Assert.assertEquals(name, "account_18");
    }


    @Test
    public void testMobileRoute() {
        router = new SGStringHashRouter("id", "mobile_passportid_mapping_{0}", 32);

//        String mobile_flag_0 = "13522010566";
//        String mobile_flag_1 = "13703211617";
//        String mobile_table_name = router.doRoute(mobile_flag_0);
//        String mobile_table_name_1 = router.doRoute(mobile_flag_1);
//        System.out.println("mobile_table_name:" + mobile_table_name);
//        System.out.println("mobile_table_name_1:" + mobile_table_name_1);

        System.out.println(router.doRoute("15501022053"));

    }


    @Test
    public void testUpmShard() {
        router = new SGStringHashRouter("id", "uniqname_passportid_mapping_{0}", 32);
        LOGGER.info("u_p_m shard." + router.doRoute("簩龖"));
    }


    /**
     * 对比DBA导入的数据分表和sg-string-hash计算的分表是否一致
     */
    public void testContrastJava_Mysql() {
        try {
            BufferedWriter bw = FileIOUtil.newWriter("c:/diff_java_mysql_stringhash.txt");
            List<String> passportIdList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Downloads\\32.txt");
            for (String passportId : passportIdList) {
                String name = router.doRoute(passportId);
                if (!name.equals("account_31")) {
                    bw.write(passportId + "\n");
                    bw.flush();
                }
            }
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
