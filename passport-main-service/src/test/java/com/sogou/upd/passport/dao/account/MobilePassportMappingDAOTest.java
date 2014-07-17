package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午3:39
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class MobilePassportMappingDAOTest extends BaseDAOTest {

    @Autowired
    private MobilePassportMappingDAO mobilePassportMappingDAO;

    @Before
    public void init() {
        int row = mobilePassportMappingDAO.insertMobilePassportMapping(MOBILE, PASSPORT_ID);
        Assert.assertEquals(row, 1);
    }

    @After
    public void end() {
        int row = mobilePassportMappingDAO.deleteMobilePassportMapping(MOBILE);
        Assert.assertEquals(row, 1);
    }

    @Test
    public void testGetPassportIdByMobile() {
        String passportId = mobilePassportMappingDAO.getPassportIdByMobile(MOBILE);
        Assert.assertEquals(passportId, PASSPORT_ID);
    }

    @Test
    public void testUpdateMobilePassportMapping() {
        int row = mobilePassportMappingDAO.updateMobilePassportMapping(MOBILE, NEW_PASSPORT_ID);
        Assert.assertEquals(row, 1);
    }

    @Test
    public void testDeleteNull() {
        // 测试删除时传入null的情况
        int row = mobilePassportMappingDAO.deleteMobilePassportMapping(null);
        Assert.assertEquals(row, 0);
    }
}
