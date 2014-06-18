package com.sogou.upd.passport.common.utils;

/**
 * 参数工具类
 * User: chengang
 * Date: 14-6-12
 * Time: 下午7:45
 */
public class ParamsUtil {

    /**
     * 参数替换
     *
     * @param param
     * @return
     */
    public static String replaceParam(String param) {
        //sec_mobile, sec_email, sec_ques,  username
        if (param.contains("username")) {
            //真实姓名
            param = param.replaceAll("username", "fullname");
        }
        if (param.contains("sec_mobile")) {
            //绑定手机号
            param = param.replaceAll("sec_mobile", "mobile");
        }
        if (param.contains("sec_email")) {
            //绑定密保邮箱
            param = param.replaceAll("sec_email", "email");
        }
        if (param.contains("sec_ques")) {
            //绑定密保问题
            param = param.replaceAll("sec_ques", "question");
        }
        return param;
    }
}
