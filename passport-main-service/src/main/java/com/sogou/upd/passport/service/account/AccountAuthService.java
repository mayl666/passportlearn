package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.AccountAuth;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-29
 * Time: 上午1:12
 * To change this template use File | Settings | File Templates.
 */
public interface AccountAuthService {

    /**
     * 验证refresh_token的合法性
     *
     * @param refreshToken
     * @return
     */
    public AccountAuth verifyRefreshToken(String refreshToken, String instanceId);

    /**
     * 初始化账号授权信息
     *
     * @param userId
     * @param passportId
     * @param clientId
     * @return
     */
    public AccountAuth initialAccountAuth(long userId, String passportId, int clientId, String instanceId) throws Exception;

    /**
     * @param userId
     * @param passportId
     * @param clientId
     * @return
     * @throws Exception
     */
    public AccountAuth updateAccountAuth(long userId, String passportId, int clientId, String instanceId) throws Exception;

    /**
     * 异步更新某用户其它状态信息
     */
    public void asynUpdateAccountAuthBySql(final String mobile, final int clientId, final String instanceId) throws SystemException;


}
