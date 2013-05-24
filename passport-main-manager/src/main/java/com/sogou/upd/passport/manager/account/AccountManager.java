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
     * 注意，返回值有以下几种情况，返回时需要检查是否为null
     *      <p><strong>username为手机号：</strong></p>
     *       1.返回passportId
     *       2.返回null，代表未绑定或未注册手机号username；
     *      <p><strong>username不是手机号：</strong></p>
     *       返回username，但账户不一定存在，因为未查account表（减少数据库查询操作）
     *
     * @param username 包括email和手机号
     * @return passportId
     */
    public String getPassportIdByUsername(String username) throws Exception;



}
