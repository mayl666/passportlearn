package com.sogou.upd.passport.web.inteceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 身份和安全验证拦截器
 * @author shipengzhi
 *
 */
public class CostTimeInteceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(CostTimeInteceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;
		if (log.isInfoEnabled()) {
			String param = ControllerHelper.getRequests(request);
			log.info("[" + request.getRequestURI() + "] executeTime : " + executeTime + "ms, params : "+param);
			
		}
	}
	

}
