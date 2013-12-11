package com.sogou.upd.passport.dao.app;

import com.sogou.upd.passport.model.config.ClientIdLevelMapping;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-6
 * Time: 下午11:52
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface ConfigDAO {

    /**
     * 对应接口与等级数据库表名称
     */
    String INTERFACE_LEVEL_TABLE_NAME = " interface_level_mapping ";

    /**
     * 应用与等级数据库表名称
     */
    String CLIENTID_LEVEL_TABLE_NAME = " clientid_level_mapping ";

    /**
     * 根据clientId查得该应用的等级
     *
     * @param clientId
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select * from" +
            CLIENTID_LEVEL_TABLE_NAME +
            "where client_id=:clientId")
    public ClientIdLevelMapping findLevelByClientId(@SQLParam("clientId") int clientId) throws DataAccessException;


    /**
     * 查询所有接口与等级的信息
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select * from" +
            INTERFACE_LEVEL_TABLE_NAME)
    public List<InterfaceLevelMapping> getInterfaceListAll() throws DataAccessException;

}
