package com.sogou.upd.passport.common;


import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.util.List;

/**
 * passport通用常量类
 */
public class CommonConstant {
	
	public static final String RESPONSE_STATUS = "status"; // 响应结果状态码，>0表示异常
	public static final String RESPONSE_STATUS_TEXT = "statusText"; // 响应结果说明
	public static final String RESPONSE_DATA = "data"; // 响应结果数据

	public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
    public static final String SEPARATOR_1 = "|";
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String NAME_VALUE_SEPARATOR = "=";

    public static final String DEFAULT_CONNECT_REDIRECT_URL = "http://account.sogou.com";
	public static final String SOGOU_ROOT_DOMAIN = ".sogou.com";
//	public static final String API_ID_SOGOU_DOMAIN = "test01.id.sogou.com";
	public static final String API_ID_SOGOU_INTERNAL_DOMAIN = "api.id.sogou.com.z.sogou-op.org";
	
	public static final int DEFAULT_COOKIE_EXPIRE = 3600 * 60 * 24 * 7; // 默认种cookie的有效期，7天
	
	//=============缓存相关配置项====================
//	public static final int TIMEOUT_ONEHOUR = 60 * 60 * 3;// 3小时, 参考http://stackoverflow.com/questions/967875/memcached-expiration-time
	public static final int PASSPORT_PROFILE_TIMEOUT = 0; // 不过期吧，为了性能
	public static final int PASSPORT_USERSTATUE_TIMEOUT = 0; // 不过期吧，为了性能
	public static final int CONNECT_AUTHORIZE_STATE_TIMEOUT = 60 * 30;  // 第三方登录授权的statue缓存有效期

    public static final List<String> SUPPORT_PROVIDER_LIST = Lists.newArrayList(); // passport支持的第三方列表

    static {
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.QQ.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.SINA.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.RENREN.toString());
    }

}
