package com.sogou.upd.passport.manager.api.connect.form.user;

import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;

import java.util.Date;

/**
 * 用户信息类
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:02
 * To change this template use File | Settings | File Templates.
 */
public class UserOpenApiParams extends BaseOpenApiParams {
    private String accessToken;
    private Date updateTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
