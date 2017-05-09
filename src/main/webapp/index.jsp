<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>

	<!--  
			模拟修改操作
			1. 原始数据为: 1, Tom, 123456,tom@atguigu.com,12
			2. 密码不能被修改.
			3. 表单回显, 模拟操作直接在表单填写对应的属性值
	-->
	<form action="mvc/testModelAttribute" method="Post">
		<input type="hidden" name="id" value="1"/>
		username: <input type="text" name="username" value="Tom"/>
		<br>
		email: <input type="text" name="email" value="tom@atguigu.com"/>
		<br>
		age: <input type="text" name="age" value="12"/>
		<br>
		<input type="submit" value="Submit"/>
	</form>
	<br><br>

<a href="mvc/testSessionAttributes">Session中传递数据</a><br><br>
<a href="mvc/testModelAndView">ModelAndView传递数据</a><br><br>
<a href="mvc/testMap">map传递数据</a>
	<br>

	<a href="mvc/testServlet">原生Servlet</a>
	<br>

	<form action="mvc/testPojo" method="post">
		username: <input type="text" name="username" /> <br> password: <input
			type="password" name="password" /> <br> email: <input
			type="text" name="email" /> <br> age: <input type="text"
			name="age" /> <br> city: <input type="text" name="address.city" />
		<br> province: <input type="text" name="address.province" /> <br>
		<input type="submit" value="Submit" />
	</form>
	<br>
	<br>

	<form action="mvc/testRest/1" method="post">
		<input type="hidden" name="_method" value="PUT"> <input
			type="submit" value="Rest PUT">
	</form>
	<br>
	<br>
	<form action="mvc/testRest/1" method="post">
		<input type="hidden" name="_method" value="DELETE"> <input
			type="submit" value="Rest DELETE">
	</form>

	<br>
	<br>
	<form action="mvc/testRest" method="POST">
		<input type="submit" value="Rest POST">
	</form>
	<br>
	<a href="mvc/testRest/1">Rest Get</a>
</body>
</html>