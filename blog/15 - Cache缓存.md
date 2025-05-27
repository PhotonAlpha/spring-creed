# 探索缓存@Cacheable

## @EnableCaching 源码解析

当要使用 `@Cacheable` 注解时需要引入 `@EnableCaching` 注解开启缓存功能。为什么呢？现在就来看看为什么要加入 @EnableCaching 注解才能开启缓存切面呢？源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CachingConfigurationSelector.class)
public @interface EnableCaching {

	boolean proxyTargetClass() default false;
	
	AdviceMode mode() default AdviceMode.PROXY;

	int order() default Ordered.LOWEST_PRECEDENCE;
}
```

可以看出是通过 `@Import` 注解来导入一个类 `CachingConfigurationSelector`，猜测下，这个类肯定是一个入口类，也可以说是个触发类。注意此处 mode() 默认是 AdviceMode.PROXY。

进入 CachingConfigurationSelector 类，源码如下：

```java
public class CachingConfigurationSelector extends AdviceModeImportSelector<EnableCaching> {

	@Override
	public String[] selectImports(AdviceMode adviceMode) {
		return switch (adviceMode) {
			case PROXY -> getProxyImports();
			case ASPECTJ -> getAspectJImports();
		};
	}
	
	private String[] getProxyImports() {
		List<String> result = new ArrayList<>(3);
		result.add(AutoProxyRegistrar.class.getName());
		result.add(ProxyCachingConfiguration.class.getName());
		if (jsr107Present && jcacheImplPresent) {
			result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
		}
		return StringUtils.toStringArray(result);
}

```

从 getProxyImports() 方法可知导入两个类：**`AutoProxyRegistrar`** 类、**`ProxyCachingConfiguration`** 类。那么接下来就重点分析这两个类是用来干啥的？

#### 1、AutoProxyRegistrar

看到 XxxRegistrar 就可以确定要注册一个类。进入核心源码如下：

```java
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean candidateFound = false;
		Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
		for (String annType : annTypes) {
			AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
			if (candidate == null) {
				continue;
			}
			Object mode = candidate.get("mode");
			Object proxyTargetClass = candidate.get("proxyTargetClass");
			if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() &&
					Boolean.class == proxyTargetClass.getClass()) {
				candidateFound = true;
				if (mode == AdviceMode.PROXY) {
					AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
					if ((Boolean) proxyTargetClass) {
						AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
						return;
					}
				}
			}
		}
	}
}

public abstract class AopConfigUtils {
  @Nullable
	public static BeanDefinition registerAutoProxyCreatorIfNecessary(
			BeanDefinitionRegistry registry, @Nullable Object source) {
		return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
	}
}
	
```

很明显在这注册了一个 `InfrastructureAdvisorAutoProxyCreator` 类，这个类和事务切面入口类就是同一个。而这个类并不会做什么逻辑，所有的逻辑都在其父类 `AbstractAutoProxyCreator` 抽象类中，该类仅仅充当一个入口类。

`AbstractAutoProxyCreator` 类又是一个 `BeanPostProcessor` 接口的应用。所以关注这个接口的两个方法。这里只需要关注第二个方法 `postProcessAfterInitialization()`，源码如下：

```java
	@Override
	public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
		if (bean != null) {
			/** 获取缓存 key */
			Object cacheKey = getCacheKey(bean.getClass(), beanName);
			if (this.earlyProxyReferences.remove(cacheKey) != bean) {
				/** 是否有必要创建代理对象 */
				return wrapIfNecessary(bean, beanName, cacheKey);
			}
		}
		return bean;
	}

```

很明显在 `AbstractAutoProxyCreator` 抽象类中会调用 `wrapIfNecessary()` 方法去判断对当前 bean 是否需要创建代理对象。那么这里根据什么来判断呢？进入该方法，核心源码如下：

```java
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {

		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);

		 /** 合适的通知不为空 */
		if (specificInterceptors != DO_NOT_PROXY) {
		
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			return proxy;
		}
		
		return bean;
	}

```

在上面这段逻辑，很明显 `getAdvicesAndAdvisorsForBean()` 方法就是判断依据。如果有值就需要创建代理，反之不需要。那么重点关注该方法内部逻辑，核心源码如下：

```java
	@Override
	@Nullable
	protected Object[] getAdvicesAndAdvisorsForBean(
			Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

		/** tc_tag-99: 查找适合这个类的 advisors 切面通知 */
		List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
		if (advisors.isEmpty()) {
			return DO_NOT_PROXY;
		}
		return advisors.toArray();
	}
```

继续进入 `findEligibleAdvisors()` 内部逻辑，核心源码如下：

```java
	protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		List<Advisor> candidateAdvisors = findCandidateAdvisors();
		List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
		extendAdvisors(eligibleAdvisors);
		if (!eligibleAdvisors.isEmpty()) {
			eligibleAdvisors = sortAdvisors(eligibleAdvisors);
		}
		return eligibleAdvisors;
	}
```

看到 `findCandidateAdvisors()` 方法，内部逻辑如下：

```java
	protected List<Advisor> findCandidateAdvisors() {
		return this.advisorRetrievalHelper.findAdvisorBeans();
	}
	
	public List<Advisor> findAdvisorBeans() {
		String[] advisorNames = this.cachedAdvisorBeanNames;
		if (advisorNames == null) {
			advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
					this.beanFactory, Advisor.class, true, false);
			this.cachedAdvisorBeanNames = advisorNames;
		}
		
		List<Advisor> advisors = new ArrayList<>();

		for (String name : advisorNames) {
			if (isEligibleBean(name)) {
				advisors.add(this.beanFactory.getBean(name, Advisor.class));
			}
		}
		return advisors;
	}
