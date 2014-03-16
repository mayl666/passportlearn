package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * 用户注册时ip次数的累加
     *
     * @param ip
     * @param uuidName
     */
    public void incRegTimes(String ip, String uuidName);

    /**
     * 内部接口注册的ip次数累加
     *
     * @param ip
     */
    public void incRegTimesForInternal(String ip);

    /**
     * 检验code是否正确
     * @param firstStr
     * @param clientId
     * @param ct
     * @param originalCode
     * @return
     */
    public boolean isCodeRight(String firstStr,int clientId,long ct,String originalCode);
    /**
     * 根据字符串获取code值
     * @param firstStr
     * @param clientId
     * @param ct
     * @return
     */
    public String getCode(String firstStr, int clientId, long ct);

    /**
     * 判断时间戳（秒）是否有效
     * @param ct
     * @return
     */
    public boolean isSecCtValid(long ct);

    /**
     * 判断时间戳（毫秒）是否有效
     * @param ct
     * @return
     */
    public boolean isMillCtValid(long ct);
}
