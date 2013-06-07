package com.sogou.upd.passport.manager.proxy.account;

import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileProxyParams;
import com.sogou.upd.passport.manager.proxy.account.form.UnBindMobileProxyParams;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public interface BindProxyManager  {

    /**
     * 绑定手机接口代理
     * @param bindMobileProxyParams
     * @return
     */
    Map<String,Object> bindMobile(BindMobileProxyParams bindMobileProxyParams);

    /**
     * 解绑手机接口代理
     * @param unBindMobileProxyParams
     * @return
     */
    Map<String, Object> unbindMobile(UnBindMobileProxyParams unBindMobileProxyParams);

    /**
     * 绑定邮箱接口
     * @param bindEmailApiParams
     * @return
     */
    Map<String, Object> bindEmail(BindEmailApiParams bindEmailApiParams);
}
