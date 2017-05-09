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
 * @SessionAttributes:���õ�������,valueΪ����,���Ҫ����session���еļ�
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
		map.put("name", "С��");
		return SUCCESS ;
	}
	
	/**
	 * Ŀ�귽���ķ���ֵ������ ModelAndView ���͡� 
	 * ���п��԰�����ͼ��ģ����Ϣ
	 * SpringMVC ��� ModelAndView �� model �����ݷ��뵽 request �������. 
	 * @return
	 */
	@RequestMapping("/testModelAndView")
	public ModelAndView testModelAndView(){
		String viewName = SUCCESS;
		ModelAndView modelAndView = new ModelAndView(viewName);
		
		//���ģ�����ݵ� ModelAndView ��.
		modelAndView.addObject("time", new Date());
		
		return modelAndView;
	}
	
	/**
	 * 
	 */
	@RequestMapping("/testSessionAttributes")
	public String testSessionAttributes(Map<String, Object> map){
		User user = new User("�ĸ�", "123", "123@qq.com", 11);
		//  
		map.put("user", user);
		
		return SUCCESS ;
	}
	
	
	/**
	 * ����@ModelAndAttributeע��
	 */
	@RequestMapping("/testModelAttribute")
	public String testModelAttribute(@ModelAttribute("abc") User user){
		System.out.println("�޸ģ� " + user);
		
		return SUCCESS ;
	}
	/**
	 *  ��@ModelAttribute��ǵķ���,��ִ��֮ǰ����ִ�д˷���
	 */
	@ModelAttribute
	public void getUser(@RequestParam(value="id",required=false)Integer id,
			Map<String,Object> map){
		if(id != null ){
			// ģ������ݿ��л�ȡ����
			User user = new User("Tom", "123456", "tom@qq.com", 12);
			user.setId(1);
			System.out.println("�����ݿ��л�ȡһ������:" + user);
			map.put("efg", user);
		}
	}
}
