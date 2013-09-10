package com.sogou.upd.passport.manager.api.connect.form.info;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.stereotype.Component;

import javax.validation.constraints.AssertTrue;

/**
 * 信息类之发图片微博参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-26
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public class InfoOpenApiParams extends BaseOpenApiParams {
    private String user_ip;  //用户ip
    @NotBlank(message = "message(消息内容)不允许为空")
    private String message;
    @NotBlank(message = "图片的URL地址不允许为空")
    @URL
    private String url;
    private String title;       //选填，人人网，QQ账号为必填项
    @URL
    private String share_url;   //选填，人人网，QQ账号为必填项

    @AssertTrue(message = "标题或share_url不允许为空")
    private boolean isCheckTitleAndUrl() {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(openid);
        //人人网和QQ空间的title和url为必填项
        if (accountTypeEnum == AccountTypeEnum.QQ || accountTypeEnum == AccountTypeEnum.RENREN) {
            if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(share_url)) {
                return false;
            }
        }
        return true;
    }

    @AssertTrue(message = "url格式不正确")
    private boolean isCheckUrl(){
        if(!url.matches("[a-zA-z]+://[^\\s]*")){
            return false;
        }
        if(!Strings.isNullOrEmpty(share_url)){
            if(!share_url.matches("[a-zA-z]+://[^\\s]*")){
                return false;
            }
        }
        return true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }
}
