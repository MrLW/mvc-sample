# springmvc

## @ModelAttributeע��ʵ�ֵĵײ�ԭ��

��Ҫ��ͨ��ʵ��������

**ǰ̨����**

```
	
	<!--  
			ģ���޸Ĳ���
			1. ԭʼ����Ϊ: 1, Tom, 123456,tom@atguigu.com,12
			2. ���벻�ܱ��޸�.
			3. ������, ģ�����ֱ���ڱ���д��Ӧ������ֵ
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

**��̨����**
```
	
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
```

**����**

ͨ��debug����,����֪����@ModelAttributeע�����εķ���ִ����ÿ��������ǰ��

```
	
	//HandlerMethodInvoker  map.put("efg", user);����ִ��֮ǰ
	public final Object invokeHandlerMethod(Method handlerMethod, Object handler,
			NativeWebRequest webRequest, ExtendedModelMap implicitModel) throws Exception {
	
	//SpringMVCTest ���д�����˶ϵ�
	map.put("efg", user);
```

```
	
	//����ÿ���� @ModelAttributeMethodsע�����ε� ����
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
				// ��ִ�����д����ʱ���ֻص�SpringMVCTest3��ִ��map.put("efg", user);
				// ��map�е����ݷ�װ��implicitModel��,implicitModel��BingAwareModelMap����,��ֵ�ֱ���map����ļ�ֵ
				//��ʱ��implicitModel��{efg=User [username=Tom, password=123456, email=tom@qq.com, age=12, address=null]}
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
			// ���Ĵ���,������и��Ʋ���
			Object[] args = resolveHandlerArguments(handlerMethodToInvoke, handler, webRequest, implicitModel);
			if (debug) {
				logger.debug("Invoking request handler method: " + handlerMethodToInvoke);
			}
			ReflectionUtils.makeAccessible(handlerMethodToInvoke);
			// �ص�SpringMVCTest3��ִ��testModelAttribute����
			return handlerMethodToInvoke.invoke(handler, args);
			
			
```

���뵽resolveHandlerArguments����

```
	
	//...
	// ���￴���Ĵ���
	else if (attrName != null) {
				// ����WebDataBinder����,���崴�����̲鿴�������
				WebDataBinder binder =
						resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
				boolean assignBindingResult = (args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
				if (binder.getTarget() != null) {
					// ������ɶ����ݵĴ��,request��
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
		if ("".equals(name)) { // ���nameΪ""�Ļ�,��SpringMVCĬ��ʹ����������ĸСд��Ϊname
			name = Conventions.getVariableNameForParameter(methodParam);
		}
		// ��ȡ��������,�˴���User����
		Class<?> paramType = methodParam.getParameterType();
		Object bindObject;
		// �鿴implicitModel�ļ��Ƿ���abc,�����������֪implicitModelֻ��def��˴˴���ִ��
		if (implicitModel.containsKey(name)) {
			bindObject = implicitModel.get(name);
		}
		// �鿴��ǰ���Ƿ������@SessionAttributeע��
		// ��ʹ���˸�ע��, �� @SessionAttributes ע��� value ����ֵ�а�����@ModelAndAttribute�е�value(�����@ModelAndAttributeע������)/��������ĸСд(û�����@ModelAndAttributeע������),����ͼ��key.
		// ���� HttpSession ������ȡ key ����Ӧ�� User����, ��������ֱ�Ӵ��뵽Ŀ�귽���������. �����������׳��쳣.��ǰ����Ҳ�������
		else if (this.methodResolver.isSessionAttribute(name, paramType)) {
			bindObject = this.sessionAttributeStore.retrieveAttribute(webRequest, name);
			if (bindObject == null) {
				raiseSessionRequiredException("Session attribute '" + name + "' required - not found in session");
			}
		}
		else {
			// ���뵽����,���÷��䴴��һ���¶���
			bindObject = BeanUtils.instantiateClass(paramType);
		}
		// ����WebDataBinder,createBinder(webRequest, bindObject, name)�������
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
	//���ٵ�DatBinder����
	public DataBinder(Object target, String objectName) {
		// target:����֮ǰ���÷��䴴����User����
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

Ok,����binder�ʹ��������,�ٴΰѴ���Ŵ�������
```
	
	//�Ѿ�ִ�����
	WebDataBinder binder =
						resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
				boolean assignBindingResult = (args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
				// ���targetΪ�½���User����,��˽���doBinder()����
				if (binder.getTarget() != null) {
					// ��ɶ���ĸ��Ʋ���,��ǰ̨���ݹ������û���Ϣȫ�����Ƶ������÷��䴴���Ķ�����
					doBind(binder, webRequest, validate, validationHints, !assignBindingResult);
				}
				args[i] = binder.getTarget();
				if (assignBindingResult) {
					args[i + 1] = binder.getBindingResult();
					i++;
				}
				// ���½�abc-User�����map��ӵ�implicitModel��,��ʱimplicitModel������map��,��ֵ�ֱ���abc��efg
				implicitModel.putAll(binder.getBindingResult().getModel());
```


## <mvc:annotation-driven />������

**�����ò���������**

```

	// ����һ����ʹ��<mvc:view-controller path="/success" view-name="success"/>��ʵ��ֱ�ӷ���ҳ���ʱ��,@RequestMapping����ʧȥ����
	// ���������ʹ��<mvc:default-servlet-handler/>�������̬��Դ���ʵ������ʱ��,@RequestMapping����ʧȥ���ý���ʧȥ����
	// ������������ʵ�����͵�ת�������ݵ�У���ʱ����Ҫ����,��ʱconvertServiceΪnull
```

**ʵ��һ**
��<mvc:default-servlet-handler/>��<mvc:annotation-driven ></mvc:annotation-driven> ��û������ʱ,conversionServiceΪnull ,��ʱDispatchServlet
��handleAdapter ������£�
```
	
	HttpRequestHandleAdapter
	SimpleControllerHandlerAdapter
	AnnotationMethodHandlerAdater
```


����<mvc:default-servlet-handler/>����<mvc:annotation-driven ></mvc:annotation-driven> conversionService��Ϊnull ,��ʱDispatchServlet������£�

```
	
	HttpRequestHandlerAdater
	SimpleControllerHandlerAdapter
```
��ʱ����һ��ע�ⷽ������������,���ܴ���@RequestMappingע�����ε���


����<mvc:default-servlet-handler/>����û��<mvc:annotation-driven ></mvc:annotation-driven> conversionService��Ϊnull ,��ʱDispatchServlet������£�

```
	
	HttpRequestHandlerAdater
	SimpleControllerHandlerAdapter
	RequestMappingHandlerAdapter
```

## @InitBinderע��

���Զ�WebDataBinder���г�ʼ��.
**ע��:**���������з���ֵ,����������void
@InitBinder����������WebDataBinder

```
	
	// ����ĸ���(ǰ̨���ݴ��ݵ���̨)���������������
	//resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)
	// �����н���
	@InitBinder
	public void initBinder(WebDataBinder binder){
		binder.setDisallowedFields("lastName");
	}
	
```


## �ļ��ϴ�

**ʵ�ֲ���**
- 1��xml��ע��CommonsMultipartResolver,id����ΪmultipartResolver
- 2��Java������ʹ��MultipartFile ���ͱ�������

**ʵ��**
```
	
	xml��
	<!-- 
		�ļ��ϴ�MultipartResolver 
		��Ҫע����ǣ��ļ���������multipartResolver,����springmvc�Ҳ���multipartResolver
	-->
	<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<property name="defaultEncoding" value="utf-8"></property>
		<property name="maxUploadSize" value="102400"></property>
	</bean>
	/*********************************************************************/
	java:
	/**
	 *  �����ļ��ϴ�
	 */
	@RequestMapping(value = "/testFileUpload")
	public String testFileUpload(@RequestParam(value="desc",required=false)String desc,
			@RequestParam("file") MultipartFile file){
		System.out.println("desc:" + desc );
		System.out.println("�ļ�����" + file.getOriginalFilename());
		return SUCCESS ;
	}
```

**Դ�����**

����,������һ����,�ͻ����springmvc�����ļ�,���ᴴ��CommonsMultipartResolver����,������DispatcherServlet���г�ʼ��
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
			// ��context��ͨ��ָ��id��ȡmultipartResolver����,MULTIPART_RESOLVER_BEAN_NAME��ʵ����multipartResolver
			// ���,������springmvc.xml������ֻ������ָ��
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

## �쳣����

ֻҪ��������������ע��,springmvc�ͻ�Ϊ������ExceptionHandlerExceptionResolver������.���û�����õĻ�,��ʹ��AnnotationMethodHandlerExceptionResolver(��ʱ)
```
	
	<mvc:annotation-driven/>
```
**Controller�쳣����**
```

	/**
	 *  1�������쳣,�쳣�����������ȼ���,Ĭ�����ұ���ȷ�ȸߵ�ע�����εķ���
	 *  �����Ҫ���쳣��Ϣ�����ҳ����,�����ʹ��ModelAndView,������ʹ��
	 *  Map���з�װ(�ᱨ��)
	 *  2�������ǰController��û���ҵ���Ӧ���쳣������,springmvc���ȥ
	 *     �ұ�@ControllerAdviceע�����ε����в���
	 */
	@ExceptionHandler({ArithmeticException.class})
	public ModelAndView handleMathException(Exception ex){
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("ex", ex.getMessage());
		return mv ;
	}
	
```

**ȫ���쳣����**

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

	// ���ע��Ĺ��ܣ�
	//���ǿ����Լ�������״̬���״̬ԭ��
	@ResponseStatus(reason="�û�������Ϊnull",code=HttpStatus.FORBIDDEN)
	public class UserNameException extends RuntimeException {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
	}
	
```

**DefaultHandlerExceptionResolver**

�������Ҫ����һЩspring���쳣,�����������:
```
	
	Default implementation of the {@link org.springframework.web.servlet.HandlerExceptionResolver   
	HandlerExceptionResolver} interface that resolves standard Spring exceptions and translates     
	them to corresponding HTTP status codes.                                                        
                                                                                                
```


**SimpleMappingExceptionResolver**

�����Ҫ����xml������,����,��ExceptionHandlerExceptionResolver��ͬ����,springmvc�Ὣ
�쳣��Ϣ�Զ��ķ�װ��request����,Ĭ��Ϊexception.

```
	
	<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
		<!-- ����request��������� -->
		<property name="exceptionAttribute" value="ex"></property>
		<property name="exceptionMappings">
			<props>
				<prop key="java.lang.ArrayIndexOutOfBoundsException">error</prop>
			</props>
		</property>
	</bean>
```

Դ�����£�

```
	
	public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";
	// ͨ���޸�����������޸��쳣����request�ļ�ֵ
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



## springmvc����������

- 1������web.xml��DispatchServlet��url-pattern��������,
	��springmvc�Ҳ����Ƿ��ж�Ӧ��ӳ��

- 2 �����ڶ�Ӧ��ӳ��,��springmvc.xml�в����Ƿ���<mvc:default-servlet-handler/>��ǩ
                 ���������Ӧ(һ���Ǿ�̬��Դ),���û��,��404,�ҿ���̨��No mapping found for http....
	  
- 3 �ҵ��˶�Ӧ��ӳ��

**Դ�����**

DispatcherServlet��

```
	
	// doDispatch()������
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// code ...
		//mappedHandler:HandlerExecutionChain����,�����˴����������������
		// ͨ��HandlerMapping�����ȡ
		mappedHandler = getHandler(processedRequest);
		// code ...
		// Determine handler adapter for the current request.
		// HandlerAdapter:�ڵ���Ŀ�귽��֮ǰ��װ��һЩ����
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
		
		// code ...
		// ������������Handle����
		if (!mappedHandler.applyPreHandle(processedRequest, response)) {
			return;
		}
		// code ...
		// Actually invoke the handler.
		// ����Ŀ�귽��
		mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
		// code ...
		// ������������postHandle����
		mappedHandler.applyPostHandle(processedRequest, response, mv);
		// code ...
		// ������ͼ
		processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
	}
	
	
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		// HandlerMapping:����ʹ�����֮���ӳ��
		// ��û������ʱ,handlerMappings������
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@66f28a1f, 
		// �������һ����ʱ�� 
		//org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping@ae73c80
		//��������<mvc:annotation-driven></mvc:annotation-driven>��ǩҳ������
		//org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping@4da39ca9,
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@6db328f8
		// ��������<mvc:annotation-driven></mvc:annotation-driven>��<mvc:default-servlet-handler/>������
		//org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping@485caa8f,
		//org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping@12c0c0b3, 
		// �����Ҫ�����̬��Դ�ķ��ʵ�
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
			
		// �����쳣
		// code ... 
		// Did the handler return a view to render?
		if (mv != null && !mv.wasCleared()) {
			// ��Ⱦ��ͼ
			render(mv, request, response);
			if (errorView) {
				WebUtils.clearErrorRequestAttributes(request);
			}
		}
		// ������������afterCompletion����
		mappedHandler.triggerAfterCompletion(request, response, null);
			
	}


	
```
HandlerExecutionChain��
```
	
	// ��������preHandle����
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


