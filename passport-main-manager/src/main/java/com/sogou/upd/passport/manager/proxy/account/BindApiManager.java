package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileBindPassportIdApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;
import com.sogou.upd.passport.common.result.Result;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindApiManager {

    /**
     * 绑定手机接口代理
     * @param bindMobileProxyParams
     * @return
     */
    Result bindMobile(BindMobileProxyParams bindMobileProxyParams);

    /**
     * 解绑手机接口代理
     * @param unBindMobileProxyParams
     * @return
     */
    Result unbindMobile(UnBindMobileProxyParams unBindMobileProxyParams);

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
}
