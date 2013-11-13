package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;

/**
 * wap登录
 * User: chenjiameng
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface WapLoginManager {
    /**
     * wap端校验用户名和密码
     * @param parameters
     * @param ip
     * @return
     */
    public Result accountLogin(WapLoginParams parameters, String ip);

    /**
     * wap端校验token，验证成功则返回passportId
     * @param token
     * @return
     */
    public Result authtoken(String token);
}
