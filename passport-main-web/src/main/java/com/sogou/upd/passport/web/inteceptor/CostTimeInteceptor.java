package com.sogou.upd.passport.web.inteceptor;


import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.service.account.InterfaceLimitedService;
import com.sogou.upd.passport.web.annotation.InterfaceLimited;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用perf4j来监控性能
 * @author shipengzhi
 *
 */
public class CostTimeInteceptor extends HandlerInterceptorAdapter {

    private static final String STOPWATCH= CommonConstant.STOPWATCH;
	private static final Logger logger = LoggerFactory.getLogger(CostTimeInteceptor.class);
    private static final Logger prefLogger =LoggerFactory.getLogger("webTimingLogger");
    private final static int SLOW_TIME = 500;

    @Autowired
    private InterfaceLimitedService interfaceLimitedService;

    private static Map clientMapping=Maps.newHashMap();

    private Lock lock=new ReentrantLock();

    @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        request.setAttribute(STOPWATCH, stopWatch);
        //获取url对应的方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        InterfaceLimited interfaceLimited = handlerMethod.getMethodAnnotation(InterfaceLimited.class);
        //检查是否加@InterfaceLimited注解，如果没加不需要验证
        if (interfaceLimited == null) {
            return true;
        }else{
                Result result = new APIResultSupport(false);
                try {
                    int clientId = Integer.parseInt(request.getParameter(CommonConstant.CLIENT_ID));

                    //获取url
                    String url = request.getRequestURI();

                    //获取接口全量限制次数
                    //本地内存中不够了再去缓存中获取
                    if (!processInterfaceLimited(clientId, url)) {
                        //初始化或获取限制的次数
                        Map<Object,Object> mapResult=interfaceLimitedService.isObtainLimitedTimesSuccess(clientId,url);
                        String getTimes= (String) mapResult.get("getTimes");
                        if (mapResult.get("flag") instanceof Boolean && !(boolean)mapResult.get("flag")) {
                            result.setCode(ErrorUtil.ERR_CODE_CLIENT_INBLACKLIST);
                            response.setContentType(HttpConstant.ContentType.JSON + ";charset=UTF-8");
                            response.getWriter().write(result.toString());
                            return false;
                        } else {
                            //此接口在缓存中还有剩余，初始化内存数据
                            Object obj = clientMapping.get(clientId);
                            if (obj != null && obj instanceof Map) {
                                Map<String, String> map = (Map<String, String>) obj;
                                map.put(url, getTimes);
                            }
                            return true;
                        }
                    }
                    return true;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
        }
		return true;
	}

    public boolean processInterfaceLimited(int clientId,String url){
        Object obj = clientMapping.get(clientId);
        if (obj != null && obj instanceof Map) {
            Map<String, String> map = (Map<String, String>) obj;
            if (map.containsKey(url)) {
                int value = Integer.parseInt(map.get(url));
                System.out.println(value);
                if (value > 0) {
                    try {
                        lock.lock();
                        map.put(url, String.valueOf(--value));
                    } finally {
                        lock.unlock();
                    }
                } else {
                    return false;
                }
            } else {
                //同一client_id的其他url
                Map<Object,Object> mapResult=interfaceLimitedService.isObtainLimitedTimesSuccess(clientId,url);
                map.put(url, (String)mapResult.get("getTimes"));
            }
        } else {

            //同一client_id的其他url
            Map<Object,Object> mapResult=interfaceLimitedService.isObtainLimitedTimesSuccess(clientId,url);
            //初始化新的client_id以及从缓存中获取limited
            Map<String, String> map = Maps.newHashMap();
            map.put(url, (String)mapResult.get("getTimes"));
            clientMapping.put(clientId, map);
        }
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        try{
            Object stopWatchObject=request.getAttribute(STOPWATCH);
            if(stopWatchObject!=null){
                StopWatch stopWatch= (StopWatch) stopWatchObject;

                StringBuilder tagBuilder=new StringBuilder(request.getRequestURI());

                //检测是否慢请求
                if(stopWatch.getElapsedTime() >= SLOW_TIME){
                    tagBuilder.append(".slow");
                }

                stopWatch.stop(tagBuilder.toString());
            }
        }catch (Exception e){
            logger.error("CostTimeInteceptor.afterCompletion error url="+request.getRequestURL(),e);
        }
    }

	

}
