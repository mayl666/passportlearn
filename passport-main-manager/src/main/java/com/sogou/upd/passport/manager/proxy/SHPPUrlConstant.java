package com.sogou.upd.passport.manager.proxy;

/**
 * 搜狐passport内部接口的相关常量
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:39
 */
public class SHPPUrlConstant {


    public static final String APP_ID="1100";

    public static final String APP_KEY="yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

    //请求SHPP时xml默认的rootNodeName
    public static final String DEFAULT_REQUEST_ROOTNODE="info";

    public static final String RESULT_STATUS="status";


    // 内部接口基本url
    private static final String BASE_INTERNAL_URL ="http://internal.passport.sohu.com/interface/";

    //检查用户名密码是否正确
    public  static final String AUTH_USER= BASE_INTERNAL_URL +"authuser";

    //绑定手机号
    public  static final String BING_MOBILE=BASE_INTERNAL_URL+"wapbindmobile";

    //解绑手机号
    public  static final String UNBING_MOBILE=BASE_INTERNAL_URL+"wapunbindmobile";

    //绑定邮箱
    public  static final String BIND_EMAIL=BASE_INTERNAL_URL+"bindemail";

    //根据老密码修改新密码
    public  static final String UPDATE_PWD=BASE_INTERNAL_URL+"updatepwd";
}
