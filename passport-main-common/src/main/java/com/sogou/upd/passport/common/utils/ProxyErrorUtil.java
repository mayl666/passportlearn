package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.StringUtil;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import java.util.Map;

/**
 * 用于将SHPP返回的error code转换为我们自己的error code
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午5:55
 */
public class ProxyErrorUtil extends ErrorUtil {

    private static final Map<String,String> SHPPERRCODE_SGPPERRCODE_MAP= Maps.newHashMapWithExpectedSize(200);

    static{
        //所有接口共用的错误码
        SHPPERRCODE_SGPPERRCODE_MAP.put("0",SUCCESS);
        SHPPERRCODE_SGPPERRCODE_MAP.put("1",ERR_CODE_COM_REQURIE);
        SHPPERRCODE_SGPPERRCODE_MAP.put("2",ERR_CODE_COM_SING);

        //authuser 登录接口
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.3",USERNAME_PWD_MISMATCH);//用户名密码不匹配
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.4",ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED);//外域用户未激活
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.5",ERR_CODE_ACCOUNT_PHONE_NOBIND);//手机号码没有绑定（wap专用）
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.6",USERNAME_PWD_MISMATCH);//校验失败
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.7",ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED);//手机注册的sohu域账号未激活
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.8",ERR_CODE_ACCOUNT_KILLED);// 账号已被锁定
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.9",USERNAME_PWD_MISMATCH);//登陆保护用户的stoken错误

        //wapbindmobile 绑定手机号
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.3",ERR_CODE_ACCOUNT_NOTHASACCOUNT);//用户不存在
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.4",ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);//用户已经绑定了手机号码
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.5",ERR_CODE_ACCOUNT_PHONE_BINDED);//该手机已经绑定了其他用户
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.6",ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);//绑定手机失败
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.7",ERR_CODE_PHONE_BIND_FREQUENCY_LIMIT);//手机绑定次数超限（一个手机一天只能绑定3次）

        //wapunbindmobile 解除手机绑定
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapunbindmobile.3",ERR_CODE_ACCOUNT_PHONE_NOBIND);//手机号码没有绑定帐号
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapunbindmobile.4",ERR_CODE_PHONE_UNBIND_FAILED);//,该用户是手机邮箱用户，不能进行解除绑定
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapunbindmobile.5",ERR_CODE_PHONE_UNBIND_FAILED);//解除绑定手机失败

        //bindemail 绑定邮箱
        SHPPERRCODE_SGPPERRCODE_MAP.put("bindemail.3",USERNAME_PWD_MISMATCH);//用户不存在或者密码错误
        SHPPERRCODE_SGPPERRCODE_MAP.put("bindemail.4",ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);//旧绑定邮箱错误
        SHPPERRCODE_SGPPERRCODE_MAP.put("bindemail.5",SUCCESS);//新的绑定邮箱没有变化
        SHPPERRCODE_SGPPERRCODE_MAP.put("bindemail.6",ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);//系统错误
        SHPPERRCODE_SGPPERRCODE_MAP.put("bindemail.7",ERR_CODE_PHONE_UNBIND_FAILED);//密码错误次数超限

        //updatepwd 修改密码
        SHPPERRCODE_SGPPERRCODE_MAP.put("updatepwd.3",ERR_CODE_ACCOUNT_NOTHASACCOUNT);//用户名不存在
        SHPPERRCODE_SGPPERRCODE_MAP.put("updatepwd.4",USERNAME_PWD_MISMATCH);//原密码校验失败
        SHPPERRCODE_SGPPERRCODE_MAP.put("updatepwd.5",ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);//新的绑定邮箱没有变化

        //wapbindmobile 查询手机号绑定的账号
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.3",ERR_CODE_ACCOUNT_PHONE_NOBIND);//手机号码没有绑定用户
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.6",SYSTEM_UNKNOWN_EXCEPTION);//查询失败

    }

    public static Map.Entry<String,String> shppErrToSgpp(String url,String status){
        String code=getCode(url,status);

        String message=ERR_CODE_MSG_MAP.get(code);

        return  new DefaultMapEntry(code,message);
    }

    private static String getCode(String url,String status){
        if(StringUtil.isBlank(status)){
            return SYSTEM_UNKNOWN_EXCEPTION;
        }
        switch(status){
            case "0":
                return SUCCESS;
            case "1":
                return ERR_CODE_COM_REQURIE;
            case "2":
                return ERR_CODE_COM_SING;
        }
        //通过分解url获取 authuser.3 这样的code
        String[] urls= url.split("/");
        String api=urls[urls.length-1];
        StringBuilder errorCodeBuilder=new StringBuilder(api);
        errorCodeBuilder.append(".");
        errorCodeBuilder.append(status);
        String errorCode=errorCodeBuilder.toString();
        if(SHPPERRCODE_SGPPERRCODE_MAP.containsKey(errorCode)){
            return SHPPERRCODE_SGPPERRCODE_MAP.get(errorCode);
        }
        return SYSTEM_UNKNOWN_EXCEPTION;
    }
}
