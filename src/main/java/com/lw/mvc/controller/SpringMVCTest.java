package com.lw.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/**
 *  ²âÊÔrest·ç¸ñµÄAPI
 * @author lw
 *
 */
@Controller
@RequestMapping("/mvc")
public class SpringMVCTest {
	
	private static final String SUCCESS = "success";
	
	@RequestMapping(value = "/testRest/{id}" , method=RequestMethod.GET)
	public String testRestGet(@PathVariable Integer id){
		System.out.println("testRest get:" + id );
		return SUCCESS ;
	}
	
	
	@RequestMapping(value = "/testRest" , method=RequestMethod.POST)
	public String testRestPost(){
		System.out.println("testRest POST");
		return SUCCESS ;
	}
	
	@RequestMapping(value = "/testRest/{id}" , method=RequestMethod.DELETE)
	public String testRestDelete(@PathVariable Integer id){
		System.out.println("testRest DELETE:" + id );
		return SUCCESS ;
	}
	
	@RequestMapping(value = "/testRest/{id}" , method=RequestMethod.PUT)
	public String testRestPut(@PathVariable Integer id){
		System.out.println("testRest PUT:" + id );
		return SUCCESS ;
	}
}
