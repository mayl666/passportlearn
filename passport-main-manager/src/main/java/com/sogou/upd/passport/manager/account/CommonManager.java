package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public interface CommonManager {

    /**
     * username包括email和手机号
     *
     * @param username
     * @return
     */
    public boolean isAccountExists(String username) throws Exception;

    /**
     * @param passportId
     * @return
     * @throws Exception
     */
    public Account queryAccountByPassportId(String passportId) throws Exception;

    /**
     * @param account
     * @return
     * @throws Exception
     */
    public boolean updateState(Account account, int newState) throws Exception;

    /**
     * @param account
     * @param password
     * @param needMD5
     * @return
     * @throws Exception
     */
    public boolean resetPassword(Account account, String password, boolean needMD5) throws Exception;

    /**
     * @param result
     * @param passportId
     * @param autoLogin
     * @return
     */
    public Result createCookieUrl(Result result, String passportId, int autoLogin);

    /**
     * 用户注册时ip次数的累加
     *
     * @param ip
     * @param uuidName
     */
    public void incRegTimes(String ip, String uuidName);

    /**
     * 只根据 passportId和autoLogin生成cookie  URL
     * @param passportId
     * @param autoLogin
     * @return
     */
    public Result createCookieUrl(String passportId, int autoLogin);

}
