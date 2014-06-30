package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.SendCaptchaApiParams;
import com.sogou.upd.passport.model.account.Account;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindApiManager {

    /**
     * 绑定邮箱接口
     * @param bindEmailApiParams
     * @return
     */
    Result bindEmail(BindEmailApiParams bindEmailApiParams);

    /**
     * 查询手机号绑定的账号
     * @param baseMoblieApiParams
     * @return
     */
    Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams);

    /**
     * 首次绑定密保手机
     * @param passportId
     * @param newMobile
     * @return
     */
    public Result bindMobile(String passportId,String newMobile);

    /**
     * 修改绑定密保手机
     * @param passportId
     * @param newMobile
     * @return
     */
    public Result modifyBindMobile(String passportId, String newMobile);

    /**
     * 直接解除密保手机绑定
     * @param mobile
     * @return
     */
    public Result unBindMobile(String mobile);
}
