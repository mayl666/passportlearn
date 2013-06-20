package com.sogou.upd.passport.web.inteceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.upd.passport.web.ControllerHelper;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 使用perf4j来监控性能
 * @author shipengzhi
 *
 */
public class CostTimeInteceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(CostTimeInteceptor.class);

    private static final org.apache.log4j.Logger prefLogger = org.apache.log4j.Logger.getLogger("webTimingLogger");

    private final static int SLOW_TIME = 500;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StopWatch stopWatch = new Log4JStopWatch(prefLogger);
        request.setAttribute("stopWatch", stopWatch);
		return true;
	}

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        try{
            Object stopWatchObject=request.getAttribute("stopWatch");
            if(stopWatchObject!=null){
                StopWatch stopWatch= (StopWatch) stopWatchObject;
                String tag= request.getRequestURI();
                if(stopWatch.getElapsedTime() >= SLOW_TIME){
                    tag+="(slow)";
                }
                String message="success";
                if(ex!=null){
                    message="failed";
                }
                stopWatch.stop(tag, message);
            }
        }catch (Exception e){
            log.error("CostTimeInteceptor.afterCompletion error url="+request.getRequestURL(),e);
        }
    }

	

}
