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
     * 初始化token
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     * @throws ServiceException
     */
    public AccountToken initialAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException;

    /**
     * 更新token
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     * @throws ServiceException
     */
    public AccountToken updateAccountToken(final String passportId, final String instanceId, AppConfig appConfig) throws ServiceException;

    /**
     * 存储accountToken
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @param accountToken
     * @throws ServiceException
     */
    public void saveAccountToken(final String passportId, final String instanceId, AppConfig appConfig, AccountToken accountToken) throws ServiceException;

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

    /**
     * 保存浏览器老的refreshtoken
     *
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @param refreshToken
     * @throws ServiceException
     */
    public void saveOldRefreshToken(final String passportId, final String instanceId, AppConfig appConfig, String refreshToken) throws ServiceException;

    /**
     * 获取浏览器 老的token
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @return
     * @throws ServiceException
     */
    public String queryOldPCToken(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     * 验证老token
     *
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     * @throws ServiceException
     */
    public boolean verifyPCOldRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) throws ServiceException;

    /**
     * 根据token来获取passportId
     *
     * @param token
     * @param clientSecret
     * @return
     * @throws ServiceException
     */
    public String getPassportIdByToken(String token, String clientSecret) throws ServiceException;

    /**
     * 兼容以SG_开头的token
     *
     * @param token
     * @param clientSecret
     * @return
     * @throws ServiceException
     */
    public String getPassportIdByOldToken(String token, String clientSecret) throws ServiceException;

    /**
     * 批量清除失效的桌面端Token
     * 场景：修改密码、封禁账号
     * @param passportId
     * @param isAsyn 是否为异步操作
     * @return
     * @throws ServiceException
     */
    public void batchRemoveAccountToken(String passportId, boolean isAsyn);

    /**
     * 清除单条失效的桌面端Token
     * 场景：修改密码、封禁账号
     * @param passportId
     * @param clientId 应用ID
     * @param instanceId 客户端实例ID
     * @return
     * @throws ServiceException
     */
    public void removeAccountToken(String passportId, int clientId, String instanceId);

}