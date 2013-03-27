package com.sogou.upd.passport.common.utils;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.common.parameter.CommonParameters;

public class ErrorUtil {

	public static Map<String, String> ERR_CODE_MSG_MAP = Maps.newHashMap();

	/***************************通用的错误代码start*********************************/
	// 系统异常错误
	public static final String ERR_CODE_COM_EXCEPTION = "10001";
	// 必填的参数错误
	public static final String ERR_CODE_COM_REQURIE = "10002";
	// 签名错误
	public static final String ERR_CODE_COM_SING = "10003";
	// access_token错误
	public static final String ERR_ACCESS_TOKEN = "10005";
	// 签名或accessToken验证失败
	public static final String ERR_OPEN_ID = "10006";
	// 传入字段不存在，请输入正确的字段
	public static final String ERR_QUERY_FIELDS = "10008";
	// 用户不存在
	public static final String ERR_CODE_COM_NOUSER = "10009";

	//***************************通用的错误代码end*********************************

	//***************************account 服务的错误代码start*********************************
	// 帐号已经注册
	public static final String ERR_CODE_ACCOUNT_REGED = "20201";
	// 短信发送已达今天的最高上限20条
	public static final String ERR_CODE_ACCOUNT_CANTSENTSMS = "20202";
	// 手机号格式错误
	public static final String ERR_CODE_ACCOUNT_PHONEERROR = "20203";
	// 一分钟内只能发一条短信
	public static final String ERR_CODE_ACCOUNT_MINUTELIMIT = "20204";
	// 没有这个用户
	public static final String ERR_CODE_ACCOUNT_NOTHASACCOUNT = "20205";
	// 登录不成功，帐号或密码错误
	public static final String ERR_CODE_ACCOUNT_LOGINERROR = "20206";
	// 体验用户不允许登录
	public static final String ERR_CODE_ACCOUNT_EXPUSERLOGIN = "20207";
	// 短信验证码错误，输入的错误或者验证码过期
	public static final String ERR_CODE_ACCOUNT_SMSCODE = "20208";
	// 帐号无法绑定
	public static final String ERR_CODE_ACCOUNT_BIND = "20209";
	// 帐号无法关联
	public static final String ERR_CODE_ACCOUNT_ASSOCIATE = "20210";
	// 密码格式非法，只能是可打印ascii字符，长度大于=6
	public static final String ERR_CODE_ACCOUNT_PASSWDFORMAT = "20211";
    // 昵称验证失败
    public static final String ERR_CODE_ACCOUNT_VERIFY_FIELDS = "20212";
    // 手机验证码发送失败
    public static final String ERR_CODE_ACCOUNT_SMSCODE_SEND = "20213";
    //用户允许注册，但注册失败
    public static final String ERR_CODE_ACCOUNT_REGISTER_FAILED = "20214";
    //验证access_token和appkey失败
    public static final String ERR_CODE_ACCOUNT_ACCESSTOKEN_FAILED = "20215";
    //手机号码和验证码不匹配
    public static final String ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE = "20216";
    //手机号获取失败
    public static final String ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS = "20217";
	//***************************account 服务的错误代码end*********************************


	//***************************profile 服务的错误代码start*********************************
	// 字段非法
	public static final String ERR_CODE_PROFILE_FIELD = "20301";


	//***************************profile 服务的错误代码end*********************************

	//***************************IMAGE 服务的错误代码start*********************************
	// 上传头像的文件扩展名不对
	public static final String ERR_CODE_PROFILE_IMGEXT = "20401";
	// 上传错误，没有找到上传的文件
	public static final String ERR_CODE_PROFILE_FILE = "20402";
	// 上传头像不成功，更新数据库错误
	public static final String ERR_CODE_PROFILE_UPDATE = "20403";
	// 获取图片的URL出错或者服务器连不上
	public static final String ERR_CODE_ACHIEVE_PROFILE = "20404";

	//***************************IMAGE 服务的错误代码end*********************************

