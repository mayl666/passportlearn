package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午5:41
 * To change this template use File | Settings | File Templates.
 */
public interface MappSSOManager {
    public Result checkAppPackageSign(int clientId, long ct, String packageSign, String udid);

}
