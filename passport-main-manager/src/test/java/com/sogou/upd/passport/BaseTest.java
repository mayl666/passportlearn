package com.sogou.upd.passport;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    protected static final String userid = "2327267612@sina.sohu.com";
    protected static final String userid_qq = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
    protected static final String userid_sina = "2327267612@sina.sohu.com";
    protected static final String userid_renren = "225106022@renren.sohu.com";
    protected static final String userid_qq_weibo = "11387D56B282DACE4CA0C82A071177E8@t.qq.sohu.com";


    protected static final String password = "testtest1";

    protected static final String uniqname = "yuedutest11";

    protected static final int clientId = 1110;

    protected static final String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";

    protected static final String modifyIp = "10.1.164.160";

    protected static final String question = "测试啊，我是来测试的";

    protected static final String answer = "测试成功";
}
