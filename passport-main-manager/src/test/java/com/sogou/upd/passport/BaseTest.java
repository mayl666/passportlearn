package com.sogou.upd.passport;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:19
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class BaseTest extends AbstractJUnit4SpringContextTests {
    protected static final String userid = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";

    //随机生成手机号码
    protected String new_mobile = new GeneratorRandomMobile().generateRandomMobile();

    protected static final String password = "111111";

    protected static final String uniqname = "你好";

    protected static final int clientId = 2009;

    protected static final String serverSecret = "Hpi%#ZT<u@hR.6F)HtfvUKf5ERYR1b";

    protected static final String modifyIp = "10.1.164.160";

    protected static final String question = "测试啊，我是来测试的";

    protected static final String answer = "测试成功";

    class GeneratorRandomMobile {
        //生成随机的手机号码
        private String generateRandomMobile() {
            String mobile = "135";
            DecimalFormat a = new DecimalFormat("00000000");//随机到非7位数时前面加0
            mobile = mobile + a.format((int) (Math.random() * 4720001));//随机数0-4720000
            return mobile;
        }
    }
}
