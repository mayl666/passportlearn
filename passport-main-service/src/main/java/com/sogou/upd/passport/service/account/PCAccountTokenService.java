package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;

/**
 * 用于登录授权oauth2的AccountToken的Service
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public interface PCAccountTokenService {

    /**
     * 初始化或更新AccountToken
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     */
    public AccountToken initialOrUpdateAccountToken(String passportId, String instanceId, AppConfig appConfig) throws ServiceException;

    /**
     * 验证AccessToken合法性并返回PassportId
     * @param token
     * @return
     */
    public String queryPassportIdByAccessToken(int clientId, String instanceId, String token, String clientSecret);

    /**
     * 查询AccountToken
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     * @throws ServiceException
     */
    public AccountToken queryAccountToken(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     * 验证accesstoken正确性
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param token
     * @return
     * @throws ServiceException
     */
    public boolean verifyAccessToken(String passportId, int clientId, String instanceId, String token) throws ServiceException;

    /**
     * 验证refreshToken正确性
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;

}