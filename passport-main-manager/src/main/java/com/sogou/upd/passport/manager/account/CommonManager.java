package com.sogou.upd.passport.manager.account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */
public interface CommonManager {

    /**
     * 根据用户名生成passportId
     *
     * @param username
     * @return
     */
    public String getPassportIdByUsername(String username) throws Exception;

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
    public void incRegTimesForInternal(String ip, int client_id);

    /**
     * 检验code是否正确
     *
     * @param firstStr
     * @param clientId
     * @param ct
     * @param originalCode
     * @return
     */
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode);

    /**
     * 根据字符串获取code值
     *
     * @param firstStr
     * @param clientId
     * @param ct
     * @return
     */
    public String getCode(String firstStr, int clientId, long ct);

}
