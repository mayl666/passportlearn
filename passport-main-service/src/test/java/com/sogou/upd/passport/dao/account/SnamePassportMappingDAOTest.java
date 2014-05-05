package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import junit.framework.Assert;
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
public class SnamePassportMappingDAOTest extends BaseDAOTest {

    @Autowired
    private SnamePassportMappingDAO snamePassportMappingDAO;

    //    @Before
    @Test
    public void init() {
        int row = snamePassportMappingDAO.insertSnamePassportMapping(SID, SNAME, PASSPORT_ID, "13621009174");
        Assert.assertEquals(row, 1);
    }

    //    @After
//    @Test
    public void end() {
        int row = snamePassportMappingDAO.deleteSnamePassportMapping(SNAME);
        Assert.assertEquals(row, 1);
    }

    @Test
    public void testGetPassportIdBySname() {
        String passportId = snamePassportMappingDAO.getPassportIdBySname("tinkame710");
        Assert.assertEquals(passportId, PASSPORT_ID);
    }

    @Test
    public void testGetPassportIdByMobile() {
        String passportId = snamePassportMappingDAO.getPassportIdByMobile("13621009174");
        Assert.assertEquals(passportId, PASSPORT_ID);
    }

    @Test
    public void testUpdateMobilePassportMapping() {
        int row = snamePassportMappingDAO.updateSnamePassportMapping(SNAME, NEW_PASSPORT_ID);
        Assert.assertEquals(row, 1);
    }

    @Test
    public void testDeleteNull() {
        // 测试删除时传入null的情况
        int row = snamePassportMappingDAO.deleteSnamePassportMapping(null);
        Assert.assertEquals(row, 0);
    }
}
