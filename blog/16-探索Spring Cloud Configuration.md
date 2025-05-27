# 探索Spring Cloud Configuration

org.springframework.cloud.config.server.environment.EnvironmentController

1. 只有给定Profile包含jdbc，才会启用JDBC作为Spring Cloud Server的存储

```java
// org.springframework.cloud.config.server.config.JdbcRepositoryConfiguration.JdbcRepositoryConfiguration
@Configuration(proxyBeanMethods = false)
@Profile("jdbc")
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnProperty(value = "spring.cloud.config.server.jdbc.enabled", matchIfMissing = true)
class JdbcRepositoryConfiguration {

	@Bean
	@ConditionalOnBean(JdbcTemplate.class)
	public JdbcEnvironmentRepository jdbcEnvironmentRepository(JdbcEnvironmentRepositoryFactory factory,
			JdbcEnvironmentProperties environmentProperties) {
		return factory.build(environmentProperties);
	}

}
```

2. Spring cloud config 加密解密

   org.springframework.cloud.config.server.config.EncryptionAutoConfiguration

   org.springframework.cloud.config.server.encryption.EncryptionController

   org.springframework.cloud.config.server.resource.ResourceController

   org.springframework.cloud.config.server.environment.EnvironmentController [http://localhost:8888/spring-cloud-config-client/local/master 会来这个controller]

   ​	- org.springframework.cloud.config.server.environment.EnvironmentEncryptorEnvironmentRepository

   

   org.springframework.cloud.config.server.encryption.KeyStoreTextEncryptorLocator#locate

3. to







## Spring Cloud Client @RefreshScope原理

如果要在运行时动态刷新配置值，需要在Bean上添加@RefreshScope，并使用spring-boot-starter-actuator提供的HTTP接口actuator/refresh来刷新配置值

org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder

RefreshScope管理了Scope=Refresh的Bean的生命周期，提供了get(获取),refreshAll（刷新）、destory（销毁）等方法

RefreshScope就是一个BeanFactoryPostProcessors,然后调用父类GenericScope的postProcessBeanDefinitionRegistry方法

该方法遍历所有的bean定义 如果当前的bean的scope为refresh,那么就把当前的bean设置为 LockedScopedProxyFactoryBean的代理对象。

@RefreshScope标注的类还有一个特点：会使用代理对象并进行延迟加载。我们来看一下postProcessBeanDefinitionRegistry方法

@RefreshScope 注解的 bean，除了会生成一个beanName的 bean，同时会生成 scopedTarget.beanName的 bean

---

RefreshScope还会监听一个ContextRefreshedEvent，该事件会在ApplicationContext初始化或者refreshed时触发.

##### ContextRefreshedEvent事件

AbstractApplicationContext#finishRefresh方法中

我们来看一下RefreshScope中的代码：

```java
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		start(event);
	}

	public void start(ContextRefreshedEvent event) {
		if (event.getApplicationContext() == this.context && this.eager && this.registry != null) {
			eagerlyInitialize();
		}
	}

	private void eagerlyInitialize() {
		for (String name : this.context.getBeanDefinitionNames()) {
			BeanDefinition definition = this.registry.getBeanDefinition(name);
			if (this.getName().equals(definition.getScope()) && !definition.isLazyInit()) {
				Object bean = this.context.getBean(name);
				if (bean != null) {
					bean.getClass();
				}
			}
		}
	}
```

如果这个bean的scope = refresh的话就会去执行getBean方法，我们可以看到bean的名字为scopedTarget.testProperties这是一个被代理过的bean

##### doGetBean

上面的`this.context.getBean(name)`中会使用BeanFactory的doGetBean方法创建Bean，不同scope有不同的创建方式：

```java
	protected <T> T doGetBean(
			String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {

		String beanName = transformedBeanName(name);
		Object beanInstance;
		...

				// 创建单例bean
				if (mbd.isSingleton()) {
					sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// Explicitly remove instance from singleton cache: It might have been put there
							// eagerly by the creation process, to allow for circular reference resolution.
							// Also remove any beans that received a temporary reference to the bean.
							destroySingleton(beanName);
							throw ex;
						}
					});
					beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}
                // 创建原型bean
				else if (mbd.isPrototype()) {
					// It's a prototype -> create a new instance.
					Object prototypeInstance = null;
					try {
					   
						beforePrototypeCreation(beanName);
						prototypeInstance = createBean(beanName, mbd, args);
					}
					finally {
						afterPrototypeCreation(beanName);
					}
					beanInstance = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
				}
				// Scope 类型创建bean
				else {
					String scopeName = mbd.getScope();
					if (!StringUtils.hasLength(scopeName)) {
						throw new IllegalStateException("No scope name defined for bean '" + beanName + "'");
					}
					Scope scope = this.scopes.get(scopeName);
					if (scope == null) {
						throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
					}
					try {
					    //使用RefreshScope父类的get方法，然后使用ConcurrentMap缓存下来
						Object scopedInstance =
						scope.get(beanName, () -> {
						 //把Bean信息存储到ThreadLocal变量中
						 beforePrototypeCreation(beanName);
							try {
								return createBean(beanName, mbd, args);
							}
							finally {
							 //把Bean信息从ThreadLocal变量中移除
							 afterPrototypeCreation(beanName);
							}
						});
						beanInstance = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
					}
					catch (IllegalStateException ex) {
						throw new ScopeNotActiveException(beanName, scopeName, ex);
					}
				}
			}
			catch (BeansException ex) {
				beanCreation.tag("exception", ex.getClass().toString());
				beanCreation.tag("message", String.valueOf(ex.getMessage()));
				cleanupAfterBeanCreationFailure(beanName);
				throw ex;
			}
			finally {
				beanCreation.end();
			}
		}

		return adaptBeanInstance(name, beanInstance, requiredType);
	}

```

- 单例和原型的Bean都是硬编码写在代码里面的
- 除了单例和原型Bean，其他Scope是由Scope对象处理的
- 使用RefreshScope父类的get方法，然后使用ConcurrentMap缓存下来

然后执行createBean创建Bean，创建Bean还是由IOC来做（createBean方法），但是获取Bean，**都由RefreshScope对象的get方法去获取**，其get方法在父类GenericScope中。GenericScope 实现了 Scope 最重要的 get(String name, ObjectFactory objectFactory) 方法，在GenericScope 里面 包装了一个内部类 **BeanLifecycleWrapperCache**。来对加了 @RefreshScope 创建的对象进行缓存，使其在不刷新时获取的都是同一个对象。（这里你可以把 BeanLifecycleWrapperCache 想象成为一个大Map 缓存了所有**@RefreshScope** 标注的对象）

##### **GenericScope中get方法**

```java
	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		BeanLifecycleWrapper value = this.cache.put(name, new BeanLifecycleWrapper(name, objectFactory));
		this.locks.putIfAbsent(name, new ReentrantReadWriteLock());
		try {
			return value.getBean();
		}
		catch (RuntimeException e) {
			this.errors.put(name, e);
			throw e;
		}
	}
	public Object getBean() {
		if (this.bean == null) {
			synchronized (this.name) {
				if (this.bean == null) {
					this.bean = this.objectFactory.getObject();
				}
			}
		}
		return this.bean;
	}

```

BeanLifecycleWrapper这个是`@RefreshScope`标记bean的一个包装类，会被存储到缓存里，在这里取不到值的话就会从objectFactory里去拿，也就是重新创建一个去执行doCreateBean方法

##### **动态刷新Bean的配置变量值**

当配置中心刷新配置之后，有两种方式可以动态刷新Bean的配置变量值，（SpringCloud-Bus还是Nacos差不多都是这么实现的）

- 向上下文发布一个RefreshEvent事件
- Http访问/refresh这个EndPoint （RefreshEndpoint）

###### **RefreshEventListener 监听器**

当我们发布一个RefreshEvent事件的时候，RefreshEventListener就会监听到,然后调用handle处理

```java
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationReadyEvent) {
			handle((ApplicationReadyEvent) event);
		}
		else if (event instanceof RefreshEvent) {
			handle((RefreshEvent) event);
		}
	}
	...
	public void handle(RefreshEvent event) {
		if (this.ready.get()) { // don't handle events before app is ready
			log.debug("Event received " + event.getEventDesc());
			//调用ContextRefresher的refresh方法
			Set<String> keys = this.refresh.refresh();
			log.info("Refresh keys changed: " + keys);
		}
	}

```

###### **ContextRefresher**

调用refresh方法

```java
	public synchronized Set<String> refresh() {
		Set<String> keys = refreshEnvironment();
		// RefreshScope调用刷新方法
		this.scope.refreshAll();
		return keys;
	}

	public synchronized Set<String> refreshEnvironment() {
	    //把原来的配置都读取出来放到map里面
		Map<String, Object> before = extract(this.context.getEnvironment().getPropertySources());
		//更新环境
		updateEnvironment();
		//获取新老之间有差异的属性源key集合
		Set<String> keys = changes(before, extract(this.context.getEnvironment().getPropertySources())).keySet();
		//发布EnvironmentChangeEvent事件，这个事件是ConfigurationPropertiesRebinder监听的
		this.context.publishEvent(new EnvironmentChangeEvent(this.context, keys));
		return keys;
	}

```

###### **updateEnvironment（更新环境）**

- 先拷贝出基础的环境属性
- 通过SpringApplicationBuilder构建了一个简单的SpringBoot启动程序
- 这里面会添加两个监听器分别为：BootstrapApplicationListener与ConfigFileApplicationListener，BootstrapApplicationListener是引导程序的核心监听器，而ConfigFileApplicationListener主要就是读取配置文件的
- 然后调用run方法启动，就是springboot的run方法。使用非WEB的方式启动
- 然后读取最新的配置出来遍历，如果不是基础属性配置的环境并且老的环境和新的环境有一样的就替换成新的环境

```java
	@Override
	protected void updateEnvironment() {
		addConfigFilesToEnvironment();
	}

	/* For testing. */ ConfigurableApplicationContext addConfigFilesToEnvironment() {
		ConfigurableApplicationContext capture = null;
		try {
		    //拷贝出基础的环境环境属性，如系统环境变量，Java的属性
			StandardEnvironment environment = copyEnvironment(getContext().getEnvironment());

			Map<String, Object> map = new HashMap<>();
			map.put("spring.jmx.enabled", false);
			map.put("spring.main.sources", "");
			// gh-678 without this apps with this property set to REACTIVE or SERVLET fail
			map.put("spring.main.web-application-type", "NONE");
			map.put(BOOTSTRAP_ENABLED_PROPERTY, Boolean.TRUE.toString());
			environment.getPropertySources().addFirst(new MapPropertySource(REFRESH_ARGS_PROPERTY_SOURCE, map));

			SpringApplicationBuilder builder = new SpringApplicationBuilder(Empty.class).bannerMode(Banner.Mode.OFF)
					.web(WebApplicationType.NONE).environment(environment);
			// Just the listeners that affect the environment (e.g. excluding logging
			// listener because it has side effects)
			builder.application().setListeners(
					Arrays.asList(new BootstrapApplicationListener(), new BootstrapConfigFileApplicationListener()));
			capture = builder.run();
			if (environment.getPropertySources().contains(REFRESH_ARGS_PROPERTY_SOURCE)) {
				environment.getPropertySources().remove(REFRESH_ARGS_PROPERTY_SOURCE);
			}
			//老的配置
			MutablePropertySources target = getContext().getEnvironment().getPropertySources();
			String targetName = null;
			//遍历新配置
			for (PropertySource<?> source : environment.getPropertySources()) {
				String name = source.getName();
				if (target.contains(name)) {
					targetName = name;
				}
				//老的环境不包含基础的配置
				if (!this.standardSources.contains(name)) {
				    //老的环境里面有新环境的配置
					if (target.contains(name)) {
					    //替换
						target.replace(name, source);
					}
					else {
						if (targetName != null) {
							target.addAfter(targetName, source);
							// update targetName to preserve ordering
							targetName = name;
						}
						else {
							// targetName was null so we are at the start of the list
							target.addFirst(source);
							targetName = name;
						}
					}
				}
			}
		}
		finally {
			ConfigurableApplicationContext closeable = capture;
			while (closeable != null) {
				try {
					closeable.close();
				}
				catch (Exception e) {
					// Ignore;
				}
				if (closeable.getParent() instanceof ConfigurableApplicationContext) {
					closeable = (ConfigurableApplicationContext) closeable.getParent();
				}
				else {
					break;
				}
			}
		}
		return capture;
	}

```

###### changes(新老键值比较)

找出改变过的值放到result里面

```java
	private Map<String, Object> changes(Map<String, Object> before, Map<String, Object> after) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : before.keySet()) {
			if (!after.containsKey(key)) {
				result.put(key, null);
			}
			else if (!equal(before.get(key), after.get(key))) {
				result.put(key, after.get(key));
			}
		}
		for (String key : after.keySet()) {
			if (!before.containsKey(key)) {
				result.put(key, after.get(key));
			}
		}
		return result;
	}

```

**由ConfigurationPropertiesRebinder来处理这个事件。调用rebind方法进行配置重新加载，rebind方法实际上就是先销毁再去创建Bean。这里会遍历所有带@ConfigurationProperties注解的Bean，但是并不包含有@RefreshScope注解的。**

**destroyBean**：如果这个Bean实现了DisposableBean接口，就是执行destroy方法。或者是有实现AutoCloseable接口就进行资源关闭操作

**initializeBean**：bean初始化的操作

```java
	@Override
	public void onApplicationEvent(EnvironmentChangeEvent event) {
		if (this.applicationContext.equals(event.getSource())
				// Backwards compatible
				|| event.getKeys().equals(event.getSource())) {
			rebind();
		}
	}
		@ManagedOperation
	public void rebind() {
		this.errors.clear();
		for (String name : this.beans.getBeanNames()) {
			rebind(name);
		}
	}

	@ManagedOperation
	public boolean rebind(String name) {
		if (!this.beans.getBeanNames().contains(name)) {
			return false;
		}
		if (this.applicationContext != null) {
			try {
				Object bean = this.applicationContext.getBean(name);
				if (AopUtils.isAopProxy(bean)) {
					bean = ProxyUtils.getTargetObject(bean);
				}
				if (bean != null) {
					// TODO: determine a more general approach to fix this.
					// see https://github.com/spring-cloud/spring-cloud-commons/issues/571
					if (getNeverRefreshable().contains(bean.getClass().getName())) {
						return false; // ignore
					}
					this.applicationContext.getAutowireCapableBeanFactory().destroyBean(bean);
					this.applicationContext.getAutowireCapableBeanFactory().initializeBean(bean, name);
					return true;
				}
			}
			catch (RuntimeException e) {
				this.errors.put(name, e);
				throw e;
			}
			catch (Exception e) {
				this.errors.put(name, e);
				throw new IllegalStateException("Cannot rebind to " + name, e);
			}
		}
		return false;
	}

```

###### ContextRefresher#refreshAll

发布事件完以后就执行 RefreshScope中的refreshAll

```java
	public void refreshAll() {
		super.destroy();
		this.context.publishEvent(new RefreshScopeRefreshedEvent());
	}

```

super.destroy() 调用父类的`org.springframework.cloud.context.scope.GenericScope#destroy()`方法。这里把cache缓存给清理掉。

###### **BeanLifecycleWrapper#wrapper.destroy();**

**缓存中的bean清除了，但是这些bean还需要销毁。**

```java
		public void destroy() {
			if (this.callback == null) {
				return;
			}
			synchronized (this.name) {
				Runnable callback = this.callback;
				if (callback != null) {
					callback.run();
				}
				this.callback = null;
				this.bean = null;
			}
		}


```

然后当我们项目中有使用到被`@RefreshScope`注释的Bean的时候，在`doGetBean`方法中从`GenericScope`中的`cache`缓存中获取不到的话就会**重新去创建Bean**。这样获取到的就是最新的值了。

获取类的中属性时，会重新调用doGetBean的

##### 总结

- 被@RefreshScope标注的Bean在创建的时候是会生产一个代理对象
- 当发布RefreshEvent事件时，会调用ContextRefresher#refresh方法，该方法会记录当前的环境，然后构建一个非web的SpringApplicationBuilder并执行其run方法。
- 通过新旧环境的比较找出修改过的属性。changes操作来变更已有的PropertySource。
- 通过EnvironmentChangeEvent事件把缓存中清除
- 再次获取对象的时候重新创建，从新的属性环境中读取最新值



https://github.com/lhstack/spring-cloud-stream-redis



## Spring Cloud Client 自定义DiscoveryClient

```java
//ConfigServerConfigDataLocationResolver.java:157#uris将会检查时候空，如果非空，会覆盖discoveryClient中的服务列表
if (StringUtils.hasText(uris)) {
			String[] uri = StringUtils.commaDelimitedListToStringArray(uris);
			String paramStr = null;
			for (int i = 0; i < uri.length; i++) {
				...
			}
			if (StringUtils.hasText(paramStr)) {
				...
			}
			configClientProperties.setUri(uri);//注意此处与DiscoveryClient冲突
		}
```

加载config-server 源码：ConfigServerConfigDataLocationResolver

```java
org.springframework.cloud.config.client.ConfigServerConfigDataLocationResolver#resolve
org.springframework.cloud.config.client.ConfigServerConfigDataLocationResolver#resolveProfileSpecific
public List<ConfigServerConfigDataResource> resolveProfileSpecific(
			ConfigDataLocationResolverContext resolverContext, ConfigDataLocation location, Profiles profiles)
			throws ConfigDataLocationNotFoundException {
		setTextEncryptorDelegate(resolverContext);
		String uris = location.getNonPrefixedValue(getPrefix());
		PropertyHolder propertyHolder = loadProperties(resolverContext, uris);//①解析uris
		ConfigClientProperties properties = propertyHolder.properties;

		ConfigurableBootstrapContext bootstrapContext = resolverContext.getBootstrapContext();
		bootstrapContext.register(ConfigClientProperties.class,
				InstanceSupplier.of(properties).withScope(BootstrapRegistry.Scope.PROTOTYPE));
		bootstrapContext.addCloseListener(event -> event.getApplicationContext()
			.getBeanFactory()
			.registerSingleton("configDataConfigClientProperties",
					event.getBootstrapContext().get(ConfigClientProperties.class)));

		bootstrapContext.registerIfAbsent(ConfigClientRequestTemplateFactory.class,
				context -> new ConfigClientRequestTemplateFactory(log, context.get(ConfigClientProperties.class)));

		bootstrapContext.registerIfAbsent(RestTemplate.class, context -> {
			ConfigClientRequestTemplateFactory factory = context.get(ConfigClientRequestTemplateFactory.class);
			RestTemplate restTemplate = createRestTemplate(factory.getProperties());
			if (restTemplate != null) {
				// shouldn't normally happen
				return restTemplate;
			}
			return factory.create();
		});

		bootstrapContext.registerIfAbsent(PropertyResolver.class,
				context -> new PropertyResolver(resolverContext.getBinder(), getBindHandler(resolverContext)));

		ConfigServerConfigDataResource resource = new ConfigServerConfigDataResource(properties, location.isOptional(),
				profiles);
		resource.setProfileSpecific(!ObjectUtils.isEmpty(profiles));
		resource.setLog(log);
		resource.setRetryProperties(propertyHolder.retryProperties);

		boolean discoveryEnabled = resolverContext.getBinder()
			.bind(CONFIG_DISCOVERY_ENABLED, Bindable.of(Boolean.class), getBindHandler(resolverContext))
			.orElse(false);

		boolean retryEnabled = resolverContext.getBinder()
			.bind(ConfigClientProperties.PREFIX + ".fail-fast", Bindable.of(Boolean.class),
					getBindHandler(resolverContext))
			.orElse(false);

		if (discoveryEnabled) {
			log.debug(LogMessage.format("discovery enabled"));
			// register ConfigServerInstanceMonitor
			bootstrapContext.registerIfAbsent(ConfigServerInstanceMonitor.class, context -> {
				ConfigServerInstanceProvider.Function function = context
					.get(ConfigServerInstanceProvider.Function.class);

				ConfigServerInstanceProvider instanceProvider;
				if (ConfigClientRetryBootstrapper.RETRY_IS_PRESENT && retryEnabled) {
					log.debug(LogMessage.format("discovery plus retry enabled"));
					RetryTemplate retryTemplate = RetryTemplateFactory.create(propertyHolder.retryProperties, log);
					instanceProvider = new ConfigServerInstanceProvider(function, resolverContext.getBinder(),
							getBindHandler(resolverContext)) {
						@Override
						public List<ServiceInstance> getConfigServerInstances(String serviceId) {
							return retryTemplate.execute(retryContext -> super.getConfigServerInstances(serviceId));
						}
					};
				}
				else {
					instanceProvider = new ConfigServerInstanceProvider(function, resolverContext.getBinder(),
							getBindHandler(resolverContext));
				}
				instanceProvider.setLog(log);

				ConfigClientProperties clientProperties = context.get(ConfigClientProperties.class);
				ConfigServerInstanceMonitor instanceMonitor = new ConfigServerInstanceMonitor(log, clientProperties,
						instanceProvider);
				instanceMonitor.setRefreshOnStartup(false);
				instanceMonitor.refresh(); //②会从DiscoveryClient获取instance list，并更新到list中
				return instanceMonitor;
			});
			// promote ConfigServerInstanceMonitor to bean so updates can be made to
			// config client uri
			bootstrapContext.addCloseListener(event -> {
				ConfigServerInstanceMonitor configServerInstanceMonitor = event.getBootstrapContext()
					.get(ConfigServerInstanceMonitor.class);
				event.getApplicationContext()
					.getBeanFactory()
					.registerSingleton("configServerInstanceMonitor", configServerInstanceMonitor);
			});
		}

		List<ConfigServerConfigDataResource> locations = new ArrayList<>();
		locations.add(resource);

		return locations;
	}  

```

