package com.sogou.upd.passport.web.inteceptor;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.annotation.UsernameInternalCase;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 拦截所有内部接口带有@InterfaceSecurity注解，
 * 校验code和ct
 * 如果校验通过执行方法
 * 如果校验不通过，根据配置的{@link com.sogou.upd.passport.web.annotation.ResponseResultType}做相应处理
 * User: shipengzhi@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午22:53
 */
public class UsernameInternalCaseInteceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(UsernameInternalCaseInteceptor.class);

    private static final long API_REQUEST_VAILD_TERM = 500000 * 60 * 1000l; //接口请求的有效期为5分钟，单位为秒

    @Autowired
    private AppConfigService appConfigService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        UsernameInternalCase internalCase = handlerMethod.getMethodAnnotation(UsernameInternalCase.class);
        if (internalCase == null) {
            return true;
        }
        Result result = new APIResultSupport(false);
        try {
            // read request parameters
            String username = request.getParameter(CommonConstant.RESQUEST_USERNAME);
            String userid = request.getParameter(CommonConstant.RESQUEST_USERID);
            if(!StringUtils.isBlank(username)){
                username = AccountDomainEnum.getInternalCase(username);
//                request.set
            }

        } catch (Exception e) {
            StringBuilder requestInfo = new StringBuilder();
            try {
                requestInfo.append(" uri:");
                requestInfo.append(request.getRequestURI());
                requestInfo.append(" ip:");
                requestInfo.append(IpLocationUtil.getIp(request));
                requestInfo.append("     requestInfo: { ");
                Map map = request.getParameterMap();
                for (Object key : map.keySet().toArray()) {
                    requestInfo.append(key.toString());
                    requestInfo.append(":");
                    requestInfo.append(request.getParameter(key.toString()));
                    requestInfo.append(",");
                }

            } catch (Exception ex) {
                log.error("get requestInfo error ", ex);
            }
            requestInfo.append("}");

            log.error("InterfaceSecurityInteceptor verify code or ct error! "+requestInfo.toString(), e);
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
        }
        return false;
    }

    /**
     * 构造code签名字符串的第一个字符，注意顺序为userid+mobile+token
     *
     * @param request
     * @return
     */
    private String buildFirstSignString(HttpServletRequest request) {
        String userid = request.getParameter("userid");
        String mobile = request.getParameter("mobile");
        String token = request.getParameter("token");
        String uniqName = request.getParameter("uniqname");
        StringBuffer firstStr = new StringBuffer();
        if (!Strings.isNullOrEmpty(userid)) {
            firstStr.append(userid);
        } else if (!Strings.isNullOrEmpty(mobile)) {
            firstStr.append(mobile);
        } else if (!Strings.isNullOrEmpty(token)) {
            firstStr.append(token);
        } else if (!Strings.isNullOrEmpty(uniqName)){
            firstStr.append(uniqName);
        }
        return firstStr.toString();
    }
}
