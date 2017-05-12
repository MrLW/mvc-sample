package com.lw.mvc.curd.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lw.mvc.curd.dao.DepartmentDao;
import com.lw.mvc.curd.dao.EmployeeDao;
import com.lw.mvc.curd.pojo.Employee;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private DepartmentDao departmentDao;
	
	@RequestMapping("/emps")
	public String list(Map<String , Object> map){
		map.put("employees", employeeDao.getAll());
		return "list"; 
	}
	
	/*@ModelAttribute
	public void getEmployee(@RequestParam(value="id",required=false)Integer id
			,Map<String, Object> map){
		Employee employee = employeeDao.get(id);
		map.put("employee", employee);
	}*/
	
	@RequestMapping(value="/emp/{id}",method=RequestMethod.PUT)
	public String update(Employee employee){
		// 此处lastName为null,需要在被@ModelAttribute修饰的方法中将Employee存入map中
		employeeDao.save(employee);
		return "redirect:/emps" ;
	}
	
	@RequestMapping("/emp/{id}")
	public String updateUI(Map<String,Object> map ,@PathVariable("id")Integer id){
		// 回显Employee
		map.put("employee", employeeDao.get(id));
		// 回显department
		map.put("departments", departmentDao.getDepartments());
		return "input" ;
	}
	
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	public String save(@Valid Employee employee,BindingResult results){
		System.out.println("results:" + results);
		employeeDao.save(employee);
		
		return "redirect:/emps" ;
	}
	
	
	@RequestMapping(value="/emp/{id}",method=RequestMethod.DELETE)
	public String delete(@PathVariable("id") Integer id){
		employeeDao.delete(id);
		return "redirect:/emps" ;
	}
	
	@RequestMapping(value="/emp",method=RequestMethod.GET)
	public String input(Map<String , Object> map){
		map.put("departments", departmentDao.getDepartments());
		map.put("employee", new Employee());
		Map<String, String> genders = new HashMap<String, String>() ;
		map.put("genders", genders);
		return "input"; 
	}
	
	
}
