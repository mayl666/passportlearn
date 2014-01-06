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
    public static final int XIAOSHUO_CLIENTID = 1115; //小说client_id
    public static final int SOHU_PCTOKEN_LEN = 30; //SOHU token长度为30
    public static final int PWD_TYPE_EXPRESS = 0; //密码类型为明文
    public static final int PWD_TYPE_CIPHER = 1; //密码类型为密文
    public static final String SG_TOKEN_START = "SG"; //4.2版本浏览器token开始标志
    public static final String SG_TOKEN_OLD_START = "SG_"; //4.2版本早期的token开始标志

    public static final String APP_CONNECT_KEY = "100294784";  //搜狗在QQ第三方开放平台的应用id
    public static final String APP_CONNECT_SECRET = "a873ac91cd703bc037e14c2ef47d2021";  //搜狗在QQ第三方开放平台对应的应用密钥
    public static final String SOHU_APP_CONNECT_KEY = "200034";  //搜狐在QQ第三方开放平台的应用id
    public static final String SOHU_APP_CONNECT_SECRET = "8c0116a88d3b5ce01f25d69a376f381f ";  //搜狐在QQ第三方开放平台对应的应用密钥
    public static final String HTTP = "http";  //http请求方式
    public static final String HTTPS = "https";//https请求方式
    public static final String CONNECT_METHOD_GET = "get";//method=get请求方式
    public static final String CONNECT_METHOD_POST = "post";//method=post请求方式
    public static final String QQ_SERVER_IP = "119.147.19.43";         //QQ测试环境ip：
    public static final String QQ_SERVER_NAME = "openapi.tencentyun.com";   //QQ正式环境可以使用域名，http的，需要sig签名
    public static final String QQ_SERVER_NAME_GRAPH = "graph.qq.com";   //QQ https请求域名，不需要sig签名
    public static final String QQ_SERVER_NAME_OPENMOBILE = "openmobile.qq.com"; //QQ https请求域名，不需要sig签名

    public static final String CLIENT_ID = "client_id";
    public static final String APP_ID = "appid";
    public static final String RESQUEST_CT = "ct";
    public static final String RESQUEST_CODE = "code";
    public static final String UNIQNAME = "uniqname";

    public static final String RESPONSE_STATUS = "status"; // 响应结果状态码，>0表示异常
    public static final String RESPONSE_STATUS_TEXT = "statusText"; // 响应结果说明
    public static final String RESPONSE_ERROR = "error"; // 响应结果数据
    public static final String RESPONSE_RU = "ru"; // 响应结果的ru

    public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
    public static final String SEPARATOR_1 = "|";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String OPENID = "openid";

    public static final String DEFAULT_CONNECT_REDIRECT_URL = "https://account.sogou.com";
    public static final String DEFAULT_WAP_URL="http://wap.sogou.com";
    public static final String DEFAULT_WAP_CONNECT_REDIRECT_URL = "http://wap.sogou.com";
    public static final String SOGOU_ROOT_DOMAIN = ".sogou.com";
    public static final String SOHU_ROOT_DOMAIN = ".sohu.com";

    public static final String DEFAULT_AVATAR_URL = "http://s5.suc.itc.cn/ux_sogou_member/src/asset/sogou/img_sogouAvatar";

    public static final int DEFAULT_COOKIE_EXPIRE = 3600 * 60 * 1; // 默认种cookie的有效期，1小时

    //=============缓存相关配置项====================
//	public static final int TIMEOUT_ONEHOUR = 60 * 60 * 3;// 3小时, 参考http://stackoverflow.com/questions/967875/memcached-expiration-time

    //在request attribut中存在的用于及时的StopWatch的name
    public static final String STOPWATCH = "stopWatch";

    public static boolean IS_USE_IEBBS_UNIQNAME = true;
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
