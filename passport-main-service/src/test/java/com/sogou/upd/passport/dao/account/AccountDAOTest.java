package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountDAOTest {

  @Inject
  private AccountDAO accountDAO;

  /**
   * 测试单条记录查询
   */
  @Test
  public void testGetAccountByPassportId() {
    String passportId = "13621009174@sohu.com";
    Account account = accountDAO.getAccountByPassportId(passportId);
    Assert.assertTrue(account != null);
  }

  /**
   * 测试根据手机号码获取Account
   */
  @Test
  public void testGetAccountByMobile() {
    Account account = accountDAO.getAccountByMobile("13621009174");
    Assert.assertTrue(account != null);
  }

  /**
   * 测试根据userId获取Account
   */
  @Test
  public void testGetAccountByUserId() {
    Account account = accountDAO.getAccountByUserId(69);
    Assert.assertTrue(account != null);
  }

  /**
   * 测试修改用户
   */
  @Test
  public void testUpdateAccount() throws SystemException {
    Account account = new Account();
    account.setId(92);
    account.setMobile("13621009174");
    account.setPasswd(PwdGenerator.generatorPwdSign("123456"));
    account.setPassportId("13621009174@sohu.com");
    int row = accountDAO.updateAccount(account);
    Assert.assertTrue(row == 1);
  }

  @Test
  public void testDeleteAccount() {
    int row = accountDAO.deleteAccountByPassportId("13621009174@sohu.com");
    Assert.assertTrue(row == 1);
  }

  @Test
  public void testSaveAccount() throws SystemException {
    Account account = new Account();
    account.setMobile("13621009174");
    account.setPasswd(PwdGenerator.generatorPwdSign("123456"));
    account.setPassportId("13621009174@sohu.com");
    account.setAccountType(2);
    account.setRegIp("10.1.164.65");
    account.setRegTime(new Date());
    account.setStatus(1);
    account.setVersion(1);
    int row = accountDAO.insertAccount(account);
    System.out.println(row);
  }

}
