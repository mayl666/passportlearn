package com.sogou.upd.passport.service.connect.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.connect.ConnectRelationDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private DBShardRedisUtils dbShardRedisUtils;

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_querySpecifyConnectRelation", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public ConnectRelation querySpecifyConnectRelation(String openid, int provider, String appKey) throws ServiceException {
        Map<String, ConnectRelation> connectRelations = queryAppKeyMapping(openid, provider);
        ConnectRelation connectRelation = null;
        if (!MapUtils.isEmpty(connectRelations)) {
            connectRelation = connectRelations.get(appKey);
        }
        return connectRelation;
    }
    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_queryAppKeyMapping", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public Map<String, ConnectRelation> queryAppKeyMapping(String openid, int provider) throws ServiceException {
        String cacheKey = buildConnectRelationKey(openid, provider);
        Map<String, ConnectRelation> connectRelations = Maps.newHashMap();
        try {
            Map<String, String> appKeyMappingConnectRelation = dbShardRedisUtils.hGetAll(cacheKey);

            if (!MapUtils.isEmpty(appKeyMappingConnectRelation)) {
                connectRelations = RedisUtils.strMapToObjectMap(appKeyMappingConnectRelation, ConnectRelation.class);
            }
            if (MapUtils.isEmpty(connectRelations)) {
                List<ConnectRelation> connectRelationList = connectRelationDAO.listConnectRelation(openid, provider);
                if (!CollectionUtils.isEmpty(connectRelationList)) {
                    connectRelations = mapToList(connectRelationList);
                    dbShardRedisUtils.hPutAllObject(cacheKey, connectRelations);
                    dbShardRedisUtils.expire(cacheKey, (int) DateAndNumTimesConstant.THREE_MONTH);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return connectRelations;
    }

    @Profiled(el = true, logger = "dbTimingLogger", tag = "service_initialConnectRelation", timeThreshold = 20, normalAndSlowSuffixesEnabled = true)
    @Override
    public boolean initialConnectRelation(ConnectRelation connectRelation) throws ServiceException {
        int row = 0;
        try {
            String openid = connectRelation.getOpenid();
            int provider = connectRelation.getProvider();
            String appKey = connectRelation.getAppKey();
            row = connectRelationDAO.insertOrUpdateConnectRelation(connectRelation.getOpenid(), connectRelation);
            if (row != 0) {
                String cacheKey = buildConnectRelationKey(openid, provider);
                dbShardRedisUtils.hPut(cacheKey, appKey, connectRelation);
                dbShardRedisUtils.expire(cacheKey, (int) DateAndNumTimesConstant.THREE_MONTH);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
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