	/***************************Connect通用的错误代码start*********************************/
	// 访问频率受限
	public static final String CONNECT_REQUEST_FREQUENCY_LIMIT = "30001";
	// 用户拒绝登录授权
	public static final String CONNECT_USER_DENIED_LOGIN = "30002";
	// 第三方关联帐号token过期，请重新关联
	public static final String CONNECT_ASSOCIATE_TOKEN_INVALID = "30003";
	// 第三方登录帐号Token过期，请重新登录
	public static final String CONNECT_TOKEN_INVALID = "30004";
	// 刷新第三方accessToken失败
	public static final String CONNECT_REFRESH_TOKEN_FAIL = "30005";
	// 发送HTTP请求失败
	public static final String HTTP_CLIENT_REQEUST_FAIL = "30006";
	// 不支持指定第三方
	public static final String UNSUPPORT_THIRDPARTY = "30007";
	// 不支持的响应结果类型
	public static final String UNSUPPORTED_RESPONSE_TYPE = "30008";
	// 无效的登录授权请求
	public static final String INVALID_REQUEST = "30009";
	// 用户未授权
	public static final String REQUEST_NO_AUTHORITY = "30010";
	// 第三方自定义错误
	public static final String CONNECT_USER_DEFINED_ERROR = "30011";
	// 没有第三方关联帐号，请关联
	public static final String CONNECT_ASSOCIATE_NOT_EXIST = "30012";
	//***************************通用的错误代码end*********************************

	//***************************Friend 服务的错误代码start************************
	// 无法读入上传文件
	public static final String READ_FILE_FAILED = "30301";
	// 上传文件失败
	public static final String UPDATE_FILE_FAILED = "30302";
	// 用户没有手机帐号，无法获取通讯录好友
	public static final String PHONE_ACCOUNT_NOT_EXISTS = "30303";
	// 关注的用户不存在
	public static final String FOLLOW_ACCOUNT_NOT_EXISTS = "30310";
	// 已经关注此用户
	public static final String ALREADY_FOLLOWED = "30311";
	//***************************Friend 服务的错误代码end*******************************

	//***************************信息类API错误代码start*********************************
	// 图片url不能为空
	public static final String PIC_URL_NOT_NULL = "30401";
	// 发送失败
	public static final String ADD_SHARE_FAIL = "30402";
	// QQ帐号未开通微博
	public static final String NO_OPEN_BLOG = "30403";
	// 上传的图片不能为空
	public static final String PIC_NOT_NULL = "30404";
	// 请使用multpart格式上传图片
	public static final String UPDATE_MULTIPART_IMAGE = "30405";
	// 不支持的图片类型
	public static final String UNSUPPORT_IMAGE_FORMAT = "30406";
	// 不允许发送相同内容
	public static final String REPEAT_CONTENT = "30407";

	//***************************信息类API错误代码end*********************************

	//***************************好友类API错误代码start********************************
	// 上传的通讯录file不能为空
	public static final String UPDATE_CONATCT_NOT_NULL = "30501";

	//***************************好友类API错误代码end********************************

