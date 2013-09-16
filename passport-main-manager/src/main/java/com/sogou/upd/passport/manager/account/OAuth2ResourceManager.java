package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public interface OAuth2ResourceManager {

    /**
     * 获取cookie值
     * @param params
     * @return
     */
    public Result getCookieValue(PCOAuth2ResourceParams params);

    /**
     * 获取完整的个人信息
     * @param params
     * @return
     */
    public Result getFullUserInfo(PCOAuth2ResourceParams params);

}
