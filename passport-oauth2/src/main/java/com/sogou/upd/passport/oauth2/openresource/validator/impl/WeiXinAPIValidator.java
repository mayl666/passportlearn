package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.WeiXinOAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-20
 * Time: 下午8:28
 * To change this template use File | Settings | File Templates.
 */
public class WeiXinAPIValidator extends AbstractClientValidator {
    // 微信错误码与passport错误码对应表
    public static Map<String, String> WEIXIN_OAUTH_ERROR_MAP = new HashMap<String, String>();

    static {
        WEIXIN_OAUTH_ERROR_MAP.put("1", ErrorUtil.ERR_CODE_COM_REQURIE);
        WEIXIN_OAUTH_ERROR_MAP.put("2", ErrorUtil.CONNECT_REQUEST_FREQUENCY_LIMIT);
        WEIXIN_OAUTH_ERROR_MAP.put("6", ErrorUtil.NO_OPEN_BLOG);
        WEIXIN_OAUTH_ERROR_MAP.put("100030", ErrorUtil.REQUEST_NO_AUTHORITY);        /*qq的accesstoken非法、过期、废除、验证失败*/
        WEIXIN_OAUTH_ERROR_MAP.put("100013", ErrorUtil.CONNECT_TOKEN_INVALID);
        WEIXIN_OAUTH_ERROR_MAP.put("100014", ErrorUtil.CONNECT_TOKEN_INVALID);
        WEIXIN_OAUTH_ERROR_MAP.put("100015", ErrorUtil.CONNECT_TOKEN_INVALID);
        WEIXIN_OAUTH_ERROR_MAP.put("100016", ErrorUtil.CONNECT_TOKEN_INVALID);
        WEIXIN_OAUTH_ERROR_MAP.put("-73", ErrorUtil.ERR_CODE_CONNECT_TOKEN_PWDERROR);
        WEIXIN_OAUTH_ERROR_MAP.put("-23", ErrorUtil.ERR_CODE_CONNECT_TOKEN_ERROR);


    }

    @Override
    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException {
        String error_code = response.getParam(WeiXinOAuthError.ERROR_CODE);
        if (!StringUtils.isEmpty(error_code) && !error_code.equals("0")) {
            String errorDesc = "";
            if (error_code.equals("5")) {
                error_code = ErrorUtil.ALREADY_FOLLOWED; // 不允许重复关注
            } else {
                error_code = WEIXIN_OAUTH_ERROR_MAP.get(error_code);
            }
            if (StringUtil.isEmpty(error_code)) {
                error_code = response.getParam(WeiXinOAuthError.ERROR_CODE);
                errorDesc = response.getParam(WeiXinOAuthError.ERROR_MSG);
            }
            throw OAuthProblemException.error(error_code).description(errorDesc);
        }

    }
}
