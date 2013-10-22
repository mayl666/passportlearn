package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-8-21
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface SHTokenService {
    /**
     *获取sohu access token
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     * @throws ServiceException
     */
    public String queryAccessToken(String passportId, int clientId, String instanceId) throws ServiceException;
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
     * 验证accesstoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param accessToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyShAccessToken(String passportId, int clientId, String instanceId, String accessToken) throws ServiceException;
    /**
     *校验refreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyShRToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;
    /**
     *校验refreshtoken和oldrefreshtoken
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyAllShRToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;
    /**
     * 保持sohu 老的refreshtoken
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @param refreshToken
     * @throws ServiceException
     */
    public void saveOldRefreshToken(final String passportId, final String instanceId, AppConfig appConfig, String refreshToken) throws ServiceException;

    /**
     * 保存accounttoken到sohu memcache
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @param accountToken
     * @throws ServiceException
     */
    public void saveAccountToken(final String passportId, final String instanceId,AppConfig appConfig,AccountToken accountToken) throws ServiceException;
}
