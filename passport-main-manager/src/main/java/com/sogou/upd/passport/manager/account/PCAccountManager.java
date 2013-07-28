package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public interface PCAccountManager {
    public Result authToken(PcAuthTokenParams authPcTokenParams);
}
