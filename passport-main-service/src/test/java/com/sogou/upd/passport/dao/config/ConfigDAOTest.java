package com.sogou.upd.passport.dao.config;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.config.ClientIdLevelMapping;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;
import com.sogou.upd.passport.service.config.ConfigService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
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
    @Autowired
    private ConfigService configService;


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
    public void testGetInterfaceCount() {
        int count = configDAO.getInterfaceCount();
        System.out.println("#######记录总条数：" + count);
    }

    @Test
    public void testInsertInterfaceLevelMapping() {
        InterfaceLevelMapping inter = new InterfaceLevelMapping();
        inter.setInterfaceName("/internal/account/regmobile");
        int row = configDAO.insertInterfaceLevelMapping(inter);
        if (row > 0) {
            System.out.println("########插入成功！");
        } else {
            System.out.println("########插入失败！");
        }
    }

    @Test
    public void testDeleteInterfaceLevelMappingById() {
        int row = configDAO.deleteInterfaceLevelMappingById("11");
        if (row > 0) {
            System.out.println("########删除成功！");
        } else {
            System.out.println("########删除失败！");
        }
    }

    @Test
    public void testUpdateInterfaceLevelMapping() {
        long id = 1;
        String interfaceName = "/internal/account/reguser";
        InterfaceLevelMapping inter = new InterfaceLevelMapping();
        inter.setId(id);
        inter.setInterfaceName(interfaceName);
        inter.setPrimaryLevel("0");
        inter.setPrimaryLevelCount("900");
        //这个时间必须不为空，否则会报语法错误，是个潜在的bug呀。。
        inter.setCreateTime(new Date());
        int row = configDAO.updateInterfaceLevelMapping(inter);
        if (row > 0) {
            System.out.println("########修改成功！");
        } else {
            System.out.println("########修改失败！");
        }
    }

    @Test
    public void testFindClientIdAndLevelList() {
        List<ClientIdLevelMapping> list = configDAO.findClientIdAndLevelList();
        if (list != null && list.size() > 0) {
            System.out.println("######应用与等级列表大小为 ：" + list.size());
        }
    }

    @Test
    public void testFindLevelByClientId() {
        String clientId = "1001";
        ClientIdLevelMapping clm = configDAO.findLevelByClientId(clientId);
        if (clm != null) {
            System.out.println("########查询成功！");
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testInsertClientAndLevel() {
        ClientIdLevelMapping clm = new ClientIdLevelMapping();
        clm.setClientId("1044");
        clm.setLevelInfo("0");
        int row = configDAO.insertClientIdAndLevel(clm);
        if (row > 0) {
            System.out.println("########插入成功！");
        } else {
            System.out.println("########插入失败！");
        }
    }

    @Test
    public void testUpdateClientIdAndLevelMapping() {
        ClientIdLevelMapping clm = new ClientIdLevelMapping();
        clm.setLevelInfo("1");
        clm.setClientId("1044");
        boolean row = configService.saveOrUpdateClientAndLevel(clm);
        if (row) {
            System.out.println("########修改成功！");
        } else {
            System.out.println("########修改失败！");
        }
    }

    @Test
    public void testGetClientIdListByLevel() {
        String level = "1";
        List<ClientIdLevelMapping> list = configDAO.getClientIdListByLevel(level);
        if (list != null && list.size() > 0) {
            System.out.println("########查询成功！");
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testGetInterfaceListAll() {
        List<InterfaceLevelMapping> list = configDAO.getInterfaceListAll();
        if (list != null && list.size() > 0) {
            System.out.println("########查询成功！");
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testGetLevelByClientId() {
        String clientId = "1001";
        ClientIdLevelMapping clm = configDAO.getLevelByClientId(clientId);
        if (clm != null) {
            System.out.println("########查询成功！");
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testGetAppNameByAppId() {
        String clientId = "1001";
        AppConfig appConfig = configDAO.getAppNameByAppId(clientId);
        if (appConfig != null) {
            System.out.println("########查询成功！" + appConfig.getClientName());
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testGetAppList() {
        List<AppConfig> appConfigList = configDAO.getAppList();
        if (appConfigList != null && appConfigList.size() > 0) {
            System.out.println("########查询成功！" + appConfigList.size());
        } else {
            System.out.println("########查询失败！");
        }
    }

    @Test
    public void testSaveOrUpdateInterfaceLevelMapping() {
        InterfaceLevelMapping ilm = new InterfaceLevelMapping();
        String interId = "";
        String interfaceName = "/internal/account/reguser/test";
        //修改
        if (!"".equals(interId) && interId != null) {
            ilm.setId(Long.parseLong(interId));
        } else {
            ilm.setPrimaryLevel("0");
            ilm.setPrimaryLevelCount("0");
            ilm.setMiddleLevel("1");
            ilm.setMiddleLevelCount("0");
            ilm.setHighLevel("2");
            ilm.setHighLevelCount("0");
        }
        ilm.setInterfaceName(interfaceName);
        ilm.setCreateTime(new Date());
        boolean row = configService.saveOrUpdateInterfaceLevelMapping(ilm);
        if (row) {
            System.out.println("########保存成功！");
        } else {
            System.out.println("########保存失败！");
        }
    }
}
