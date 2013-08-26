package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-8-21
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface SHTokenService {
    /**
     * 获取SH refreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     * @throws ServiceException
     */
    public String queryRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     * 获取老refreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     * @throws ServiceException
     */
    public String queryOldRefreshToken(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     *校验refreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifshRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;
}
