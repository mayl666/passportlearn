package com.sogou.upd.passport.web;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.web.converters.CustomDateEditor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    public static final
    String
            INTERNAL_HOST =
            "api.id.sogou.com.z.sogou-op.org;dev01.id.sogou.com;test01.id.sogou.com";

    protected static Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 判断是否是服务端签名
     */
    protected boolean isServerSig(String client_signature, String signature) {
        if (StringUtils.isEmpty(signature)) {
            return false;
        }
        return true;
    }

    /**
     * 验证参数是否有空参数
     */
    protected boolean hasEmpty(String... args) {

        if (args == null) {
            return false;
        }

        Object[] argArray = getArguments(args);
        for (Object obj : argArray) {
            if (obj instanceof String && StringUtils.isEmpty((String) obj)) {
                return true;
            }
        }
        return false;
    }

    private Object[] getArguments(Object[] varArgs) {
        if (varArgs.length == 1 && varArgs[0] instanceof Object[]) {
            return (Object[]) varArgs[0];
        } else {
            return varArgs;
        }
    }

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

    protected boolean isInternalRequest(HttpServletRequest request) {

        String host = request.getServerName();
        String[] hosts = INTERNAL_HOST.split(";");
        int i = Arrays.binarySearch(hosts, host);
        if (i >= 0) {
            return true;
        }
        return false;
    }

    protected Result setDefaultModelForResult(Result result, String uniqName, AccountToken accountToken, String loginType) throws Exception {
        result.setDefaultModel("accesstoken", accountToken.getAccessToken());
        result.setDefaultModel("refreshtoken", accountToken.getRefreshToken());
        result.setDefaultModel("nick", Coder.enBase64(uniqName));
        result.setDefaultModel("sname", Coder.enBase64(accountToken.getPassportId()));
        result.setDefaultModel("passport", Coder.enBase64(accountToken.getPassportId()));
        result.setDefaultModel("result", 0);
        result.setDefaultModel("sid", 0);
        result.setDefaultModel("logintype", loginType);
        return result;
    }


}
