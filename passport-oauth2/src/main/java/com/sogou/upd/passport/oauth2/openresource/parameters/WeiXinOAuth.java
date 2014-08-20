package com.sogou.upd.passport.oauth2.openresource.parameters;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-20
 * Time: 下午7:10
 * To change this template use File | Settings | File Templates.
 */
public class WeiXinOAuth {

    /* 通用请求参数 */
    public static final String APP_SECRET = "secret"; // 微信分配给passport的AppSecret
    public static final String USER_INFO = "userinfo"; // 用户信息
    public static final String OPENID = "openid"; // WeiXin用户id
    public static final String FORMAT = "format"; // 定义API返回的数据格式

    /* 用户类API响应 */
    public static final String NICK_NAME = "nickname"; // 昵称
    public static final String SEX = "sex"; // 性别
    public static final String PROVINCE = "province"; // 省份
    public static final String CITY = "city"; // 城市
    public static final String COUNTRY = "country"; // 国家
    public static final String HEADIMGURL = "headimgurl"; // 用户头像
    public static final String PROVILEGE = "privilege"; //用户特权信息
    public static final String UNIONID = "unionid"; // 用户统一标识

}
