package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-23
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public class UniqNameValidator implements ConstraintValidator<UniqName, String> {

    @Override
    public void initialize(UniqName uniqName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        return checkLength(value) && checkSensitive(value) && checkElement(value);
    }

    //检查昵称长度是否符合规则，长度在2——12字符之间
    private boolean checkLength(String value) {
        return value.length() >= 2 && value.length() <= 12;
    }

    //检查昵称中是否含有敏感词,昵称不能含有搜狗，sogou ,sougou字样
    private boolean checkSensitive(String value) {
        String regx = "^(?!.*搜狗)(?!.*sogou)(?!.*sougou)";
        return value.matches(regx);
    }

    //检查昵称是否符合组成规则，只能使用中文、字母、数字和下划线组合，但不能以下划线开头或结尾
    private boolean checkElement(String value) {
        String regx = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        return value.matches(regx);
    }

    /**
     * 昵称不包含特殊字符，只保留 中文、英文大小写字母、空格、-、_
     */
    public static boolean isValidUniqName(String str) {
        if (StringUtils.isNotBlank(str)) {
            str = str.trim();
            for (int i = 0; i < str.length(); i++) {
                String ch = String.valueOf(str.charAt(i));
                boolean isDigest = Pattern.matches("[a-zA-Z0-9_\\-\\s]", ch);
                if (!isDigest) {
                    boolean isChinese = Pattern.matches("[\\u4e00-\\u9fa5]", ch);
                    if (!isChinese) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
