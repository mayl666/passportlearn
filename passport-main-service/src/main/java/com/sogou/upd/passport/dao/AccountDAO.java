package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.model.account.Account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountDAO {

  /**
   * 验证合法，用户注册
   */
  public int insertAccount(Account account);

  /**
   * 根据passportId获取Account
   */
  @SQL(
      " select * from account where passport_id=(:passport_id)")
  public Account queryAccountByPassportId(@SQLParam("passport_id") String passport_id);

  /**
   * 根据手机号码获取Account todo 和getAccountByPassportId合并，动态查询sql
   */
  public Account getAccountByMobile(String mobile);

  /**
   * 根据userId获取Account
   */
  public Account getAccountByUserId(long userId);

  /**
   * 根据主键id获取passportId
   */
  public String getPassportIdByUserId(long userId);

  /**
   * 根据passportId查询对应的主键Id
   */
  public long getUserIdByPassportId(String passportId);

  /**
   * 根据passportId删除用户的Account信息，内部调试接口使用
   */
  public void deleteAccountByPassportId(String passportId);

  /**
   * 修改用户信息
   */
  public int updateAccount(Account account);

}
