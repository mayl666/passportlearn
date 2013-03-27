package com.sogou.upd.passport.web;

import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.connect.OAuthToken;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class BaseConnectController extends BaseController {

    protected AccountConnect buildAccountConnect(long userId, int clientId, int provider, int accountRelation, OAuthToken authToken) {
        AccountConnect connect = new AccountConnect();
        connect.setUserId(userId);
        connect.setClientId(clientId);
        connect.setProvider(provider);
        connect.setAccountRelation(accountRelation);
        connect.setConnectUid(authToken.getConnectUid());
        connect.setConnectAccessToken(authToken.getAccessToken());
        connect.setConnectExpireIn(authToken.getExpiresIn());
        connect.setConnectRefreshToken(authToken.getRefreshToken());
        connect.setCreate_time(new Date());
        return connect;
    }
}
