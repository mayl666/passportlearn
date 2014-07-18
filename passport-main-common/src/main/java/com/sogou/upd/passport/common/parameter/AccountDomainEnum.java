package com.sogou.upd.passport.common.parameter;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
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
    INDIVID(6); // 个性化

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
        if (Strings.isNullOrEmpty(username)) {
            return AccountDomainEnum.UNKNOWN;
        }

        // 验证纯手机号
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            return AccountDomainEnum.PHONE;
        }

        // 验证手机账号（如137****@sohu.com）和sogou域账号
        if (username.endsWith(CommonConstant.SOHU_SUFFIX)) {
            String prefix = username.substring(0, username.lastIndexOf(
                    CommonConstant.SOHU_SUFFIX));
            if (PhoneUtil.verifyPhoneNumberFormat(prefix)) {
                return AccountDomainEnum.PHONE;
            }
        }

        if (username.endsWith(CommonConstant.SOGOU_SUFFIX)) {
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
        } else {
            return AccountDomainEnum.INDIVID;
        }

    }

    //获取内部大小写的处理方式：搜狗个性账号、外域账号全部小写处理
    public static String getInternalCase(String userId) {
        AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(userId);
        if (accountDomainEnum == AccountDomainEnum.SOGOU || accountDomainEnum == AccountDomainEnum.INDIVID || accountDomainEnum == AccountDomainEnum.OTHER) {
            return userId.toLowerCase();
        }
        return userId;
    }

    ///act/authtoken接口中：第三方和@focus.cn账号以外，其他账号都按小写处理
    public static String getAuthtokenCase(String userId) {
        if (userId.endsWith("@focus.cn")) {
            return userId;
        }
        if (AccountDomainEnum.THIRD == AccountDomainEnum.getAccountDomain(userId)) {
            return userId;
        }
        return userId.toLowerCase();
    }

    //判断是否为手机号或者无@账号，以判断sohu+
    public static boolean isPhoneOrIndivid(String username) {
        return (isPhone(username) || isIndivid(username));
    }

    public static boolean isPhone(String username) {
        return PhoneUtil.verifyPhoneNumberFormat(username);
    }

    public static boolean isIndivid(String username) {
        return (!username.contains("@")) && !isPhone(username);
    }

    public static boolean isPassportId(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        if (username.matches(".+@[a-zA-Z0-9.]+$")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
