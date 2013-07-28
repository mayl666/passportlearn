package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.PCAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PCAccountServiceImpl implements PCAccountService{
    private static final Logger logger = LoggerFactory.getLogger(PCAccountServiceImpl.class);

    @Autowired
    private KvUtils kvUtils;

    @Override
    public boolean checkToken(String key, String token) throws ServiceException {
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(token)){
            return false;
        }
        boolean result = false;
        try {
            String storeToken = kvUtils.get(key);
            if(token.equals(storeToken)){
                result = true;
            }
        } catch (Exception e) {
            logger.error("recordNum:cacheKey" + key, e);
            throw new ServiceException(e);
        }
        return result;
    }
}
