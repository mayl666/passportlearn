package com.sogou.upd.passport.web;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-7-10
 * Time: 下午5:51
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
@Ignore
public class TestLogBack {


    @Test
    public void testSync(){
       Logger log = LoggerFactory.getLogger("userOperationLogger");
       log.info("aaaaaa");
    }

}
