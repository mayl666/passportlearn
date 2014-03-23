package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-23
 * Time: 下午9:41
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdTypeValidator implements ConstraintValidator<FindPwdType, String> {

    @Override
    public void initialize(FindPwdType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        if ("bind_email".equals(value) || "reg_email".equals(value) || "bind_mobile".equals(value)
                || "reg_mobile".equals(value) || "question".equals(value)) {
            return true;
        }
        return false;
    }
}
