<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	
	<form action="mvc/testConversionServiceConverer" method="POST">
		<!-- lastname-email-department.id 例如: GG-gg@atguigu.com-105 -->
		Employee: <input type="text" name="employee" value="aa-aa@qq.com-101"/>
		<input type="submit" value="Submit"/>
	</form>
	<br><br>
	<!-- 
		1.使用form标签,可以快速开发出表单页面,而且可以回显
		2. 注意:
		可以通过 modelAttribute 属性指定绑定的模型属性,
		若没有指定该属性，则默认从 request 域对象中读取 command 的表单 bean
		如果该属性值也不存在，则会发生错误。
	 -->
	<form:form action="emp" method="post" modelAttribute="employee" >
		<!-- path属性对应HTML表单的name属性 -->
		<c:if test="${employee.id == null }">
			LastName: <form:input path="lastName"/><br>
		</c:if>
		<c:if test="${employee.id !=null }">
			<form:hidden path="id"/>
			<input type="hidden" name="_method" value="PUT">
		</c:if>
			Email: <form:input path="email"/><br>
		<br>
		<br>
		Department:<form:select items="${departments }"
						 path="department.id"
						 itemLabel="departmentName"
						 itemValue="id">
						
					</form:select>
					<input type="submit" value="submit">
	</form:form>
</body>
</html>