package com.sogou.upd.passport.common;


import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.util.List;

/**
 * passport通用常量类
 */
public class CommonConstant {

    public static final int SGPP_DEFAULT_CLIENTID = 1120;
    public static final int PC_CLIENTID = 1044; //浏览器输入法桌面端client_id
    public static final int PINYIN_MAC_CLIENTID = 1105; //输入法MAC版client_id
    public static final int XIAOSHUO_CLIENTID = 1115; //小说client_id
    public static final int CAIPIAO_CLIENTID = 2012; //彩票client_id
    public static final int TEEMO_CLIENTID = 2020; //teemo client_id
    public static final int MAIL_CLIENTID = 1014;//mail client_id

    public static final int PWD_TYPE_EXPRESS = 0; //密码类型为明文
    public static final int PWD_TYPE_CIPHER = 1; //密码类型为密文
    public static final String SG_TOKEN_START = "SG"; //4.2版本浏览器token开始标志
    public static final String SG_TOKEN_OLD_START = "SG_"; //4.2版本早期的token开始标志
    public static final String PC_REDIRECT_GETUSERINFO = "getuserinfo"; //跳转到个人中心页面
    public static final String PC_REDIRECT_AVATARURL = "avatarurl"; //跳转到修改头像页面
    public static final String PC_REDIRECT_PASSWORD = "password"; //跳转到修改密码页面
    public static final String SOGOU_IME_UA = "sogou_ime"; //搜狗输入法header里的User-Agent标识

    public static final String APP_CONNECT_KEY = "100294784";  //搜狗在QQ第三方开放平台的应用id
    public static final String APP_CONNECT_SECRET = "a873ac91cd703bc037e14c2ef47d2021";  //搜狗在QQ第三方开放平台对应的应用密钥
    public static final String SOHU_APP_CONNECT_KEY = "200034";  //搜狐在QQ第三方开放平台的应用id
    public static final String SOHU_APP_CONNECT_SECRET = "8c0116a88d3b5ce01f25d69a376f381f ";  //搜狐在QQ第三方开放平台对应的应用密钥
    public static final String HTTP = "http";  //http请求方式
    public static final String HTTPS = "https";//https请求方式
    public static final String QQ_SERVER_IP = "119.147.19.43";         //QQ测试环境ip：
    public static final String QQ_SERVER_NAME = "openapi.tencentyun.com";   //QQ正式环境可以使用域名，http的，需要sig签名
    public static final String QQ_SERVER_NAME_GRAPH = "graph.qq.com";   //QQ https请求域名，不需要sig签名
    public static final String QQ_SERVER_NAME_OPENMOBILE = "openmobile.qq.com"; //QQ https请求域名，不需要sig签名
    public static final int WITH_CONNECT_ORIGINAL = 1;      //1表示需要从第三方获取原始信息,默认为0，不返回第三方原始信息
    public static final int NOT_WITH_CONNECT_ORIGINAL = 0;      //1表示需要从第三方获取原始信息,默认为0，不返回第三方原始信息

    public static final String USERNAME = "username";
    public static final String CLIENT_ID = "client_id";
    public static final String APP_ID = "appid";
    public static final String RESQUEST_CT = "ct";
    public static final String RESQUEST_CODE = "code";
    public static final String BROWER_VERSION = "v";//浏览器客户端版本
    public static final String INSTANCE_ID = "instanceid";//终端唯一标示
    public static final String THIRD_APPID = "third_appid";//应用在第三方平台独立申请的appid
    public static final String MAPP_REQUEST_HEADER_SIGN = "cinfo";

    public static final String RESPONSE_STATUS = "status"; // 响应结果状态码，>0表示异常
    public static final String RESPONSE_STATUS_TEXT = "statusText"; // 响应结果说明
    public static final String RESPONSE_ERROR = "error"; // 响应结果数据
    public static final String RESPONSE_RU = "ru"; // 响应结果的ru

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String SEPARATOR_1 = "|";

    public static final String JOINER_SEPARATOR = ";";
    public static final String KEY_VALUE_SEPARATOR = "=";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String OPENID = "openid";

    public static final String DEFAULT_INDEX_URL = "https://account.sogou.com";
    public static final String DEFAULT_WAP_INDEX_URL = "http://m.account.sogou.com";
    public static final String DEFAULT_WAP_URL = "http://wap.sogou.com";
    public static final String SOGOU_ROOT_DOMAIN = ".sogou.com";
    public static final String SOGOU_SUFFIX = "@sogou.com";
    public static final String SOHU_SUFFIX = "@sohu.com";
    public static final String EMAIL_REG_VERIFY_URL = "https://account.sogou.com/web/reg/emailverify";

    public static final String PP_COOKIE_URL = "http://account.sogou.com/act/setppcookie";

    //web端 生成cookie并且种cookie标示
    public static final int CREATE_COOKIE_AND_SET = 0;

    //桌面端产品 生成cookie 不种标示
    public static final int CREATE_COOKIE_NOT_SET = 1;

    public static final int DEFAULT_COOKIE_EXPIRE = 3600 * 60 * 1; // 默认种cookie的有效期，1小时
    public static final long COOKIE_REQUEST_VAILD_TERM = 5 * 60; //接口请求的有效期为5分钟，单位为秒
    public static final long COOKIE_REQUEST_VAILD_TERM_IN_MILLI = 5 * 60 * 1000; //接口请求的有效期为5分钟，单位为秒

    public static final String LOGIN_IN_BLACKLIST = "1"; //用户名或者ip在黑名单的标识

    public static final String SIGN_IN_BLACKLIST = "1";//黑名单标识

    //=============缓存相关配置项====================
    //在request attribut中存在的用于及时的StopWatch的name
    public static final String STOPWATCH = "stopWatch";

    public static final String HTTPS_HEADER = "X-Https";
    public static final String HTTPS_VALUE = "https";
    public static final String USER_AGENT = "User-Agent";
    public static final String SSL_PROTOCOL = "X-SSL-Protocol";

    public static final String REFER = "referer";

    //域名不正确
    public static final String DOMAIN_ERROR = "域名不正确";

    //module 黑名单数据 连接符
    public static final String MODULE_BLACK_LIST_DATA_JOINER = "^";

    // passport支持的第三方列表
    public static final List<String> SUPPORT_PROVIDER_LIST = Lists.newArrayList();

    static {
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.QQ.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.SINA.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.RENREN.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.TAOBAO.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.BAIDU.toString());
        SUPPORT_PROVIDER_LIST.add(AccountTypeEnum.WEIXIN.toString());
    }

}
