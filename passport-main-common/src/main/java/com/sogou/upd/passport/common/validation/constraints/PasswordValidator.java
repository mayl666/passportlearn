package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 检查密码格式
 * 格式为AsciiPrintable，为字母、数字、字符且长度为6~16位
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(Password constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        return StringUtils.isAsciiPrintable(value) && value.length() >= 6 && value.length() <= 16;
    }
}
