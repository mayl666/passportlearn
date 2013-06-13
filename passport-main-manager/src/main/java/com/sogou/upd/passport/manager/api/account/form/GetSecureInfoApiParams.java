package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

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
public class GetSecureInfoApiParams extends BaseUserApiParams {

    @NotBlank(message = "需要返回的字段fields不能为空")
    private String fields;

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
