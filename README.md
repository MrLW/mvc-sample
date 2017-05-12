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


## <mvc:annotation-driven />的作用

**不配置产生的问题**

```

	// 问题一、当使用<mvc:view-controller path="/success" view-name="success"/>来实现直接访问页面的时候,@RequestMapping将会失去作用
	// 问题二、当使用<mvc:default-servlet-handler/>来解决静态资源访问的问题的时候,@RequestMapping将会失去作用将会失去作用
	// 问题三、当想实现类型的转换和数据的校验的时候需要配置,此时convertService为null
```

**实验一**
当<mvc:default-servlet-handler/>和<mvc:annotation-driven ></mvc:annotation-driven> 都没有配置时,conversionService为null ,此时DispatchServlet
的handleAdapter 情况如下：
```
	
	HttpRequestHandleAdapter
	SimpleControllerHandlerAdapter
	AnnotationMethodHandlerAdater
```


当有<mvc:default-servlet-handler/>还有<mvc:annotation-driven ></mvc:annotation-driven> conversionService不为null ,此时DispatchServlet情况如下：

```
	
	HttpRequestHandlerAdater
	SimpleControllerHandlerAdapter
```
此时少了一个注解方法处理适配器,不能处理被@RequestMapping注解修饰的类


当有<mvc:default-servlet-handler/>但是没有<mvc:annotation-driven ></mvc:annotation-driven> conversionService不为null ,此时DispatchServlet情况如下：

```
	
	HttpRequestHandlerAdater
	SimpleControllerHandlerAdapter
	RequestMappingHandlerAdapter
```

## @InitBinder注解

可以对WebDataBinder进行初始化.
**注意:**方法不能有返回值,申明必须是void
@InitBinder方法参数是WebDataBinder

```
	
	// 对象的复制(前台数据传递到后台)和下面操作都是在
	//resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)
	// 方法中进行
	@InitBinder
	public void initBinder(WebDataBinder binder){
		binder.setDisallowedFields("lastName");
	}
	
```


## 文件上传

**实现步骤**
- 1、xml中注册CommonsMultipartResolver,id必须为multipartResolver
- 2、Java代码中使用MultipartFile 类型变量接收

**实例**
```
	
	xml：
	<!-- 
		文件上传MultipartResolver 
		需要注意的是：文件名必须是multipartResolver,否则springmvc找不到multipartResolver
	-->
	<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<property name="defaultEncoding" value="utf-8"></property>
		<property name="maxUploadSize" value="102400"></property>
	</bean>
	/*********************************************************************/
	java:
	/**
	 *  测试文件上传
	 */
	@RequestMapping(value = "/testFileUpload")
	public String testFileUpload(@RequestParam(value="desc",required=false)String desc,
			@RequestParam("file") MultipartFile file){
		System.out.println("desc:" + desc );
		System.out.println("文件名：" + file.getOriginalFilename());
		return SUCCESS ;
	}
```

**源码分析**

首先,服务器一启动,就会加载springmvc配置文件,即会创建CommonsMultipartResolver对象,接着在DispatcherServlet进行初始化
```
	
	/** Well-known name for the MultipartResolver object in the bean factory for this namespace. */
	public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";
	
	protected void initStrategies(ApplicationContext context) {
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolvers(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}
	
	private void initMultipartResolver(ApplicationContext context) {
		try {
			// 从context中通过指定id获取multipartResolver对象,MULTIPART_RESOLVER_BEAN_NAME其实就是multipartResolver
			// 因此,我们在springmvc.xml中配置只能这样指定
			this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using MultipartResolver [" + this.multipartResolver + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Default is no multipart resolver.
			this.multipartResolver = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate MultipartResolver with name '" + MULTIPART_RESOLVER_BEAN_NAME +
						"': no multipart request handling provided");
			}
		}
	}
	
	
```

## 异常处理

只要我们配置了如下注解,springmvc就会为其配置ExceptionHandlerExceptionResolver处理类.如果没有配置的话,则使用AnnotationMethodHandlerExceptionResolver(过时)
```
	
	<mvc:annotation-driven/>
```
**Controller异常处理**
```

	/**
	 *  1、处理异常,异常处理是有优先级的,默认先找被精确度高的注解修饰的方法
	 *  如果想要把异常信息输出的页面上,则可以使用ModelAndView,不可以使用
	 *  Map进行封装(会报错)
	 *  2、如果当前Controller中没有找到对应的异常处理方法,springmvc则会去
	 *     找被@ControllerAdvice注解修饰的类中查找
	 */
	@ExceptionHandler({ArithmeticException.class})
	public ModelAndView handleMathException(Exception ex){
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("ex", ex.getMessage());
		return mv ;
	}
	
```

**全局异常处理**

```
	
	@ControllerAdvice
	public class HandlerException {
	
		@ExceptionHandler({ArithmeticException.class})
		public ModelAndView handleMathException(Exception ex){
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("ex", ex);
			return mv ;
		}
	}
```

**ResponseStatusExceptionResolver**

