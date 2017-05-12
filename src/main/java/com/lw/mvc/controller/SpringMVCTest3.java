package com.lw.mvc.controller;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lw.mvc.curd.dao.EmployeeDao;
import com.lw.mvc.curd.pojo.Employee;
import com.lw.mvc.exception.UserNameException;
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
	
	@Autowired
	private EmployeeDao employeeDao;
	
	/**
	 *  测试类型转换
	 * @return
	 */
	@RequestMapping("/testConversionServiceConverer")
	public String testConversionServiceConverer(Employee employee){
		System.out.println("save before:" + employee); // 测试initBinder方法的执行时机
		employeeDao.save(employee);
		System.out.println("save after:" + employee); // 测试initBinder方法的执行时机
		return "redirect:/emps" ;
	} 
	
	// 先会进入到save()方法
	@InitBinder
	public void initBinder(WebDataBinder binder){
		binder.setDisallowedFields("lastName");
	}
	
	@RequestMapping(value = "/testRedirect" )
	public String testRedirect(){
		System.out.println("testRedirect");
		return "redirect:/index.jsp" ;
	} 
	
	
	@RequestMapping(value = "/testViewResolver" )
	public String testViewResolver(Map<String, Object> map){
		
		return SUCCESS ;
	} 
	
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
	@ResponseBody
	@RequestMapping("/testJson")
	public Collection<Employee> testJson(){
		return employeeDao.getAll();
	}
	/**
	 *  测试单文件上传
	 *  单文件上传MultipartFile类型可以不用@RequestParam修饰,但是多文件上传必须
	 */
	@RequestMapping(value = "/testFileUpload")
	public String testFileUpload(@RequestParam(value="desc",required=false)String desc,
			/*@RequestParam("file")*/ MultipartFile file){
		System.out.println("desc:" + desc );
		System.out.println("文件名：" + file.getOriginalFilename());
		return SUCCESS ;
	}
	
	/**
	 *  测试单文件上传
	 */
	@RequestMapping(value = "/testManyFileUpload")
	public String testManyFileUpload(@RequestParam(value="desc",required=false)String desc,
			@RequestParam("myfiles") MultipartFile[] myfiles){
		for (MultipartFile file : myfiles) {
			System.out.println("文件名:" + file.getOriginalFilename());
		}
		return SUCCESS ;
	}
	
	/**
	 *  测试异常处理
	 */
	@RequestMapping(value = "/testException")
	public String testException(@RequestParam(value="id") Integer id){
		int a = 10 / id ;
		return SUCCESS ;
	}
	/**
	 *  处理异常
	 */
	@ExceptionHandler({ArithmeticException.class})
	public ModelAndView handleMathException(Exception ex){
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("ex", ex.getMessage());
		return mv ;
	}
	
	/**
	 *  处理异常2
	 */
	@RequestMapping("/testResponseStatusExceptionResolver")
	public String testResponseStatusExceptionResolver(@RequestParam("id") int id){
		if(id == 13){
			throw new UserNameException() ;
		}
		return "success" ;
	}
	
	@RequestMapping("/testSimpleMappingExceptionResolver")
	public String testSimpleMappingExceptionResolver(@RequestParam("id") int id){
		String[] vals = new String[5] ;
		System.out.println(vals[id]);
		return "success" ;
	}
}
