package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查回调的url是否在sogou.com或sohu.com域下
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class RuValidator implements ConstraintValidator<Ru, String> {

    @Override
    public void initialize(Ru constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        String rootPath;
        try {
            Pattern p = Pattern.compile("(?<=(http|https)://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(value);
            matcher.find();
            rootPath = matcher.group();
        } catch (Exception e) {
            return false;
        }

        if (!rootPath.equalsIgnoreCase("sogou.com") && !rootPath.equalsIgnoreCase("sohu.com")) {
            return false;
        }

        return true;
    }

}
