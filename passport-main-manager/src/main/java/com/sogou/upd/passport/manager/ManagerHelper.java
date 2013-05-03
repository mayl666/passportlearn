package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class ManagerHelper {

    /**
     * 创建一个第三方账户对象
     */
    public static ConnectToken buildConnectToken(String passportId, int provider, String appKey, String openid, String accessToken, long expiresIn, String refreshToken) {
        ConnectToken connect = new ConnectToken();
        connect.setPassportId(passportId);
        connect.setProvider(provider);
        connect.setAppKey(appKey);
        connect.setOpenid(openid);
        connect.setAccessToken(accessToken);
        connect.setExpiresIn(expiresIn);
        connect.setRefreshToken(refreshToken);
        connect.setCreateTime(new Date());
        return connect;
    }

    /**
     * 创建一个第三方关系关系（反查表）对象
     */
    public static ConnectRelation buildConnectRelation(String openid, int provider, String passportId, String appKey) {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setOpenid(openid);
        connectRelation.setProvider(provider);
        connectRelation.setPassportId(passportId);
        connectRelation.setAppKey(appKey);
        return connectRelation;
    }
}
