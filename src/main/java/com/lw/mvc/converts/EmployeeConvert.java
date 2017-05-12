package com.lw.mvc.converts;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.lw.mvc.curd.pojo.Department;
import com.lw.mvc.curd.pojo.Employee;

/**
 *  �Զ�������ת����
 * @author lw
 */
@Component
public class EmployeeConvert implements Converter<String, Employee> {

	public Employee convert(String source) {
		if(source != null ){
			String[] items = source.split("-");
			if(items.length == 3 ){
				//<!-- lastname-email-department.id ����: GG-gg@atguigu.com-105 -->
				
				Employee employee = new Employee() ;
				employee.setLastName(items[0]);
				employee.setEmail(items[1]);
				employee.setId(null);
				Department department = new Department() ;
				department.setId(Integer.parseInt(items[2]));
				employee.setDepartment(department);
				return employee ;
			}
		}
		return null;
	}

}
