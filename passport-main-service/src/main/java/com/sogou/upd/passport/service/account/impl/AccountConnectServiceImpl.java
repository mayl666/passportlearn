package com.sogou.upd.passport.service.account.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sogou.upd.passport.dao.account.AccountConnectMapper;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.service.account.AccountConnectService;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午8:08
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountConnectServiceImpl implements AccountConnectService {

	@Inject
	private AccountConnectMapper accountConnectMapper;

	@Override
	public List<AccountConnect> listAccountConnectByQuery(AccountConnectQuery query) {

		return accountConnectMapper.getAccountConnectByQuery(query);
	}

	@Override
	public boolean initialAccountConnect(AccountConnect accountConnect) {
		int id = accountConnectMapper.insertAccountConnect(accountConnect);
		if (id != 0) { return true; }
		return false;
	}

	@Override
	public boolean updateAccountConnect(AccountConnect accountConnect) {
		int row = accountConnectMapper.updateAccountConnect(accountConnect);
		if (row != 0) { return true; }
		return false;
	}

}
