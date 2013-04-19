package com.sogou.upd.passport.service;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountConnectServiceTest extends AbstractJUnit4SpringContextTests {

  @Inject
  private AccountConnectService accountConnectService;

  //根据userId获取openId
  @Test
  public void testGetOpenIdByQuery()
  {
    System.out.println(accountConnectService.getOpenIdByQuery(new AccountConnectQuery(87,4,1001)));
  }
}
