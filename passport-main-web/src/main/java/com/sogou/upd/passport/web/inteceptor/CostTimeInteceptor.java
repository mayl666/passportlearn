package com.sogou.upd.passport.web.inteceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.upd.passport.common.lang.StringUtil;
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

    private static final String STOPWATCH= "stopWatch";

    private static final String CLIENT_ID_PARAM="client_id";

	private static final Logger log = LoggerFactory.getLogger(CostTimeInteceptor.class);

    private static final org.apache.log4j.Logger prefLogger = org.apache.log4j.Logger.getLogger("webTimingLogger");

    private final static int SLOW_TIME = 500;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StopWatch stopWatch = new Log4JStopWatch(prefLogger);
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
                String tag= request.getRequestURI();
                if(stopWatch.getElapsedTime() >= SLOW_TIME){
                    tag+="(slow)";
                }
                String message="success";
                if(ex!=null){
                    message="failed";
                }
                stopWatch.stop(tag, message);
                String cleintId = request.getParameter(CLIENT_ID_PARAM);
                if(!StringUtil.isBlank(cleintId)&&request.getMethod().toUpperCase().equals("POST")){
                    stopWatch.stop(tag+"-"+cleintId);
                }
            }
        }catch (Exception e){
            log.error("CostTimeInteceptor.afterCompletion error url="+request.getRequestURL(),e);
        }
    }

	

}
