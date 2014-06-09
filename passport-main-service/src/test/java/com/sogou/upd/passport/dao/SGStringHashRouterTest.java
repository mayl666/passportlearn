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
//        String id = "056B15F99925016562B24E2070AE7AF5@qq.sohu.com";
//        String id = "wangqingemail@sohu.com";    //  account_07      wangqingemail@sohu.com
//        String id = "wangqingtest@sogou.com";
//        String id = "gang.chen0505@gmail.com";   //  account_03      gang.chen0505@gmail.com
//        String id = "002zmm@163.com";
//        String id = "wangqingdata@sohu.com";
//        String id = "13693610763@sohu.com";
//        String id = "wangqing31278@163.com";    // account_06
//        String id = "FCE6E963554E2F279B96D92FE40CEBEE@qq.sohu.com";     // account_08 18库不存在
//        String id = "056B15F99925016562B24E2070AE7AF5@qq.sohu.com";       //
//        String passportId1 = "897203D1C04149F1A3D1B9FC6F4FE871@qq.sohu.com";       // account_04
//        String passportId2 = "againshow@163.com";       //   account_26
//        String passportId3 = "18643922663@sohu.com";       //   account_11
//        String passportId4 = "lyf1462075862@sohu.com";       //  account_21
//        String passportId5 = "loveair1981@sohu.com";       //  account_21
//        String passportId6 = "zjhan@sogou.com";       //   account_22
        String passportId7 = "apple529@sogou.com";       //  account_29
//        String passportId8 = "zhangsu11@17173.com";       // account_08
        String passportId8 = "feiwenxi123@sogou.com";       //
//        System.out.println(router.doRoute(passportId1));
//        System.out.println(router.doRoute(passportId2));
//        System.out.println(router.doRoute(passportId3));
//        System.out.println(router.doRoute(passportId4));
//        System.out.println(router.doRoute(passportId5));
//        System.out.println(router.doRoute(passportId6));
        System.out.println(router.doRoute(passportId7));
        System.out.println(router.doRoute(passportId8));
    }


    @Test
    public void testUpmShard() {
        router = new SGStringHashRouter("id", "uniqname_passportid_mapping_{0}", 32);

//        String passportId0 = "KeSyren1234";  //KeSyren1234  wangqingemail@sohu.com                     uniqname_passportid_mapping_30
//        String passportId1 = "Again";       //  Again  897203D1C04149F1A3D1B9FC6F4FE871@qq.sohu.com    uniqname_passportid_mapping_19
//        String passportId2 = "again";       // again   againshow@163.com                               uniqname_passportid_mapping_03
//        String passportId3 = "一帘幽梦";       // 一帘幽梦  D4BA9CBB2E4BBA3739DE1CE5AB69453B@qq.sohu.com  guizupet120@126.com     uniqname_passportid_mapping_11
//        String passportId4 = "追求简单的心情";       // 追求简单的心情  david1978@sogou.com       uniqname_passportid_mapping_29
//        String passportId5 = "rostan";       // rostan  konsy2005@sohu.com  uniqname_passportid_mapping_01
//        String passportId6 = "茅屋有闭";       // zxj55xmy@sohu.com     uniqname_passportid_mapping_21
//        String passportId7 = "赵包子";       // 18643922663@sohu.com   uniqname_passportid_mapping_25
//        String passportId8 = "zjhan658";     //  uniqname_passportid_mapping_12
        String passportId8 = "不晓得5337";     //   uniqname_passportid_mapping_05
        String passportId9 = "apple0529";     //   uniqname_passportid_mapping_31

//        System.out.println(router.doRoute(passportId0));
//        System.out.println(router.doRoute(passportId1));
//        System.out.println(router.doRoute(passportId2));
//        System.out.println(router.doRoute(passportId3));
//        System.out.println(router.doRoute(passportId4));
//        System.out.println(router.doRoute(passportId5));
//        System.out.println(router.doRoute(passportId6));
//        System.out.println(router.doRoute(passportId7));
        System.out.println(router.doRoute(passportId8));
        System.out.println(router.doRoute(passportId9));
    }


    @Test
    public void testMobileRoute() {
        router = new SGStringHashRouter("id", "mobile_passportid_mapping_{0}", 32);

        String mobile_flag_0 = "15228657121";
        String mobile_flag_1 = "13703211617";
        String mobile_table_name = router.doRoute(mobile_flag_0);
        String mobile_table_name_1 = router.doRoute(mobile_flag_1);
        System.out.println("mobile_table_name:" + mobile_table_name);
        System.out.println("mobile_table_name_1:" + mobile_table_name_1);

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
