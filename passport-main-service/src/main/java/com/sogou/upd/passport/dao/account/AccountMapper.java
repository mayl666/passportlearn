package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;

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
     * 验证合法，用户注册
     * @param account
     */
    public int saveAccount(Account account);

    /**
     * 根据passportId查询Account
     * @param passportId
     * @return
     */
    public Account getAccountByPassportId(String passportId);

    /**
     * 根据手机号码查询Account
     * todo 和getAccountByPassportId合并，动态查询sql
     * @param mobile
     * @return
     */
    public Account getAccountByMobile(String mobile);

    /**
     * 根据主键id获取passportId
     * @param userId
     * @return
     */
    public String getPassportIdByUserId(long userId);

    /**
     * 根据passportId查询对应的主键Id
     * @param passportId
     * @return
     */
    public long getUserIdByPassportId(String passportId);

    /**
     * 根据passportId删除用户的Account信息，内部调试接口使用
     * @param passportId
     * @return
     */
    public void deleteAccountByPassportId(String passportId);

    /**
     * 修改用户信息
     * @param account
     * @return
     */
    public int updateAccount(Account account);

}
