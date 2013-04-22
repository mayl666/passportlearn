package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
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
public class ConnectRelationDAOTest extends BaseDAOTest {

    @Autowired
    private ConnectRelationDAO connectRelationDAO;

    @Before
    public void init() {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setOpenid(OPENID);
        connectRelation.setAppKey(APP_KEY);
        connectRelation.setPassportId(PASSPORT_ID);
        connectRelation.setProvider(ACCOUNT_TYPE);
        int row1 = connectRelationDAO.insertConnectRelation(OPENID, connectRelation);
        connectRelation.setAppKey(OTHER_APP_KEY);
        int row2 = connectRelationDAO.insertConnectRelation(OPENID, connectRelation);
        Assert.assertTrue(row1 == 1 && row2 == 1);
    }

    @After
    public void end() {
        int row = connectRelationDAO.deleteConnectRelation(OPENID, ACCOUNT_TYPE);
        Assert.assertEquals(row, 2);
    }

    @Test
    public void testListConnectRelation() {
        List<ConnectRelation> list = connectRelationDAO.listConnectRelation(OPENID, ACCOUNT_TYPE);
        Assert.assertEquals(list.size(), 2);
    }

}
