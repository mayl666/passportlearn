package com.sogou.upd.passport.web;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

public class BaseController {

    protected static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private AppConfigService appConfigService;

    protected static String getIp(HttpServletRequest request) {
        String sff = request.getHeader("X-Forwarded-For");// 根据nginx的配置，获取相应的ip
        if (Strings.isNullOrEmpty(sff)) {
            sff = request.getHeader("X-Real-IP");
        }
        if (Strings.isNullOrEmpty(sff)) {
            return Strings.isNullOrEmpty(request.getRemoteAddr()) ? "" : request.getRemoteAddr();
        }
        String[] ips = sff.split(",");
        String realip = ips[0];
        return realip;
    }

    public boolean isAccessAccept(int clientId, HttpServletRequest request) {
        String apiName = request.getRequestURI();
        apiName = apiName.substring(apiName.lastIndexOf("/") + 1, apiName.length());
        String requestIp = getIp(request);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                return false;
            }
            String scope = appConfig.getScope();
            if (!Strings.isNullOrEmpty(apiName) && !StringUtil.splitStringContains(scope, ",", apiName)) {
                return false;
            }
            String serverIp = appConfig.getServerIp();
            if (!Strings.isNullOrEmpty(requestIp) && !StringUtil.splitStringContains(serverIp, ",", requestIp)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("isAccessAccept error, api:" + apiName, e);
            return false;
        }
    }

    /**
     * 跳转到回跳地址
     *
     * @param response
     * @param ru
     * @param errorCode
     * @param errorMsg
     * @throws Exception
     */
    public void returnErrMsg(HttpServletResponse response, String ru, String errorCode, String errorMsg) throws Exception {
//        if (Strings.isNullOrEmpty(ru) || "域名不正确".equals(errorMsg)) {
//            ru = CommonConstant.DEFAULT_INDEX_URL;
//        }

        //fix invalid ru redirect 安全漏洞
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }

        if (StringUtils.contains(errorMsg, CommonConstant.DOMAIN_ERROR) || CommonConstant.DOMAIN_ERROR.equals(errorMsg)) {
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }

        Map paramMap = Maps.newHashMap();
        paramMap.put("errorCode", errorCode);
        paramMap.put("errorMsg", errorMsg);
        ru = ServletUtil.applyOAuthParametersString(ru, paramMap);
        response.sendRedirect(ru);
        return;
    }

    /*
     * jsonp的cb参数相关方法
     */
    protected boolean isCleanString(String cb) {
        if (Strings.isNullOrEmpty(cb)) {
            return true;
        }
        String cleanValue = Jsoup.clean(cb, Whitelist.none());
        return cleanValue.equals(cb);
    }

    /**
     * 获取request header的输入法的UA标识，如果包含sogou_ime，则代表是输入法，否则返回空
     *
     * @param request
     * @return
     */
    protected String getHeaderUserAgent(HttpServletRequest request) {
        String ua = request.getHeader(CommonConstant.USER_AGENT);
        ua = !Strings.isNullOrEmpty(ua) && ua.contains(CommonConstant.SOGOU_IME_UA) ? ua : ""; //输入法的标识
        return ua;
    }

}
