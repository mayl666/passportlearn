package com.sogou.upd.passport.manager.proxy.account.form;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Set;

/**
 * 获取用户安全相关的信息
 * 返回
 * bindmail 绑定邮箱
 * bindmobile 绑定手机号
 * question 密保问题
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-8
 * Time: 下午2:22
 */
public class GetUserSecureInfoApiParams {

    @NotBlank(message = "用户id不能为空")
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
