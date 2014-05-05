package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午3:58
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ConnectRelationDAOTest extends BaseDAOTest {

    @Autowired
    private ConnectRelationDAO connectRelationDAO;

    //    @Before
    @Test
    public void init() {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setOpenid(OPENID);
        connectRelation.setProvider(ACCOUNT_TYPE);
        connectRelation.setAppKey(APP_KEY);
        connectRelation.setPassportId(PASSPORT_ID);
//        int row1 = connectRelationDAO.insertConnectRelation(OPENID, connectRelation);
        connectRelation.setAppKey(OTHER_APP_KEY);
        int row2 = connectRelationDAO.insertOrUpdateConnectRelation(OPENID, connectRelation);
        System.out.println(row2);
//        Assert.assertTrue(row1 == 1 && row2 == 1);
    }
//
//    @Test
//    public void testGetSpecifyConnectToken() {
//        ConnectRelation connectRelation = connectRelationDAO.getSpecifyConnectToken(OPENID, ACCOUNT_TYPE, APP_KEY);
//        Assert.assertTrue(connectRelation != null);
//    }
//
//    @Test
//    public void testListConnectRelation() {
//        List<ConnectRelation> list = connectRelationDAO.listConnectRelation(OPENID, ACCOUNT_TYPE);
//        Assert.assertEquals(list.size(), 2);
//    }
//
//    @After
//    public void end() {
//        int row = connectRelationDAO.deleteConnectRelation(OPENID, ACCOUNT_TYPE);
//        Assert.assertEquals(row, 2);
//    }
}