```

	// 这个注解的功能：
	//我们可以自己来定制状态码和状态原因
	@ResponseStatus(reason="用户名不能为null",code=HttpStatus.FORBIDDEN)
	public class UserNameException extends RuntimeException {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
	}
	
```

**DefaultHandlerExceptionResolver**

这个类主要处理一些spring的异常,具体解释如下:
```
	
	Default implementation of the {@link org.springframework.web.servlet.HandlerExceptionResolver   
	HandlerExceptionResolver} interface that resolves standard Spring exceptions and translates     
	them to corresponding HTTP status codes.                                                        
                                                                                                
```


**SimpleMappingExceptionResolver**

这个主要是在xml中配置,如下,和ExceptionHandlerExceptionResolver不同的是,springmvc会将
异常信息自动的封装到request域中,默认为exception.

```
	
	<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
		<!-- 放入request域的属性名 -->
		<property name="exceptionAttribute" value="ex"></property>
		<property name="exceptionMappings">
			<props>
				<prop key="java.lang.ArrayIndexOutOfBoundsException">error</prop>
			</props>
		</property>
	</bean>
```

源码如下：

```
	
	public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";
	// 通过修改这个属性来修改异常存入request的键值
	private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		// Expose ModelAndView for chosen error view.
		String viewName = determineViewName(ex, request);
		if (viewName != null) {
			// Apply HTTP status code for error views, if specified.
			// Only apply it if we're processing a top-level request.
			Integer statusCode = determineStatusCode(request, viewName);
			if (statusCode != null) {
				applyStatusCodeIfPossible(request, response, statusCode);
			}
			return getModelAndView(viewName, ex, request);
		}
		else {
			return null;
		}
	}
	
	protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
		return getModelAndView(viewName, ex);
	}
	
	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		ModelAndView mv = new ModelAndView(viewName);
		if (this.exceptionAttribute != null) {
			if (logger.isDebugEnabled()) { 
				logger.debug("Exposing Exception as model attribute '" + this.exceptionAttribute + "'");
			}
			mv.addObject(this.exceptionAttribute, ex);
		}
		return mv;
	}
```



## springmvc的运行流程

- 1、按照web.xml中DispatchServlet的url-pattern拦截请求,
	在springmvc找查找是否有对应的映射

- 2 不存在对应的映射,择到springmvc.xml中查找是否有<mvc:default-servlet-handler/>标签
                 如果有则响应(一般是静态资源),如果没有,则报404,且控制台报No mapping found for http....
	  
- 3 找到了对应的映射

**源码分析**

DispatcherServlet类

```
	
	// doDispatch()方法中
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// code ...
		//mappedHandler:HandlerExecutionChain类型,包含了处理器对象和拦截器
		// 通过HandlerMapping对象获取
		mappedHandler = getHandler(processedRequest);
		// code ...
		// Determine handler adapter for the current request.
		// HandlerAdapter:在调用目标方法之前封装的一些操作
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
		
		// code ...
		// 调用拦截器的Handle方法
		if (!mappedHandler.applyPreHandle(processedRequest, response)) {
			return;
		}
		// code ...
		// Actually invoke the handler.
		// 调用目标方法
		mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
		// code ...
		// 调用拦截器的postHandle方法
		mappedHandler.applyPostHandle(processedRequest, response, mv);
		// code ...
		// 处理视图
		processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
	}
	
	
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		// HandlerMapping:请求和处理器之间的映射
		// 当没有配置时,handlerMappings有两个
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@66f28a1f, 
		// 这个类是一个过时类 
		//org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping@ae73c80
		//当配置了<mvc:annotation-driven></mvc:annotation-driven>标签页是两个
		//org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping@4da39ca9,
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@6db328f8
		// 当配置了<mvc:annotation-driven></mvc:annotation-driven>和<mvc:default-servlet-handler/>有三个
		//org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping@485caa8f,
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@12c0c0b3, 
		// 这个主要解决静态资源的访问的
		//org.springframework.web.servlet.handler.SimpleUrlHandlerMapping@239f017e 
		for (HandlerMapping hm : this.handlerMappings) {
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
			}
			HandlerExecutionChain handler = hm.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}
	
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
			
		// 处理异常
		// code ... 
		// Did the handler return a view to render?
		if (mv != null && !mv.wasCleared()) {
			// 渲染视图
			render(mv, request, response);
			if (errorView) {
				WebUtils.clearErrorRequestAttributes(request);
			}
		}
		// 调用拦截器的afterCompletion方法
		mappedHandler.triggerAfterCompletion(request, response, null);
			
	}


	
```
HandlerExecutionChain类
```
	
	// 拦截器的preHandle方法
	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				if (!interceptor.preHandle(request, response, this.handler)) {
					triggerAfterCompletion(request, response, null);
					return false;
				}
				this.interceptorIndex = i;
			}
		}
		return true;
	}
	
	void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = interceptors.length - 1; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				interceptor.postHandle(request, response, this.handler, mv);
			}
		}
	}
```


