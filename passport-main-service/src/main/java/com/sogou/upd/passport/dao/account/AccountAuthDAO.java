package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountAuth;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午10:46 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountAuthDAO {

  /**
   * 根据refresh_token获取AccountAuth信息 todo 可以和getUserAuthByUserId()合并，缓存怎么存？分表怎么查？
   */
  @SQL("select * from account_auth where refresh_token=:refresh_token")
  public AccountAuth getAccountAuthByRefreshToken(@SQLParam("refresh_token") String refresh_token);

  /**
   * 根据access_token获取AccountAuth信息 todo 可以和getUserAuthByUserId()合并，缓存怎么存？分表怎么查？
   */
  @SQL("select * from account_auth where access_token=:access_token")
  public AccountAuth getAccountAuthByAccessToken(@SQLParam("access_token") String access_token);

  /**
   * 根据userid删除AccountAuth信息，内部调试接口使用
   */
  @SQL("delete from account_auth where user_id=:user_id")
  public int deleteAccountAuthByUserId(@SQLParam("user_id") long user_id);

  /**
   * 往用户状态表中插入一条记录
   */
  @SQL(
      "insert into account_auth(user_id,access_token,refresh_token,access_valid_time,refresh_valid_time,"
      + "client_id,instance_id) values (:a.userId,:a.accessToken,:a.refreshToken,:a.accessValidTime,"
      + ":a.refreshValidTime,:a.clientId,:a.instanceId)")
  public int insertAccountAuth(@SQLParam("a") AccountAuth accountAuth);

  /**
   * 往用户状态表中插入一条记录
   * 有多个唯一索引时不要使用，存在则更新，不存在则插入
   */
  @SQL("insert into account_auth(user_id,access_token,refresh_token,access_valid_time,refresh_valid_time,client_id,"
       + "instance_id) values (:a.userId,:a.accessToken,:a.refreshToken,:a.accessValidTime,:a.refreshValidTime,"
       + ":a.clientId,:a.instanceId) "
       + "ON DUPLICATE KEY UPDATE "
       + "access_token=:a.accessToken,refresh_token=:a.refreshToken,access_valid_time=:a.accessValidTime,refresh_valid_time=:a.refreshValidTime")
  public int saveAccountAuth(@SQLParam("a") AccountAuth accountAuth);

  /**
   * 更新用户状态表
   */
  @SQL("update account_auth set #if(:a.accessToken != null){access_token=:a.accessToken,}"
       + "#if(:a.refreshToken != null){refresh_token=:a.refreshToken,}"
       + "#if(:a.accessValidTime != 0){access_valid_time=:a.accessValidTime,}"
       + "#if(:a.refreshValidTime != 0){refresh_valid_time=:a.refreshValidTime} "
       + "where user_id=:a.userId and client_id=:a.clientId and instance_id=:a.instanceId")
  public int updateAccountAuth(@SQLParam("a") AccountAuth accountAuth);

  /**
   * 根据userId查询所有记录，返回list集合
   */
  @SQL("select * from account_auth  where user_id=:user_id and client_id=:client_id")
  public List<AccountAuth> getAccountAuthListById(@SQLParam("user_id") long user_id,
                                                  @SQLParam("client_id") int client_id);

  /**
   * 批量更新某用户对应的状态记录
   */
  @SQL("update account_auth set #if(:a.accessToken != null){access_token=:a.accessToken,}"
       + "#if(:a.refreshToken != null){refresh_token=:a.refreshToken,}"
       + "#if(:a.accessValidTime != 0){access_valid_time=:a.accessValidTime,}"
       + "#if(:a.refreshValidTime != 0){refresh_valid_time=:a.refreshValidTime} "
       + "where user_id=:a.userId and client_id=:a.clientId and instance_id=:a.instanceId")
  public int[] batchUpdateAccountAuth(@SQLParam("a") List<AccountAuth> list);

}
