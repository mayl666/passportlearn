package com.sogou.upd.passport.web.inteceptor;

/**
 *
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:37
 */
public interface HostHolder {

    /**
     * 判断是否已经登陆
     * @return
     */
    boolean isLogin();

    /**
     * 设置登陆用户的passport_id
     * @param passportId
     */
    void setPassportId(String passportId);

    /**
     * 获取登陆用户的passport_id
     * @return
     */
    String getPassportId();

    /**
     * 设置昵称
     * @param nickName
     */
    void setNickName(String nickName);
}
