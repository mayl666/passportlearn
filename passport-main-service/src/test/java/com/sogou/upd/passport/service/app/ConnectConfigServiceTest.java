package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.model.app.ConnectConfig;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-21
 * Time: 下午9:19
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ConnectConfigServiceTest extends BaseTest {

    @Inject
    private ConnectConfigService connectConfigService;

    @Test
    public void testModifyConnectConfig() {
        ConnectConfig connectConfig = new ConnectConfig();
        connectConfig.setClientId(1001);
        connectConfig.setProvider(4);
        connectConfig.setAppKey("3363779877");
        connectConfig.setAppSecret("201cf182fc60b872bea8da76c8af5b4c");
        connectConfig.setScope("");
        boolean success = connectConfigService.modifyConnectConfig(connectConfig);
        Assert.assertTrue(success);
    }
}
