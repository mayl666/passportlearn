package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.BaseTest;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-20 Time: 下午8:02 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
public class AppConfigServiceTest extends BaseTest {
    private static int CLIENT_ID = 1120;
    private static int CLIENT_ID_NOEXIST = 0;

    @Autowired
    private AppConfigService appConfigService;

    @Test
    public void testQueryClientName() {
        String resultStr = appConfigService.queryClientName(CLIENT_ID);
        Assert.assertTrue(resultStr.equals("搜狗通行证"));
        resultStr = appConfigService.queryClientName(CLIENT_ID_NOEXIST);
        Assert.assertNull(resultStr);
    }

}
