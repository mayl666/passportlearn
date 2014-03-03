package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;

/**
 * 用户类第三方开放平台接口代理
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
public interface UserOpenApiManager {

    public Result getUserInfo(UserOpenApiParams userOpenApiParams);


}
