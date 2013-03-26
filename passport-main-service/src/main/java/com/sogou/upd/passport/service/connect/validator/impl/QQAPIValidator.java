package com.sogou.upd.passport.service.connect.validator.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.parameters.QQOAuthError;

public class QQAPIValidator extends AbstractResponseValidator {

    // qq错误码与passport错误码对应表
    public static Map<String, String> QQ_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        QQ_OAUTH_ERROR_MAP.put("1", ErrorUtil.ERR_CODE_COM_REQURIE);
        QQ_OAUTH_ERROR_MAP.put("2", ErrorUtil.CONNECT_REQUEST_FREQUENCY_LIMIT);
        QQ_OAUTH_ERROR_MAP.put("6", ErrorUtil.NO_OPEN_BLOG);
        QQ_OAUTH_ERROR_MAP.put("100030", ErrorUtil.REQUEST_NO_AUTHORITY);        /*qq的accesstoken非法、过期、废除、验证失败*/
        QQ_OAUTH_ERROR_MAP.put("100013", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100014", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100015", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100016", ErrorUtil.CONNECT_ASSOCIATE_TOKEN_INVALID);
    }

    @Override
    public void validateErrorResponse(OAuthResponse response) throws ProblemException {
        String error_code = response.getParam(QQOAuthError.ERROR_CODE);
        String second_error_code = response.getParam(QQOAuthError.SECOND_ERROR_CODE);
        if (!StringUtils.isEmpty(error_code) && !error_code.equals("0")) {
            String errorDesc = "";
            if (error_code.equals("5") && second_error_code.equals("80103")) {
                error_code = ErrorUtil.ALREADY_FOLLOWED; // 不允许重复关注
            } else {
                error_code = QQ_OAUTH_ERROR_MAP.get(error_code);
            }
            if (StringUtil.isEmpty(error_code)) {
                error_code = ErrorUtil.CONNECT_USER_DEFINED_ERROR;
                errorDesc = response.getParam(QQOAuthError.ERROR_DESCRIPTION);
            }
            throw ProblemException.error(error_code).description(errorDesc);
        }

    }

}
