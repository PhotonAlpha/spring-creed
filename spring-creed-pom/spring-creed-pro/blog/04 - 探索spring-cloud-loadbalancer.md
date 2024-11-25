# 探索Spring Cloud Loadbalancer

参考文档：

- [spring docs - simplediscoveryclient](https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#simplediscoveryclient)
- [spring docs - custom-loadbalancer-configuration](https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#custom-loadbalancer-configuration)

## 如何开始一个Loadbalancer

以下都是以simple模式为例

1. 首先定义配置文件 application.yml

   ```yaml
   spring:
     cloud:
       loadbalancer:
         health-check:
           refetch-instances-interval: 60s
       discovery:
         client:
           simple:
             instances:
               my-cluster:
                 - instanceId: cluster-1
                   host: 127.0.0.1
                   port: 8088
                   secure: false
   
                 - instanceId: cluster-2
                   host: 127.0.0.1
                   port: 8089
                   secure: false
   ```

2. 配置相应的client

   ```java
   @Configuration
   @LoadBalancerClients(value = {
       @LoadBalancerClient(name = "my-cluster", configuration = MyLoadBalancerConfiguration.class),
       @LoadBalancerClient(name = "my-cluster2", configuration = MyLoadBalancerConfiguration.class)
   })
   public class ClusterRestTemplateConfiguration {
       @Bean("healthCheckRestTemplate")
       public RestTemplate healthCheckRestTemplate() {
           return new RestTemplate();
       }
       @Bean("defaultRestTemplate")
       @LoadBalanced
       public RestTemplate restTemplate() {
           return new RestTemplate();
       }
       @Bean
       @LoadBalanced
       public RestTemplate restTemplate2() {
           return new RestTemplate();
       }
   }
   
   @Slf4j 
   //注意此处不可以加@Configuration
   public class MyLoadBalancerConfiguration {
       private static final String SLASH = "/";
       @Bean //默认可用配置
       public ServiceInstanceListSupplier discoveryClientWithHealthChecksServiceInstanceListSupplier(
               ConfigurableApplicationContext context, @Qualifier(HEALTH_CHECK_REST_TEMPLATE) RestTemplate restTemplate) {
           log.info("initializing ServiceInstanceListSupplier");
           ServiceInstanceListSupplierBuilder.DelegateCreator healthCheckCreator = (appContext, delegate) -> {
               LoadBalancerClientFactory loadBalancerClientFactory = appContext.getBean(LoadBalancerClientFactory.class);
               return blockingHealthCheckServiceInstanceListSupplier(restTemplate, delegate, loadBalancerClientFactory);
           };
   
           return ServiceInstanceListSupplier.builder()
                   .withHints()
                   .withBlockingDiscoveryClient()
                   .withBlockingHealthChecks(restTemplate)
                   .build(context);
       }
     
       // @Bean 自定义配置
       public ServiceInstanceListSupplier discoveryClientWithHealthChecksServiceInstanceListSupplier(
               ConfigurableApplicationContext context, @Qualifier(HEALTH_CHECK_REST_TEMPLATE) RestTemplate restTemplate) {
           log.info("initializing ServiceInstanceListSupplier");
           ServiceInstanceListSupplierBuilder.DelegateCreator healthCheckCreator = (appContext, delegate) -> {
               LoadBalancerClientFactory loadBalancerClientFactory = appContext.getBean(LoadBalancerClientFactory.class);
               return blockingHealthCheckServiceInstanceListSupplier(restTemplate, delegate, loadBalancerClientFactory);
           };
   
           return ServiceInstanceListSupplier.builder()
                   .withHints()
                   .withBlockingDiscoveryClient()
                   .with(healthCheckCreator)
                   .build(context);
       }
       private ServiceInstanceListSupplier blockingHealthCheckServiceInstanceListSupplier(RestTemplate restTemplate,
                                                                                          ServiceInstanceListSupplier delegate,
                                                                                          ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
           return new MyHealthCheckServiceInstanceListSupplier(delegate, loadBalancerClientFactory,
                   (serviceInstance, healthCheckPath) -> Mono.defer(() -> {
                       URI uri = UriComponentsBuilder.fromUriString(getUri(serviceInstance, healthCheckPath)).build()
                               .toUri();
                       try {
                           return Mono
                                   .just(HttpStatus.OK.equals(restTemplate.getForEntity(uri, Void.class).getStatusCode()));
                       }
                       catch (Exception ignored) {
                         	// 自定义health check
                           return Mono.just(false);
                       }
                   }));
       }
   
       static String getUri(ServiceInstance serviceInstance, String healthCheckPath) {
           if (StringUtils.hasText(healthCheckPath)) {
               String path = healthCheckPath.startsWith(SLASH) ? healthCheckPath : SLASH + healthCheckPath;
               return serviceInstance.getUri().toString() + path;
           }
           return serviceInstance.getUri().toString();
       }
   
   }
   ```

   > [!IMPORTANT]
   >
   > @LoadBalancerClient or @LoadBalancerClients 自定义Configuration类 添加**@Configuration** 导致异常
   >
   > The classes you pass as `@LoadBalancerClient` or `@LoadBalancerClients` configuration arguments should either not be **annotated** with **`@Configuration`** or **be outside component scan scope**.

3. 下面就可以愉快的使用**RestTemplate**

   ```java
   restTemplate.getForObject("http://my-cluster/person/student/1?total=10", String.class)
   ```

   

## 源码解读



### @LoadBalanced 注解作用

1. **org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration**

   以下是部分源码

   ```java
   @AutoConfiguration
   	static class DeferringLoadBalancerInterceptorConfig {
   		@Bean
   		@ConditionalOnMissingBean
   		public DeferringLoadBalancerInterceptor deferringLoadBalancerInterceptor(
   				ObjectProvider<BlockingLoadBalancerInterceptor> loadBalancerInterceptorObjectProvider) {
   			return new DeferringLoadBalancerInterceptor(loadBalancerInterceptorObjectProvider);
   		}
   		//此处会启用一个 BeanPostProcessor 将 DeferringLoadBalancerInterceptor 注入到 LoadBalancerRestClientBuilderBeanPostProcessor
   		@Bean
   		@ConditionalOnBean(DeferringLoadBalancerInterceptor.class)
   		@ConditionalOnMissingBean
   		LoadBalancerRestClientBuilderBeanPostProcessor lbRestClientPostProcessor(
   				DeferringLoadBalancerInterceptor loadBalancerInterceptor, ApplicationContext context) {
   			return new LoadBalancerRestClientBuilderBeanPostProcessor(loadBalancerInterceptor, context);
   		}
   	}
   
   	@AutoConfiguration
   	@Conditional(RetryMissingOrDisabledCondition.class)
   	static class LoadBalancerInterceptorConfig {
   		@Bean
   		public LoadBalancerInterceptor loadBalancerInterceptor(LoadBalancerClient loadBalancerClient,
   				LoadBalancerRequestFactory requestFactory) {
   			return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
   		}
       //此处会添加LoadBalancerInterceptor拦截器
   		@Bean
   		@ConditionalOnMissingBean
   		public RestTemplateCustomizer restTemplateCustomizer(LoadBalancerInterceptor loadBalancerInterceptor) {
   			return restTemplate -> {
   				List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());
   				list.add(loadBalancerInterceptor);
   				restTemplate.setInterceptors(list);
   			};
   		}
   	}
   ```

2. **org.springframework.cloud.client.loadbalancer.LoadBalancerRestClientBuilderBeanPostProcessor**

   很简单，就是在RestTemplate中注入一个拦截器，下面是源码

   ```java
   	@Override
   	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
   		if (bean instanceof RestClient.Builder) {
   			if (context.findAnnotationOnBean(beanName, LoadBalanced.class) == null) {
   				return bean;
   			}
   			((RestClient.Builder) bean).requestInterceptor(loadBalancerInterceptor);
   		}
   		return bean;
   	}
   ```

3. **org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor**

   请求被拦截后，最终都是委托给了`LoadBalancerClient`处理。

   ```java
   private final LoadBalancerClient loadBalancer;
   
   @Override
   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
       throws IOException {
     URI originalUri = request.getURI();
     String serviceName = originalUri.getHost();
     Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
     return loadBalancer.execute(serviceName, requestFactory.createRequest(request, body, execution));
   }
   ```

   下面就轮到**`@LoadBalancerClient`**来工作了

   此拦截器拦截请求后把它的`serviceName`委托给了`LoadBalancerClient`去执行，根据`ServiceName`可能对应N多个实际的`Server`，因此就可以从众多的Server中运用均衡算法，挑选出一个最为合适的`Server`做最终的请求（它持有真正的请求执行器`ClientHttpRequestExecution`）。

   

4. `@Qualifier`作用

   > [!TIP]
   >
   > 此注解中包含一个`@Qualifier`,他的作用是专门用来注入制定对象对象的注解
   >
   > ```java
   > /** 自定义注解 */
   > @Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
   > @Retention(RetentionPolicy.RUNTIME)
   > @Documented
   > @Inherited
   > @Qualifier
   > public @interface QualifierDate {
   >   //
   > }
   > 
   > /** 当前日期，应该是2022年*/
   > @Bean
   > public Date curDate(){
   >     return new Date();
   > }
   > 
   > /**明年日期 ，应该是2023年*/
   > @Bean
   > @QualifierDate
   > public Date nextYear(){
   >     Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
   >     ca.setTime(new Date());  //设置时间为当前时间
   >     ca.add(Calendar.YEAR, +1); //年份+1
   >     return ca.getTime();
   > }
   > // 那么在注入到集合的时候，就可以使用@QualifierDate来限定：
   > @Autowired
   > @QualifierDate
   > List<Date> dates = Collections.emptyList();
   > ```

### @LoadBalancerClients @LoadBalancerClient 注解作用

请求被拦截后，最终都是委托给了`LoadBalancerClient`处理。

```java
// 由使用负载平衡器选择要向其发送请求的服务器的类实现
public interface ServiceInstanceChooser {

	// 从负载平衡器中为指定的服务选择Service服务实例。
	// 也就是根据调用者传入的serviceId，负载均衡的选择出一个具体的实例出来
	ServiceInstance choose(String serviceId);
}

// 它自己定义了三个方法
public interface LoadBalancerClient extends ServiceInstanceChooser {
	
	// 执行请求
	<T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;
	<T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;
	
	// 重新构造url：把url中原来写的服务名 换掉 换成实际的
	URI reconstructURI(ServiceInstance instance, URI original);
}
```

它只有一个实现类org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient （ServiceInstanceChooser是有多个实现类的~）。

```java
@Override
	public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request)
			throws IOException {
		if (serviceInstance == null) {
			throw new IllegalArgumentException("Service Instance cannot be null, serviceId: " + serviceId);
		}
		DefaultResponse defaultResponse = new DefaultResponse(serviceInstance);
		Set<LoadBalancerLifecycle> supportedLifecycleProcessors = getSupportedLifecycleProcessors(serviceId);
		Request lbRequest = request instanceof Request ? (Request) request : new DefaultRequest<>();
		supportedLifecycleProcessors
				.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, new DefaultResponse(serviceInstance)));
		try {
			T response = request.apply(serviceInstance);//替换url，参考下面的替换链路
			Object clientResponse = getClientResponse(response);
			supportedLifecycleProcessors
					.forEach(lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
							lbRequest, defaultResponse, clientResponse)));
			return response;
		}
		catch (IOException iOException) {
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
					new CompletionContext<>(CompletionContext.Status.FAILED, iOException, lbRequest, defaultResponse)));
			throw iOException;
		}
		catch (Exception exception) {
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
					new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, defaultResponse)));
			ReflectionUtils.rethrowRuntimeException(exception);
		}
		return null;
	}

```



替换 url的链路：

1. org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient#execute(String serviceId, LoadBalancerRequest<T> request)
2. org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory
3. org.springframework.cloud.client.loadbalancer.BlockingLoadBalancerRequest
4. org.springframework.cloud.client.loadbalancer.ServiceRequestWrapper#getURI()



#### 最最重要的核心功能**ServiceInstance choose(String serviceId);**

org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration默认配置了org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer

```java
	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
				.getIfAvailable(NoopServiceInstanceListSupplier::new);
    // supplier.get(request)方法
    //.next()取Flux的第一个元素
    //.map(serviceInstances -> processInstanceResponse(supplier, serviceInstances)); 获取serviceInstances，调用算法得到 Response<ServiceInstance>
		return supplier.get(request).next()
				.map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
	}
```

以org.springframework.cloud.loadbalancer.core.HealthCheckServiceInstanceListSupplier为例

```java
---壹---
org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplierBuilder	
public ServiceInstanceListSupplierBuilder withBlockingDiscoveryClient() {
		if (baseCreator != null && LOG.isWarnEnabled()) {
			LOG.warn("Overriding a previously set baseCreator with a blocking DiscoveryClient baseCreator.");
		}
		this.baseCreator = context -> {
			DiscoveryClient discoveryClient = context.getBean(DiscoveryClient.class);
			// 壹创建一个基本的DiscoveryClientServiceInstanceListSupplier，从配置文件中获取service instances
			return new DiscoveryClientServiceInstanceListSupplier(discoveryClient, context.getEnvironment());
		};
		return this;
	}
---贰---
public HealthCheckServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
    ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory,
    BiFunction<ServiceInstance, String, Mono<Boolean>> aliveFunction) {
  super(delegate);
  this.healthCheck = loadBalancerClientFactory.getProperties(getServiceId()).getHealthCheck();
  // ①从配置中获取health check endpoint
  //
  defaultHealthCheckPath = healthCheck.getPath().getOrDefault("default", "/actuator/health");
  this.aliveFunction = aliveFunction; // ② 执行BiFunction，即heal check的具体算法
  Repeat<Object> aliveInstancesReplayRepeat = Repeat
      .onlyIf(repeatContext -> this.healthCheck.getRefetchInstances())
      .fixedBackoff(healthCheck.getRefetchInstancesInterval());// ③ 启用 health check 定时器，如果是enabled，fixedBackoff即多久之后重复执行
  // defer懒加载执行，只有在被subscribe时才执行。相比于just是立即执行。 此处返回 DiscoveryClientServiceInstanceListSupplier中得到的instances
  // repeatWhen 满足条件会定时执行
  // healthCheckFlux 执行health check检查
  // 返回alive的instances
  Flux<List<ServiceInstance>> aliveInstancesFlux = Flux.defer(delegate).repeatWhen(aliveInstancesReplayRepeat)
      .switchMap(serviceInstances -> healthCheckFlux(serviceInstances).map(alive -> List.copyOf(alive)));
  // delaySubscription 延迟对此 Flux 源的订阅，直到给定的时间段过去。延迟是通过并行默认 Scheduler 引入的。
  // replay 将此 Flux 变成可连接的热源，并缓存最后发出的信号以供后续订阅者使用。将保留最多给定历史大小的 onNext 信号。完成和错误也将被重放。请注意，replay(0) 将仅缓存终端信号而不会过期, Re-connects are not supported.
  // refCount 不仅能够在订阅者接入的时候自动触发，还会检测订阅者的取消动作。如果订阅者全部取消订阅，则会将源“断开连接”，再有新的订阅者接入的时候才会继续“连上”发布者
  aliveInstancesReplay = aliveInstancesFlux.delaySubscription(healthCheck.getInitialDelay()).replay(1)
      .refCount(1);// ⑤ 生成数据源flux
}

@Override
public void afterPropertiesSet() {
  Disposable healthCheckDisposable = this.healthCheckDisposable;
  if (healthCheckDisposable != null) {
    healthCheckDisposable.dispose();
  }
  this.healthCheckDisposable = aliveInstancesReplay.subscribe(); //⑤订阅更新以下可用的服务
}

protected Flux<List<ServiceInstance>> healthCheckFlux(List<ServiceInstance> instances) {
  // ④ 调用isAlive方法，过滤instances
		Repeat<Object> healthCheckFluxRepeat = Repeat.onlyIf(repeatContext -> healthCheck.getRepeatHealthCheck())
				.fixedBackoff(healthCheck.getInterval());
		return Flux.defer(() -> {
			List<Mono<ServiceInstance>> checks = new ArrayList<>(instances.size());
			for (ServiceInstance instance : instances) {
				Mono<ServiceInstance> alive = isAlive(instance).onErrorResume(error -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format(
								"Exception occurred during health check of the instance for service %s: %s",
								instance.getServiceId(), instance.getUri()), error);
					}
					return Mono.empty();
				}).timeout(healthCheck.getInterval(), Mono.defer(() -> {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format(
								"The instance for service %s: %s did not respond for %s during health check",
								instance.getServiceId(), instance.getUri(), healthCheck.getInterval()));
					}
					return Mono.empty();
				})).handle((isHealthy, sink) -> {
					if (isHealthy) {
						sink.next(instance);//过滤false的instance
					}
				});

				checks.add(alive);
			}
			List<ServiceInstance> result = new ArrayList<>();
			if (healthCheck.isUpdateResultsList()) {
        //返回实时更新的alive instances
				return Flux.merge(checks).map(alive -> {
					result.add(alive);
					return result;
				}).defaultIfEmpty(result);
			}
			return Flux.merge(checks).collectList();
		}).repeatWhen(healthCheckFluxRepeat);
	}

	@Override
	public Flux<List<ServiceInstance>> get() {
		return aliveInstancesReplay; //一旦被订阅此处会被刷新，根据⑤
	}
```

`handle` 方法有点不同：它是一个实例方法，意味着它被链接到一个现有的源（就像常见的操作符）上。它存在于 `Mono` 和 `Flux` 中。

它靠近于 `generate`，从某种意义上说，它使用 `SynchronousSink` 并只允许逐个的发出。但是，`handle` 可以用来从每个源元素中生成一个任意值，可能会跳过一些元素。这样，它可以作为 `map` 和 `filter` 的组合。handle的方法签名如下：

```java
Flux<R> handle(BiConsumer<T, SynchronousSink<R>>);
```

让我们考虑一个例子。响应式流规范不允许序列中的值为 `null`。但是你想使用一个预先存在的方法作为map函数来执行 `map`，而该方法有时返回null怎么办？

例如，下面的方法可以安全地应用于整数源：

```java
public String alphabet(int letterNumber) {
	if (letterNumber < 1 || letterNumber > 26) {
		return null;
	}
	int letterIndexAscii = 'A' + letterNumber - 1;
	return "" + (char) letterIndexAscii;
}
```

然后，我们可以使用 `handle` 来删除任何空值：

Example 13. `handle` 用于 "**映射和消除null值**" 的场景

```java
Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
    .handle((i, sink) -> {
        String letter = alphabet(i); 
        if (letter != null) 
            sink.next(letter); 
    });

alphabet.subscribe(System.out::println);
```

> [!TIP]
>
> 1. 映射到字母。
>
> 2. 如果 “map函数” 返回null….
>
> 3. 通过不调用 `sink.next` 来过滤掉它。

将打印出：

```
M
I
T
```







