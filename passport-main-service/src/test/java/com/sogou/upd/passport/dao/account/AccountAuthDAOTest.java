package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.AccountAuthDAO;
import com.sogou.upd.passport.dao.AppConfigDAO;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.app.AppConfig;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountAuthDAOTest {

  @Autowired
  private AccountAuthDAO accountAuthDAO;

  private static final long USER_Id = 80;
  private static final String REFRESH_TOKEN =  "oFohPgnYrnAC79ZRf1wdkxPbR0A3opImhXgRGDpJntExkRqf7flwUvzteCYNpQEQwO6hhSRleH97riuy6heBNdo7H2jlIeXvqqlNd-Fh2ao_dOZG0Zf67822RSIwfrzQ";
  private static final String ACCESS_TOKEN = "KWp2N45BvYQqW53-LAPpgTFs_Dc7KD1Z0KZQFq7iRh-OWWNguVWrNXe5rSuo74olPsVCVKB4OjP7gFgVIkGMjQ7EEbCOaOQgkkfzXf_n2AqvWCm3aQF3s25r_6-9W6EtUiFKVWH7epzOdzRVzslEK92tLICaAm1yLARXn33BXks";
  private static final String INSTANCE_ID = "02020110011111F4E7587A9D4893420EB97D1C1365DF95";


  /**
   * 测试根据refresh_token获取AccountAuth信息
   */
  @Test
  public void testGetAccountAuthByRefreshToken() {
    AccountAuth aa = accountAuthDAO.getAccountAuthByRefreshToken(REFRESH_TOKEN);
    Assert.assertTrue(aa != null);
  }

  /**
   * 测试根据access_token获取AccountAuth信息
   */
  @Test
  public void testGetAccountAuthByAccessToken() {
    AccountAuth aa = accountAuthDAO.getAccountAuthByAccessToken(ACCESS_TOKEN);
    Assert.assertTrue(aa != null);
  }

  @Test
  public void testDelete_Insert(){
     int row1 = accountAuthDAO.deleteAccountAuthByUserId(USER_Id);
    AccountAuth aa = newAccountAuth();
    int row2 = accountAuthDAO.insertAccountAuth(aa);
    Assert.assertTrue(row1 == 1 && row2 == 1);
  }

  /**
   * 测试往用户状态表中插入或修改一条记录
   */
  @Test
  public void testSaveAccountAuth() {
    AccountAuth aa = newAccountAuth();
    int row = accountAuthDAO.saveAccountAuth(aa);
    Assert.assertTrue(row == 1);
  }

  /**
   * 测试根据userId,clientId查询所有这两个字段等于给定值，但instanceId不等于给定的这个值的记录，返回list集合
   */
//  @Test
//  public void testBatchFindAccountAuthByUserId() {
//    AccountAuth aa = new AccountAuth();
//    aa.setUserId(USER_Id);
//    aa.setClientId(CLIENT_ID);
//    aa.setInstanceId(INSTANCE_ID);
//    List<AccountAuth> list = accountAuthMapper.batchFindAccountAuthByUserId(aa);
//    if (list != null && list.size() > 0) {
//      System.out.println(list.size());
//    } else {
//      System.out.println("没有记录!!!");
//    }
//  }

//

  /**
   * todo 批量问题
   */
  @Test
  public void testBatchUpdateAccountAuth() {

  }

  private AccountAuth newAccountAuth(){
    AccountAuth aa = new AccountAuth();
    aa.setUserId(80);
    aa.setInstanceId(INSTANCE_ID);
    aa.setClientId(1001);
    aa.setAccessToken(ACCESS_TOKEN);
    aa.setRefreshToken(REFRESH_TOKEN);
    aa.setAccessValidTime(1365576915165l);
    aa.setRefreshValidTime(1380524115165l);
    return aa;
  }

}