```

从上述代码中可以得知，Spring 通过调用 `beanNamesForTypeIncludingAncestors(Advisor.class)` 方法来获取所有 Spring 容器中实现 Advsior 接口的实现类。

那么现在先看到 `ProxyCachingConfiguration` 类，源码如下：

```java
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

	// cache_tag: 缓存方法增强器
	@Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor(
			CacheOperationSource cacheOperationSource, CacheInterceptor cacheInterceptor) {

		BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
		// 缓存用来解析一些属性封装的对象 CacheOperationSource
		advisor.setCacheOperationSource(cacheOperationSource);
		// 缓存拦截器执行对象
		advisor.setAdvice(cacheInterceptor);
		if (this.enableCaching != null) {
			advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
		}
		return advisor;
	}
	// 先省略一部分...
}
```

很明显 `BeanFactoryCacheOperationSourceAdvisor` 实现了 Advisor 接口。那么在上面调用 `beanNamesForTypeIncludingAncestors(Advisor.class)` 方法时就可以获取到该实例。该实例就会被提添加到候选集合 `advisors` 中返回。

okay，看完 `findCandidateAdvisors()` 方法， 再看到 `findAdvisorsThatCanApply()` 方法，前一个方法获取到了 Spring 容器中所有实现了 Advisor 接口的实现。然后再调用 `findAdvisorsThatCanApply()` 方法，去判断有哪些 Advisor 适用于当前 bean。进入 `findAdvisorsThatCanApply()` 内部逻辑，核心源码如下：

```java
	protected List<Advisor> findAdvisorsThatCanApply(
			List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {

		ProxyCreationContext.setCurrentProxiedBeanName(beanName);
		try {
			return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
		}
		finally {
			ProxyCreationContext.setCurrentProxiedBeanName(null);
		}
	}


public abstract class AopUtils {
	public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {

		/** 没有切面，匹配个屁 */
		if (candidateAdvisors.isEmpty()) {
			return candidateAdvisors;
		}

		List<Advisor> eligibleAdvisors = new ArrayList<>();
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
				eligibleAdvisors.add(candidate);
			}
		}
		boolean hasIntroductions = !eligibleAdvisors.isEmpty();
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor) {
				// already processed
				continue;
			}
			// tc_tag-96: 开始 for 循环 candidateAdvisors 每个增强器，看是否能使用与这个 bean
			if (canApply(candidate, clazz, hasIntroductions)) {
				eligibleAdvisors.add(candidate);
			}
		}
		return eligibleAdvisors;
	}
}
```

继续进入 `canApply()` 核心逻辑如下：

```java
	public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {

		/**
		 * 通过 Pointcut 获取到 ClassFilter 类的匹配器
		 * 然后匹配 targetClass 是否在 Pointcut 配置的包路径下面么？具体实现看 AspectJExpressionPointcut
		 */
		if (!pc.getClassFilter().matches(targetClass)) {
			return false;
		}
		/**
		 * 通过 Pointcut 获取到 MethodMatcher 类的匹配器
		 * 然后判断这个类下面的方法是否在 Pointcut 配置的包路径下面么？
		 * 或者是这个方法上是否标注了 @Transactional、@Cacheable等注解呢？
		 */
		MethodMatcher methodMatcher = pc.getMethodMatcher();
		if (methodMatcher == MethodMatcher.TRUE) {
			// No need to iterate the methods if we're matching any method anyway...
			return true;
		}

		IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
		if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
			introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
		}

		Set<Class<?>> classes = new LinkedHashSet<>();
		if (!Proxy.isProxyClass(targetClass)) {
			classes.add(ClassUtils.getUserClass(targetClass));
		}
		classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
		for (Class<?> clazz : classes) {
			/**
			 * tc_tag1: 获取这个 targetClass 类下所有的方法，开始挨个遍历是否满 Pointcut 配置的包路径下面么？
			 * 或者是这个方法上是否标注了 @Transactional、@Cacheable等注解呢？
			 */
			Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
			for (Method method : methods) {
				if (introductionAwareMethodMatcher != null ?
						introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) :
						/**
						 * tc_tag2: 注意对于 @Transactional 注解的 Pointcut 匹配还是比较复杂的，匹配逻辑在 TransactionAttributeSourcePointcut
						 */
						methodMatcher.matches(method, targetClass)) {
					return true;
				}
			}
		}

		return false;
	}

```

从上面源码中放眼过去，就能看到两个熟悉的老朋友：`ClassFilter` 类、`MethodMatcher` 类，`ClassFilter` 类是用来判断当前 bean 所在的 Class 上面是否有标注 `@Caching`、`@Cacheable`、`@CachePut`、`@CacheEvict` 注解。`MethodMatcher` 类是用来判断当前 bean 的方法上是否有标注 `@Caching`、`@Cacheable`、`@CachePut`、`@CacheEvict` 注解。

那么这两个过滤器对象是在哪里创建的呢？

回到 `ProxyCachingConfiguration` 类，源码如下：

```java
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

	// cache_tag: 缓存方法增强器
	@Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor(
			CacheOperationSource cacheOperationSource, CacheInterceptor cacheInterceptor) {

		BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
		// 缓存用来解析一些属性封装的对象 CacheOperationSource
		advisor.setCacheOperationSource(cacheOperationSource);
		// 缓存拦截器执行对象
		advisor.setAdvice(cacheInterceptor);
		if (this.enableCaching != null) {
			advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
		}
		return advisor;
	}
}

```

看到 `BeanFactoryCacheOperationSourceAdvisor` 类，我们知道一个 `Advisor` 必然是由 `Advice` 和 `Pointcut` 组成的。
而 `Pointcut` 肯定是由 `ClassFilter` 和 `MethodMatcher` 组成的。进入该类，源码如下：

```java
public class BeanFactoryCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	@Nullable
	private CacheOperationSource cacheOperationSource;

	private final CacheOperationSourcePointcut pointcut = new CacheOperationSourcePointcut() {

		@Nullable
		protected CacheOperationSource getCacheOperationSource() {
			return cacheOperationSource;
		}
	};

	public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
		this.cacheOperationSource = cacheOperationSource;
	}

	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}
}
```

关注到 `CacheOperationSourcePointcut` 类，进入源码如下：

```java
final class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

	public CacheOperationSourcePointcut() {
		setClassFilter(new CacheOperationSourceClassFilter());
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return (this.cacheOperationSource == null ||
				!CollectionUtils.isEmpty(this.cacheOperationSource.getCacheOperations(method, targetClass)));
	}

	@Nullable
	protected abstract CacheOperationSource getCacheOperationSource();

	private class CacheOperationSourceClassFilter implements ClassFilter {

		@Override
		public boolean matches(Class<?> clazz) {
			if (CacheManager.class.isAssignableFrom(clazz)) {
				return false;
			}
			CacheOperationSource cas = getCacheOperationSource();
			return (cas == null || cas.isCandidateClass(clazz));
		}
	}
}
```

从源码中可以看出 `ClassFilter = CacheOperationSourceClassFilter`，`MethodMatcher = CacheOperationSourcePointcut`。这里 ClassFilter 的 matches() 方法不对类进行过滤。这个类过滤放到了 MethodMatcher 的 matches() 方法中，和对方法的过滤集成在一起。

所以这里关注到 MethodMatcher#matches() 匹配方法，看看它是怎么进行匹配的。核心源码如下：

```java
final class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return (this.cacheOperationSource == null ||
				!CollectionUtils.isEmpty(this.cacheOperationSource.getCacheOperations(method, targetClass)));
	}
}
```

进入 getCacheOperations() 方法，核心源码如下：

```java
	@Override
	@Nullable
	public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
		if (method.getDeclaringClass() == Object.class) {
			return null;
		}
		Object cacheKey = getCacheKey(method, targetClass);
		Collection<CacheOperation> cached = this.operationCache.get(cacheKey);

		if (cached != null) {
			return (cached != NULL_CACHING_MARKER ? cached : null);
		}
		else {
      // cache_tag: 计算缓存注解上面的配置的值，然后封装成 CacheOperation 缓存属性对象，基本和事物的一样
			// 注意每个缓存注解对应一种不同的解析处理
			Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
			if (cacheOps != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Adding cacheable method '" + method.getName() + "' with operations: " + cacheOps);
				}
				this.operationCache.put(cacheKey, cacheOps);
			}
			else {
				this.operationCache.put(cacheKey, NULL_CACHING_MARKER);
			}
			return cacheOps;
		}
	}
