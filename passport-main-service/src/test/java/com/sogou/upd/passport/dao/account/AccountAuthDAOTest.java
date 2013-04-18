package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.AccountAuthDAO;
import com.sogou.upd.passport.model.account.AccountAuth;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountAuthDAOTest {

  @Inject
  private AccountAuthDAO accountAuthDAO;

  private static final long USER_Id = 80;
  private static final int CLIENT_ID = 1001;
  private static final
  String
      REFRESH_TOKEN =
      "oFohPgnYrnAC79ZRf1wdkxPbR0A3opImhXgRGDpJntExkRqf7flwUvzteCYNpQEQwO6hhSRleH97riuy6heBNdo7H2jlIeXvqqlNd-Fh2ao_dOZG0Zf67822RSIwfrzQ";
  private static final
  String
      ACCESS_TOKEN =
      "KWp2N45BvYQqW53-LAPpgTFs_Dc7KD1Z0KZQFq7iRh-OWWNguVWrNXe5rSuo74olPsVCVKB4OjP7gFgVIkGMjQ7EEbCOaOQgkkfzXf_n2AqvWCm3aQF3s25r_6-9W6EtUiFKVWH7epzOdzRVzslEK92tLICaAm1yLARXn33BXks";
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

  /**
   * 测试单条记录删除和插入
   */
  @Test
  public void testDelete_Insert() {
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
    Assert.assertEquals(row, 1);
  }

  /**
   * 测试单条记录更新
   */
  @Test
  public void testUpdateAccountAuth() {
    AccountAuth aa = newAccountAuth();
    int row = accountAuthDAO.updateAccountAuth(aa);
    Assert.assertEquals(row, 1);
  }

  /**
   * 测试根据userId,clientId查询所有这两个字段等于给定值，但instanceId不等于给定的这个值的记录，返回list集合
   */
  @Test
  public void testGetAccountAuthListById() {
    List<AccountAuth> list = accountAuthDAO.getAccountAuthListById(USER_Id, CLIENT_ID);
    Assert.assertTrue(!list.isEmpty());

  }

  /**
   * todo 批量问题
   */
  @Test
  public void testBatchUpdateAccountAuth() {
    AccountAuth aa1 = newAccountAuth();
    AccountAuth aa2 = new AccountAuth();
    aa2.setUserId(59);
    aa2.setClientId(1001);
    aa2.setInstanceId("0202011001101142F5863E026F49DF86F32ED4F78529CD");
    aa2.setAccessToken(
        "ENVrhxoL5TJzBvANuomzSOzSJsa9LMoExUZd29sQJhhZ5WaXcR3HpRPfhSEavw6XNFktyveUtUrnaNXvrdQYgT4BR6NLQgjmXoI3M9uwacF_qzPW53OSPx9mulU7u8r_6Tdgyim0fXfusGcvg5SRogeBXuJZc72ODIoPkjtN8WA");
    aa2.setRefreshToken(
        "INSpGKanbz3H4EGImYeRIaiCQt3i0hnqaZeyWdeAbFoVctmEFZlAG4K6pjK1zezeE5GUqth3v97i6d3xuQS7sr2Hn8C15pMFEOSGykY-Xg7U9FCgxX8y3_S_gk0Q1ppv");
    aa2.setAccessValidTime(1366868561100l);
    aa2.setRefreshValidTime(1381815761100l);
    List<AccountAuth> list = new ArrayList<AccountAuth>();
    list.add(aa1);
    list.add(aa2);
    int[] row = accountAuthDAO.batchUpdateAccountAuth(list);
    Assert.assertEquals(row.length, 2);
  }

  private AccountAuth newAccountAuth() {
    AccountAuth aa = new AccountAuth();
    aa.setUserId(USER_Id);
    aa.setInstanceId(INSTANCE_ID);
    aa.setClientId(CLIENT_ID);
    aa.setAccessToken(ACCESS_TOKEN);
    aa.setRefreshToken(REFRESH_TOKEN);
    aa.setAccessValidTime(1365576915165l);
    aa.setRefreshValidTime(1380524115165l);
    return aa;
  }

}
