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
    protected static final String userid = "1747428841@sina.sohu.com";

    protected static final String password = "testtest1";

    protected static final String uniqname = "你好";

    protected static final int clientId = 2007;

    protected static final String serverSecret = "udj0D>~Ez`:%Zbj`wFfh8mW`lB[{(]";

    protected static final String modifyIp = "10.1.164.160";

    protected static final String question = "测试啊，我是来测试的";

    protected static final String answer = "测试成功";
}
