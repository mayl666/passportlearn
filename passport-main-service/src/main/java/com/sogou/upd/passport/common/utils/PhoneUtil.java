package com.sogou.upd.passport.common.utils;

import org.apache.commons.lang3.StringUtils;

public class PhoneUtil {

	public static final String PHONE_FORMAT = "(^[0-9]{3,4}-[0-9]{3,8}$)|^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|2|3|5|6|7|8|9])\\d{8}$";
	public static final int PHONE_LENTH = 11;
	/**
	 * 验证手机号码、电话号码是否有效
	 * 新联通 （中国联通+中国网通）手机号码开头数字 130、131、132、145、155、156、185、186 　　 
	 * 新移动 （中国移动+中国铁通）手机号码开头数字 134、135、136、137、138、139、147、150、151、152、157、158、159、182、183、187、188
	 * 新电信 （中国电信 <http://baike.baidu.com/view/3214.htm>+中国卫通）手机号码开头数字 133、153、189、180 
	 */
	public static boolean verifyPhoneNumberFormat(String photo) {

		if (!StringUtils.isEmpty(photo)) {
			return photo.matches(PHONE_FORMAT);
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		String[] phones = new String[] { "1523620111", "11011363254", "15811363254", "15811364216", "15811364216",
				"13011111111", "15811364216", "022-6232903-22", "022-6232903", "+8615811364216", "8615811224181" };
		for (int i = 0; i < phones.length; i++) {
			System.out.print(phones[i] + "  ");
			System.out.println(PhoneUtil.verifyPhoneNumberFormat(phones[i]));
		}

		String phone = "8615811224181";
		if (phone.length() > PhoneUtil.PHONE_LENTH) {
			phone = phone.substring(phone.length() - PhoneUtil.PHONE_LENTH);
		}
	}
}
