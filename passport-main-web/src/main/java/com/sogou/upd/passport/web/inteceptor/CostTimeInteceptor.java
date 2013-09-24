package com.sogou.upd.passport.web.inteceptor;


import com.sogou.upd.passport.common.CommonConstant;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用perf4j来监控性能
 * @author shipengzhi
 *
 */
public class CostTimeInteceptor extends HandlerInterceptorAdapter {

    private static final String STOPWATCH= CommonConstant.STOPWATCH;

	private static final Logger log = LoggerFactory.getLogger(CostTimeInteceptor.class);

    private static final Logger prefLogger =LoggerFactory.getLogger("webTimingLogger");

    private final static int SLOW_TIME = 500;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        request.setAttribute(STOPWATCH, stopWatch);
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
                    tagBuilder.append("-slow");
                }

                stopWatch.stop(tagBuilder.toString());
            }
        }catch (Exception e){
            log.error("CostTimeInteceptor.afterCompletion error url="+request.getRequestURL(),e);
        }
    }

	

}
