package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

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

    /**
     * 检查移动端退出接口签名
     * MD5(sgid+client_id+instance_id+ client_secret)
     * @return
     */
    public Result checkMappLogoutCode(String sgid, String client_id, String instance_id, String actualCode);

    /**
     * 检查移动端接口签名
     * 目前用到的有数据统计、动态配置
     * @param uniqSign 唯一标识，例如:udid、imei等
     * @param clientId
     * @param ct unix时间戳
     * @return
     */
    public boolean checkMappCode(String uniqSign, int clientId, long ct, String actualCode);
}
