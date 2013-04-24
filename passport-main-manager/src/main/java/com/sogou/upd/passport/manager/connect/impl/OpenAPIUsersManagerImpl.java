package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.connect.OpenAPIUsersManager;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:59
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OpenAPIUsersManagerImpl implements OpenAPIUsersManager {

    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public Result getOpenIdByPassportId(String passportId, int clientId, int provider) {
        String appKey = connectConfigService.querySpecifyAppKey(clientId, provider);
        if (appKey == null) {
            Result.buildError(ErrorUtil.UNSUPPORT_THIRDPARTY);
        }
        String openid = connectTokenService.querySpecifyOpenId(passportId, provider, appKey);
        if (Strings.isNullOrEmpty(openid)) {
            return Result.buildError(ErrorUtil.ERR_CODE_CONNECT_OBTAIN_OPENID_ERROR);
        } else {
            return Result.buildSuccess("查询成功", "openid", openid);
        }

    }
}
