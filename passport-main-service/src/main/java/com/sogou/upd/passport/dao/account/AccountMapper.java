package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */

/**
 * 用户主表的接口mapper文件
 */
public interface AccountMapper {
    /**
     * 根据传入的参数，手机号码和密码,查询该手机是否已经注册
     * @return
     */
    public Account checkIsRegisterAccount(Account account);
    /**
     * 验证合法，用户注册
     * @param account
     */
    public int userRegister(Account account);
    /**
     * 根据用户名密码获取用户
     * @param
     * @return
     */
    public Account getUserAccount(Map<String,String> queryMap);


}
