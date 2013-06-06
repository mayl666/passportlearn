//package com.sogou.upd.passport.web.internal.inteceptor;
//
//import com.sogou.upd.passport.common.lang.StringUtil;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * User: ligang201716@sogou-inc.com
// * Date: 13-6-5
// * Time: 下午2:06
// */
//public class InternalMethodFilter  extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        Map m=new HashMap(request.getParameterMap());
//
//        String hMethod= request.getHeader("internal_method");
//
//        if(!StringUtil.isBlank(hMethod)){
//            filterChain.doFilter(request,response);
//        }
//        ParameterRequestWrapper wrapRequest=new ParameterRequestWrapper(request);
//        String passportId= request.getParameter("passport_id");
//        String value=null;
//        if(passportId!=null&&passportId.equals("proxy")){
//            value="proxy";
//        }else{
//            value="sgpp";
//        }
//
//        wrapRequest.setHeader("internal_method",value);
//
//        filterChain.doFilter(wrapRequest,response);
//    }
//}
