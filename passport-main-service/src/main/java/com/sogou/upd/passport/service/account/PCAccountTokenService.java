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
     * 初始化AccountToken并存储
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     */
    public AccountToken initialAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException;

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
     * 更新或插入AccountToken
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     * @throws ServiceException
     */
    public AccountToken updateOrInsertAccountToken(String passportId, String instanceId, AppConfig appConfig) throws ServiceException;

    public boolean verifyAccessToken(String passportId, int clientId, String instanceId, String token) throws ServiceException;

    public boolean verifyRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;
}