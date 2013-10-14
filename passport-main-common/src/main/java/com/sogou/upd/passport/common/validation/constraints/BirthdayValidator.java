package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-4
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class BirthdayValidator implements ConstraintValidator<Birthday, String> {
    @Override
    public void initialize(Birthday constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 这个正则表达式仅适用于2000-5-5这种格式，日期在1900-0-0到2099-12-31之间,输入2000-05-05也正确
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        String regx = "^(19|20)\\d{2}-(1[0-2]|0[0-9])-(0[1-9]|[1-2][0-9]|3[0-1])$";
        boolean flag = value.matches(regx);
        if (!value.matches(regx)) {
            return false;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
