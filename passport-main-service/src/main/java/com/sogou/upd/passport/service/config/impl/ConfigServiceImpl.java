package com.sogou.upd.passport.service.config.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.config.ConfigDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.config.ClientIdLevelMapping;
import com.sogou.upd.passport.model.config.InterfaceLevelMapping;
import com.sogou.upd.passport.service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-6
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private RedisUtils redisUtils;

    private static final String CACHE_PREFIX_PASSPORT_INTER_AND_LEVEL = CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED_INIT;

    /**
     * 修改之前需要读出接口信息
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    @Override
    public InterfaceLevelMapping findInterfaceById(String id) throws ServiceException {
        try {
            InterfaceLevelMapping inter = configDAO.findInterfaceById(id);
            if (inter != null) {
                return inter;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return null;
    }

    /**
     * 页面加载后，需要显示接口列表信息
     *
     * @return
     * @throws ServiceException
     */
    @Override
    public List<InterfaceLevelMapping> findInterfaceLevelMappingList() throws ServiceException {
        List<InterfaceLevelMapping> listInters;
        try {
            listInters = configDAO.findInterfaceLevelMappingList();
            if (listInters != null && listInters.size() > 0) {
                return listInters;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 在接口列表中，新增或修改某个接口信息,新增id为空，修改id非空，只针对接口，没有等级信息
     *
     * @param interfaceLevelMapping 新增或修改接口和等级信息
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean saveOrUpdateInterfaceLevelMapping(InterfaceLevelMapping interfaceLevelMapping) throws ServiceException {
        int row;
        try {
            if (interfaceLevelMapping.getId() != 0) {
                //修改接口
                row = configDAO.updateInterfaceLevelMapping(interfaceLevelMapping);
            } else {
                //新增接口
                row = configDAO.insertInterfaceLevelMapping(interfaceLevelMapping);
            }
            if (row != 0) {
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 在接口列表中，删除某个接口信息
     *
     * @param id 要删除的接口id
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean deleteInterfaceLevelById(String id) throws ServiceException {
        try {
            //删除数据库中接口
            int row = configDAO.deleteInterfaceLevelMappingById(id);
            if (row != 0) {
                //查询接口信息
                InterfaceLevelMapping ilm = configDAO.findInterfaceById(id);
                String key = ilm.getInterfaceName();
                //查询所有应用列表
                List<ClientIdLevelMapping> listResult = configDAO.findClientIdAndLevelList();
                if (listResult != null && listResult.size() > 0) {
                    for (ClientIdLevelMapping clm : listResult) {
                        String cacheKey = buildCacheKey(clm.getClientId());
                        //删除缓存中以该接口为key的缓存记录
                        redisUtils.hDelete(cacheKey, key);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInterfaceCount() throws ServiceException {
        int count;
        try {
            count = configDAO.getInterfaceCount();
        } catch (Exception e) {
            throw new ServiceException();
        }
        return count;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 在显示应用和等级列表后，先会读出每个应用已有等级
     *
     * @return
     * @throws ServiceException
     */
    @Override
    public ClientIdLevelMapping findLevelByClientId(String clientId) throws ServiceException {
        if (clientId != null) {
            ClientIdLevelMapping clientIdLevelMapping = configDAO.findLevelByClientId(clientId);
            if (clientIdLevelMapping != null) {
                return clientIdLevelMapping;
            }
        }
        return null;
    }

    /**
     * 显示应用与等级的下拉列表信息
     *
     * @return
     * @throws ServiceException
     */
    @Override
    public List<ClientIdLevelMapping> findClientIdLevelMappingList() throws ServiceException {
        List<ClientIdLevelMapping> list;
        try {
            list = configDAO.findClientIdAndLevelList();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 保存应用与等级的映射关系,其中涉及到写缓存,会往缓存里写应用与接口的对应关系及次数,就要根据等级查出下面所有的接口列表
     *
     * @param clientIdLevelMapping
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    @Override
    public boolean saveOrUpdateClientAndLevel(ClientIdLevelMapping clientIdLevelMapping) throws ServiceException {
        try {
            //更新数据库中应用与等级映射关系
            int row = configDAO.updateClientIdAndLevelMapping(clientIdLevelMapping);
            if (row != 0) {
                String hashCacheKey = buildCacheKey(clientIdLevelMapping.getClientId());
                String level = clientIdLevelMapping.getLevelInfo();
                //获取接口列表
                List<InterfaceLevelMapping> interfaceList = configDAO.getInterfaceListAll();
                for (InterfaceLevelMapping ilm : interfaceList) {
                    //key是接口名称，value是此等级下该接口对应的频次限制
                    String key = ilm.getInterfaceName();
                    String value = getValue(ilm, level);
                    //更新缓存,更新该应用下所有接口等级
                    redisUtils.hPut(hashCacheKey, key, value);
                }
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 为研总提供的接口
     *
     * @param clientId
     * @return
     * @throws ServiceException
     */
    @Override
    public Map<String, String> getMapsFromCacheKey(String clientId) throws ServiceException {
        String cacheKey = buildCacheKey(clientId);
        Map<String, String> maps;
        List<InterfaceLevelMapping> list;
        try {
            //先读缓存
            maps = redisUtils.hGetAll(cacheKey);
            //如果没有，读数据库
            if (maps == null && maps.size() == 0) {
                //先根据应用id得到该应用对应的等级
                ClientIdLevelMapping clm = configDAO.findLevelByClientId(clientId);
                if (clm != null) {
                    String level = clm.getLevelInfo();
                    //再根据该等级读出此等级下所有接口及对应的频次限制次数
                    list = configDAO.getInterfaceListAll();
                    if (list != null && list.size() > 0) {
                        for (InterfaceLevelMapping inter : list) {
                            String key = inter.getInterfaceName();
                            String value = getValue(inter, level);
                            maps.put(key, value);
                        }
                        return maps;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 在页面上显示三个等级下的所有接口及其对应的次数
     *
     * @return
     * @throws ServiceException
     */
    @Override
    public Map<String, List<InterfaceLevelMapping>> getInterfaceMapByLevel() throws ServiceException {
        List<InterfaceLevelMapping> interListAll = configDAO.getInterfaceListAll();
        Map<String, List<InterfaceLevelMapping>> interAndLevelMap = Maps.newHashMap();
        List<InterfaceLevelMapping> primaryList = new ArrayList<>();
        List<InterfaceLevelMapping> middleList = new ArrayList<>();
        List<InterfaceLevelMapping> highList = new ArrayList<>();
        if (interListAll != null && interListAll.size() > 0) {
            for (InterfaceLevelMapping inter : interListAll) {
                if (inter.getPrimaryLevel() != null && "0".equals(inter.getPrimaryLevel())) {
                    primaryList.add(inter);
                } else if (inter.getMiddleLevel() != null && "1".equals(inter.getMiddleLevel())) {
                    middleList.add(inter);
                } else if (inter.getHighLevel() != null && "2".equals(inter.getHighLevel())) {
                    highList.add(inter);
                }
            }
            interAndLevelMap.put("primaryList", primaryList);
            interAndLevelMap.put("middleList", middleList);
            interAndLevelMap.put("highList", highList);
            if (!interAndLevelMap.isEmpty()) {
                return interAndLevelMap;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getValue(InterfaceLevelMapping inter, String level) {
        String value = null;
        switch (level) {
            case "0":
                value = inter.getPrimaryLevelCount();
                break;
            case "1":
                value = inter.getMiddleLevelCount();
                break;
            case "2":
                value = inter.getHighLevelCount();
                break;
        }
        return value;
    }

    private String buildCacheKey(String clientId) {
        return CACHE_PREFIX_PASSPORT_INTER_AND_LEVEL + clientId;
    }
}
