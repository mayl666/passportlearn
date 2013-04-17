package com.sogou.upd.passport.dao.account.mapper;

import com.sogou.upd.passport.dao.AccountDAO;
import com.sogou.upd.passport.model.account.Account;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountDAOTest {

  @Autowired
  private AccountDAO accountDAO;

  @Test
  public void testGetAccountByPassportId() {
    String passportId = "13621009174@sohu.com";
    Account account = accountDAO.queryAccountByPassportId(passportId);
    Assert.assertTrue(account != null);
  }

}
