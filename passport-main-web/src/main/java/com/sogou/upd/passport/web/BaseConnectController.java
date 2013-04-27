package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ServletUtil;

import javax.servlet.http.HttpServletResponse;
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
        String cookieValue = AccountTypeEnum.getProviderStr(provider) + "_state";
        ServletUtil.setCookie(res, uuid, cookieValue, CommonConstant.DEFAULT_COOKIE_EXPIRE);
    }
}