```

发现这段代码和 `@Transactional` 注解匹配一模一样，其实就是用的同一套逻辑，只不过注解不同而已。匹配过程是一模一样的，看到 `computeCacheOperations()` 方法，核心源码如下：

```java
	@Nullable
	private Collection<CacheOperation> computeCacheOperations(Method method, @Nullable Class<?> targetClass) {
		// Don't allow non-public methods, as configured.
		if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			return null;
		}

		// The method may be on an interface, but we need metadata from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

		// First try is the method in the target class.
		Collection<CacheOperation> opDef = findCacheOperations(specificMethod);
		if (opDef != null) {
			return opDef;
		}

		// Second try is the caching operation on the target class.
		opDef = findCacheOperations(specificMethod.getDeclaringClass());
		if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
			return opDef;
		}

		if (specificMethod != method) {
			// Fallback is to look at the original method.
			opDef = findCacheOperations(method);
			if (opDef != null) {
				return opDef;
			}
			// Last fallback is the class of the original method.
			opDef = findCacheOperations(method.getDeclaringClass());
			if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
				return opDef;
			}
		}

		return null;
	}
}

public class AnnotationCacheOperationSource extends AbstractFallbackCacheOperationSource implements Serializable {
  @Override
	@Nullable
	protected Collection<CacheOperation> findCacheOperations(Method method) {
		return determineCacheOperations(parser -> parser.parseCacheAnnotations(method));
	}
  	@Nullable
	protected Collection<CacheOperation> determineCacheOperations(CacheOperationProvider provider) {
		Collection<CacheOperation> ops = null;
		for (CacheAnnotationParser parser : this.annotationParsers) {
			Collection<CacheOperation> annOps = provider.getCacheOperations(parser);
			if (annOps != null) {
				if (ops == null) {
					ops = annOps;
				}
				else {
					Collection<CacheOperation> combined = new ArrayList<>(ops.size() + annOps.size());
					combined.addAll(ops);
					combined.addAll(annOps);
					ops = combined;
				}
			}
		}
		return ops;
	}
  
}

public class SpringCacheAnnotationParser implements CacheAnnotationParser, Serializable {
  	@Nullable
	private Collection<CacheOperation> parseCacheAnnotations(
			DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {

		Collection<? extends Annotation> annotations = (localOnly ?
				AnnotatedElementUtils.getAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS) :
				AnnotatedElementUtils.findAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS));
		if (annotations.isEmpty()) {
			return null;
		}

		Collection<CacheOperation> ops = new ArrayList<>(1);
		annotations.stream().filter(Cacheable.class::isInstance).map(Cacheable.class::cast).forEach(
				cacheable -> ops.add(parseCacheableAnnotation(ae, cachingConfig, cacheable)));
		annotations.stream().filter(CacheEvict.class::isInstance).map(CacheEvict.class::cast).forEach(
				cacheEvict -> ops.add(parseEvictAnnotation(ae, cachingConfig, cacheEvict)));
		annotations.stream().filter(CachePut.class::isInstance).map(CachePut.class::cast).forEach(
				cachePut -> ops.add(parsePutAnnotation(ae, cachingConfig, cachePut)));
		annotations.stream().filter(Caching.class::isInstance).map(Caching.class::cast).forEach(
				caching -> parseCachingAnnotation(ae, cachingConfig, caching, ops));
		return ops;
	}
}
```

从上面源码可以看到匹配逻辑，会去匹配当前这个 bean 的方法上是否有 `@Cacheable` 等注解。方法没找到，就找当前 bean 所在的类上，在没找到就找接口方法，在没找到，继续找接口类上，还没找到直接返回 null。这个就是他的 MethodMatcher#matches() 匹配过程。

所以 `canApply()` 方法调用的底层就是调用 `MethodMatcher` 去进行匹配。`findAdvisorsThatCanApply()` 方法调用的就是 `canApply()`，就是这样一个匹配过程。如果能匹配成功，表示 Advisor 可以用于加强当前 bean。那么就需要去调用 `createProxy()` 方法创建代理对象。让代理对象去调用 Advisor 里面的增强逻辑，执行完之后再调用目标方法。返回到上层调用 `getAdvicesAndAdvisorsForBean()`，源码如下：

```java
	@Override
	@Nullable
	protected Object[] getAdvicesAndAdvisorsForBean(
			Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

		/** 查找适合这个类的 advisors 切面通知 */
		List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
		if (advisors.isEmpty()) {
			return DO_NOT_PROXY;
		}
		return advisors.toArray();
	}

```

返回到最上层调用 `wrapIfNecessary()` ，源码如下：

```java
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {

		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);

		 /** 合适的通知不为空 */
		if (specificInterceptors != DO_NOT_PROXY) {
		
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			return proxy;
		}
		
		return bean;
	}
```

最后调用 `createProxy()` 创建代理对象。至此在 Spring 启动时创建代理对象环节完成。接下来要分析调用流程。

#### 2、ProxyCachingConfiguration

对于该类就是提供缓存需要的支持类。比如 `Advisor`，通过 `@Bean` 方式提前注册到 Spring 容器中。源码如下：

```java
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

	// cache_tag: 缓存方法增强器
	@Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor(
			CacheOperationSource cacheOperationSource, CacheInterceptor cacheInterceptor) {

		BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
		// 缓存用来解析一些属性封装的对象 CacheOperationSource
		advisor.setCacheOperationSource(cacheOperationSource);
		// 缓存拦截器执行对象
		advisor.setAdvice(cacheInterceptor);
		if (this.enableCaching != null) {
			advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
		}
		return advisor;
	}

	// cache_tag: Cache 注解解析器
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheOperationSource cacheOperationSource() {
		return new AnnotationCacheOperationSource();
	}

	// cache_tag: 缓存拦截器执行器
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheInterceptor cacheInterceptor(CacheOperationSource cacheOperationSource) {
		CacheInterceptor interceptor = new CacheInterceptor();
		interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
		interceptor.setCacheOperationSource(cacheOperationSource);
		return interceptor;
	}

}
```

这里只需要关注 `Advisor`。而 `CacheOperationSource` 对象只是对 `@Cacheable` 等注解解析之后的属性封装而已。

> [!NOTE]
>
> org.springframework.cache.interceptor.CacheAspectSupport.CacheOperationContexts#determineSyncFlag(Method method) 检查sync属性
>
> Advisor = Advice + Pointcut
>
> Advisor = BeanFactoryCacheOperationSourceAdvisor
>
> Advice = CacheInterceptor
>
> Pointcut = CacheOperationSourcePointcut
>
> Pointcut = ClassFilter + MethodMatcher
>
> ClassFilter = CacheOperationSourceClassFilter
>
> MethodMatcher = CacheOperationSourcePointcut
>
> 最终你会发现 @EnableCaching 注解和 @EnableTransactionManagement 基本一模一样，只是换了个注解，把 @Transactional 换成了 @Cacheable 等注解。

那么看看这个类里面到底做了什么事情？进入 CacheInterceptor 类，核心源码如下：

```java
public class CacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable {

