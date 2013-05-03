package com.sogou.upd.passport.manager.form;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ConnectDisplay;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-25
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectLoginParams {

    @NotNull(message = "provider不能为空")
    private String p; // provider
    @Min(value = 1, message = "appid不能为空")
    private int client_id; // 应用id
    @NotNull(message = "ru不能为空")
    private String ru;  // 回调地址

    private String display;  // 样式
    private boolean force = true;   // 是否强制输入用户名、密码登录
    private String type;     // 应用类型

    public static final List<String> SUPPORT_PROVIDER_LIST = Lists.newArrayList(); // passport支持的第三方列表

    static {
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.QQ.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.SINA.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.RENREN.toString());
    }

    @AssertTrue(message = "不支持的第三方")
    private boolean isSupportProvider() {
        if (this.p != null && !SUPPORT_PROVIDER_LIST.contains(this.p)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "不支持的display")
    private boolean isSupportDisplay() {
        if (display != null && !ConnectDisplay.isSupportDisplay(display, this.p)) {
            return false;
        }
        return true;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
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

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
