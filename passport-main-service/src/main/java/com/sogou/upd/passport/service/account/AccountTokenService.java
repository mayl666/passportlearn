package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountToken;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-29 Time: 上午1:12 To change this template use File | Settings |
 * File Templates.
 */
public interface AccountTokenService {

    /**
     * 验证refresh_token的合法性
     *
     * @return refreshToken不存在或过期则返回null
     */
    public AccountToken verifyRefreshToken(String refreshToken, String instanceId) throws ServiceException;

    /**
     * 验证access_token的合法性
     *
     * @return refreshToken不存在或过期则返回null
     */
    public AccountToken verifyAccessToken(String accessToken) throws ServiceException;

    /**
     * 根据passportId获取AccountToken
     *
     * @param passportId
     * @return 不存在返回null
     */
    public AccountToken queryAccountTokenByPassportId(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     * 初始化账号授权信息
     */
    public AccountToken initialAccountToken(String passportId, int clientId, String instanceId) throws ServiceException;

    /**
     * @param passportId
     * @param clientId
     * @return
     * @throws Exception
     */
    public AccountToken updateAccountToken(String passportId, int clientId,
                                           String instanceId) throws ServiceException;

    /**
     * 删除AccountAuth（内部debug接口使用）
     */
    public boolean deleteAccountTokenByPassportId(String passportId) throws ServiceException;

    /**
     * 异步更新某用户其它状态信息
     */
    public void asynbatchUpdateAccountToken(final String passportId, final int clientId) throws ServiceException;


}
