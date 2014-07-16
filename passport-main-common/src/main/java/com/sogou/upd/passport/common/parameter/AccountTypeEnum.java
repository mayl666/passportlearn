package com.sogou.upd.passport.common.parameter;


import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sogou.upd.passport.common.utils.PhoneUtil;

/**
 * 账号类型，第三方，邮箱，手机号码
 *
 * @author shipengzhi
 */
public enum AccountTypeEnum {

    UNKNOWN(0), //未知
    EMAIL(1), // 外域邮箱账号
    PHONE(2), // 手机号码
    QQ(3), // QQ
    SINA(4), // Sina微博
    RENREN(5), // 人人
    TAOBAO(6), // 淘宝
    BAIDU(7),  // 百度
    SOGOU(8),  // @sogou.com账号
    SOHU(9);  //sohu域账号

    // provider数字与字符串映射字典表
    private static BiMap<String, Integer> PROVIDER_MAPPING_DICT = HashBiMap.create();

    static {
        PROVIDER_MAPPING_DICT.put("unknown", UNKNOWN.getValue());
        PROVIDER_MAPPING_DICT.put("email", EMAIL.getValue());
        PROVIDER_MAPPING_DICT.put("phone", PHONE.getValue());
        PROVIDER_MAPPING_DICT.put("qq", QQ.getValue());
        PROVIDER_MAPPING_DICT.put("sina", SINA.getValue());
        PROVIDER_MAPPING_DICT.put("renren", RENREN.getValue());
        PROVIDER_MAPPING_DICT.put("taobao", TAOBAO.getValue());
        PROVIDER_MAPPING_DICT.put("baidu", BAIDU.getValue());
        PROVIDER_MAPPING_DICT.put("sohu", SOHU.getValue());
    }

    private int value;

    AccountTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int getProvider(String providerStr) {
        return PROVIDER_MAPPING_DICT.get(providerStr);
    }

    public static String getProviderStr(int provider) {
        return PROVIDER_MAPPING_DICT.inverse().get(provider);
    }

    public static boolean isPhone(String account, int provider) {
        if (PhoneUtil.verifyPhoneNumberFormat(account)) {
            if (provider == PHONE.getValue() || provider == UNKNOWN.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmail(String account, int provider) {
        if (Strings.isNullOrEmpty(account)) {
            return false;
        }
        if (account.contains("@")) {
            if (provider == EMAIL.getValue() || provider == UNKNOWN.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConnect(int provider) {
        if (provider != PHONE.getValue() && provider != EMAIL.getValue() && provider != SOHU.getValue() && provider != SOGOU.getValue()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSOHU(int provider) {
        if (provider == SOHU.getValue()) {
            return true;
        } else {
            return false;
        }
    }

    // TODO:以后需要与AccountDomainEnum整合，或者将此Enum只针对第三方，但是需要考虑到所有以provider为参数的地方
    // 该方法只针对第三方账号，返回值只有第三方类型或UNKNOWN
    public static AccountTypeEnum getAccountType(String username) {
        if (Strings.isNullOrEmpty(username)) {
            return UNKNOWN;
        }

        if (username.endsWith("@qq.sohu.com")) {
            return QQ;
        }

        if (username.endsWith("@sina.sohu.com")) {
            return SINA;
        }

        if (username.endsWith("@renren.sohu.com")) {
            return RENREN;
        }

        if (username.endsWith("@taobao.sohu.com")) {
            return TAOBAO;
        }

        if (username.endsWith("@baidu.sohu.com")) {
            return BAIDU;
        }

        return UNKNOWN;
    }

    /**
     * 生成第三方账号的passportId
     *
     * @param openid
     * @param provider
     * @return
     */
    public static String generateThirdPassportId(String openid, String provider) {
        return openid + "@" + provider + ".sohu.com";
    }

    /**
     * 根据passportId,截取openid
     *
     * @param passportId
     * @return
     */
    public static String getOpenIdByPassportId(String passportId) {
        return passportId.split("@")[0];
    }

    @Override
    public String toString() {
        return getProviderStr(getValue());
    }

}
