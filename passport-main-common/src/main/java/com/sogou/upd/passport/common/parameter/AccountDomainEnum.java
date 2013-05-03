package com.sogou.upd.passport.common.parameter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.sogou.upd.passport.common.utils.PhoneUtil;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午5:59 To change this template use
 * File | Settings | File Templates.
 */
public enum AccountDomainEnum {
    UNKNOWN(0), //未知
    SOGOU(1), // 搜狗
    SOHU(2), // 搜狐域
    PHONE(3), // 手机
    OTHER(4), // 外域
    THIRD(5); // 第三方

    // 域数字与字符串映射字典表
    private static BiMap<String, Integer> DOMAIN_MAPPING_DICT = HashBiMap.create();

    static {
        DOMAIN_MAPPING_DICT.put("sogou", SOGOU.getValue());
        DOMAIN_MAPPING_DICT.put("sohu", SOHU.getValue());
        DOMAIN_MAPPING_DICT.put("phone", PHONE.getValue());
        DOMAIN_MAPPING_DICT.put("other", OTHER.getValue());
        DOMAIN_MAPPING_DICT.put("third", THIRD.getValue());
        DOMAIN_MAPPING_DICT.put("unknown", UNKNOWN.getValue());
    }

    private int value;

    AccountDomainEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int getDomain(String domainStr) {
        return DOMAIN_MAPPING_DICT.get(domainStr);
    }

    public static String getDomainStr(int domain) {
        return DOMAIN_MAPPING_DICT.inverse().get(domain);
    }

    /**
     * 检测用户名的所属域，username不包括"手机号@sogou.com"这种格式
     *
     * @param username
     * @return
     */
    public static int getAccountDomain(String username) {
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            return getDomain("phone");
        }
        if (username.endsWith("@sogou.com")) {
            // TODO: 判定字符串是否可以设置在配置类或文件中
            return getDomain("sogou");
        } else if (username.endsWith("@sohu.com") || username.endsWith("@chinaren.com") ||
                username.endsWith("@focus.cn") || username.endsWith("@vip.sohu.com")) {
            return getDomain("sohu");
        } else if (username.matches(".+@[a-zA-Z0-9]+\\.sohu\\.com$")) {
            return getDomain("third");
        } else if (username.contains("@")) {
            return getDomain("other");
        }
        return getDomain("unknown");
    }

    @Override
    public String toString() {
        return getDomainStr(getValue());
    }
}
