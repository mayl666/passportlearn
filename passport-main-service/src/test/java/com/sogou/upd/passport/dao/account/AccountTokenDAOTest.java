package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.AccountToken;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
public class AccountTokenDAOTest extends BaseDAOTest {

    @Autowired
    private AccountTokenDAO accountAuthDAO;

    @Before
    public void init() {
        AccountToken aa1 = newAccountAuth();
        int row1 = accountAuthDAO.insertAccountToken(aa1.getPassportId(), aa1);
        AccountToken aa2 = newAccountAuth();
        aa2.setInstanceId(OTHER_INSTANCE_ID);
        int row2 = accountAuthDAO.saveAccountToken(aa2.getPassportId(), aa2);
        Assert.assertTrue(row1 == 1 && row2 == 1);
    }

    @Test
    public void testGetAccountTokenByPassportId() {
        AccountToken accountToken = accountAuthDAO.getAccountTokenByPassportId(PASSPORT_ID, CLIENT_ID, INSTANCE_ID);
        Assert.assertTrue(accountToken != null);
    }

    @Test
    public void testListAccountTokenByPassportId() {
        List<AccountToken> accountTokens = accountAuthDAO.listAccountTokenByPassportId(PASSPORT_ID);
        Assert.assertEquals(accountTokens.size(), 2);
    }

    /**
     * 测试单条记录更新
     */
    @Test
    public void testUpdateAccountAuth() {
        AccountToken aa = newAccountAuth();
        aa.setAccessValidTime(111l);
        int row = accountAuthDAO.updateAccountToken(aa.getPassportId(), aa);
        Assert.assertEquals(row, 1);
    }

    /**
     * 测试根据userId,clientId查询所有这两个字段等于给定值，但instanceId不等于给定的这个值的记录，返回list集合
     */
    @Test
    public void testListAccountTokenByPassportIdAndClientId() {
        List<AccountToken> list = accountAuthDAO.listAccountTokenByPassportIdAndClientId(PASSPORT_ID);
        Assert.assertEquals(list.size(), 2);

    }

    @After
    public void end() {
        int row = accountAuthDAO.deleteAccountTokenByPassportId(PASSPORT_ID);
        Assert.assertTrue(row == 2);
    }

    /**
     * todo 批量问题
     */
//    @Test
//    public void testBatchUpdateAccountAuth() {
//        AccountToken aa1 = newAccountAuth();
//        AccountToken aa2 = new AccountToken();
//        aa2.setPassportId("18610807269@sohu.com");
//        aa2.setClientId(1001);
//        aa2.setInstanceId("0202011001101142F5863E026F49DF86F32ED4F78529CD");
//        aa2.setAccessToken(
//                "ENVrhxoL5TJzBvANuomzSOzSJsa9LMoExUZd29sQJhhZ5WaXcR3HpRPfhSEavw6XNFktyveUtUrnaNXvrdQYgT4BR6NLQgjmXoI3M9uwacF_qzPW53OSPx9mulU7u8r_6Tdgyim0fXfusGcvg5SRogeBXuJZc72ODIoPkjtN8WA");
//        aa2.setRefreshToken(
//                "INSpGKanbz3H4EGImYeRIaiCQt3i0hnqaZeyWdeAbFoVctmEFZlAG4K6pjK1zezeE5GUqth3v97i6d3xuQS7sr2Hn8C15pMFEOSGykY-Xg7U9FCgxX8y3_S_gk0Q1ppv");
//        aa2.setAccessValidTime(1366868561100l);
//        aa2.setRefreshValidTime(1381815761100l);
//        List<AccountToken> list = new ArrayList<AccountToken>();
//        list.add(aa1);
//        list.add(aa2);
//        int[] row = accountAuthDAO.batchUpdateAccountToken(list);
//        Assert.assertEquals(row.length, 2);
//    }
    private AccountToken newAccountAuth() {
        AccountToken aa = new AccountToken();
        aa.setPassportId(PASSPORT_ID);
        aa.setInstanceId(INSTANCE_ID);
        aa.setClientId(CLIENT_ID);
        aa.setAccessToken(ACCESS_TOKEN);
        aa.setRefreshToken(REFRESH_TOKEN);
        aa.setAccessValidTime(1365576915165l);
        aa.setRefreshValidTime(1380524115165l);
        return aa;
    }

}
