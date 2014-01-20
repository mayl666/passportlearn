package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.CommonConstant;
import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu+浏览器个人中心页
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2IndexParams extends PCOAuth2BaseParams {
    @NotBlank(message = "accesstoken illegal")
    private String accesstoken;   //获取的访问token

    /**
     * 默认为getuserinfo
     * type=getuserinfo--跳转到个人中心页面
     * type=avatarurl--跳转到修改头像页面
     * type=password--跳转到修改密码页面
     */
    private String type = CommonConstant.PC_REDIRECT_GETUSERINFO;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