	@Override
	@Nullable
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();

		CacheOperationInvoker aopAllianceInvoker = () -> {
			try {
				return invocation.proceed();
			}
			catch (Throwable ex) {
				throw new CacheOperationInvoker.ThrowableWrapper(ex);
			}
		};

		Object target = invocation.getThis();
		Assert.state(target != null, "Target must not be null");
		try {
			// cache_tag: 开始执行真正的缓存拦截逻辑
			return execute(aopAllianceInvoker, target, method, invocation.getArguments());
		}
		catch (CacheOperationInvoker.ThrowableWrapper th) {
			throw th.getOriginal();
		}
	}
}

```

继续追踪 execute() 方法，核心源码如下：

```java
	@Nullable
	protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {

		// CacheMethodInterceptor 的父类 CacheAspectSupport 实现了接口 InitializingBean 接口，就是在这里把 initialized 设置成 true 的
		if (this.initialized) {
			Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
			CacheOperationSource cacheOperationSource = getCacheOperationSource();
			if (cacheOperationSource != null) {
				Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
				if (!CollectionUtils.isEmpty(operations)) {
					return execute(invoker, method,
							new CacheOperationContexts(operations, method, args, target, targetClass));
				}
			}
		}
		return invoker.invoke();
	}
```

ultimateTargetClass() 方法不用看，就是拿到目标类， 接着分析 `getCacheOperationSource()` 方法，核心源码如下：

```java
	@Nullable
	public CacheOperationSource getCacheOperationSource() {
		return this.cacheOperationSource;
	}

```

可以看到是返回一个 CacheOperationSource 类实例，其实这个类实例在 ProxyCachingConfiguration 类中就已经通过 @Bean 注解注入，它就是 `CacheOperationSource`。可以简单理解这个类就是对 @Cacheable 等注解进行解析收集。

然后再看到 getCacheOperations(method,targetClass) 方法逻辑，可以看到把当前正在调用的 method 方法，和目目标类作为参数传入进去然后做处理，核心源码如下：

```java
	@Override
	@Nullable
	public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
		if (method.getDeclaringClass() == Object.class) {
			return null;
		}

		Object cacheKey = getCacheKey(method, targetClass);
		Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);

		if (cached != null) {
			return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
		}
		else {
			// cache_tag: 计算缓存注解上面的配置的值，然后封装成 CacheOperation 缓存属性对象，基本和事物的一样
			// 注意每个缓存注解对应一种不同的解析处理
			Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
			if (cacheOps != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
				}
				this.attributeCache.put(cacheKey, cacheOps);
			}
			else {
				this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
			}
			return cacheOps;
		}
	}


```

继续进入 `computeCacheOperations()` 方法，核心源码如下：

```java
	@Nullable
	private Collection<CacheOperation> computeCacheOperations(Method method, @Nullable Class<?> targetClass) {
		// Don't allow no-public methods as required.
		if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			return null;
		}

		// The method may be on an interface, but we need attributes from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

		// First try is the method in the target class.
		Collection<CacheOperation> opDef = findCacheOperations(specificMethod);
		if (opDef != null) {
			return opDef;
		}

		// cache_tag: 在 method 方法上找到缓存相关的注解封装成 CacheOperation 缓存对象属性，跟事物基本一样
		opDef = findCacheOperations(specificMethod.getDeclaringClass());
		if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
			return opDef;
		}

		if (specificMethod != method) {
			// Fallback is to look at the original method.
			opDef = findCacheOperations(method);
			if (opDef != null) {
				return opDef;
			}
			// Last fallback is the class of the original method.
			opDef = findCacheOperations(method.getDeclaringClass());
			if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
				return opDef;
			}
		}

		return null;
	}

```

从上述源码可以看出，会判断当前被调用的方法上是否有 `@Cacheable` 等注解修饰，若没有，继续找该方法所在类上是否有，若没有，继续找父类接口方法 ，若还没有，继续找父类接口上是否有。最后都没找到就返回 null，表示当前方法不需要被代理。直接调用目标方法走正常执行逻辑。若是找到了那就要走切面逻辑。

看到 `findCacheOperations()` 方法，核心源码如下：

```java
	@Override
	@Nullable
	protected Collection<CacheOperation> findCacheOperations(Class<?> clazz) {
		return determineCacheOperations(parser -> parser.parseCacheAnnotations(clazz));
	}

	@Nullable
	private Collection<CacheOperation> parseCacheAnnotations(
			DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {

		Collection<? extends Annotation> anns = (localOnly ?
				AnnotatedElementUtils.getAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS) :
				AnnotatedElementUtils.findAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS));
		if (anns.isEmpty()) {
			return null;
		}
		// cache_tag: 熟悉不能再熟悉的缓存注解 Cacheable/CacheEvict/CachePut/Caching
		// 注意每一种类型的注解解析是不太一样的哦，具体看 parseCacheableAnnotation() 解析方法
		final Collection<CacheOperation> ops = new ArrayList<>(1);
		anns.stream().filter(ann -> ann instanceof Cacheable).forEach(
				ann -> ops.add(parseCacheableAnnotation(ae, cachingConfig, (Cacheable) ann)));
		anns.stream().filter(ann -> ann instanceof CacheEvict).forEach(
				ann -> ops.add(parseEvictAnnotation(ae, cachingConfig, (CacheEvict) ann)));
		anns.stream().filter(ann -> ann instanceof CachePut).forEach(
				ann -> ops.add(parsePutAnnotation(ae, cachingConfig, (CachePut) ann)));
		anns.stream().filter(ann -> ann instanceof Caching).forEach(
				ann -> parseCachingAnnotation(ae, cachingConfig, (Caching) ann, ops));
		return ops;
	}

