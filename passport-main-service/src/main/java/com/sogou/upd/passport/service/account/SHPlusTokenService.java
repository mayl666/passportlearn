package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午2:25
 * To change this template use File | Settings | File Templates.
 */
public interface SHPlusTokenService {

    /**
     * 验证accesstoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param accessToken
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     */
    public boolean verifyShPlusAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException;
    /**
     *校验refreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyShPlusRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;
}
