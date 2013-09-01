package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcGetTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;

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
     * 验证refreshtoken是否正确
     * @param pcRefreshTokenParams
     * @return
     */
    public boolean verifyRefreshToken(PcRefreshTokenParams pcRefreshTokenParams);

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
}