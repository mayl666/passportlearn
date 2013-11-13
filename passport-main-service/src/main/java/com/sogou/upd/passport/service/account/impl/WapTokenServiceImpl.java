package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.WapTokenService;
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
public class WapTokenServiceImpl implements WapTokenService {
    private static final Logger logger = LoggerFactory.getLogger(WapTokenServiceImpl.class);

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void saveWapToken(String token,final String passportId) throws ServiceException {
        try {
            redisUtils.setWithinSeconds(token,passportId, DateAndNumTimesConstant.TIME_FIVEMINUTES);
        } catch (Exception e) {
            logger.error("saveWapToken Fail, token:" + token + ", passportId:" + passportId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String getPassprotIdByToken(String token) throws ServiceException {
        try {
            return  redisUtils.get(token);
        } catch (Exception e) {
            logger.error("getPassprotIdByToken Fail, token:" + token, e);
            throw new ServiceException(e);
        }
    }

}
