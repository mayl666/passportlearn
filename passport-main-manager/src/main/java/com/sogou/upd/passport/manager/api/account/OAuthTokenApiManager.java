package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.manager.form.RefreshPcTokenParams;

/**
 * PC端登录流程token的内部接口Manager
 * User: chenjiameng
 * Date: 13-7-24
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public interface OAuthTokenApiManager {
    /**
     * 验证refreshtoken正确性
     * @param pcRefreshTokenParams
     * @return
     */
    public Result refreshToken(PcRefreshTokenParams pcRefreshTokenParams);
}
