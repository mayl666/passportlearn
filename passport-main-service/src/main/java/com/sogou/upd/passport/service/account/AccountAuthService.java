package com.sogou.upd.passport.service.account;

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
     * 根据userId查询list集合
     * @param userId
     * @return
     */
    public List<AccountAuth> findAccountAuthListByUserId(long userId);

    /**
     * 批量更新某个用户的状态记录表信息
     * @param list
     */
    public void batchUpdateAccountAuth(List<AccountAuth> list);

}
