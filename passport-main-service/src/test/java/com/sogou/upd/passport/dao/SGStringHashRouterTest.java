package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.dao.dal.routing.SGStringHashRouter;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

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
        String id = "8A9D49D98E126BC6B8330FD95E0EF31D@qq.sohu.com";
        String name = router.doRoute(id);
        System.out.println(name);
//        Assert.assertEquals(name, "account_18");
    }


    @Test
    public void testMobileRoute() {
        router = new SGStringHashRouter("id", "mobile_passportid_mapping_{0}", 32);

        String mobile_flag_0 = "13522010566";
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
