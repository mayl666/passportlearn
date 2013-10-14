package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-4
 * Time: 上午11:56
 * To change this template use File | Settings | File Templates.
 */
public class GenderValidator implements ConstraintValidator<Gender, String> {
    @Override
    public void initialize(Gender constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        String regx = "^(1|2)$";
        if (!value.matches(regx)) {
            return false;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
