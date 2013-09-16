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
public class PCOAuth2UpdateNickParams {

    @NotBlank(message = "昵称不能为空")
    private String nick;

    private String sname = "";  //账号，sohu+继承而来，这里不做处理

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
