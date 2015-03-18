package com.sogou.upd.passport.web.inteceptor;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
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
public class InterfaceSecurityInteceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(InterfaceSecurityInteceptor.class);

    private static final long API_REQUEST_VAILD_TERM = 500000 * 60 * 1000l; //接口请求的有效期为5分钟，单位为秒

    @Autowired
    private AppConfigService appConfigService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler) throws java.lang.Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        InterfaceSecurity security = handlerMethod.getMethodAnnotation(InterfaceSecurity.class);
        //检查是否加@InterfaceSecurity注解，如果没加不需要验证
        if (security == null) {
            return true;
        }
        Result result = new APIResultSupport(false);
        try {
            // read request parameters
            String client_id = ServletRequestUtils.getRequiredStringParameter(request, CommonConstant.CLIENT_ID);
            int clientId = Integer.parseInt(StringUtils.trim(client_id));

            String c_t = ServletRequestUtils.getRequiredStringParameter(request, CommonConstant.RESQUEST_CT);
            long ct = Long.parseLong(StringUtils.trim(c_t));

//            int clientId = Integer.parseInt(request.getParameter(CommonConstant.CLIENT_ID));
//            long ct = Long.parseLong(request.getParameter(CommonConstant.RESQUEST_CT));
            String originalCode = request.getParameter(CommonConstant.RESQUEST_CODE);

            String firstStr = buildFirstSignString(request);
            if (!Strings.isNullOrEmpty(firstStr)) {
                AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
                if (appConfig != null) {
                    String secret = appConfig.getServerSecret();
                    String code = ManagerHelper.generatorCode(firstStr.toString(), clientId, secret, ct);
                    long currentTime = System.currentTimeMillis();
                    if (code.equalsIgnoreCase(originalCode) && ct > currentTime - API_REQUEST_VAILD_TERM) {
                        return true;
                    } else {
                        result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
                    }
                } else {
                    result.setCode(ErrorUtil.INVALID_CLIENTID);
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
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

            log.warn("InterfaceSecurityInteceptor verify code or ct error! " + requestInfo.toString(), e);
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
        }


        //检测不通过，根据配置返回
        ResponseResultType resultType = security.resultType();
        String msg = "";
        switch (resultType) {
            case json:
                msg = result.toString();
                response.setContentType(HttpConstant.ContentType.JSON + ";charset=UTF-8");
                response.getWriter().write(msg);
                break;
            case xml:
            case txt:
            case forward:
            case redirect:
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
        String key = request.getParameter("key");
        StringBuffer firstStr = new StringBuffer();
        if (!Strings.isNullOrEmpty(userid)) {
            firstStr.append(userid);
        } else if (!Strings.isNullOrEmpty(mobile)) {
            firstStr.append(mobile);
        } else if (!Strings.isNullOrEmpty(token)) {
            firstStr.append(token);
        } else if (!Strings.isNullOrEmpty(uniqName)) {
            firstStr.append(uniqName);
        }
        //适配debug接口，只有一个key参数的签名校验
        if (firstStr.length() == 0 && !Strings.isNullOrEmpty(key)) {
            firstStr.append(key);
        }
        return firstStr.toString();
    }
}