```

从这里可以看出，如果从方法上找到有 `@Cacheable` 等注解修饰，那么就会根据不同的注解类型封装成不同的对象，比如 `@Cacheable => CacheableOperation`、`@CacheEvict => CacheEvictOperation` 等。然后将这些对象添加到集合返回出去。

最后返回调用处 `getCacheOperations(method, targetClass)` 源码如下：

```java
	@Nullable
	protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
		// Check whether aspect is enabled (to cope with cases where the AJ is pulled in automatically)
		// CacheMethodInterceptor 的父类 CacheAspectSupport 实现了接口 InitializingBean 接口，就是在这里把 initialized 设置成 true 的
		if (this.initialized) {
			Class<?> targetClass = getTargetClass(target);
			CacheOperationSource cacheOperationSource = getCacheOperationSource();
			if (cacheOperationSource != null) {
				Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
				if (!CollectionUtils.isEmpty(operations)) {
					return execute(invoker, method,
							new CacheOperationContexts(operations, method, args, target, targetClass));
				}
			}
		}

		return invoker.invoke();
	}

```

因为 operations 不为 null、那么就会执行 execute() 方法，这里直接 new 个 CacheOperationContexts 缓存上下文，用来封装一堆的参数。这是 Spring 最喜欢干的事情，将多个参数封成一个参数，方便传递书写阅读，反正好处多多。execute() 核心源码如下：

```java
	@Nullable
	private Object execute(final CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
			// cache_tag: 只有在缓存注解上面标注了 sync=true 才会进入,默认 false
      if (contexts.isSynchronized()) {
        // Special handling of synchronized invocation
        return executeSynchronized(invoker, method, contexts);
      }

      // Process any early evictions
    // cache_tag: 执行 CacheEvict 注解的作用，其实就是去回调 clear() 方法，在目标方法之前调用(一般不会这样干吧)
      processCacheEvicts(contexts.get(CacheEvictOperation.class), true,
          CacheOperationExpressionEvaluator.NO_RESULT);

      // Check if we have a cached value matching the conditions
    // cache_tag: 执行 Cacheable 注解的作用，缓存命中，是否获取到了值，获取到了就叫做命中了
      Object cacheHit = findCachedValue(invoker, method, contexts);
      if (cacheHit == null || cacheHit instanceof Cache.ValueWrapper) {
        return evaluate(cacheHit, invoker, method, contexts);
      }
      return cacheHit;
  }


@Nullable
	private Object evaluate(@Nullable Object cacheHit, CacheOperationInvoker invoker, Method method,
			CacheOperationContexts contexts) {

		// Re-invocation in reactive pipeline after late cache hit determination?
		if (contexts.processed) {
			return cacheHit;
		}

		Object cacheValue;
		Object returnValue;

		if (cacheHit != null && !hasCachePut(contexts)) {
			// If there are no put requests, just use the cache hit
			cacheValue = unwrapCacheValue(cacheHit);
			returnValue = wrapCacheValue(method, cacheValue);
		}
		else {
			// Invoke the method if we don't have a cache hit
      // cache_tag: 如果没有命中缓存，就直接执行目标方法
			returnValue = invokeOperation(invoker);
			cacheValue = unwrapReturnValue(returnValue);
		}

		// Collect puts from any @Cacheable miss, if no cached value is found
		List<CachePutRequest> cachePutRequests = new ArrayList<>(1);
		if (cacheHit == null) {
			collectPutRequests(contexts.get(CacheableOperation.class), cacheValue, cachePutRequests);
		}

		// Collect any explicit @CachePuts
		collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);

		// Process any collected put requests, either from @CachePut or a @Cacheable miss
		for (CachePutRequest cachePutRequest : cachePutRequests) {
			Object returnOverride = cachePutRequest.apply(cacheValue);
			if (returnOverride != null) {
				returnValue = returnOverride;
			}
		}

		// Process any late evictions
    // cache_tag: 执行 CacheEvict 注解的作用，其实就是去回调 clear() 方法
		Object returnOverride = processCacheEvicts(
				contexts.get(CacheEvictOperation.class), false, returnValue);
		if (returnOverride != null) {
			returnValue = returnOverride;
		}

		// Mark as processed for re-invocation after late cache hit determination
		contexts.processed = true;

		return returnValue;
	}
```

可以看到第一行就会去执行 processCacheEvicts() 方法，但是看到该方法的第2个参数，传入的是 true，表示在方法之前调用。但是注意 `@CacheEvict` 注解上的默认值是 false，进入该方法内部逻辑，如下：

```java
	private void processCacheEvicts(
			Collection<CacheOperationContext> contexts, boolean beforeInvocation, @Nullable Object result) {

		if (contexts.isEmpty()) {
			return null;
		}
		List<CacheOperationContext> applicable = contexts.stream()
				.filter(context -> (context.metadata.operation instanceof CacheEvictOperation evict &&
						beforeInvocation == evict.isBeforeInvocation())).toList();
		if (applicable.isEmpty()) {
			return null;
		}

		if (result instanceof CompletableFuture<?> future) {
			return future.whenComplete((value, ex) -> {
				if (ex == null) {
					performCacheEvicts(applicable, result);
				}
			});
		}
		if (this.reactiveCachingHandler != null) {
			Object returnValue = this.reactiveCachingHandler.processCacheEvicts(applicable, result);
			if (returnValue != ReactiveCachingHandler.NOT_HANDLED) {
				return returnValue;
			}
		}
		performCacheEvicts(applicable, result);
		return null;
	}

```

很明显这里的 if 条件满足不了。因为 @CacheEvict 注解默认是 false ，processCacheEvicts() 方法传入的 beforeInvocation = true 明显不相等。除非你在 @CacheEvict(beforeInvocation=true) 才会走这段逻辑。具体这段逻辑是做什么呢？我们不妨先看看 performCacheEvict() 方法，核心源码如下：

```java
private void performCacheEvict(
			CacheOperationContext context, CacheEvictOperation operation, @Nullable Object result) {
  for (CacheOperationContext context : contexts) {
        CacheEvictOperation operation = (CacheEvictOperation) context.metadata.operation;
        if (isConditionPassing(context, result)) {
          Object key = context.getGeneratedKey();
          for (Cache cache : context.getCaches()) {
            if (operation.isCacheWide()) {
              logInvalidating(context, operation, null);
              doClear(cache, operation.isBeforeInvocation());
            }
            else {
              if (key == null) {
                key = generateKey(context, result);
              }
              logInvalidating(context, operation, key);
              doEvict(cache, key, operation.isBeforeInvocation());
            }
          }
        }
      }
	}

```

这里获取到所有的 Cache 缓存，但是为什么这里能够 get 出来呢？实在哪里赋值的呢？这个问题留在后面补齐。先不分析。假设获取到了所有的 Cache，比如 Redis、LocalMap 等等，然后调用 doEvict() 方法，核心源码如下：

```java
	protected void doEvict(Cache cache, Object key, boolean immediate) {
		try {
			if (immediate) {
				cache.evictIfPresent(key);
			}
			else {
				cache.evict(key);
			}
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheEvictError(ex, cache, key);
		}
	}

