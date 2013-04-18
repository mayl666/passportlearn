package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.dao.account.AccountConnectDAO;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.service.account.AccountConnectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-24 Time: 下午8:08 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class AccountConnectServiceImpl implements AccountConnectService {

  @Autowired
  private AccountConnectDAO accountConnectDAO;

  @Override
  public List<AccountConnect> listAccountConnectByQuery(AccountConnectQuery query) {
    return accountConnectDAO.getAccountConnectListByQuery(query);
  }

  @Override
  public boolean initialAccountConnect(AccountConnect accountConnect) {
    int id = accountConnectDAO.insertAccountConnect(accountConnect);
    return id != 0;
  }

  @Override
  public boolean updateAccountConnect(AccountConnect accountConnect) {
    int row = accountConnectDAO.updateAccountConnect(accountConnect);
    return row != 0;
  }

  @Override
  public String getUidByUserId(long userId) {
    String Uid = null;
    if (userId != 0) {
      Uid = accountConnectDAO.getUidByUserId(userId);
    }
    return Uid == null ? null : Uid;
  }
}
