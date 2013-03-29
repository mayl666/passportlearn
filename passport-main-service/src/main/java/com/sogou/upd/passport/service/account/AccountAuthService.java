package com.sogou.upd.passport.service.account;

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
     * @param refreshToken
     * @return
     */
    public boolean verifyRefreshToken(String refreshToken);
}
