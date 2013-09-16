package com.sogou.upd.passport.common;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.util.List;
import java.util.Set;

/**
 * passport通用常量类
 */
public class CommonConstant {

    public static final int SGPP_DEFAULT_CLIENTID = 1120;
    public static final int PC_CLIENTID = 1044; //浏览器输入法桌面端client_id
    public static final int BROWSER_CLIENTID = 1065; //浏览器输入法桌面端client_id
    public static final int PINYIN_MAC_CLIENTID = 1105; //输入法MAC版client_id

    public static final String CLIENT_ID = "client_id";
    public static final String RESQUEST_CT = "ct";
    public static final String RESQUEST_CODE = "code";
    public static final String UNIQNAME = "uniqname";

    public static final String RESPONSE_STATUS = "status"; // 响应结果状态码，>0表示异常
    public static final String RESPONSE_STATUS_TEXT = "statusText"; // 响应结果说明
    public static final String RESPONSE_DATA = "data"; // 响应结果数据
    public static final String RESPONSE_RU = "ru"; // 响应结果的ru

    public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
    public static final String SEPARATOR_1 = "|";

    public static final String DEFAULT_CONNECT_REDIRECT_URL = "https://account.sogou.com";
    public static final String SOGOU_ROOT_DOMAIN = ".sogou.com";
    public static final String SOHU_ROOT_DOMAIN = ".sohu.com";
    //	public static final String API_ID_SOGOU_DOMAIN = "test01.id.sogou.com";
    public static final String API_ID_SOGOU_INTERNAL_DOMAIN = "api.id.sogou.com.z.sogou-op.org";

    public static final String DEFAULT_AVATAR_URL = "http://s5.suc.itc.cn/ux_sogou_member/src/asset/sogou/img_sogouAvatar";

    public static final int DEFAULT_COOKIE_EXPIRE = 3600 * 60 * 1; // 默认种cookie的有效期，1小时

    //=============缓存相关配置项====================
//	public static final int TIMEOUT_ONEHOUR = 60 * 60 * 3;// 3小时, 参考http://stackoverflow.com/questions/967875/memcached-expiration-time

    //在request attribut中存在的用于及时的StopWatch的name
    public static final String STOPWATCH = "stopWatch";

    // passport支持的第三方列表
    public static final List<String> SUPPORT_PROVIDER_LIST = Lists.newArrayList();

    static {
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.QQ.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.SINA.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.RENREN.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.TAOBAO.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.BAIDU.toString());
    }

    // 不用通行证统一的第三方appkey的应用，client_id|provider
    public static final Set SPECIAL_CONNECT_CONFIG_SET = Sets.newHashSet();

    static {
        SPECIAL_CONNECT_CONFIG_SET.add(CommonHelper.constructSpecialConnectKey(1001, 4));
        SPECIAL_CONNECT_CONFIG_SET.add(CommonHelper.constructSpecialConnectKey(2001, 4));
    }

}
