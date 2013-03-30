package com.sogou.upd.passport.web;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.exception.ApplicationException;
import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * API工具类
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * API请求参数校验
     */
    public static <T> String validateParams(T requestParams) {

        StringBuilder sb = new StringBuilder();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(requestParams);
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            sb.append(constraintViolation.getMessage()).append(CommonParameters.SEPARATOR_1);
        }

        return sb.toString();
    }
}
