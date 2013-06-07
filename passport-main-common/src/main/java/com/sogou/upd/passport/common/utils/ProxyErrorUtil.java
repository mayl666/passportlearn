package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;

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
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.8",ERR_CODE_ACCOUNT_FREEZE);// 账号已被锁定
        SHPPERRCODE_SGPPERRCODE_MAP.put("authuser.9",USERNAME_PWD_MISMATCH);//登陆保护用户的stoken错误

        //wapbindmobile 绑定手机号
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.3",ERR_CODE_ACCOUNT_NOTHASACCOUNT);//用户不存在
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.4",ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);//用户已经绑定了手机号码
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.5",ERR_CODE_ACCOUNT_PHONE_NOBIND);//该手机已经绑定了用户
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.6",ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);//绑定手机失败
        SHPPERRCODE_SGPPERRCODE_MAP.put("wapbindmobile.7",ERR_CODE_PHONE_BIND_FREQUENCY_LIMIT);//手机绑定次数超限（一个手机一天只能绑定3次）

    }

}
