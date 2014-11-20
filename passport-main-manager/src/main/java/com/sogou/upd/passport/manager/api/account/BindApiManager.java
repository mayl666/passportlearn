package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindApiManager {

    /**
     * 绑定邮箱接口
     *
     * @return
     */
    Result bindEmail(String passportId, int clientId, String password, String newEmail, String oldEmail, String ru);

    /**
     * 首次绑定密保手机
     *
     * @param passportId
     * @param newMobile
     * @return
     */
    public Result bindMobile(String passportId, String newMobile, Account account);

    /**
     * 修改绑定密保手机
     *
     * @param passportId
     * @param newMobile
     * @return
     */
    public Result modifyBindMobile(String passportId, String newMobile);

}
