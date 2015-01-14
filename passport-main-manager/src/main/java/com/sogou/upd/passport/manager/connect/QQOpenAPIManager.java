package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-14
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public interface QQOpenAPIManager {

    public String get_qqfriends(String userid, String tkey, String third_appid) throws Exception;
}
