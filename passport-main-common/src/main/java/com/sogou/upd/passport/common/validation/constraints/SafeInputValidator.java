package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证输入内容是否有效
 * 包含任何html元素的输入都被认为是非法输入
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class SafeInputValidator implements ConstraintValidator<SafeInput, String> {

    @Override
    public void initialize(SafeInput constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        String cleanValue = Jsoup.clean(value, Whitelist.none());
        return cleanValue.equals(value);
    }
}
