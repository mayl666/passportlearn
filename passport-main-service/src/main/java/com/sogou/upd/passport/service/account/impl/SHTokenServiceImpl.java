package com.sogou.upd.passport.service.account.impl;

import com.danga.MemCached.MemCachedClient;
import com.sogou.upd.passport.common.utils.MemcacheUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.SHToken;
import com.sogou.upd.passport.service.account.SHTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-8-21
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SHTokenServiceImpl implements SHTokenService{
    private static final Logger logger = LoggerFactory.getLogger(SHTokenServiceImpl.class);

    @Autowired
    private MemcacheUtils memUtils;

    @Autowired
    private MemCachedClient rTokenMaster;
    /**
     * 构造SHToken的key
     * 格式为：passport|clientId|instanceId
     * passportId_clientId_instanceId：AccountToken的映射
     */
    private String buildKeyStr(String passportId, int clientId) {
        return passportId + "|" + clientId;
    }

    private String buildTsKeyStr(String passportId, int clientId, String instanceId) {
        return passportId + "|" + clientId + "|" + instanceId;
    }

    private String buildOldKeyStr(String passportId, int clientId) {
        return "old|" + passportId + "|" + clientId;
    }

    private String buildOldTsKeyStr(String passportId, int clientId, String instanceId) {
        return "old|" + passportId + "|" + clientId + "|" + instanceId;
    }


    @Override
    public SHToken queryRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException {
        try {
            String key = buildKeyStr(passportId, clientId);
            Object value = rTokenMaster.get(key);
            if(value != null){
                System.out.println("valueStr:"+value.toString());

            }

            String tsKey = buildTsKeyStr(passportId, clientId,instanceId);
            Object tsValue = rTokenMaster.get(tsKey);
            if(tsValue != null){
                System.out.println("tsValue:"+tsValue.toString());
            }

            String oldKey = buildOldKeyStr(passportId, clientId);
            Object oldValue = rTokenMaster.get(oldKey);
            if(oldValue != null){
                System.out.println("valueStr:"+oldValue.toString());

            }

            String tsOldKey = buildOldTsKeyStr(passportId, clientId, instanceId);
            Object tsOldValue = rTokenMaster.get(tsOldKey);
            if(tsOldValue != null){
                System.out.println("tsValue:"+tsOldValue.toString());
            }

            return null;
        } catch (Exception e) {
            logger.error("Query AccountToken Fail, passportId:" + passportId + ", clientId:" + clientId + ", instanceId:" + instanceId, e);
            throw new ServiceException(e);
        }
    }
}
