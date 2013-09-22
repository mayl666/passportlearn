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
public class PCOAuth2IndexParams extends PCOAuth2BaseParams{
    @NotBlank(message = "accesstoken illegal")
    private String accesstoken;   //获取的访问token

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
