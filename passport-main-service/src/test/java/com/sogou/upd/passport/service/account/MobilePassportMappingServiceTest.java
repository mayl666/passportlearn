package com.sogou.upd.passport.service.account;

import com.sun.org.apache.xml.internal.security.Init;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;
import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-18
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class MobilePassportMappingServiceTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private MobilePassportMappingService mobilePassportMappingService;

    private static final String mobile = "13545329008";
    private static final String passportId = mobile + "@sohu.com";
    private static final String passportId_sogou = "liulingtest@sogou.com";
    private static final String passportId_mail = "liuling465@163.com";

    @Test
    public void testInitMobilePassportMapping() {
        boolean isSuccess = mobilePassportMappingService.initialMobilePassportMapping(mobile, passportId);
        Assert.assertTrue(isSuccess);
    }

    @Test
    public void testQueryPassportIdByMobile() {
        boolean isDeleteSuccess = mobilePassportMappingService.deleteMobilePassportMapping(mobile);
        Assert.assertTrue(isDeleteSuccess);
    }


}
