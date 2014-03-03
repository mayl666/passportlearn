package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.exception.ServiceException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午2:25
 * To change this template use File | Settings | File Templates.
 */
public interface SHPlusTokenService {

    /**
     * 根据RefreshToken获得AccessToken
     *
     * @param passportId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public String queryATokenByRToken(String passportId, String instanceId, String refreshToken, String sid) throws ServiceException;

}
