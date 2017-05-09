# springmvc

## @ModelAttribute注解实现的底层原理

主要是通过实例来分析

**前台代码**

```
	
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
```

**后台代码**
```
	
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
```

**分析**

通过debug调试,我们知道被@ModelAttribute注解修饰的方法执行在每个方法的前面

```
	
	//HandlerMethodInvoker  map.put("efg", user);方法执行之前
	public final Object invokeHandlerMethod(Method handlerMethod, Object handler,
			NativeWebRequest webRequest, ExtendedModelMap implicitModel) throws Exception {
	
	//SpringMVCTest 此行代码打了断点
	map.put("efg", user);
```

```
	
	//遍历每个被 @ModelAttributeMethods注解修饰的 方法
	for (Method attributeMethod : this.methodResolver.getModelAttributeMethods()) {
				Method attributeMethodToInvoke = BridgeMethodResolver.findBridgedMethod(attributeMethod);
				Object[] args = resolveHandlerArguments(attributeMethodToInvoke, handler, webRequest, implicitModel);
				if (debug) {
					logger.debug("Invoking model attribute method: " + attributeMethodToInvoke);
				}
				String attrName = AnnotationUtils.findAnnotation(attributeMethod, ModelAttribute.class).value();
				if (!"".equals(attrName) && implicitModel.containsAttribute(attrName)) {
					continue;
				}
				ReflectionUtils.makeAccessible(attributeMethodToInvoke);
				// 在执行这行代码的时候又回到SpringMVCTest3中执行map.put("efg", user);
				// 将map中的数据封装到implicitModel中,implicitModel是BingAwareModelMap类型,键值分别是map传入的键值
				//此时的implicitModel：{efg=User [username=Tom, password=123456, email=tom@qq.com, age=12, address=null]}
				Object attrValue = attributeMethodToInvoke.invoke(handler, args);
				if ("".equals(attrName)) {
					Class<?> resolvedType = GenericTypeResolver.resolveReturnType(attributeMethodToInvoke, handler.getClass());
					attrName = Conventions.getVariableNameForReturnType(attributeMethodToInvoke, resolvedType, attrValue);
				}
				if (!implicitModel.containsAttribute(attrName)) {
					implicitModel.addAttribute(attrName, attrValue);
				}
			}
			// ...
			// 核心代码,这里进行复制操作
			Object[] args = resolveHandlerArguments(handlerMethodToInvoke, handler, webRequest, implicitModel);
			if (debug) {
				logger.debug("Invoking request handler method: " + handlerMethodToInvoke);
			}
			ReflectionUtils.makeAccessible(handlerMethodToInvoke);
			// 回到SpringMVCTest3中执行testModelAttribute方法
			return handlerMethodToInvoke.invoke(handler, args);
			
			
```

进入到resolveHandlerArguments方法

```
	
	//...
	// 这里看核心代码
	else if (attrName != null) {
				// 创建WebDataBinder对象,具体创建过程查看下面分析
				WebDataBinder binder =
						resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
				boolean assignBindingResult = (args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
				if (binder.getTarget() != null) {
					// 这里完成对数据的存放,request中
					doBind(binder, webRequest, validate, validationHints, !assignBindingResult);
				}
				args[i] = binder.getTarget();
				if (assignBindingResult) {
					args[i + 1] = binder.getBindingResult();
					i++;
				}
				implicitModel.putAll(binder.getBindingResult().getModel());
			}
		}

		return args;
```

```
	
	private WebDataBinder resolveModelAttribute(String attrName, MethodParameter methodParam,
			ExtendedModelMap implicitModel, NativeWebRequest webRequest, Object handler) throws Exception {

		// Bind request parameter onto object...
		String name = attrName; // abc
		if ("".equals(name)) { // 如果name为""的话,则SpringMVC默认使用类名首字母小写作为name
			name = Conventions.getVariableNameForParameter(methodParam);
		}
		// 获取参数类型,此处是User类型
		Class<?> paramType = methodParam.getParameterType();
		Object bindObject;
		// 查看implicitModel的键是否有abc,由上面分析可知implicitModel只有def因此此处不执行
		if (implicitModel.containsKey(name)) {
			bindObject = implicitModel.get(name);
		}
		// 查看当前类是否添加了@SessionAttribute注解
		// 若使用了该注解, 且 @SessionAttributes 注解的 value 属性值中包含了@ModelAndAttribute中的value(添加了@ModelAndAttribute注解的情况)/类名首字母小写(没有添加@ModelAndAttribute注解的情况),这里就简称key.
		// 则会从 HttpSession 中来获取 key 所对应的 User对象, 若存在则直接传入到目标方法的入参中. 若不存在则将抛出异常.当前例子也不会进行
		else if (this.methodResolver.isSessionAttribute(name, paramType)) {
			bindObject = this.sessionAttributeStore.retrieveAttribute(webRequest, name);
			if (bindObject == null) {
				raiseSessionRequiredException("Session attribute '" + name + "' required - not found in session");
			}
		}
		else {
			// 进入到这里,利用反射创建一个新对象
			bindObject = BeanUtils.instantiateClass(paramType);
		}
		// 创建WebDataBinder,createBinder(webRequest, bindObject, name)下面分析
		WebDataBinder binder = createBinder(webRequest, bindObject, name);
		initBinder(handler, name, binder, webRequest);
		return binder;
	}
```

createBinder(webRequest, bindObject, name)
```
	
	protected WebDataBinder createBinder(NativeWebRequest webRequest, Object target, String objectName)
			throws Exception {
		return new WebRequestDataBinder(target, objectName);
	}
	
	public WebRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}
	//跟踪到DatBinder类中
	public DataBinder(Object target, String objectName) {
		// target:就是之前利用反射创建的User对象
		// objectName:abc
		if (target != null && target.getClass() == javaUtilOptionalClass) {
			this.target = OptionalUnwrapper.unwrap(target);
		}
		else {
			this.target = target;
		}
		this.objectName = objectName;
	}
```

Ok,这样binder就创建完成了,再次把代码放大到下面中
```
	
	//已经执行完成
	WebDataBinder binder =
						resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
				boolean assignBindingResult = (args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
				// 这个target为新建的User对象,因此进入doBinder()方法
				if (binder.getTarget() != null) {
					// 完成对象的复制操作,将前台传递过来的用户信息全部复制到新利用反射创建的对象中
					doBind(binder, webRequest, validate, validationHints, !assignBindingResult);
				}
				args[i] = binder.getTarget();
				if (assignBindingResult) {
					args[i + 1] = binder.getBindingResult();
					i++;
				}
				// 重新将abc-User对象的map添加到implicitModel中,此时implicitModel有两个map了,键值分别是abc和efg
				implicitModel.putAll(binder.getBindingResult().getModel());
```
