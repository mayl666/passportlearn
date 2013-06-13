package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;

/**
 *
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 下午2:04
 */
public class CheckUserApiParams extends BaseApiParams {

    /**
     * 要检查的用户名
     */
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
