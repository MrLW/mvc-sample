package com.lw.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 *  自定义拦截器
 *  1、创建一个类实现HandlerInterceptor
 *  2、在配置文件中配置如下
 * <mvc:interceptors>
 *		<!-- 配置自定义拦截器 -->
 *		<bean class="com.lw.mvc.interceptor.FirstInterceptor"></bean>
 *	</mvc:interceptors>
 * @author lw
 */
public class FirstInterceptor implements HandlerInterceptor {

	/**
	 *  目标方法之前被调用
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("[FirstInterceptor] preHandle");
		return true;
	}
	/**
	 *  调用目标方法之后,渲染视图之前调用
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("[FirstInterceptor] postHandle");
	}

	/**
	 *  渲染视图之后被调用
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("[FirstInterceptor] afterCompletion");

	}

}
