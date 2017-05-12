package com.lw.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 *  �Զ���������
 *  1������һ����ʵ��HandlerInterceptor
 *  2���������ļ�����������
 * <mvc:interceptors>
 *		<!-- �����Զ��������� -->
 *		<bean class="com.lw.mvc.interceptor.FirstInterceptor"></bean>
 *	</mvc:interceptors>
 * @author lw
 */
public class FirstInterceptor implements HandlerInterceptor {

	/**
	 *  Ŀ�귽��֮ǰ������
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("[FirstInterceptor] preHandle");
		return true;
	}
	/**
	 *  ����Ŀ�귽��֮��,��Ⱦ��ͼ֮ǰ����
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("[FirstInterceptor] postHandle");
	}

	/**
	 *  ��Ⱦ��ͼ֮�󱻵���
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("[FirstInterceptor] afterCompletion");

	}

}
