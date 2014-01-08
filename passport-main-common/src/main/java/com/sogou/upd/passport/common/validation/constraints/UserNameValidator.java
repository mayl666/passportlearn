package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-30
 * Time: 下午2:03
 * To change this template use File | Settings | File Templates.
 */
public class UserNameValidator implements ConstraintValidator<UserName, String> {
    @Override
    public void initialize(UserName constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        if (value.indexOf("@") == -1) {
            if (!PhoneUtil.verifyPhoneNumberFormat(value)) {
                //个性账号格式是否拼配，{3，15}就表示4--16位，必须字母开头，不作大小写限制
                String regx = "[a-z]([a-zA-Z0-9_.-]{3,15})";
                boolean flag = value.matches(regx);
                if (!flag) {
                    return false;
                }
            }
        } else {
            //邮箱格式,与sohu的邮箱格式相匹配了
            String reg = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
            boolean flag = value.matches(reg);
            if (!flag) {
                return false;
            }
        }
        return true;
    }
}
