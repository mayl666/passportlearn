package com.sogou.upd.passport.dao.config;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-11
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDAOTest extends BaseDAOTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ConfigDAO configDAO;


    @Test
    public void testFindInterfaceById() {
        String id = "1";
        InterfaceLevelMapping inter = configDAO.findInterfaceById(id);
        System.out.println("接口名称：##########" + inter.getInterfaceName());
    }

    @Test
    public void testFindInterfaceLevelMappingList() {
        List<InterfaceLevelMapping> list = configDAO.findInterfaceLevelMappingList();
        if (list != null && list.size() > 0) {
            System.out.println("#########list size is: " + list.size());
            for (InterfaceLevelMapping inter : list) {
                System.out.println("######interfaceName is " + inter.getInterfaceName() + "----> " + inter.getHighLevel() + "----->" + inter.getHighLevelCount());
            }
        }
    }

    @Test
    public void testGetInterfaceCount(){
        int count = configDAO.getInterfaceCount();
        System.out.println("" + count);
    }
}
