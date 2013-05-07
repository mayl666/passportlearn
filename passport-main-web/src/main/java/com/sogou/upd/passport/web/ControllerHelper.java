package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * API工具类
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
public class ControllerHelper {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * API请求参数校验
     */
    public static <T> String validateParams(T requestParams) {

        StringBuilder sb = new StringBuilder();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(requestParams);
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            sb.append(constraintViolation.getMessage()).append(CommonConstant.SEPARATOR_1);
        }

        return sb.toString();
    }

    public static String getRequests(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Map parameterMap = request.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        for (String key : keys) {
            params.append(key).append("=").append(request.getParameter(key));
        }
        String queryString = "";
        if (params.length() > 0) {
            queryString = params.deleteCharAt(params.length() - 1).toString();
        }
        return queryString;
    }


}
