package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;

import java.util.List;

public interface AccountConnectMapper {

  /**
   * 插入一条新记录
   */
  public int insertAccountConnect(AccountConnect accountConnect);

  /**
   * 根据query查询AccountConnect
   */
  public List<AccountConnect> getAccountConnectByQuery(AccountConnectQuery query);

  /**
   * 查询主账号的绑定列表和副账号是否已经注册或绑定过
   */
  public List<AccountConnect> findBindConnectByQuery(AccountConnectQuery query);

  /**
   * 更新AccountConnect
   */
  public int updateAccountConnect(AccountConnect accountConnect);
}
