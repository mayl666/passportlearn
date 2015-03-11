package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.IllegalwordUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-3-10
 * Time: 下午8:33
 * To change this template use File | Settings | File Templates.
 */
public class IllegalSensitiveValidator implements ConstraintValidator<IllegalSensitive, String> {

    @Override
    public void initialize(IllegalSensitive constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }

        if (checkUniqSensitiveSet(value)) {
            return false;
        }

        return true;
    }

    //校验昵称是否包含敏感词汇：true为包含敏感词汇，遍历set
    public boolean checkUniqSensitiveSet(String uniqname) {
        long startTime = System.nanoTime();
        boolean isSensitive = false;
        for (String sensitiveWord : IllegalwordUtil.SENSITIVE_SET) {
            if (uniqname.contains(sensitiveWord)) {
                isSensitive = true;
                break;
            }
        }
        long endTime = System.nanoTime();
        long spendTime = endTime - startTime;
        System.out.println("check uniqname sensitive by set:" + spendTime);
        return isSensitive;
    }
}
