package com.sogou.upd.passport.web.internal.connect.qq.form;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-12-4
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
public class OpenApiResult {
    private String ret;  //QQ开放平台返回的错误码
    private String msg;  //QQ开放平台返回的错误信息
    private String is_lost; //QQ开放平台返回的数据是否丢失，如果应用不考虑cache可以完全不关心。0或者不返回：完全没有丢失，可以缓存。1：有一部分数据错误，不要缓存
    private String value;//返回的熄灭或点亮值

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIs_lost() {
        return is_lost;
    }

    public void setIs_lost(String is_lost) {
        this.is_lost = is_lost;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
