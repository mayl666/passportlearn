package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.CommonConstant;
import org.junit.Ignore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午1:49 To change this template use
 * File | Settings | File Templates.
 *
 * 此类是为验证ControllerHelper检验params类的有效性，供包内测试单元调用，原类在web模块
 */
@Ignore
public class ControllerHelperForTest {
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
        String validateResult;
        if (sb.length() > 0) {
            validateResult = sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            validateResult = "";
        }
        return validateResult;
    }

    public static String getRequests(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Map parameterMap = request.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        for (String key : keys) {
            params.append(key).append("=").append(request.getParameter(key)).append("&");
        }
        String queryString = "";
        if (params.length() > 0) {
            queryString = params.deleteCharAt(params.length() - 1).toString();
        }
        return queryString;
    }
}
