package com.sogou.upd.passport.web;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
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

    public static <T> Result checkParams(T requestParams) {
        Result result = new APIResultSupport(false);
        String validateResult = validateParams(requestParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    public static Result process(Result result, int client_id, String ru) {
        if (result == null) {
            result = new APIResultSupport(true);
        }

        result.setDefaultModel(CommonConstant.CLIENT_ID, client_id);

        if (!Strings.isNullOrEmpty(ru)) {
            result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
        }
        return result;
    }
}
