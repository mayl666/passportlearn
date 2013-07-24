package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
        return (value.length() >= 2 && value.length() <= 12) && (value.matches("^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$"));
    }
}
