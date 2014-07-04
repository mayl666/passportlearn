package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-4
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
public interface WapResetPwdManager {

    public Result sendFindPwdMobileCode(String mobile, String clientId) throws Exception;
}
