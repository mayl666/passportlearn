package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.dao.AccountDAO;
import com.sogou.upd.passport.dao.AppConfigDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AppConfigTest {

  @Autowired
  private AppConfigDAO appConfigDAO;

  @Test
  public void testGetAppConfigByClientId(){
     AppConfig appConfig = appConfigDAO.getAppConfigByClientId(1001);
    Assert.assertTrue(appConfig != null);
  }

}
