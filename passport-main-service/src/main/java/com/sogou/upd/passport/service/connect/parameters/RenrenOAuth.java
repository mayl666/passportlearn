package com.sogou.upd.passport.service.connect.parameters;

public class RenrenOAuth {

	/* 通用参数 */
	public static final String METHOD = "method"; // 请求的方法名
	public static final String SIGN = "sig"; // 签名认证
	public static final String VERSION = "v"; // API的版本号
	public static final String FORMAT = "format"; // 返回值的格式
	public static final String FIELDS = "fields"; // 返回的字段列表，不同的方法值不同

	/* 通用值 */
	public static final String V1 = "1.0"; // 固定版本为1.0
	public static final String JSON = "json"; // 返回值的格式

	/* 用户类API请求参数 */
	public static final String UIDS = "uids"; // 需要查询的用户的ID，多个ID用逗号隔开。当此参数为空时，缺省值为登录用户的ID。
	/* 用户类API响应参数 */
	public static final String NAME = "name"; // 用户昵称
	public static final String MAIN_URL = "mainurl"; // 200*200头像
	public static final String SEX = "sex"; // 性别

	/* 关系类API请求参数 */
	public static final String COUNT = "count"; // 单页返回的记录条数，默认为50
	public static final String PAGE = "page"; // 返回结果的页码，默认为1
	/* 关系类API响应参数 */
	public static final String USER = "users";
	public static final String TOTAL_NUM = "total_number";

	/* 信息类API参数 */
	// 发布新鲜事，请求
	public static final String FEED_NAME = "name"; // 新鲜事标题 注意：最多30个字符
	public static final String FEED_DESCRIPTION = "description"; // 新鲜事主体内容 注意：最多200个字符
	public static final String FEED_URL = "url"; // 新鲜事标题和图片指向的链接
	public static final String FEED_IMAGE = "image"; // 新鲜事图片地址
	public static final String FEED_CAPTION = "caption"; // 新鲜事副标题 注意：最多20个字符
	// 发布新鲜事，响应
	public static final String FEED_ID = "post_id"; // 新鲜事发送成功后的id

}
