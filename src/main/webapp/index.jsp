<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript" src="scripts/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#testJson").click(function() {
			var url = this.href;
			var args = {};
			$.post(url, args, function(data) {
				for (var i = 0; i < data.length; i++) {
					var id = data[i].id;
					var lastName = data[i].lastName;

					alert(id + ": " + lastName);
				}
			});
			return false;
		});
	})
</script>
</head>
<body>
	
	
	<a href="mvc/testSimpleMappingExceptionResolver?id=10">Test SimpleMappingExceptionResolver</a>
	<br><br>
	<!-- 测试ResponseStatusExceptionResolver -->
	<a href="mvc/testResponseStatusExceptionResolver?id=10">Test ResponseStatusExceptionResolver</a>
	<br><br>
	<!-- 测试异常处理 -->
	<a href="mvc/testException?id=10">Test Exception</a>
	<!-- 多文件上传 
		1、配置MultipartResolver
		2、 
	-->
	<form action="mvc/testManyFileUpload" method="POST" enctype="multipart/form-data">
		文件1: <input type="file" name="myfiles"/>
		文件2: <input type="file" name="myfiles"/>
		<input type="submit" value="Submit"/>
	</form>
	
	<!-- 单文件上传 
		1、配置MultipartResolver
		2、 
	-->
	<form action="mvc/testFileUpload" method="POST" enctype="multipart/form-data">
		File: <input type="file" name="file"/>
		Desc: <input type="text" name="desc"/>
		<input type="submit" value="Submit"/>
	</form>
	
	<br><br>
	<a href="mvc/testJson" id="testJson">Test Json</a>
	<br>
	<br>
	<a href="emps">Rest Get</a>
</body>
</html>