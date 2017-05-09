package com.lw.mvc.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.lw.mvc.pojo.User;
/**
 * @author lw
 */
/**
 * @SessionAttributes:作用到类上面,value为数组,存放要放入session域中的键
 */
@SessionAttributes(value={"ghi"})
@Controller
@RequestMapping("/mvc")
public class SpringMVCTest3 {
	
	private static final String SUCCESS = "success";
	
	
	/**
	 */
	@RequestMapping(value = "/testMap" )
	public String testMap(Map<String, Object> map){
		map.put("name", "小明");
		return SUCCESS ;
	}
	
	/**
	 * 目标方法的返回值可以是 ModelAndView 类型。 
	 * 其中可以包含视图和模型信息
	 * SpringMVC 会把 ModelAndView 的 model 中数据放入到 request 域对象中. 
	 * @return
	 */
	@RequestMapping("/testModelAndView")
	public ModelAndView testModelAndView(){
		String viewName = SUCCESS;
		ModelAndView modelAndView = new ModelAndView(viewName);
		
		//添加模型数据到 ModelAndView 中.
		modelAndView.addObject("time", new Date());
		
		return modelAndView;
	}
	
	/**
	 * 
	 */
	@RequestMapping("/testSessionAttributes")
	public String testSessionAttributes(Map<String, Object> map){
		User user = new User("文哥", "123", "123@qq.com", 11);
		//  
		map.put("user", user);
		
		return SUCCESS ;
	}
	
	
	/**
	 * 测试@ModelAndAttribute注解
	 */
	@RequestMapping("/testModelAttribute")
	public String testModelAttribute(@ModelAttribute("abc") User user){
		System.out.println("修改： " + user);
		
		return SUCCESS ;
	}
	/**
	 *  被@ModelAttribute标记的方法,在执行之前都会执行此方法
	 */
	@ModelAttribute
	public void getUser(@RequestParam(value="id",required=false)Integer id,
			Map<String,Object> map){
		if(id != null ){
			// 模拟从数据库中获取对象
			User user = new User("Tom", "123456", "tom@qq.com", 12);
			user.setId(1);
			System.out.println("从数据库中获取一个对象:" + user);
			map.put("efg", user);
		}
	}
}
