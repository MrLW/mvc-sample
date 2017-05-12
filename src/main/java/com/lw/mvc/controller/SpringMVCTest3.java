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
 * @SessionAttributes:���õ�������,valueΪ����,���Ҫ����session���еļ�
 */
@SessionAttributes(value={"ghi"})
@Controller
@RequestMapping("/mvc")
public class SpringMVCTest3 {
	
	private static final String SUCCESS = "success";
	
	@Autowired
	private EmployeeDao employeeDao;
	
	/**
	 *  ��������ת��
	 * @return
	 */
	@RequestMapping("/testConversionServiceConverer")
	public String testConversionServiceConverer(Employee employee){
		System.out.println("save before:" + employee); // ����initBinder������ִ��ʱ��
		employeeDao.save(employee);
		System.out.println("save after:" + employee); // ����initBinder������ִ��ʱ��
		return "redirect:/emps" ;
	} 
	
	// �Ȼ���뵽save()����
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
	@ResponseBody
	@RequestMapping("/testJson")
	public Collection<Employee> testJson(){
		return employeeDao.getAll();
	}
	/**
	 *  ���Ե��ļ��ϴ�
	 *  ���ļ��ϴ�MultipartFile���Ϳ��Բ���@RequestParam����,���Ƕ��ļ��ϴ�����
	 */
	@RequestMapping(value = "/testFileUpload")
	public String testFileUpload(@RequestParam(value="desc",required=false)String desc,
			/*@RequestParam("file")*/ MultipartFile file){
		System.out.println("desc:" + desc );
		System.out.println("�ļ�����" + file.getOriginalFilename());
		return SUCCESS ;
	}
	
	/**
	 *  ���Ե��ļ��ϴ�
	 */
	@RequestMapping(value = "/testManyFileUpload")
	public String testManyFileUpload(@RequestParam(value="desc",required=false)String desc,
			@RequestParam("myfiles") MultipartFile[] myfiles){
		for (MultipartFile file : myfiles) {
			System.out.println("�ļ���:" + file.getOriginalFilename());
		}
		return SUCCESS ;
	}
	
	/**
	 *  �����쳣����
	 */
	@RequestMapping(value = "/testException")
	public String testException(@RequestParam(value="id") Integer id){
		int a = 10 / id ;
		return SUCCESS ;
	}
	/**
	 *  �����쳣
	 */
	@ExceptionHandler({ArithmeticException.class})
	public ModelAndView handleMathException(Exception ex){
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("ex", ex.getMessage());
		return mv ;
	}
	
	/**
	 *  �����쳣2
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
