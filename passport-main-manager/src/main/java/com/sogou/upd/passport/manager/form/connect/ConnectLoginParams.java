package com.sogou.upd.passport.manager.form.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.oauth2.common.types.ConnectDisplay;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-25
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectLoginParams {

    @NotBlank(message = "provider不能为空")
    private String provider; // provider

    @Min(0)
    private String client_id; // 应用id
    @Min(0)
    private String appid; // 浏览器和输入法会传1044，这个没让搜狐转发时把参数名改了，搜狗做兼容

    private String ru = "https://account.sogou.com";  // 回调地址

    private String display;  // 样式
    private boolean forcelogin = true;   // 是否强制输入用户名、密码登录
    private String type = "web";     // 应用类型
    private String from = ""; //浏览器移动端，type=token时，from=mob；样式均为移动端上的样式
    private String ts;

    @AssertTrue(message = "Client_id不允许为空")
    private boolean isEmptyClientId(){
        return !Strings.isNullOrEmpty(appid) || !Strings.isNullOrEmpty(client_id);
    }

    @AssertTrue(message = "不支持的第三方")
    private boolean isSupportProvider() {
        if (Strings.isNullOrEmpty(this.provider)) {
            return true;
        }
        if (this.provider != null && !CommonConstant.SUPPORT_PROVIDER_LIST.contains(this.provider)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "不支持的display")
    private boolean isSupportDisplay() {
        if (display != null && !ConnectDisplay.isSupportDisplay(display, this.provider)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "不支持的type")
    private boolean isSupportType() {
        if (type != null && !ConnectTypeEnum.isSupportType(type)) {
            return false;
        }
        return true;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isForcelogin() {
        return forcelogin;
    }

    public void setForcelogin(boolean forcelogin) {
        this.forcelogin = forcelogin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
