package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.app.ConnectConfig;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午3:50
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ConnectConfigDAOTest extends BaseDAOTest {

    @Autowired
    private ConnectConfigDAO connectConfigDAO;

    @Test
    public void testGetConnectConfigByClientIdAndProvider() {
        ConnectConfig connectConfig = connectConfigDAO.getConnectConfigByClientIdAndProvider(CLIENT_ID, 4);
        Assert.assertTrue(connectConfig != null);
    }

    @Test
    public void testGetConnectConfigByAppIdAndProvider() {
        ConnectConfig connectConfig = connectConfigDAO.getConnectConfigByAppIdAndProvider("100294784", 3);
        Assert.assertTrue(connectConfig != null);
    }

    @Test
    public void testUpdateConnectConfig() {
        ConnectConfig connectConfig = new ConnectConfig();
        connectConfig.setClientId(CLIENT_ID);
        connectConfig.setProvider(4);
        connectConfig.setAppKey("3363779877");
        connectConfig.setAppSecret("201cf182fc60b872bea8da76c8af5b4c");
        connectConfig.setScope("");
        int row = connectConfigDAO.updateConnectConfig(connectConfig);
        Assert.assertTrue(row > 0);
    }
}
