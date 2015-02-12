package com.sogou.upd.passport.manager.form.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Domain;
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
    @URL
    @Ru
    private String ru = "https://account.sogou.com";  // 回调地址

    private String display;  // 样式
    private boolean forcelogin = true;   // 是否强制输入用户名;、密码登录
    private String type = "web";     // 应用类型；
    private String from = ""; //浏览器移动端，type=token时，from=mob；样式均为移动端上的样式；单点登录时，type=mapp,from=sso,返回sgid和用户信息；
    private String ts;   //终端的实例ID

    private String viewPage; // qq为搜狗产品定制化页面， sgIME为输入法PC端弹泡样式
    @Domain
    private String domain;   // 非sogou.com域名的业务线使用，登录成功后种非sogou.com域的cookie
    private String thirdInfo = "";   // thirdInfo=0或1；0表示去搜狗通行证个人信息，1表示获取第三方个人信息
    private String third_appid; //如果应用使用独立appid，则传入第三方appid值，并且根据不同的第三方传递不同的appid； 默认使用passport的appid；

    private String v; //浏览器根据v判断显示新旧UI
    /**以下三个参数用于修改qq登陆自动计时功能
     *  User: Zhangbangcheng
     */
    private String format;//
    private String container;//承载登录框iframe的容器。这里头iframe的高度会自动调整改变，默认400x362

    @AssertTrue(message = "Client_id不允许为空")
    private boolean isEmptyClientId() {
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
        if (Strings.isNullOrEmpty(display)) {
            if (ConnectTypeEnum.WAP.toString().equals(getType())) {
                this.display = "mobile";
            } else {
                this.display = "page";
            }
        } else {
            this.display = display;
        }
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

    public String getViewPage() {
        return viewPage;
    }

    public void setViewPage(String viewPage) {
        this.viewPage = viewPage;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getThirdInfo() {
        return thirdInfo;
    }

    public void setThirdInfo(String thirdInfo) {
        this.thirdInfo = thirdInfo;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }
}
