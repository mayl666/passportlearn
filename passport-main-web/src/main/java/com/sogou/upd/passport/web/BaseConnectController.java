package com.sogou.upd.passport.web;

import java.util.Date;

import com.sogou.upd.passport.model.account.AccountConnect;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class BaseConnectController extends BaseController {

	protected AccountConnect buildAccountConnect(long userId, int clientId, int accountType, int accountRelation,
			String connectUid, String accessToken, long expiresIn, String refreshToken) {
		AccountConnect connect = new AccountConnect();
		connect.setUserId(userId);
		connect.setClientId(clientId);
		connect.setAccountType(accountType);
		connect.setAccountRelation(accountRelation);
		connect.setConnectUid(connectUid);
		connect.setConnectAccessToken(accessToken);
		connect.setConnectExpiresIn(expiresIn);
		connect.setConnectRefreshToken(refreshToken);
		connect.setCreateTime(new Date());
		return connect;
	}
}
