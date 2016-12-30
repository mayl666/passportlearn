package com.sogou.upd.passport.oauth2.openresource.parameters;

public class QQOAuth {

    /* 通用请求参数 */
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key"; // QQ分配给passport的appKey
    public static final String USER_INFO = "userinfo"; // 用户信息
    public static final String OPENID = "openid"; // QQ用户id
    public static final String UNIONID = "unionid"; // QQ unionid
    public static final String FORMAT = "format"; // 定义API返回的数据格式
    public static final String MOBILE_DISPLAY = "mobile"; // 对应display=mobile
    public static final String WML_DISPLAY = "wml"; // 对应的g_ut=1
    public static final String XHTML_DISPLAY = "xhtml"; // 对应的g_ut=2
    public static final String VIEW_PAGE = "viewPage"; // qq为搜狗产品定制化页面
    public static final String USERCANCEL = "usercancel"; // qq wap取消授权
    public static final String SHOW_AUTH_ITEMS = "show_auth_items"; // 是否隐藏授权信息 0:隐藏

    public static final int NO_AUTH_ITEMS = 0; // 是否隐藏授权信息 0:隐藏

    /* 用户类API请求 */
    /* 用户类API响应 */
    public static final String NICK_NAME = "nickname"; // 昵称
    public static final String FIGURE_URL_1 = "figureurl_qq_1"; // /user/get_user_info接口返回的头像url，尺寸40*40
    public static final String FIGURE_URL_2 = "figureurl_qq_2"; // /user/get_user_info接口返回的头像url，尺寸100*100需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
    public static final String FIGURE_URL_40 = "faceurl40"; // /oauth2/token接口返回的100尺寸头像
    public static final String FIGURE_URL_100 = "faceurl100"; // /oauth2/token接口返回的100尺寸头像
    public static final String GENDER = "gender"; // 性别

    /* 信息类API请求参数 */
    public static final String CONTENT = "content"; // 表示要发表的微博内容。必须为UTF-8编码，最长为140个汉字，也就是420字节。如果微博内容中有URL，后台会自动将该URL转换为短URL，每个URL折算成11个字节。若在此处@好友，需正确填写好友的微博账号，而非昵称。
    public static final String JING = "jing"; // 经度，为实数，最多支持10位有效数字。有效范围：-180.0到+180.0，+表示东经，默认为0.0。
    public static final String WEI = "wei"; // 纬度，为实数，最多支持10位有效数字。有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
    public static final String CLIENTIP = "clientip"; // 用户ip。必须正确填写真实ip，不能为内网ip及以127或255开头的ip。
    public static final String PIC = "pic"; // 要上传的图片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例）。图片仅支持gif、jpeg、jpg、png、bmp及ico格式（所有图片都会重新压缩，gif被重新压缩后不会再有动画效果），图片size小于4M。
    public static final String[] SUPPORT_PIC_TYPE_ARRAY = {"gif", "jpeg", "jpg", "png", "bmp",
            "ico"};

    /* 好友类API请求参数 */
    public static final String FOPENIDS = "fopenids"; // 要收听的用户的openid列表。多个openid之间用“_”隔开，最多30个。
}
