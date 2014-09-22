package com.sogou.upd.passport.web.inteceptor;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.ParseCookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 拦截所有请求，通过cookie验证用户是否已经登录
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午9:14
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler) throws java.lang.Exception {
       try{
           if (this.setPassportId(request)) {
               this.setNickname(request);
           }
       }catch (Exception e){
           logger.error("LoginInterceptor parse cookie parse header error ",e);
       }
       return true;
    }

    public boolean setPassportId(HttpServletRequest request) {
        try {
            String passportId = request.getHeader(LoginConstant.USER_ID_HEADER);
            //解决中文账号cookie问题
            if (passportId.indexOf("@focus.cn") > 0) {
                char[] carr = passportId.toCharArray();
                byte[] barr = new byte[carr.length];
                for (int i = 0; i < carr.length; i++) {
                    barr[i] = (byte) (carr[i]);
                }
                try {
                    passportId = new String(new String(barr, "utf-8"));
                    // email = new String(new String(barr, "utf-8").getBytes(),
                    // "GBK");
                } catch (UnsupportedEncodingException e) {
                }
            }

            if (StringUtil.isBlank(passportId)) {
                return false;
            }
            hostHolder.setPassportId(passportId);
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    public boolean setNickname(HttpServletRequest request) {
        Map<String, String> userinfoMap = ParseCookieUtil.parsePpinf(request);
        if (!userinfoMap.containsKey(ParseCookieUtil.PPINF_UNIQNAME)) {
            return false;
        }
        String nickname = userinfoMap.get(ParseCookieUtil.PPINF_UNIQNAME);
        try {
            nickname = URLDecoder.decode(nickname, CommonConstant.DEFAULT_CHARSET);
            if(StringUtil.isBlank(nickname)||nickname.startsWith("搜狐网友")){
                nickname=userinfoMap.get(ParseCookieUtil.PPINF_USERID);
            }
            hostHolder.setNickName(nickname);
            return true;
        } catch (UnsupportedEncodingException e) {
        }
        return false;
    }
}
