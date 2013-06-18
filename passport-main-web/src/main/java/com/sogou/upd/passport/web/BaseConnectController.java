package com.sogou.upd.passport.web;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */
public class BaseConnectController extends BaseController {

    /**
     * 为防止CRSF攻击，OAuth登录授权需传递state参数
     * 种cookie，uuid=provider_state
     */
    protected void writeOAuthStateCookie(HttpServletResponse res, String uuid, int provider) {
        String cookieValue = CommonHelper.constructStateCookieKey(provider);
        ServletUtil.setCookie(res, uuid, cookieValue, CommonConstant.DEFAULT_COOKIE_EXPIRE);
    }

    /**
     * 第三方登录接口type为移动端时，需要在ru后追加status和statusText
     *
     * @param type      /connect/login接口的type参数
     * @param errorCode 错误码
     * @param errorText 错误文案
     * @return
     */
    protected String buildAppErrorRu(String type, String errorCode, String errorText) {
        String url = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        boolean isApp = type.equals(ConnectTypeEnum.APP.toString());
        if (isApp && !Strings.isNullOrEmpty(errorCode)) {
            Map params = Maps.newHashMap();
            params.put(CommonConstant.RESPONSE_STATUS, errorCode);
            if (Strings.isNullOrEmpty(errorText)) {
                errorText = ErrorUtil.ERR_CODE_MSG_MAP.get(errorCode);
            }
            params.put(CommonConstant.RESPONSE_STATUS_TEXT, errorText);
            url = QueryParameterApplier.applyOAuthParametersString(url, params);
        }
        return url;
    }


}
