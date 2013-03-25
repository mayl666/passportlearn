package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.model.connect.OAuthToken;
import com.sogou.upd.passport.service.account.AccountConnectService;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午8:08
 * To change this template use File | Settings | File Templates.
 */
public class AccountConnectServiceImpl implements AccountConnectService{
    @Override
    public List<AccountConnect> listAccountConnectByQuery(AccountConnectQuery query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean initialAccountConnect(AccountConnect accountConnect) {
        // TODO add dao implement，return userid
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updateAccountConnect(AccountConnect accountConnect) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
