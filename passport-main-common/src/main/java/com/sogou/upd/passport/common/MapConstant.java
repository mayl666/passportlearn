package com.sogou.upd.passport.common;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.RedisUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Set;

/**
 * 接口频次限制相关mapping
 * User: mayan
 * Date: 13-11-3
 * Time: 上午11:42
 */
public class MapConstant {

    private RedisUtils redisUtils;
    private Map<Integer,Map<String,String>> clientInterfaceMapping = null;

    public Map <Integer,Map<String,String>> getClientInterfaceMapping() {
        if(MapUtils.isEmpty(clientInterfaceMapping)){
            //获取clientId列表
            Set<String> clientIdSet=redisUtils.smember(CacheConstant.CACHE_PREFIX_CLIENTID);
            //获取clientId对应的接口列表
            if(clientIdSet!=null && clientIdSet.size()>0){
                clientInterfaceMapping=Maps.newHashMap();
                 for (String clientId:clientIdSet){
                     String cacheKey=CacheConstant.CACHE_PREFIX_CLIENTID_INTERFACE_LIMITED_INIT + clientId;
                     Map<String,String> interfaceTimesMapping= redisUtils.hGetAll(cacheKey);
                     if(MapUtils.isNotEmpty(interfaceTimesMapping)){
                         clientInterfaceMapping.put(Integer.parseInt(clientId), interfaceTimesMapping);
                     }
                 }
            }
        }
        return clientInterfaceMapping;
    }

    public Map <Integer,Map<String,String>> updateClientInterfaceMapping(){
        Map <Integer,Map<String,String>> map=getClientInterfaceMapping();
        return map;
    }

    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }
}