	static {
		ERR_CODE_MSG_MAP.put(ERR_CODE_COM_REQURIE, "参数错误,请输入必填的参数");
		ERR_CODE_MSG_MAP.put(ERR_CODE_COM_SING, "参数错误,签名过期或者不合法");
		ERR_CODE_MSG_MAP.put(ERR_CODE_COM_NOUSER, "用户不存在");
		ERR_CODE_MSG_MAP.put(ERR_QUERY_FIELDS, "传入字段不存在，请输入正确的字段");

		ERR_CODE_MSG_MAP.put(ERR_ACCESS_TOKEN, "access_token错误");
		ERR_CODE_MSG_MAP.put(ERR_OPEN_ID, "openid错误");

		// account
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGED, "这个帐号已经注册过啦");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_CANTSENTSMS, "今天的短信已经到20条上限啦");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONEERROR, "呃，地球上没有这个手机号");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_MINUTELIMIT, "一分钟内只能发一条短信");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_NOTHASACCOUNT, "帐号不存在");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_LOGINERROR, "帐号或密码不正确");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_EXPUSERLOGIN, "呃，没有这个手机号");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE, "验证码不正确");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_BIND, "绑定帐号已登录过，无法绑定");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ASSOCIATE, "此帐号无法关联，可能已经关联过了");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PASSWDFORMAT, "请输入6-16位的数字、字母或字符");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_VERIFY_FIELDS,"昵称格式有误，只能包含中文、英文大小写,-,_,字母或空格");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_SMSCODE_SEND,"手机验证码发送失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_REGISTER_FAILED,"用户注册失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_ACCESSTOKEN_FAILED,"验证access_token和appkey失败");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE,"手机号码和验证码不匹配");
        ERR_CODE_MSG_MAP.put(ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS,"手机号获取失败");


		// profile
		ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_FIELD, "字段非法");

		// image
		ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_IMGEXT, "图片扩展名不对，支持的格式为：bmp, gif, jpg, jpeg, png");
		ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_FILE, "没有找到上传的文件");
		ERR_CODE_MSG_MAP.put(ERR_CODE_PROFILE_UPDATE, "上传头像失败");
		ERR_CODE_MSG_MAP.put(ERR_CODE_ACHIEVE_PROFILE, "URL或服务器出错，待会儿再试试吧");

		// connect
		ERR_CODE_MSG_MAP.put(CONNECT_REQUEST_FREQUENCY_LIMIT, "超过第三方接口的访问限制");
		ERR_CODE_MSG_MAP.put(CONNECT_USER_DENIED_LOGIN, "用户拒绝登录授权");
		ERR_CODE_MSG_MAP.put(CONNECT_ASSOCIATE_TOKEN_INVALID, "第三方关联帐号token过期，请重新关联");
		ERR_CODE_MSG_MAP.put(CONNECT_TOKEN_INVALID, "第三方帐号Token过期，请重新登录");
		ERR_CODE_MSG_MAP.put(CONNECT_REFRESH_TOKEN_FAIL, "刷新第三方accessToken失败");
		ERR_CODE_MSG_MAP.put(HTTP_CLIENT_REQEUST_FAIL, "发送HTTP请求失败");
		ERR_CODE_MSG_MAP.put(UNSUPPORT_THIRDPARTY, "该接口不支持指定第三方");
		ERR_CODE_MSG_MAP.put(UNSUPPORTED_RESPONSE_TYPE, "不支持的响应结果类型");
		ERR_CODE_MSG_MAP.put(INVALID_REQUEST, "无效的请求");
		ERR_CODE_MSG_MAP.put(REQUEST_NO_AUTHORITY, "用户没有对该api进行授权");
		ERR_CODE_MSG_MAP.put(CONNECT_ASSOCIATE_NOT_EXIST, "第三方关联帐号不存在，请先关联");

		// info
		ERR_CODE_MSG_MAP.put(PIC_URL_NOT_NULL, "图片url不能为空");
		ERR_CODE_MSG_MAP.put(ADD_SHARE_FAIL, "发布失败");
		ERR_CODE_MSG_MAP.put(NO_OPEN_BLOG, "还没开通微博呢");
		ERR_CODE_MSG_MAP.put(PIC_NOT_NULL, "上传图片不能为空");
		ERR_CODE_MSG_MAP.put(UPDATE_MULTIPART_IMAGE, "请使用multpart格式上传图片");
		ERR_CODE_MSG_MAP.put(UNSUPPORT_IMAGE_FORMAT, "不支持的图片类型");
		ERR_CODE_MSG_MAP.put(REPEAT_CONTENT, "同样的内容请勿重复发送");

		// friend
		ERR_CODE_MSG_MAP.put(UPDATE_CONATCT_NOT_NULL, "通讯录是空的哦");
		ERR_CODE_MSG_MAP.put(READ_FILE_FAILED, "无法读取上传文件");
		ERR_CODE_MSG_MAP.put(UPDATE_FILE_FAILED, "上传通讯录失败");
		ERR_CODE_MSG_MAP.put(PHONE_ACCOUNT_NOT_EXISTS, "没找到你的通讯录呢");
		ERR_CODE_MSG_MAP.put(FOLLOW_ACCOUNT_NOT_EXISTS, "您关注的用户不存在或未注册过帐号");
		ERR_CODE_MSG_MAP.put(ALREADY_FOLLOWED, "已经关注此用户");

	}

	public static Map<String, Object> buildExceptionError(String msg) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, ERR_CODE_COM_EXCEPTION);
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		return retMap;
	}

	public static Map<String, Object> buildError(String code) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, code);
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, ERR_CODE_MSG_MAP.get(code));
		return retMap;
	}

	public static Map<String, Object> buildError(String code, String msg) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, code);
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		return retMap;
	}

	public static Map<String, Object> buildSuccess(String msg, Map<String, Object> data) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, "0");
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		retMap.put(CommonParameters.RESPONSE_DATA, data == null ? Collections.emptyMap() : data);
		return retMap;
	}
	public static Map<String, Object> buildSuccess(String msg, Object data) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, "0");
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		retMap.put(CommonParameters.RESPONSE_DATA, data == null ? Collections.emptyMap() : data);
		return retMap;
	}
	public static String buildErrorJson(String code, String msg) {
		JsonObject json = new JsonObject();
		json.addProperty(CommonParameters.RESPONSE_STATUS, code);
		json.addProperty(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		return new Gson().toJson(json);
	}


	public static Map<String, String> getERR_CODE_MSG_MAP() {
		return ERR_CODE_MSG_MAP;
	}

	public static String getERR_CODE_MSG(String code) {
		return ERR_CODE_MSG_MAP.get(code);
	}

	public static void setERR_CODE_MSG_MAP(Map<String, String> eRR_CODE_MSG_MAP) {
		ERR_CODE_MSG_MAP = eRR_CODE_MSG_MAP;
	}

}
