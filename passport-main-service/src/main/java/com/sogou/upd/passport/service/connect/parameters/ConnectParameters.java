package com.sogou.upd.passport.service.connect.parameters;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * connect相关的参数类
 * @author shipengzhi
 */
public final class ConnectParameters {

	// 第三方参数
	public static final String CONNECT_OPENID = "connect_openid"; // 用户id
	public static final String CONNECT_NAME = "connect_name"; // 用户昵称
	public static final String CONNECT_HEAD_URL = "connect_head_url"; // 头像url
	
	public static final String CONNECT_AUTHZ_COOKIE = "connect_authz"; // 第三方账号授权cookie地址
	public static final String CONNECT_SYS_COOKIE = "connect_sys"; // 第三方账号登录硬件信息cookie地址
	public static final int APP_RU_COOKIE_AGE = 3600 * 24; // 提供的回调url有效期
    public static final List<String> SUPPORT_PROVIDER_LIST = new ArrayList<String>(); // passport接入的第三方列表
	// 获取sina省份/城市ID转换表的url
	public static final String SINA_PROVINCES_FORMAT_URL = "http://api.t.sina.com.cn/provinces.json";

}
