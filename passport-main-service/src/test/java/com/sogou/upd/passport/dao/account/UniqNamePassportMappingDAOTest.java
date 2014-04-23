package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

/**
 * 昵称映射单元测试
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-4-21
 * Time: 下午3:48
 */
public class UniqNamePassportMappingDAOTest extends BaseDAOTest {

    @Autowired
    private UniqNamePassportMappingDAO mappingDAO;


    @Test
    public void testGetTotalCount() {
        int count = mappingDAO.getUpmTotalCount();
        Assert.assertNotNull(count);
    }


    @Test
    public void testGetUpmByPassportId() {

        String passport_id = "502011527@renren.sohu.com";
        String passport_id_not_exist = "sdfsdfsdfsdfsdfsd111111@sohu.com";

        UniqnamePassportMapping mapping = mappingDAO.getUpmByPassportId(passport_id);
        UniqnamePassportMapping notExistMapping = mappingDAO.getUpmByPassportId(passport_id_not_exist);
        Assert.assertNotNull(mapping);
        Assert.assertNotNull(notExistMapping);
    }

    @Test
    public void testInsertUpm32() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        int result = 0;
        try {
            result = mappingDAO.insertUpm0To32("???", "72CDA7AA41E0713A9DE6178306FCD7AC@qq.sohu.com", timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(result);
        }


    }

}
