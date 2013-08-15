package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseApiParams;

/**
 * 信息类第三方开放平台接口代理
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-29
 * Time: 下午12:21
 * To change this template use File | Settings | File Templates.
 */
public interface InfoOpenApiManager {

    public Result addUserShareOrPic(BaseApiParams baseApiParams);

}
