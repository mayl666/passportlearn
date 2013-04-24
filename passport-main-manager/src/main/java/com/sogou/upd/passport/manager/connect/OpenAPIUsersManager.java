package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:57
 * To change this template use File | Settings | File Templates.
 */
public interface OpenAPIUsersManager {

    /**
     * 根据passportId获取第三方openid
     *
     * @param passportId
     * @return
     */
    public Result getOpenIdByPassportId(String passportId,int clientId,int accountType);
}
