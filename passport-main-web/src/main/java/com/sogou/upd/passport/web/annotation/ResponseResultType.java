package com.sogou.upd.passport.web.annotation;

import com.sogou.upd.passport.common.utils.ErrorUtil;

/**
 * 用于表示需要登录时给前端返回何种格式的信息
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午10:46
 */
public enum ResponseResultType {
    json("{\"status\":\""+ ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED+"\",\"data\":{},\"statusText\":\"%s\"}"),    //返回json格式信息
    xml("<?xml version=\"1.0\" ?> " + //返回xml格式的信息
            "<code>"+ ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED+"</code>" +
            "<statusText>%s</statusText>"),
    txt("%s"),                        //返回字符串
    redirect("/web/login"),                     //302到登陆页面
    forward("/web/login");                      //后台forward到登陆页面

    private String resultStr;

    private ResponseResultType(String resultStr){
        this.resultStr=resultStr;
    }

    public String value(){
        return this.resultStr;
    }
}
