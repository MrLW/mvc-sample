package com.lw.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lw.mvc.pojo.User;
/**
 * @author lw
 */
@Controller
@RequestMapping("/mvc")
public class SpringMVCTest2 {
	
	private static final String SUCCESS = "success";
	
	/**
	 * @CookieValue:获取一个cookie值 
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value = "/testCookieValue" , method=RequestMethod.PUT)
	public String testCookieValue(@CookieValue("JSESSIONID") String sessionId){
		System.out.println("testCookieValue sessionId:" + sessionId );
		return SUCCESS ;
	}
	
	/**
	 *  传递POJO
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/testPojo" , method=RequestMethod.POST)
	public String testPojo(User user){
		System.out.println("testPojo:" + user);
		return SUCCESS ;
	}
	
	
	/**
	 *  测试原生Servlet,通过debug调试查看源码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/testServlet" )
	public String testServlet(HttpServletRequest request,HttpServletResponse response){
		System.out.println("request:" + request + "response:" + response);
		return SUCCESS ;
	}
	
	
	
}
