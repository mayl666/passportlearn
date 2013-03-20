package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.model.UserProfile;
import com.sogou.upd.passport.service.TestService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: mayan
 * Date: 13-3-12
 * Time: 下午6:04
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config.xml", "classpath:com/sogou/upd/passport/dao/TestMapper.xml"})
public class TestMyBatis extends AbstractJUnit4SpringContextTests {

    @Resource
    private TestService testService;

    @Test
    public void testFind() {
        List<UserProfile> list=testService.getAllUserProfile();
        System.out.println(list.size());

    }

}
