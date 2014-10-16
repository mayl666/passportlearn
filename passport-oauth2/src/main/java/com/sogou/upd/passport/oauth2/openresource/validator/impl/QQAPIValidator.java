package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class QQAPIValidator extends AbstractClientValidator {

    // qq错误码与passport错误码对应表
    public static Map<String, String> QQ_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        QQ_OAUTH_ERROR_MAP.put("1", ErrorUtil.ERR_CODE_COM_REQURIE);
        QQ_OAUTH_ERROR_MAP.put("2", ErrorUtil.ERR_CODE_CONNECT_REQUEST_FREQUENCY_LIMIT);
        QQ_OAUTH_ERROR_MAP.put("6", ErrorUtil.NO_OPEN_BLOG);
        QQ_OAUTH_ERROR_MAP.put("100030", ErrorUtil.ERR_CODE_CONNECT_REQUEST_NO_AUTHORITY);        /*qq的accesstoken非法、过期、废除、验证失败*/
        QQ_OAUTH_ERROR_MAP.put("100013", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100014", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100015", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("100016", ErrorUtil.ERR_CODE_CONNECT_TOKEN_INVALID);
        QQ_OAUTH_ERROR_MAP.put("-73", ErrorUtil.ERR_CODE_CONNECT_TOKEN_PWDERROR);
        QQ_OAUTH_ERROR_MAP.put("-23", ErrorUtil.ERR_CODE_CONNECT_TOKEN_ERROR);


    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
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
                error_code = response.getParam(QQOAuthError.ERROR_CODE);
                errorDesc = response.getParam(QQOAuthError.ERROR_DESCRIPTION);
            }
            throw OAuthProblemException.error(error_code).description(errorDesc);
        }

    }

}
