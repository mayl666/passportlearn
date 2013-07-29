package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseApiParams;

/**
 * 关系类第三方开放平台接口代理
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:26
 * To change this template use File | Settings | File Templates.
 */
public interface FriendsOpenApiManager {

    public Result getUserFriends(BaseApiParams baseApiParams);

}
