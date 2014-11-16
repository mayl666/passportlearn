package com.sogou.upd.passport.manager.api.connect.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class BaseOpenApiParams extends BaseApiParams {

    @NotBlank(message = "userid不允许为空")
    protected String userid; //通行证账号，如果用于绑定则是主账号
//    @NotBlank(message = "openid不允许为空")
    protected String openid; //通行证账号，***@provider.sohu.com
    protected Object params;//调用各个api所需的参数
    protected String third_appid;  //如果应用使用独立appid，需要传入不同第三方对应的appid; 如果不传，表示使用passport的appid；

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }
}
