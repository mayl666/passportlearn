package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.account.AccountConnectDAO;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountConnectDAOTest {

  @Inject
  private AccountConnectDAO accountConnectDAO;

  private AccountConnectQuery query1; // connectUid
  private AccountConnectQuery query2; // connectUid+accountType
  private AccountConnectQuery query3; // connectUid+accountType+clientId+userId
  private AccountConnect insert_accountConnect = new AccountConnect();

  private static final long INSERT_USER_Id = 1;
  private static final int CLIENT_ID = 1001;
  private static final String CONNECT_UID = "1666643531";
  private static final int ACCOUNT_TYPE = 4;

  @Before
  public void init() {
    query1 = new AccountConnectQuery();
    query1.setConnectUid(CONNECT_UID);
    query2 = new AccountConnectQuery(CONNECT_UID, ACCOUNT_TYPE);
    query3 = new AccountConnectQuery(CONNECT_UID, ACCOUNT_TYPE, CLIENT_ID, 87);
    insert_accountConnect.setUserId(INSERT_USER_Id);
    insert_accountConnect.setClientId(1001);
    insert_accountConnect.setConnectUid("1111");
    insert_accountConnect.setAccountType(4);
    insert_accountConnect.setAccountRelation(0);
    insert_accountConnect.setConnectAccessToken("fdafasfa");
    insert_accountConnect.setConnectRefreshToken("dagaga");
    insert_accountConnect.setConnectExpiresIn(436222l);
    insert_accountConnect.setCreateTime(new Date());
  }

  /**
   * 根据query查询AccountConnect
   */
  @Test
  public void testGetAccountConnectByQuery_connectUid() {
    List<AccountConnect> list = accountConnectDAO.getAccountConnectListByQuery(this.query1);
    Assert.assertTrue(!list.isEmpty());
  }

  @Test
  public void testGetAccountConnectByQuery_connectUid_accountType() {
    List<AccountConnect> list = accountConnectDAO.getAccountConnectListByQuery(this.query2);
    Assert.assertTrue(!list.isEmpty());
  }

  /**
   * 查询主账号的绑定列表和副账号是否已经注册或绑定过
   */
  @Test
  public void testFindBindConnectByQuery() {
    List<AccountConnect> list = accountConnectDAO.findBindConnectByQuery(this.query3);
    Assert.assertTrue(!list.isEmpty());
  }

  /**
   * 插入一条新记录
   */
  @Test
  public void testAccountConnectInsert_delete() {
    int row1 = accountConnectDAO.insertAccountConnect(this.insert_accountConnect);
    int row2 = accountConnectDAO.deleteAccountConnectByUserId(INSERT_USER_Id);
    Assert.assertTrue(row1 == 1 && row2 == 1);
  }

  /**
   * 更新AccountConnect
   */
  @Test
  public void testUpdateAccountConnect() {
    AccountConnect update_accountConnect = new AccountConnect();
    update_accountConnect.setUserId(80);
    update_accountConnect.setClientId(CLIENT_ID);
    update_accountConnect.setConnectUid(CONNECT_UID);
    update_accountConnect.setAccountType(ACCOUNT_TYPE);
    update_accountConnect.setAccountRelation(1);
    update_accountConnect.setConnectAccessToken("2.00RHEnoBou16VDb1d222a0ee05YXhB");
    update_accountConnect.setConnectExpiresIn(7814098);
    int row = accountConnectDAO.updateAccountConnect(update_accountConnect);
    Assert.assertEquals(row, 1);
  }

  /**
   * 根据userId获取Uid todo mapper里暂没添加相应的查询方法
   */
  public void testGetUidByUserId() {
  }

}
