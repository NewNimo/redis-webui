package com.nimo.rediswebui.intercptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


/**
 * 
* @author nimo
* @date 2017年11月23日 
* @version V1.0  
* @Description: Session 拦截器
 */
@Component
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {


	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView arg3)
			throws Exception {
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session= request.getSession();
		if(session.getAttribute("user")==null){
			 String url = "/index";
			 response.sendRedirect(url);
			 //request.getRequestDispatcher(url).forward(request,response);
			 log.info("preHandle请求方法 {}", request.getRequestURI());
		     return false;
		}
		if(request.getMethod().equals(RequestMethod.GET.toString())){
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			String arg = JSON.toJSONString(request.getParameterMap());
			MDC.put("traceId", uuid);
			log.info("{} 请求方法 {} 参数为:{}", uuid, request.getRequestURI(), arg);
		}
		return true;
	}

}
