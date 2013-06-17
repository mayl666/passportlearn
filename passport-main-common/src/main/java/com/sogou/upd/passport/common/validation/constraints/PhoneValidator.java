package com.sogou.upd.passport.common.validation.constraints;

import com.sogou.upd.passport.common.utils.PhoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        return PhoneUtil.verifyPhoneNumberFormat(object);
    }
}
