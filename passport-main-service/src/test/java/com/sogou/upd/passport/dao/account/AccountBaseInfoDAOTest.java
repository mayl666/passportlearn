package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-27
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public class AccountBaseInfoDAOTest extends BaseDAOTest {

    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;


    @Test
    public void testGetConnectCount() {
        int count = accountBaseInfoDAO.getConnectTotalCount();
        System.out.println("count:" + count);
    }


    @Test
    public void testListAccountBaseInfo() {
        int pageSize = 10000;
        int pageIndex = 0;
        List<AccountBaseInfo> list = accountBaseInfoDAO.listConnectBaseInfoByPage(pageIndex, pageSize);
        Assert.assertTrue(list.size() == 10000);

    }

    @Test
    public void testGetNotThirdPartyCount() {
        int totalCount = accountBaseInfoDAO.getNotThirdPartyTotalCount();
        Assert.assertNotNull(totalCount);
    }

    @Test
    public void testGetNotThirdPartyAccountByPage() {
        int pageIndex = 0;
        int pageSize = 10000;
        List<AccountBaseInfo> list = accountBaseInfoDAO.getNotThirdPartyAccountByPage(pageIndex, pageSize);
        Assert.assertTrue(list.size() > 0);

    }
}
