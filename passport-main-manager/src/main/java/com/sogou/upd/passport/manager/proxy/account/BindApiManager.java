package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileApiParams;
import com.sogou.upd.passport.common.result.Result;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindApiManager {

    /**
     * 绑定手机接口代理
     * @param bindMobileApiParams
     * @return
     */
    Result bindMobile(BindMobileApiParams bindMobileApiParams);

    /**
     * 解绑手机接口代理
     * @param baseMoblieApiParams
     * @return
     */
    Result unbindMobile(BaseMoblieApiParams baseMoblieApiParams);

    /**
     * 绑定邮箱接口
     * @param bindEmailApiParams
     * @return
     */
    Result bindEmail(BindEmailApiParams bindEmailApiParams);

    /**
     * 查询手机号绑定的账号
     * @param mobileBindPassportIdApiParams
     * @return
     */
    Result getPassportIdFromMobile(MobileBindPassportIdApiParams mobileBindPassportIdApiParams);

    /**
     * 查询手机号绑定的账号；
     * @param baseMoblieApiParams
     * @return
     */
    Result queryPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams);
}
