package com.sogou.upd.passport.web.inteceptor;


import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
//import com.sogou.upd.passport.service.app.InterfaceLimitedService;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.apache.commons.collections.MapUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用perf4j来监控性能
 *
 * @author shipengzhi
 */
public class CostTimeInteceptor extends HandlerInterceptorAdapter {

    private static final String STOPWATCH = CommonConstant.STOPWATCH;
    private static final Logger logger = LoggerFactory.getLogger(CostTimeInteceptor.class);
    private static final Logger prefLogger = LoggerFactory.getLogger("webTimingLogger");
    private final static int SLOW_TIME = 500;
    private final static int CONNECT_SLOW_TIME = 1000;


//    @Autowired
//    private InterfaceLimitedService interfaceLimitedService;


//    private static Map clientMapping = new ConcurrentHashMap();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        try {
            Map map= request.getParameterMap();
            StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
            request.setAttribute(STOPWATCH, stopWatch);
        }catch (Exception e){
            logger.error("CostTimeInteceptor error:" + request.getRequestURI(), e);
            return false;
        }
        return true;
//        //获取url对应的方法
//        if (!(handler instanceof HandlerMethod)) {
//            return true;
//        }

//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        InterfaceLimited interfaceLimited = handlerMethod.getMethodAnnotation(InterfaceLimited.class);
        //检查是否加@InterfaceLimited注解，如果没加不需要验证

//        Result result = new APIResultSupport(true);
//        int clientId=0;
//        try {
//            String client_id=request.getParameter(CommonConstant.CLIENT_ID);
//            //先取client_id，如果没有再获取appid
//            if(Strings.isNullOrEmpty(client_id)){
//                client_id=request.getParameter(CommonConstant.APP_ID);
//            }
//            clientId = Integer.parseInt(Strings.isNullOrEmpty(client_id)?"1120":client_id);
//
//            //获取url
//            String url = request.getRequestURI();
//
//            //获取接口全量限制次数
//            //本地内存中不够了再去缓存中获取
//            if (!obtainInterfaceLimited(clientId, url)) {
//                //初始化或获取限制的次数
//                Map<Object, Object> mapResult = interfaceLimitedService.initInterfaceTimes(clientId, url);
//                if (MapUtils.isNotEmpty(mapResult)) {
//                    AtomicInteger atomicGetTimes = (AtomicInteger) mapResult.get("getTimes");
//                    //flag为false 代表缓存中次数已经消耗完，接口频次受限
//                    if (mapResult.get("flag") instanceof Boolean && !(boolean) mapResult.get("flag")) {
//                        result.setSuccess(false);
//                        result.setCode(ErrorUtil.INVOKE_BEYOND_FREQUENCY_LIMIT);
//                        response.setContentType(HttpConstant.ContentType.JSON + ";charset=UTF-8");
//                        response.getWriter().write(result.toString());
//                        return false;
//                    } else {
//                        //在缓存中还有剩余，初始化内存数据
//                        initMemoryTimes(clientId, url, atomicGetTimes);
//                        return true;
//                    }
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            logger.error("CostTimeInteceptor error:" + clientId+" "+request.getRequestURI(), e);
//        } finally {
//            if(!result.isSuccess()){
//                String passportId=this.getPassportId(request);
//                String errorCode=result.getCode();
//                UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), Integer.toString(clientId), errorCode, getIp(request));
//                String referer = request.getHeader("referer");
//                userOperationLog.putOtherMessage("ref", referer);
//                UserOperationLogUtil.log(userOperationLog);
//            }
//        }
//        return true;
    }

//    //在缓存中还有剩余，初始化内存数据
//    public void initMemoryTimes(int clientId, String url, AtomicInteger atomicGetTimes) {
//        if (MapUtils.isNotEmpty(clientMapping)) {
//            Object obj = clientMapping.get(clientId);
//            if (obj != null && obj instanceof Map) {
//                Map<String, AtomicInteger> map = (ConcurrentHashMap<String, AtomicInteger>) obj;
//                atomicGetTimes.getAndDecrement();
//                map.put(url, atomicGetTimes);
//            }
//        }
//    }
//
//    private String getPassportId(HttpServletRequest request){
//         try{
//             String passportId=request.getParameter("username");
//             if(!StringUtil.isBlank(passportId)){
//                return passportId;
//             }
//             passportId=request.getParameter("userid");
//             if(!StringUtil.isBlank(passportId)){
//                 return passportId;
//             }
//             return "";
//         }catch (Exception e){
//             logger.error("",e);
//             return "";
//         }
//    }



//    public boolean obtainInterfaceLimited(int clientId, String url) {
//        //读取内存中限制次数
//        Object obj = clientMapping.get(clientId);
//        if (obj != null && obj instanceof Map) {
//            Map<String, AtomicInteger> map = (ConcurrentHashMap<String, AtomicInteger>) obj;
//            if (map.containsKey(url)) {
//                AtomicInteger atomicGetMemeryTimes = (AtomicInteger) map.get(url);
//                if (atomicGetMemeryTimes != null && atomicGetMemeryTimes.get() >= 1) {
//                    atomicGetMemeryTimes.getAndDecrement();
//                } else {
//                    //内存中无可用次数
//                    return false;
//                }
//            } else {
//                //同一client_id的其他url
//                Map<Object, Object> mapResult = interfaceLimitedService.initInterfaceTimes(clientId, url);
//                if (MapUtils.isNotEmpty(mapResult)) {
//                    AtomicInteger atomicGetTimes = (AtomicInteger) mapResult.get("getTimes");
//                    atomicGetTimes.getAndDecrement();
//                    map.put(url, atomicGetTimes);
//                }
//            }
//        } else {
//            //同一client_id的其他url是否在内存中
//            Map<Object, Object> mapResult = interfaceLimitedService.initInterfaceTimes(clientId, url);
//            if (MapUtils.isNotEmpty(mapResult)) {
//                AtomicInteger atomicGetTimes = (AtomicInteger) mapResult.get("getTimes");
//                atomicGetTimes.getAndDecrement();
//                //初始化新的client_id以及从缓存中获取limited存放内存中
//                Map<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();
//                map.put(url, atomicGetTimes);
//                clientMapping.put(clientId, map);
//            }
//        }
//        return true;
//    }

    private String getIp(HttpServletRequest request) {
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

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        try {
            Object stopWatchObject = request.getAttribute(STOPWATCH);
            if (stopWatchObject != null) {
                StopWatch stopWatch = (StopWatch) stopWatchObject;

                StringBuilder tagBuilder = new StringBuilder(request.getRequestURI());
                String url = tagBuilder.toString();

                //是否为第三方请求
                if(url.contains("/connect/")){
                    if (stopWatch.getElapsedTime() >= CONNECT_SLOW_TIME) {
                        tagBuilder.append(".slow");
                    }
                }else {
                    if (stopWatch.getElapsedTime() >= SLOW_TIME) {
                        tagBuilder.append(".slow");
                    }
                }


                stopWatch.stop(tagBuilder.toString());
            }
        } catch (Exception e) {
            logger.error("CostTimeInteceptor.afterCompletion error url=" + request.getRequestURL(), e);
        }
    }


}
