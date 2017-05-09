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
