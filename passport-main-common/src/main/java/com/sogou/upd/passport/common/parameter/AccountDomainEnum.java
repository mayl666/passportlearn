package com.sogou.upd.passport.common.parameter;

import com.sogou.upd.passport.common.utils.PhoneUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    THIRD(5), // 第三方
    INDIVIDUATION(6); // 个性化

    // 域数字与字符串映射字典表
    private static List<String> SOHU_DOMAIN = new ArrayList<>();

    static {
        SOHU_DOMAIN.add("@sohu.com");
        SOHU_DOMAIN.add("@vip.sohu.com");
        SOHU_DOMAIN.add("@chinaren.com");
        SOHU_DOMAIN.add("@focus.cn");
        SOHU_DOMAIN.add("@game.sohu.com");
        SOHU_DOMAIN.add("@bo.sohu.com");
        SOHU_DOMAIN.add("@changyou.com");
        SOHU_DOMAIN.add("@sms.sohu.com");
        SOHU_DOMAIN.add("@sol.sohu.com");
        SOHU_DOMAIN.add("@wap.sohu.com");
        SOHU_DOMAIN.add("@17173.com");
        SOHU_DOMAIN.add("@37wanwan.com");
    }

    private int value;

    AccountDomainEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 检测用户名的所属域，username不包括"手机号@sogou.com"这种格式
     *
     * @param username
     * @return
     */
    public static AccountDomainEnum getAccountDomain(String username) {

        // 验证纯手机号
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            return AccountDomainEnum.PHONE;
        }

        // 验证手机账号（如137****@sohu.com）和sogou域账号
        if (username.endsWith("@sohu.com")) {
            String prefix = username.substring(0, username.lastIndexOf(
                    "@sohu.com"));
            if (PhoneUtil.verifyPhoneNumberFormat(prefix)) {
                return AccountDomainEnum.PHONE;
            }
        }

        if (username.endsWith("@sogou.com")) {
            return AccountDomainEnum.SOGOU;
        }

        // 验证SOHU域账号，在SOHU_DOMAIN中限定
        for (String sohuSuffix : SOHU_DOMAIN) {
            if (username.endsWith(sohuSuffix)) {
                return AccountDomainEnum.SOHU;
            }
        }

        if (username.matches(".+@[a-zA-Z0-9]+\\.sohu\\.com$")) {
            return AccountDomainEnum.THIRD;
        } else if (username.contains("@")) {
            return AccountDomainEnum.OTHER;
        } else{
//            return  AccountDomainEnum.INDIVIDUATION;
        }
        return AccountDomainEnum.UNKNOWN;
    }

}
