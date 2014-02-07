package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-29
 * Time: 上午12:53
 * To change this template use File | Settings | File Templates.
 */
public class PcBaseParams {

    @NotBlank(message = "userid不允许为空")
    private String userid;   //登录账号
    @Min(0)
    @NotBlank(message = "appid不允许为空")
    private String appid;   //产品在passport申请的id，为四位数字

    private String ts = "";  //客户端的实例id

    public String getUserid() {
        String internalUsername = AccountDomainEnum.getInternalCase(userid);
        setUserid(internalUsername);
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