```

很明显这里留了个钩子方法，也就是模版方法，具体 Cache 怎么保存留个子类去实现，比如 Redis、LocalMap 等等。doEvict() 方法就是去清除缓存，每个缓存的清除方式不一样。清除缓存看对应缓存 API 即可。

看完 processCacheEvicts() 方法，接着看 findInCaches() 方法，很明显是个查询方法，核心源码如下：

```java
@Nullable
	private Object findInCaches(CacheOperationContext context, Object key,
			CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {

		for (Cache cache : context.getCaches()) {
			if (CompletableFuture.class.isAssignableFrom(context.getMethod().getReturnType())) {
				CompletableFuture<?> result = cache.retrieve(key);
				if (result != null) {
					return result.thenCompose(value -> (CompletableFuture<?>) evaluate(
							(value != null ? CompletableFuture.completedFuture(unwrapCacheValue(value)) : null),
							invoker, method, contexts));
				}
			}
			if (this.reactiveCachingHandler != null) {
				Object returnValue = this.reactiveCachingHandler.findInCaches(
						context, cache, key, invoker, method, contexts);
				if (returnValue != ReactiveCachingHandler.NOT_HANDLED) {
					return returnValue;
				}
			}
			Cache.ValueWrapper result = doGet(cache, key);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
```

进入 doGet()，源码如下：

```java
	@Nullable
	protected Cache.ValueWrapper doGet(Cache cache, Object key) {
		try {
			// cache_tag: 调用第三方实现获取对应的值，比如 redis、EhCacheCache 等
			return cache.get(key);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheGetError(ex, cache, key);
			return null;  // If the exception is handled, return a cache miss
		}
	}
```

很明显又是一个钩子方法，留给子类去实现。`get()` 就是去获取缓存，可以从 Redis、LocalMap 等等。然后有值就返回该值，叫做缓存命中，没有返回 null 叫做缓存不命中。

假设缓存没命中，它还会先调用 `collectPutRequests()` 方法，去生成一个 CachePutRequest 请求头（看到 put 肯定是要把从目标方法中获取的数据存到缓存中）然后调用目标方法获取数据。

假设缓存命中，直接返回缓存中的值。

然后接着又会执行 collectPutRequests() 方法，去准备 CachePutRequest 请求头，但是如果上述已经有了 CachePutRequest 请求头的话，这里就会直接获取到不会重新 new 一个新的。

上面的 CachePutRequest 请求头准备好后，就要看是去处理这个请求头，调用 apply() 方法进行处理，源码如下：

```java
	public void apply(@Nullable Object result) {
		if (this.context.canPutToCache(result)) {
			for (Cache cache : this.context.getCaches()) {
				doPut(cache, this.key, result);
			}
		}
	}

	protected void doPut(Cache cache, Object key, @Nullable Object result) {
		try {
			cache.put(key, result);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCachePutError(ex, cache, key, result);
		}
	}
```

可以看到这里又是一个钩子方法，留给具体的子类去实现。put() 就是往缓存中存值。具体怎么存缓存看对应 API。

最后又会看到调用 processCacheEvicts() 方法，但是此时第二个参数为 true，和 `@CacheEvicts` 注解中的默认值刚好相等，所以这里这个 processCacheEvicts() 方法就会被调用，就是去把缓存中的值清除。

回过头仔细细品此段逻辑总结：

`@Cacheable` 注解：先去查对应缓存(Redis、LocalMap 等缓存)，缓存命中直接返回，未命中，先创建 CachePutRequest 请求头，在去调用目标方法获取数据(可能从数据库中查询数据等)，然后将查到的数据保存到对应缓存中，最后返回获取到的数据。

`@CacheEvicts` 注解：如果设置 beforeInvocation = true，表示先删除缓存，然后再调用目标方法，反之先调用目标方法，然后删除缓存。

`@CachePut` 注解：每次都会重新放一份数据到缓存中。

**最后在说一下下面这段代码中的 context.getCaches() 是在哪个地方赋上值的，为什么在这里就直接能够 get 到 caches 值？？？** 源码如下：

```java
	private void performCacheEvicts(List<CacheOperationContext> contexts, @Nullable Object result) {
		for (CacheOperationContext context : contexts) {
			CacheEvictOperation operation = (CacheEvictOperation) context.metadata.operation;
			if (isConditionPassing(context, result)) {
				Object key = context.getGeneratedKey();
				for (Cache cache : context.getCaches()) {
					if (operation.isCacheWide()) {
						logInvalidating(context, operation, null);
						doClear(cache, operation.isBeforeInvocation());
					}
					else {
						if (key == null) {
							key = generateKey(context, result);
						}
						logInvalidating(context, operation, key);
						doEvict(cache, key, operation.isBeforeInvocation());
					}
				}
			}
		}
	}
```

利用 IDEA 引用功能，一步步定位到最上层调用处，然后我们从这段入口代码开始分析，源码如下：

```java
	@Nullable
	protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
		// Check whether aspect is enabled (to cope with cases where the AJ is pulled in automatically)
    // CacheMethodInterceptor 的父类 CacheAspectSupport 实现了接口 InitializingBean 接口，就是在这里把 initialized 设置成 true 的
		if (this.initialized) {
			Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
			CacheOperationSource cacheOperationSource = getCacheOperationSource();
			if (cacheOperationSource != null) {
				Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
				if (!CollectionUtils.isEmpty(operations)) {
					return execute(invoker, method,
							new CacheOperationContexts(operations, method, args, target, targetClass));
				}
			}
		}

		return invokeOperation(invoker);
	}
```

注意这个方法 `getCacheOperationSource()` 会去解析 `@Cacheable` 等注解的配置属性，解析到一个注解就封装名称一个 `CacheOperation` 对象。然后添加到集合中。这个集合中就有每个注解对应的配置信息。最后将这个集合在包装到 `CacheOperationSource` 对象中。反正 Spring 就是喜欢这样层层包装对象。

然后在通过 `CacheOperationSource` 对象获取到集合，这个集合都是解析好的 `@Cacheable` 等注解配置信息。最后 Spring 又把这些解析好的集合 operations 封装到 `CacheOperationContexts` 对象中。

进入 `CacheOperationContexts` 构造中，源码如下：

```java
	public CacheOperationContexts(Collection<? extends CacheOperation> operations, Method method,
			Object[] args, Object target, Class<?> targetClass) {

		this.contexts = new LinkedMultiValueMap<>(operations.size());
		for (CacheOperation op : operations) {
			this.contexts.add(op.getClass(), getOperationContext(op, method, args, target, targetClass));
		}
		this.sync = determineSyncFlag(method);
	}

```

在 `CacheOperationContexts` 类构造器中挨个遍历集合 `operations`，这里面存的都是前面解析好的 `@Cacheable` 等注解的配置信息。

进入 `getOperationContext()` 方法，源码如下：

```java
	protected CacheOperationContext getOperationContext(
			CacheOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {

		CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
		return new CacheOperationContext(metadata, args, target);
	}

```

将 `@Cacheable`、或 `@CacheEvict`、或 `@CachePut`、或 `@Caching` 注解封装成单独的 `CacheOperationContext` 对象，反正 Spring 就喜欢这样干，喜欢把多个参数合并成一个大的上下文参数。好处多多。

然后进入 `CacheOperationContext` 类构造方法中，源码如下：

```java
	public CacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
		this.metadata = metadata;
		this.args = extractArgs(metadata.method, args);
		this.target = target;
		// cache_tag: 保存所有获取到的 Cache
		this.caches = CacheAspectSupport.this.getCaches(this, metadata.cacheResolver);
		this.cacheNames = createCacheNames(this.caches);
	}

```

然后可以看到在这里 caches 缓存被赋值了。那么看下具体是怎么赋值的。进入 getCaches() 方法，源码如下：

```java
	protected Collection<? extends Cache> getCaches(
			CacheOperationInvocationContext<CacheOperation> context, CacheResolver cacheResolver) {
		// cache_tag: 解析有哪些 Cache 比如 redis 等，就跟解析有哪些数据源一样一样的
		Collection<? extends Cache> caches = cacheResolver.resolveCaches(context);
		if (caches.isEmpty()) {
			throw new IllegalStateException("No cache could be resolved for '" +
					context.getOperation() + "' using resolver '" + cacheResolver +
					"'. At least one cache should be provided per cache operation.");
		}
		return caches;
	}

```

继续跟踪，进入 resolveCaches() 方法，源码如下：

```java
	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		/**
		 * 获取到注解上指定的缓存名称，比如 @Cacheable 指定有 "myMapCache","myRedisCache" 两个缓存名称
		 */
		Collection<String> cacheNames = getCacheNames(context);
		if (cacheNames == null) {
			return Collections.emptyList();
		}
		Collection<Cache> result = new ArrayList<>(cacheNames.size());
		for (String cacheName : cacheNames) {
			Cache cache = getCacheManager().getCache(cacheName);
			if (cache == null) {
				throw new IllegalArgumentException("Cannot find cache named '" +
						cacheName + "' for " + context.getOperation());
			}
			result.add(cache);
		}
		return result;
	}

```

上面这段代码有三个地方非常重要。`getCacheNames()` 获取 `@Cacheable` 等注解上配置的 cacheNames 属性值。`getCacheManager()` 方法获取到某个 `CacheManager` 实例。然后通过这个实例拿到对应的 `Cache` 缓存实例。然后将这个缓存实例添加到 result 集合中返回最终赋值给 caches 成员变量。所以最终它在上面那个地方就能够直接 get 到数据。

这里重点研究下这三个方法，进入 getCacheNames() 方法，核心源码如下：

```java
	@Override
	protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
		return context.getOperation().getCacheNames();
	}

