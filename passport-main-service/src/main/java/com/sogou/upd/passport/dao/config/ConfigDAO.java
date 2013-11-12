package com.sogou.upd.passport.dao.config;

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
     * 根据id查询接口信息
     *
     * @param id
     * @return
     * @throws DataAccessException
     */
    @SQL("select * from" +
            INTERFACE_LEVEL_TABLE_NAME +
            " where id=:id")
    public InterfaceLevelMapping findInterfaceById(@SQLParam("id") String id) throws DataAccessException;

    /**
     * 获取所有接口配置信息列表
     */
    @SQL("select * from" +
            INTERFACE_LEVEL_TABLE_NAME
    )
    public List<InterfaceLevelMapping> findInterfaceLevelMappingList() throws DataAccessException;

    /**
     * 查询接口列表的总行数
     *
     * @return
     * @throws DataAccessException
     */
    @SQL("select count(*) from " +
            INTERFACE_LEVEL_TABLE_NAME)
    public int getInterfaceCount() throws DataAccessException;

    /**
     * 新增配置信息接口,三个等级新增时会有默认数值
     */
    @SQL("insert into" +
            INTERFACE_LEVEL_TABLE_NAME +
            "(id,interface_name,primary_level,primary_level_count,middle_level,middle_level_count,high_level,high_level_count) " +
            "values (:inter.id,:inter.interfaceName,:inter.primaryLevel,:inter.primaryLevelCount,:inter.middleLevel,:inter.middleLevelCount,:inter.highLevel,:inter.highLevelCount)"
    )
    public int insertInterfaceLevelMapping(@SQLParam("inter") InterfaceLevelMapping inter) throws DataAccessException;

    /**
     * 根据id删除对应接口配置信息
     */
    @SQL("delete from" +
            INTERFACE_LEVEL_TABLE_NAME +
            "where id=:id"
    )
    public int deleteInterfaceLevelMappingById(@SQLParam("id") String id) throws DataAccessException;

    /**
     * 修改接口配置信息
     */
    @SQL("update " +
            INTERFACE_LEVEL_TABLE_NAME +
            "set interface_name=:inter.interfaceName where id=:inter.id")
    public int updateInterfaceLevelMapping(@SQLParam("inter") InterfaceLevelMapping inter) throws DataAccessException;

    /**
     * 查询应用与等级映射信息
     *
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select * from " +
            CLIENTID_LEVEL_TABLE_NAME)
    public List<ClientIdLevelMapping> findClientIdAndLevelList() throws DataAccessException;

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
    public ClientIdLevelMapping findLevelByClientId(@SQLParam("clientId") String clientId) throws DataAccessException;

    /**
     * 新增应用与等级关系
     *
     * @param clm
     * @return
     * @throws DataAccessException
     */
    @SQL("insert into" +
            CLIENTID_LEVEL_TABLE_NAME +
            "(id,client_id,level_info,interface_name)" +
            "values (:clm.id,:clm.clientId,:clm.levelInfo,:clm.interfaceName)"
    )
    public int insertClientIdAndLevel(@SQLParam("clm") ClientIdLevelMapping clm) throws DataAccessException;

    /**
     * 修改应用与等级的映射信息
     *
     * @param clientIdLevelMapping
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("update " +
            CLIENTID_LEVEL_TABLE_NAME +
            "set level_info=:clientIdLevelMapping.levelInfo where client_id=:clientIdLevelMapping.clientId")
    public int updateClientIdAndLevelMapping(@SQLParam("clientIdLevelMapping") ClientIdLevelMapping clientIdLevelMapping) throws DataAccessException;

    /**
     * 查出某一等级下的所有接口
     *
     * @param level
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    @SQL("select * from " +
            CLIENTID_LEVEL_TABLE_NAME +
            "where level_info=:level")
    public List<ClientIdLevelMapping> getClientIdListByLevel(@SQLParam("level") String level) throws DataAccessException;

    /**
     * 根据应用id查该应用对应的等级
     *
     * @param clientId
     * @return
     * @throws DataAccessException
     */
    @SQL("select * from " +
            CLIENTID_LEVEL_TABLE_NAME +
            "where client_id=:clientId"
    )
    public ClientIdLevelMapping getLevelByClientId(@SQLParam("clientId") String clientId) throws DataAccessException;

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
