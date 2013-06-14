package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: chenjiameng Date: 13-6-11 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class WebAddProblemParameters {
//    @NotBlank(message = "client_id不允许为空!")
//    @Min(0)
    private String clientId= "1100";
//    @NotBlank(message = "passportId不允许为空!")
    private String passportId;
    @NotBlank(message = "请选择反馈类型!")
    private int typeId;
    @NotBlank(message = "反馈标题不允许为空!")
    private String titile;
    @NotBlank(message = "反馈内容不允许为空!")
    private String content;
    @NotBlank(message = "email不允许为空!")
    private String email;

    @NotBlank(message = "验证码不允许为空!")
    private String captcha;//验证码
    @NotBlank(message = "标识码不允许为空!")
    private String token;//标识码

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