```

非常简单直接获取值即可。前面就已经解析好了 @Cacheable 等注解配置信息并封装到 CacheOperation 对象中，所以这里直接通过 context.getOperation() 就能够获取到对应注解的 CacheOperation 封装对象。从而可以从这个封装对象中获取到 cacheNames。就是这么简单。

在看看 getCacheManager() 方法，源码如下：

```java
	public CacheManager getCacheManager() {
		Assert.state(this.cacheManager != null, "No CacheManager set");
		return this.cacheManager;
	}

```

发现又是直接拿来使用，那么肯定又是在某个地方赋值初始化了。其实这个 `CacheManager` 需要自己来定义。因为你配置的 CacheManager 将决定你使用什么样的缓存。比如你要通过 @Bean 注解配置 `RedisCacheManager` 实例，那么必然 `RedisCacheManager` 引入的肯定是 Redis 缓存。EhCacheCacheManager 引入的必然是 EhCacheCache 缓存。这个 CacheManager 就是用户自定义配置的缓存管理类。当然也可以自定义。

然后再看看 CacheManager 中，提供了一个 getCache() 方法，可以用来获取一个缓存实例。

```java
public interface CacheManager {

	// 根据名字获取某个缓存
	@Nullable
	Cache getCache(String name);

	// 获取到所有缓存的名字
	Collection<String> getCacheNames();
}

```

这里面的缓存实例你完全可以自定义，只需要实现 Cache 接口即可。例子如下：

```java
public class MyMapCache implements Cache {

	public static final Map<Object, Object> map = new ConcurrentHashMap<>();

	private String cacheName;

