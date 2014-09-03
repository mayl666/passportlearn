package com.sogou.upd.passport.manager.account;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.JsonUtil;
import org.junit.Test;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-9-2
 * Time: 下午8:18
 */
public class CookieManagerImplTest extends BaseTest {


    /**
     * decrypt SG PPinf clientid:4:2009|crt:10:1409652043|refnick:27:%E5%8D%90%E9%99%B6%E5%8D%8D|trust:1:1|userid:44:5EED6E92B98534E2B74020F85C875186@qq.sohu.com|uniqname:27:%E5%8D%90%E9%99%B6%E5%8D%8D|
     * decrypt SH PPinf loginid:0:|userid:44:3A0CE5AF0B7346693366EABC5445156D@qq.sohu.com|serviceuse:30:000000000000000000000000000000|crt:0:|emt:1:0|appid:4:1120|trust:1:1|partnerid:1:0|relation:0:|uuid:16:11fab0b6f0e8430x|uid:16:11fab0b6f0e8430x|uniqname:36:%E6%98%9F%E7%9B%B4%E8%87%B3%E6%88%90|refuserid:32:3A0CE5AF0B7346693366EABC5445156D|refnick:6:╄→ 直至成|
     */
    @Test
    public void testPPinf() {
        //搜狗算法生成的ppinf
        //sginf=1|1409652043|1410861643|Y2xpZW50aWQ6NDoyMDA5fGNydDoxMDoxNDA5NjUyMDQzfHJlZm5pY2s6Mjc6JUU1JThEJTkwJUU5JTk5JUI2JUU1JThEJThEfHRydXN0OjE6MXx1c2VyaWQ6NDQ6NUVFRDZFOTJCOTg1MzRFMkI3NDAyMEY4NUM4NzUxODZAcXEuc29odS5jb218dW5pcW5hbWU6Mjc6JUU1JThEJTkwJUU5JTk5JUI2JUU1JThEJThEfA

        String decryptSGPPinf = "Y2xpZW50aWQ6NDoyMDA5fGNydDoxMDoxNDA5NjUyMDQzfHJlZm5pY2s6Mjc6JUU1JThEJTkwJUU5JTk5JUI2JUU1JThEJThEfHRydXN0OjE6MXx1c2VyaWQ6NDQ6NUVFRDZFOTJCOTg1MzRFMkI3NDAyMEY4NUM4NzUxODZAcXEuc29odS5jb218dW5pcW5hbWU6Mjc6JUU1JThEJTkwJUU5JTk5JUI2JUU1JThEJThEfA";
        System.out.println("=======decrypt SG PPinf " + Coder.decodeBASE64(decryptSGPPinf));

        //搜狐算法生成的ppinf
        //ppinf=2|1409655301|1410864901|bG9naW5pZDowOnx1c2VyaWQ6NDQ6M0EwQ0U1QUYwQjczNDY2OTMzNjZFQUJDNTQ0NTE1NkRAcXEuc29odS5jb218c2VydmljZXVzZTozMDowMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjA6fGVtdDoxOjB8YXBwaWQ6NDoxMTIwfHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6MTFmYWIwYjZmMGU4NDMweHx1aWQ6MTY6MTFmYWIwYjZmMGU4NDMweHx1bmlxbmFtZTozNjolRTYlOTglOUYlRTclOUIlQjQlRTglODclQjMlRTYlODglOTB8cmVmdXNlcmlkOjMyOjNBMENFNUFGMEI3MzQ2NjkzMzY2RUFCQzU0NDUxNTZEfHJlZm5pY2s6NjrilYTihpIg55u06Iez5oiQfA
        String decryptSHPPinf = "bG9naW5pZDowOnx1c2VyaWQ6NDQ6M0EwQ0U1QUYwQjczNDY2OTMzNjZFQUJDNTQ0NTE1NkRAcXEuc29odS5jb218c2VydmljZXVzZTozMDowMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjA6fGVtdDoxOjB8YXBwaWQ6NDoxMTIwfHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6MTFmYWIwYjZmMGU4NDMweHx1aWQ6MTY6MTFmYWIwYjZmMGU4NDMweHx1bmlxbmFtZTozNjolRTYlOTglOUYlRTclOUIlQjQlRTglODclQjMlRTYlODglOTB8cmVmdXNlcmlkOjMyOjNBMENFNUFGMEI3MzQ2NjkzMzY2RUFCQzU0NDUxNTZEfHJlZm5pY2s6NjrilYTihpIg55u06Iez5oiQfA";
        System.out.println("=======decrypt SH PPinf " + Coder.decodeBASE64(decryptSHPPinf));

    }


    @Test
    public void testAppModuleReplace() {

        //数据格式： 1100:2|1111:10|1112:10|1113:2

        String demoData = "v:10|1100:2|1111:10|1112:10|1113:2";
        Map<String, String> splitMap = Splitter.on("|").withKeyValueSeparator(":").split(demoData);

        Map<String, String> testContains = Maps.newHashMap();
        if (testContains.containsKey("a")) {
            System.out.println("contains a");
        } else {
            System.out.println("not contains a");
        }

        if (splitMap.containsKey("1118")) {
            System.out.println("splitMap  contains 1118");
        } else {
            System.out.println("splitMap  not contains 1118");
        }
        System.out.println("testAppModuleReplace splitMap:" + splitMap.toString());
    }


}
