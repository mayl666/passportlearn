package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-18 Time: 下午3:34 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface AccountConnectDAO {

  /**
   * 根据query查询AccountConnect
   *
   * @param query
   * @return
   */
  @SQL("select * from account_connect where #if(:q.connectUid != null){connect_uid=:q.connectUid}"
       + "#if(:q.connectUid != null){ and } "
       + "#if(:q.accountType > 0){account_type=:q.accountType}")
  public List<AccountConnect> getAccountConnectListByQuery(@SQLParam("q") AccountConnectQuery query);

  /**
   * 查询主账号的绑定列表和副账号是否已经注册或绑定过
   *
   * @param query
   * @return
   */
  @SQL("select * from account_connect where "
       + "(user_id=:q.userId and account_type=:q.accountType and client_id=:q.clientId) or "
       + "(connect_uid=:q.connectUid and account_type=:q.accountType and client_id=:q.clientId)")
  public List<AccountConnect> findBindConnectByQuery(@SQLParam("q") AccountConnectQuery query);

  /**
   * 更新AccountConnect
   *
   * @param accountConnect
   * @return
   */
  @SQL("update account_connect set "
       + "#if(:a.connectRefreshToken != null){connect_refresh_token=:a.connectRefreshToken,} "
       + "#if(:a.accountRelation >= 0){account_relation=:a.accountRelation,}"
       + "#if(:a.connectExpiresIn > 0){connect_expires_in=:a.connectExpiresIn,}"
       + "#if(:a.connectAccessToken != null){connect_access_token=:a.connectAccessToken} "
       + "where user_id=:a.userId and connect_uid=:a.connectUid and account_type=:a.accountType and client_id=:a.clientId")
  public int updateAccountConnect(@SQLParam("a") AccountConnect accountConnect);

  /**
   * 插入一条新记录
   *
   * @param accountConnect
   * @return
   */
  @SQL("insert into account_connect(user_id,client_id,account_relation,account_type,connect_uid,connect_access_token,"
       + "connect_expires_in,connect_refresh_token,create_time) values(:a.userId,:a.clientId,:a.accountRelation,"
       + ":a.accountType,:a.connectUid,:a.connectAccessToken,:a.connectExpiresIn,:a.connectRefreshToken,:a.createTime)")
  public int insertAccountConnect(@SQLParam("a") AccountConnect accountConnect);

  /**
   * 删除一条记录，（Unit Test使用）
   * @param user_id
   * @return
   */
  @SQL("delete from account_connect where user_id=:user_id")
  public int deleteAccountConnectByUserId(@SQLParam("user_id") long user_id);

  /**
   * 根据userId获取Uid todo mapper里暂没添加相应的查询方法
   *
   * @param userId
   * @return
   */
  public String getUidByUserId(long userId);

}
