package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.MobileRegParams;

/**
 * 注册管理 User: mayan Date: 13-4-15 Time: 下午4:43 To change this template use File | Settings | File
 * Templates.
 */
public interface AccountRegManager {

    /**
     * 手机用户正式注册接口
     *
     * @param regParams 参数封装的对象
     * @return Result格式的返回值，提示注册成功信息
     */
    public Result mobileRegister(MobileRegParams regParams, String ip);

}
