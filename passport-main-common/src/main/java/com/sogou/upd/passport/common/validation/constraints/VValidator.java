package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查v是否是指定版本
 * wap版本:1-简易版；2-炫彩版；5-触屏版  0- json
 * User: mayan
 * Date: 14-1-15
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class VValidator implements ConstraintValidator<V, String> {

    private static List SUPPORT_VERSION = Lists.newArrayList();

    static {
        SUPPORT_VERSION.add("0");
        SUPPORT_VERSION.add("1");
        SUPPORT_VERSION.add("2");
        SUPPORT_VERSION.add("5");
    }

    @Override
    public void initialize(V constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        if (!SUPPORT_VERSION.contains(value)) {
            return false;
        }

        return true;
    }

}
