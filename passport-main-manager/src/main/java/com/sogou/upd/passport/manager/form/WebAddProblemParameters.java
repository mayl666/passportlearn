package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: chenjiameng Date: 13-6-11 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebAddProblemParameters {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;
    @NotBlank(message = "passportId不允许为空!")
    private String passportId;
    @NotBlank(message = "请选择反馈类型!")
    private int typeId;
    @NotBlank(message = "反馈内容不允许为空!")
    private String content;
    @NotBlank(message = "qq内容不允许为空!")
    private String qq;

    @NotBlank(message = "验证码不允许为空!")
    private String captcha;//验证码
    @NotBlank(message = "标识码不允许为空!")
    private String token;//标识码

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
}
