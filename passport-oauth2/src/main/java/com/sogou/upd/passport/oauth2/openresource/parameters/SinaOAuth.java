package com.sogou.upd.passport.oauth2.openresource.parameters;


import com.sogou.upd.passport.oauth2.common.OAuth;

public class SinaOAuth extends OAuth {

	/* 通用参数 */
	public static final String SOURCE = "source"; // 新浪分配给passport的appKey，即sina.thirdPartyKey
	public static final String UID = "uid"; // 新浪微博用户id

	/* 用户类API响应参数 */
	public static final String SCREEN_NAME = "screen_name"; // 用户昵称
	public static final String PROFILE_IMAGE_URL = "profile_image_url"; // 头像url
	public static final String AVATAR_LARGE = "avatar_large"; // 大头像url
	public static final String GENDER = "gender"; // 性别，m：男、f：女、n：未知
	public static final String DESC = "description"; // 用户描述
	public static final String PROVINCE = "province"; // 用户所在地区ID
	public static final String CITY = "city"; // 用户所在城市ID
	public static final String LOCATION = "location"; // 用户所在地

    // 获取sina省份/城市ID转换表的url
    public static final String SINA_PROVINCES_FORMAT_URL = "http://api.t.sina.com.cn/provinces.json";

	/* 关系类API请求参数 */
	public static final String COUNT = "count"; // 单页返回的记录条数，默认为50
	public static final String PAGE = "page"; // 返回结果的页码，默认为1
	public static final String SORT = "sort"; // 排序类型，0：按关注时间最近排序，默认为0

	/* 关系类API响应参数 */
	public static final String USER = "users";
	public static final String TOTAL_NUM = "total_number";

	/* 信息类API参数 */
	// 微博，请求
	public static final String STATUS = "status"; // 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字
	public static final String LAT = "lat"; // 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0
	public static final String LONG = "long"; // 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0
	public static final String PIC = "pic"; // 要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M
	public static final String URL = "url"; // 图片的URL地址，必须以http开头
	// 微博，响应
	public static final String CONTENT_ID = "id"; // 字符串型的微博ID
}
