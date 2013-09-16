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

    public String queryPassportBySHPlusId(String shPlusId);

    /**
     * 校验refreshtoken
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyShPlusRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;

    /**
     * 通过获取token获取资源
     *
     * @param instance_id
     * @param access_token
     * @param scope
     * @param resource_type
     * @return
     * @throws ServiceException
     */
    public String getResourceByToken(String instance_id, String access_token, String scope, String resource_type) throws ServiceException;
}
