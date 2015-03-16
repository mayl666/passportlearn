package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-14
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public interface QQOpenAPIManager {

    public String getQQFriends(String userid, String tkey, String third_appid) throws Exception;

    public ConnectUserInfoVO getQQUserInfo(String openId, String openKey, ConnectConfig connectConfig);
}
