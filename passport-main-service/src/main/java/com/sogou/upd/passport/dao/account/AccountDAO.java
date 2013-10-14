package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountDAO {

  /**
   * 对应数据库表名称
   */
  String TABLE_NAME = " account ";

  /**
   * 所有字段列表
   */
  String
      ALL_FIELD =
      " id, passport_id, passwd, mobile, reg_time, reg_ip, status, version, account_type, image, nickname  ";

  /**
   * 值列表
   */
  String
      VALUE_FIELD =
      " :account.id, :account.passportId, :account.passwd, :account.mobile, :account.regTime, :account.regIp, :account.status, :account.version, :account.accountType, :account.image, :account.nickname ";

  /**
   * 修改字段列表
   */
  String
      UPDATE_FIELD =
      " passport_id = :account.passportId, passwd = :account.passwd, mobile = :account.mobile, reg_time = :account.regTime, reg_ip = :account.regIp, status = :account.status, version = :account.version, account_type = :account.accountType , image=:account.image, nickname=:account.nickname ";

  /**
   * 根据passportId获取Account
   */
  @SQL("select" +
          ALL_FIELD +
          "from" +
          TABLE_NAME +
          " where passport_id=:passport_id")
  public Account getAccountByPassportId(@SQLParam("passport_id") String passport_id) throws
                                                                                     DataAccessException;

  /**
   * 根据passportId删除用户的Account信息， 内部调试接口使用
   */
  @SQL("delete from" +
          TABLE_NAME +
          " where passport_id=:passport_id")
  public int deleteAccountByPassportId(@SQLParam("passport_id") String passport_id) throws
                                                                                    DataAccessException;

  /**
   * 修改用户信息
   */
  @SQL("update " +
          TABLE_NAME +
          " set passwd=:passwd where passport_id=:passport_id")
  public int updatePassword(@SQLParam("passwd") String passwd,
                            @SQLParam("passport_id") String passport_id) throws
                                                                         DataAccessException;

  /**
   * 修改绑定手机
   */
  @SQL("update " +
          TABLE_NAME +
          " set mobile=:mobile where passport_id=:passport_id")
  public int updateMobile(@SQLParam("mobile") String mobile,
                          @SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 修改昵称
     */
    @SQL("update " +
            TABLE_NAME +
            " set nickname=:nickname where passport_id=:passport_id")
    public int updateNickName(@SQLParam("nickname") String nickname,
                            @SQLParam("passport_id") String passport_id) throws DataAccessException;

  /**
   * 封禁或解禁用户
   */
  @SQL("update " +
       TABLE_NAME +
       " set status=:status where passport_id=:passport_id")
  public int updateState(@SQLParam("status") int status,
                          @SQLParam("passport_id") String passport_id) throws DataAccessException;

    /**
     * 修改或设置头像
     */
    @SQL("update " +
            TABLE_NAME +
            " set image=:image where passport_id=:passport_id")
    public int updateImage(@SQLParam("image") String image,
                           @SQLParam("passport_id") String passport_id) throws DataAccessException;


    /**
   * 验证合法，用户注册
   */
  @SQL(
      "insert into " +
              TABLE_NAME +
              "(passport_id,passwd,mobile,reg_time,reg_ip,status,version,account_type,nickname,image) "
      + "values (:passport_id,:account.passwd,:account.mobile,:account.regTime,:account.regIp,:account.status,:account.version,"
      + ":account.accountType,:account.nickname,:account.image)")
  public int insertAccount(@SQLParam("passport_id") String passport_id,
                           @SQLParam("account") Account account) throws DataAccessException;

}
