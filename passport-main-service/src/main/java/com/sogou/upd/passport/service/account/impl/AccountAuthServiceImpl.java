package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.dao.account.AccountAuthDAO;
import com.sogou.upd.passport.dao.account.AccountConnectDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-29 Time: 上午1:20 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class AccountAuthServiceImpl implements AccountAuthService {

  @Autowired
  private AppConfigService appConfigService;
  @Autowired
  private AccountAuthDAO accountAuthDAO;
  @Autowired
  private AccountDAO accountDAO;
  @Autowired
  private AccountConnectDAO accountConnectDAO;
  @Autowired
  private TaskExecutor taskExecutor;

  @Override
  public AccountAuth verifyRefreshToken(String refreshToken, String instanceId) {
    // TODO 加缓存
    if (!Strings.isNullOrEmpty(refreshToken)) {
      AccountAuth accountAuth = accountAuthDAO.getAccountAuthByRefreshToken(refreshToken);
      if (isValidRefreshToken(accountAuth, instanceId)) {
        return accountAuth;
      }
    }
    return null;
  }

  @Override
  public AccountAuth verifyAccessToken(String accessToken) {
    // TODO 加缓存
    if (!Strings.isNullOrEmpty(accessToken)) {
      AccountAuth accountAuth = accountAuthDAO.getAccountAuthByAccessToken(accessToken);
      if (accountAuth != null && accountAuth.getAccessValidTime() > System.currentTimeMillis()) {
        return accountAuth;
      }
    }
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isAbleBind(long userId, String connectUid, int accountType, int clientId) {
    Account account = accountDAO.getAccountByUserId(userId);
    if (account == null || !account.isNormalAccount()) {  // 账号是否存在且正常
      return false;
    }
    if (account.getAccountType() == accountType) { // 与主账号同一类型的账号不允许被绑定
      return false;
    }
    AccountConnectQuery query = new AccountConnectQuery(connectUid, accountType, clientId, userId);
    List<AccountConnect> accountConnect = accountConnectDAO.findBindConnectByQuery(query);
    if (!accountConnect.isEmpty()) {
      return false;
    }
    return true;
  }

  @Override
  public AccountAuth initialAccountAuth(long userId, String passportId, int clientId,
                                        String instanceId) throws SystemException {
    AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId, instanceId);
    long id = accountAuthDAO.insertAccountAuth(accountAuth);
    if (id != 0) {
      return accountAuth;
    }
    return null;
  }

  @Override
  public AccountAuth updateAccountAuth(long userId, String passportId, int clientId,
                                       String instanceId) throws Exception {
    AccountAuth accountAuth = newAccountAuth(userId, passportId, clientId, instanceId);
    if (accountAuth != null) {
      int accountRow = accountAuthDAO.saveAccountAuth(accountAuth);
      return accountRow == 0 ? null : accountAuth;
    }
    return null;
  }

  @Override
  public int deleteAccountAuthByUserId(long user_id) {
    int row = accountAuthDAO.deleteAccountAuthByUserId(user_id);
    return row;
  }

  /**
   * 异步生成某用户的除当前客户端外的其它客户端的用户状态信息
   */
  @Override
  public void asynUpdateAccountAuthBySql(final String mobile, final int clientId,
                                         final String instanceId) throws SystemException {
    taskExecutor.execute(new Runnable() {
      @Override
      public void run() {
        Account account = null;
        if (mobile != null) {
          //根据手机号查询该用户信息
          account = accountDAO.getAccountByMobile(mobile);
        }
        List<AccountAuth> listNew = new ArrayList<AccountAuth>();
        List<AccountAuth> listResult = null;
        if (account != null) {
          long userId = account.getId();
          //根据该用户的id去auth表里查询用户状态记录，返回list
          listResult = accountAuthDAO.getAccountAuthListById(userId, clientId);
          //过滤掉同步执行过的实例，异步更新剩余实例
          filterCurrentInstance(listResult, instanceId);

          if (listResult != null && listResult.size() > 0) {
            for (AccountAuth aa : listResult) {
              //生成token及对应的auth对象，添加至listNew列表中，批量更新数据库
              AccountAuth accountAuth = null;
              try {
                accountAuth =
                    newAccountAuth(userId, account.getPassportId(), aa.getClientId(),
                                   aa.getInstanceId());
                accountAuth.setId(aa.getId());
              } catch (SystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
              }
              if (accountAuth != null) {
                listNew.add(accountAuth);
              }
            }
          }
        }
        if (listNew != null && listNew.size() > 0) {
          accountAuthDAO.batchUpdateAccountAuth(listNew);
        }
      }
    });
  }

  private void filterCurrentInstance(List<AccountAuth> listResult, String instanceId) {
    for (AccountAuth accountAuth : listResult) {
      if (instanceId.equals(accountAuth.getInstanceId())) {
        listResult.remove(accountAuth);
      }
    }
  }


  /**
   * 验证refresh是否在有效期内，instanceId是否正确
   */
  private boolean isValidRefreshToken(AccountAuth accountAuth, String instanceId) {
    if (accountAuth != null && accountAuth.getRefreshValidTime() > System.currentTimeMillis()
        && instanceId.equals(accountAuth.getInstanceId())) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 构造一个新的AccountAuth
   */
  private AccountAuth newAccountAuth(long userId, String passportID, int clientId,
                                     String instanceId) throws SystemException {

    AppConfig appConfig = appConfigService.getAppConfigByClientId(clientId);
    AccountAuth accountAuth = new AccountAuth();
    if (appConfig != null) {
      int accessTokenExpiresIn = appConfig.getAccessTokenExpiresin();
      int refreshTokenExpiresIn = appConfig.getRefreshTokenExpiresin();

      String accessToken;
      String refreshToken;
      try {
        accessToken =
            TokenGenerator
                .generatorAccessToken(passportID, clientId, accessTokenExpiresIn, instanceId);
        refreshToken = TokenGenerator.generatorRefreshToken(passportID, clientId, instanceId);
      } catch (Exception e) {
        throw new SystemException(e);
      }
      accountAuth.setUserId(userId);
      accountAuth.setClientId(clientId);
      accountAuth.setAccessToken(accessToken);
      accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(accessTokenExpiresIn));
      accountAuth.setRefreshToken(refreshToken);
      accountAuth.setRefreshValidTime(TokenGenerator.generatorVaildTime(refreshTokenExpiresIn));
      accountAuth.setInstanceId(instanceId);
    }

    return accountAuth;
  }


}
