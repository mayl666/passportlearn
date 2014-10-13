package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 上午10:51 To change this template use
 * File | Settings | File Templates.
 * <p/>
 * 安全限制、检查验证相关
 */
public interface CheckManager {

    /**
     * 检测页面随机验证码
     *
     * @param captcha
     * @param token
     * @return
     * @throws Exception
     */
    public boolean checkCaptcha(String captcha, String token);

    /**
     * 检查token
     *
     * @param scode
     * @param id
     * @return
     * @throws Exception
     */
    public boolean checkScode(String scode, String id);
}
