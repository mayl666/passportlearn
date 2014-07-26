package com.sogou.upd.passport;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:19
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class BaseTest{

    //主账号
    protected static final String userid_connect = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
    protected static final String userid_phone = "13581695053@sohu.com";
    protected static final String userid_email = "loveerin9460@163.com";
    protected static final String userid_sogou_1 = "liulingtest01@sogou.com";
    protected static final String userid_sogou_1_another = "liulingtest01";
    protected static final String userid_sogou_2 = "osadnfdf@sogou.com";
    protected static final String userid_invild = "osadnfdf4r";
    protected static final String mobile_userid_sogou = "shipengzhi1986@sogou.com";
}
