package com.sogou.upd.passport.service.config;


import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.config.ClientIdLevelMapping;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-6
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigService {

    /**
     * 修改前先读出该条记录
     *
     * @param id
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public InterfaceLevelMapping findInterfaceById(String id) throws ServiceException;

    /**
     * 获取配置信息列表
     *
     * @return
     */
    public List<InterfaceLevelMapping> findInterfaceLevelMappingList() throws ServiceException;

    /**
     * 新增或修改接口
     *
     * @param interfaceLevelMapping 新增或修改接口和等级信息
     * @return
     */
    public boolean saveOrUpdateInterfaceLevelMapping(InterfaceLevelMapping interfaceLevelMapping) throws ServiceException;

    /**
     * 删除接口
     *
     * @param id 要删除的接口id
     * @return
     */
    public boolean deleteInterfaceLevelById(String id) throws ServiceException;

    /**
     * 查询接口列表的总行数
     *
     * @return
     * @throws ServiceException
     */
    public int getInterfaceCount() throws ServiceException;

    /**
     * 根据应用id查询对应的等级信息
     *
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public ClientIdLevelMapping findLevelByClientId(String clientId) throws ServiceException;

    /**
     * 获取所有应用id
     *
     * @return
     */
    public List<ClientIdLevelMapping> findClientIdLevelMappingList() throws ServiceException;

    /**
     * 保存应用和等级信息
     *
     * @param clientIdLevelMapping
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public boolean saveOrUpdateClientAndLevel(ClientIdLevelMapping clientIdLevelMapping) throws ServiceException;

    /**
     * 根据hashKey获取对应的key-value键值对
     *
     * @param clientId
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public Map<String, String> getMapsFromCacheKey(String clientId) throws ServiceException;

    /**
     * 查询所有接口及对应等级信息，按三个不同的等级划分
     *
     * @return
     * @throws ServiceException
     */
    public Map<String, List<InterfaceLevelMapping>> getInterfaceMapByLevel() throws ServiceException;

    /**
     * 根据应用id查询该应用对应的等级
     *
     * @param clientId
     * @return
     * @throws ServiceException
     */
    public ClientIdLevelMapping getLevelByClientId(String clientId) throws ServiceException;

}
