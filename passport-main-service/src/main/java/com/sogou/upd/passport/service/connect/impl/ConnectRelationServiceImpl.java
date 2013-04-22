package com.sogou.upd.passport.service.connect.impl;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.connect.ConnectRelationDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ConnectRelationServiceImpl implements ConnectRelationService {

    private static final String CACHE_PREFIX_OPENID_CONNECTRELATION = CacheConstant.CACHE_PREFIX_OPENID_CONNECTRELATION;

    @Autowired
    private ConnectRelationDAO connectRelationDAO;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public ConnectRelation querySpecifyConnectRelation(String openid, int provider, String appKey) throws ServiceException {
        Map<String, ConnectRelation> connectRelations = queryAppKeyMapping(openid, provider);
        ConnectRelation connectRelation = null;
        if (!connectRelations.isEmpty()) {
            connectRelation = connectRelations.get(appKey);
        }
        return connectRelation;
    }

    @Override
    public Map<String, ConnectRelation> queryAppKeyMapping(String openid, int provider) throws ServiceException {
        String cacheKey = buildConnectRelationKey(openid, provider);
        Map<String, ConnectRelation> connectRelations = Maps.newHashMap();
        try {
            Map<String, String> appKeyMappingConnectRelation = redisUtils.hGetAll(cacheKey);
            Type type = new TypeToken<ConnectRelation>() {
            }.getType();
            connectRelations = RedisUtils.strMapToObjectMap(appKeyMappingConnectRelation, type);
            if (connectRelations.isEmpty()) {
                List<ConnectRelation> connectRelationList = connectRelationDAO.listConnectRelation(openid, provider);
                if (!CollectionUtils.isEmpty(connectRelationList)) {
                    connectRelations = mapToList(connectRelationList);
                    redisUtils.set(cacheKey, connectRelations);
                }
            }
        } catch (ServiceException e) {
            throw e;
        }
        return connectRelations;
    }

    @Override
    public boolean initialConnectRelation(ConnectRelation connectRelation) throws ServiceException {
        int row = 0;
        try {
            String openid = connectRelation.getOpenid();
            int provider = connectRelation.getProvider();
            String appKey = connectRelation.getAppKey();
            row = connectRelationDAO.insertConnectRelation(connectRelation.getOpenid(), connectRelation);
            if (row != 0) {
                String cacheKey = buildConnectRelationKey(openid, provider);
                redisUtils.hPut(cacheKey, appKey, connectRelation);
                return true;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            throw new ServiceException(e);
        }
    }

    private String buildConnectRelationKey(String openid, int provider) {
        return CACHE_PREFIX_OPENID_CONNECTRELATION + openid + "_" + provider;
    }

    private Map<String, ConnectRelation> mapToList(List<ConnectRelation> connectRelationList) {
        Map<String, ConnectRelation> map = Maps.newHashMap();
        for (ConnectRelation connectRelation : connectRelationList) {
            map.put(connectRelation.getAppKey(), connectRelation);
        }
        return map;
    }

}
