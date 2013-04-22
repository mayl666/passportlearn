package com.sogou.upd.passport.dao.connect;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.model.connect.ConnectToken;
import junit.framework.Assert;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class ConnectTokenDAOTest extends BaseDAOTest {

    @Inject
    private ConnectTokenDAO accountConnectDAO;

    private ConnectToken connectToken = new ConnectToken();

    @Before
    public void init() {
        connectToken.setPassportId(PASSPORT_ID);
        connectToken.setAppKey(APP_KEY);
        connectToken.setOpenid(OPENID);
        connectToken.setProvider(ACCOUNT_TYPE);
        connectToken.setAccessToken(ACCESS_TOKEN);
        connectToken.setRefreshToken(REFRESH_TOKEN);
        connectToken.setExpiresIn(436222l);
        connectToken.setCreateTime(new Date());
        int row = accountConnectDAO.insertAccountConnect(PASSPORT_ID, connectToken);
        Assert.assertTrue(row == 1);
    }

    @After
    public void end() {
        int row = accountConnectDAO.deleteConnectTokenByPassportId(PASSPORT_ID);
        Assert.assertTrue(row == 1);
    }

    @Test
    public void testGetSpecifyConnectToken() {
        ConnectToken connectToken = accountConnectDAO.getSpecifyConnectToken(PASSPORT_ID, ACCOUNT_TYPE, APP_KEY);
        Assert.assertTrue(connectToken != null);
    }

    @Test
    public void testUpdateAccountConnect() {
        connectToken.setAccessToken("123");
        int row = accountConnectDAO.updateConnectToken(PASSPORT_ID, connectToken);
        Assert.assertEquals(row, 1);
    }

}
