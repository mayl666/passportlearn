package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.ReturnGeneratedKeys;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountDAO {

  /**
   * 根据passportId获取Account
   */
  @SQL("select * from account where passport_id=:passport_id")
  public Account getAccountByPassportId(@SQLParam("passport_id") String passport_id);

  /**
   * 根据手机号码获取Account
   */
  @SQL("select * from account where mobile=:mobile")
  public Account getAccountByMobile(@SQLParam("mobile") String mobile);

  /**
   * 根据userId获取Account
   */
  @SQL("select * from account where id=:id")
  public Account getAccountByUserId(@SQLParam("id") long id);

  /**
   * 根据passportId删除用户的Account信息，内部调试接口使用
   */
  @SQL("delete from account where passport_id=:passport_id")
  public int deleteAccountByPassportId(@SQLParam("passport_id") String passport_id);

  /**
   * 修改用户信息
   */
  @SQL("update account set mobile=:a.mobile,passwd=:a.passwd where id=:a.id")
  public int updateAccount(@SQLParam("a") Account account);

  /**
   * 验证合法，用户注册
   */
  @ReturnGeneratedKeys
  @SQL(
      "insert into account(passport_id,passwd,mobile,reg_time,reg_ip,status,version,account_type) "
      + "values (:a.passportId,:a.passwd,:a.mobile,:a.regTime,:a.regIp,:a.status,:a.version,"
      + ":a.accountType)")
  public int insertAccount(@SQLParam("a") Account account);

}
