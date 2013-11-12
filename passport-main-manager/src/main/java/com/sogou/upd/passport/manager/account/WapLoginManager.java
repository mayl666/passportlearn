package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebLoginParams;

/**
 * wap登录
 * User: chenjiameng
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface WapLoginManager {
    public Result accountLogin(WebLoginParams parameters, String ip);
}
