package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.*;
import com.sogou.upd.passport.model.app.AppConfig;

/**
 * 桌面端登录流程Manager
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public interface PCAccountManager {
    /**
     * 此接口处理两种情况下的生成pairToken：
     * 1.验证用户名和密码；
     * 2.验证由refreshtoken生成的sig；
     *
     * @param pcTokenParams
     * @return
     */
    public Result createPairToken(PcPairTokenParams pcTokenParams);

    /**
     * 浏览器4.2版本登陆接口
     * @param loginParams
     * @param ip
     * @return
     */
    public Result oauth2Login(PCOAuth2LoginParams loginParams, String ip);

    /**
     * 根据refreshtoken换一个token用来延长登陆
     * @param pcRefreshTokenParams
     * @return
     */
    public Result authRefreshToken(PcRefreshTokenParams pcRefreshTokenParams);

    /**
     * 验证token并根据token换取cookie
     *
     * @param authPcTokenParams
     * @return
     */
    public Result authToken(PcAuthTokenParams authPcTokenParams);

    /**
     *验证refreshtoken是否正确
     * @param passportId
     * @param clientId
     * @param instanceId
     * @param refreshToken
     * @return
     */
    public boolean verifyRefreshToken(String passportId, int clientId, String instanceId, String refreshToken);

    /**
     * 获取sig
     * @param passportId
     * @param clientId
     * @param timestamp
     * @return
     */
    public String getSig(String passportId, int clientId,String refresh_token,String timestamp) throws Exception;

    /**
     * 只生成token，不需要校验密码或者sig
     * @param clientId
     * @param passportId
     * @param instanceId
     * @return
     */
    public Result createConnectToken(int clientId, String passportId, String instanceId);

    /**
     * 从浏览器论坛获取用户昵称
     * 如果为空，则返回用户名@前面一段
     * @param passportId
     * @return
     */
    public String getBrowserBbsUniqname(String passportId);

    /**
     * 通过clientId获取不同的昵称
     * @param passportId
     * @param clientId
     * @return
     */
    public String getUniqnameByClientId(String passportId,int clientId);

    /**
     * 创建账号account
     * @param passportId
     * @param instanceId
     * @param clientId
     * @return
     */
    public Result createAccountToken(String passportId, String instanceId,int  clientId);

    /**
     * 更新AccountToken
     * @param passportId
     * @param instanceId
     * @param appConfig
     * @return
     */
    public Result updateAccountToken(String passportId, String instanceId, AppConfig appConfig);
}