	public MyMapCache(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public String getName() {
		return cacheName;
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(Object key) {
		System.out.println(">>>>>>我是 MyMapCache 缓存中的 get() 方法");
		Object o = map.get(key);
		if (Objects.nonNull(o)) {
			return new SimpleValueWrapper(o);
		}
		return null;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {

		return (T)map.get(key);
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return (T)map.get(key);
	}

	@Override
	public void put(Object key, Object value) {
		System.out.println(">>>>>>我是 MyMapCache 缓存中的 put() 方法");
		map.put(key, value);
	}

	@Override
	public void evict(Object key) {
		map.remove(key);
	}

	@Override
	public void clear() {
		map.clear();
	}
}

```

最终 CacheManager 返回的就是我们自定义的缓存实例 MyMapCache。最后将这个 MyMapCache 实例添加到 result 集合最后赋值给 this.caches 成员变量。

不过实现 CacheManager 接口有个缺点，每次只能返回一个 Cache 实例，如果想要返回多个呢？怎么办，所以这里 Spring 早就想到，提前给你准备好了 AbstractCacheManager 抽象类。它有个 loadCaches() 方法，源码如下：

```java
public abstract class AbstractCacheManager implements CacheManager, InitializingBean {

	// cache_tag: 封装成 Map，方便 getCache(cacheName) 操作
	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

	// cache_tag: 用来保存外界传进来的缓存管理名称，一般的话就只有一个，最多两个(本地缓存+redis缓存)
	private volatile Set<String> cacheNames = Collections.emptySet();
	@Override
	public void afterPropertiesSet() {
		initializeCaches();
	}

	public void initializeCaches() {
		// cache_tag: 预留给 CacheManager 管理类的方法，可以查看 SimpleCacheManager 子类
		// 这里我们就是配置的 SimpleCacheManager 缓存管理类，Cache 使用的 ConcurrentMapCache
		// 每个子类 Cache 都实现了 Cache 接口定义的 CRUD 方法
		Collection<? extends Cache> caches = loadCaches();

		// 下面就是一堆的赋值，如果 caches 是空的话，就是代码没哟配置缓存相关的东西，自然而然下面所有逻辑都不走了
		synchronized (this.cacheMap) {
			this.cacheNames = Collections.emptySet();
			this.cacheMap.clear();
			Set<String> cacheNames = new LinkedHashSet<>(caches.size());
			for (Cache cache : caches) {
				String name = cache.getName();
				this.cacheMap.put(name, decorateCache(cache));
				cacheNames.add(name);
			}
			this.cacheNames = Collections.unmodifiableSet(cacheNames);
		}
	}

	protected abstract Collection<? extends Cache> loadCaches();
}

```

在 `loadCaches()` 方法中可以返回非常多 Cache 实例，那么这么多实例要怎么存呢，肯定需要有映射关系，那么必然采用 Map，那么 key 就是对应的 cacheName，value 就是对应的 Cache，Spring 就是这样设计的。这对于需要做双缓存、三缓存设计就非常有帮助。具体源码可以看到 AbstractCacheManager 是 CacheManager 的扩展类，并且实现 InitializingBean 接口，那么就需要关注这个类的 afterPropertiesSet() 方法，源码如下：

```java
	@Override
	public void afterPropertiesSet() {
		initializeCaches();
	}

	public void initializeCaches() {
		// cache_tag: 预留给 CacheManager 管理类的方法，可以查看 SimpleCacheManager 子类
		// 这里我们就是配置的 SimpleCacheManager 缓存管理类，Cache 使用的 ConcurrentMapCache
		// 每个子类 Cache 都实现了 Cache 接口定义的 CRUD 方法
		Collection<? extends Cache> caches = loadCaches();

		// 下面就是一堆的赋值，如果 caches 是空的话，就是代码没哟配置缓存相关的东西，自然而然下面所有逻辑都不走了
		synchronized (this.cacheMap) {
			this.cacheNames = Collections.emptySet();
			this.cacheMap.clear();
			Set<String> cacheNames = new LinkedHashSet<>(caches.size());
			for (Cache cache : caches) {
				String name = cache.getName();
				this.cacheMap.put(name, decorateCache(cache));
				cacheNames.add(name);
			}
			this.cacheNames = Collections.unmodifiableSet(cacheNames);
		}
	}

```

从上述代码中发现通过 `loadCaches()` 方法加载进来的 Cache 实例，都被一个个的存放到了 cacheMap 容器中。因为 Cache 类实例时多个，必然需要建立映射关系，所以存 Map 再好不过。

然后再看看在调用的过程中是怎么获取到对应的 Cache 缓存实例，源码如下：

```java

	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		/**
		 * 获取到注解上指定的缓存名称，比如 @Cacheable 指定有 "myMapCache","myRedisCache" 两个缓存名称
		 */
		Collection<String> cacheNames = getCacheNames(context);
		if (cacheNames == null) {
			return Collections.emptyList();
		}
		Collection<Cache> result = new ArrayList<>(cacheNames.size());
		for (String cacheName : cacheNames) {
			Cache cache = getCacheManager().getCache(cacheName);
			if (cache == null) {
				throw new IllegalArgumentException("Cannot find cache named '" +
						cacheName + "' for " + context.getOperation());
			}
			result.add(cache);
		}
		return result;
	}

```

getCacheManager() 获取的是实现了 AbstractCacheManager 的类，目前发现 Spring 有个 SimpleCacheManager 类已经实现，那么获取到的假设就是 SimpleCacheManager 管理类，然后进入 getCache() 方法，直接使用的就是父类 AbstractCacheManager 的模版方法，源码如下：

```java
	@Override
	@Nullable
	public Cache getCache(String name) {
		// Quick check for existing cache...
		Cache cache = this.cacheMap.get(name);
		if (cache != null) {
			return cache;
		}

		// The provider may support on-demand cache creation...
		Cache missingCache = getMissingCache(name);
		if (missingCache != null) {
			// Fully synchronize now for missing cache registration
			synchronized (this.cacheMap) {
				cache = this.cacheMap.get(name);
				if (cache == null) {
					cache = decorateCache(missingCache);
					this.cacheMap.put(name, cache);
					updateCacheNames(name);
				}
			}
		}
		return cache;
	}

```

直接从 cacheMap 中获取对应 Cache 实例即可，因为上述已经创建好 cacheMap 映射。这就是 Spring 对多级缓存设计的支持方案。





# 探索spring boot micrometer

1. org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration

   ```java
   @Import({ BravePropagationConfigurations.PropagationWithoutBaggage.class,
   		BravePropagationConfigurations.PropagationWithBaggage.class, //此处会引入MDCScopeDecorator
   		BravePropagationConfigurations.NoPropagation.class })
   public class BraveAutoConfiguration {
     	@Bean
   		@ConditionalOnMissingBean(CorrelationScopeDecorator.class)
   		ScopeDecorator correlationScopeDecorator(CorrelationScopeDecorator.Builder builder) {
   			return builder.build(); //此处会注入traceId spanId
   		}
   }
   ```

   启动的时候自动装配Brave Trace Id. 默认使用 B3Propagation

   ```java
       Propagation.Factory propagationFactory = B3Propagation.FACTORY;
   
   public static final class FactoryBuilder {
       InjectorFactory.Builder injectorFactoryBuilder = InjectorFactory.newBuilder(Format.MULTI)
           .clientInjectorFunctions(Format.MULTI) //此处会注入B3Propagation X-B3-TraceId X-B3-SpanId
           .producerInjectorFunctions(Format.SINGLE_NO_PARENT)
           .consumerInjectorFunctions(Format.SINGLE_NO_PARENT);
   }
   ```

2. org.springframework.web.filter.ServerHttpObservationFilter#doFilterInternal

   ```java
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
   			throws ServletException, IOException {
   
   		Observation observation = createOrFetchObservation(request, response);
   		try (Observation.Scope scope = observation.openScope()) { //每个请求过来的时候，都会开启scope
   			filterChain.doFilter(request, response);
   		}
   		catch (Exception ex) {
   			observation.error(unwrapServletException(ex));
   			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
   			throw ex;
   		}
   		finally {
   			// Only stop Observation if async processing is done or has never been started.
   			if (!request.isAsyncStarted()) {
   				Throwable error = fetchException(request);
   				if (error != null) {
   					observation.error(error);
   				}
   				observation.stop();
   			}
   		}
   	}
   
   ```

3. dsa

4. io.micrometer.tracing.brave.bridge.BravePropagator#extract 创建 SpanBuilder，并在io.micrometer.tracing.brave.bridge.BraveSpanBuilder#span 开启Span

1. io.micrometer.tracing.brave.bridge.W3CPropagation#injector 和 #extractor 

2. brave.context.slf4j.MDCScopeDecorator

   brave.baggage.CorrelationScopeDecorator初始化创建 TRACE_ID, SPAN_ID

3. brave.propagation.B3Propagation#extract 解析B3 traceId

4. org.springframework.web.filter.ServerHttpObservationFilter spring在每个请求到来之时会自动开启scope

5. **brave.baggage.CorrelationScopeDecorator#decorateScope 用于获取并更新traceId** 【！！这里很重要】