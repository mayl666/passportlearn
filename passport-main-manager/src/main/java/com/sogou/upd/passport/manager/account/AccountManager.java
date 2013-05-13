package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public interface AccountManager {

    /**
     * username包括email和手机号
     *
     * @param username
     * @return
     */
    public boolean isAccountExists(String username) throws Exception;

    /**
     * username包括email和手机号
     *
     * @param username
     * @return passportId
     */
    public String getPassportIdByUsername(String username) throws Exception;



}
