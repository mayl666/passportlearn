package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.app.AppConfig;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
@Ignore
public class AppConfigDAOTest extends BaseDAOTest {

    @Autowired
    private AppConfigDAO appConfigDAO;

    @Test
    public void testGetAppConfigByClientId() {
        AppConfig appConfig = appConfigDAO.getAppConfigByClientId(CLIENT_ID);
        Assert.assertTrue(appConfig != null);
    }

}
