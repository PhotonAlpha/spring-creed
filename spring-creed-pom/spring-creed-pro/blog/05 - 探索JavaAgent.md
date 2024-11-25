1. [JavaAgent从入门到内存马](https://juejin.cn/post/6991844645147770917#heading-7)
2. [Java—JavaAgent探针](http://www.enmalvi.com/2022/05/16/java-javaagent/#Java_Agent_shi_xian_yuan_li)
3. [开源项目 proxy-agent](https://github.com/YingXinGuo95/proxy-agent) 以及使用[例子](https://blog.csdn.net/cs4290790/article/details/143230992)







### javaagent 动态注册Spring  boot  bean

涉及到Spring相关的，都用Bean解决，特别是利用Spring的AOP能力解决这类监控，或者利用中间件的扩展机制实现。剩下的搞不定的，再用字节码增强实现。好处举几个例子如下:

1、动态添加 Mybatis的Plugin，实现特定的SQL逻辑统计、拦截、监控。
2、动态添加Dubbo的Filter的逻辑，实现特定的RPC的统计、拦截、监控。
3、对OpenFeign，或者其他的Bean，直接声明AOP，进行调用拦截。

目前Spring的应用主要有两大类：

1.Spring MVC的，跑在Tomcat上。
2.SpringBoot的，可能是Fatjar内置了Tomcat容器；也可能是ThinJar，使用外置Tomcat。



**如果我想动态的添加一些Bean，让Spring容器能感知到这些额外的Bean;然后再让这些Bean通过AOP、BeanFactory或者Aware接口来实现我们特定的监控逻辑，那么监控的逻辑开发就比字节码增强简单很多。**

想让Spring能动态感知到额外的Bean,我目前总结的有如下方式：


1、通过SpringBoot的 META-INF/spring.factories的机制，动态感知到加入的Bean。前提是：SpringBoot要能扫描到包，但是在FatJar模式下，依赖的所有jar都已经在压缩包内了，势必需要修改这个发布包，这就违背了初衷，不需要动程序包**。而且不通用，SpringMVC不识别。**
2、如果是SpringBoot的Thinjar模式或者Spring MVC，可以把动态Bean的代码添加到Tomcat的webapps的lib里，让Spring的component scan去发现。但是又不能动态的修改程序包的componet scan配置。

 

针对上面的问题，我想到了一个办法，并测试成功，就是针对Spring的bean加载流程进行字节码增强。先让Spring的Classloader能加载外部的动态jar文件，再把外部Bean注册到Spring里。具体实现使用ByteBuddy进行操作


如果是FatJar的SpringBoot，增强 org.springframework.boot.loader.Launcher.createClassLoader,因为这里是从Fatjar加载所有依赖包的逻辑，可以增强它，让他发现额外的外部依赖包。

```java
# 其中的AppendSpringBootJarLoaderAdvisor 就是追加 addUrlList 到Fatjar中已有的jar包列表中
# 因为SpringBoot使用了自定义的ClassLoader，这种增强可以绕过自定义ClassLoader的限制，加载到外部文件
private static AgentBuilder appendSpringBootJarList(AgentBuilder agentBuilder, List<URL> addUrlList){
    AppendSpringBootJarLoaderAdvisor.addUrlList = addUrlList;
    return agentBuilder.type(named("org.springframework.boot.loader.Launcher"))
            .transform((builder, typeDescription, classLoader, module) ->
                    builder.method(named("createClassLoader").and(takesArgument(0, URL[].class))).intercept(
                    MethodDelegation.withDefaultConfiguration().withBinders(
                            Morph.Binder.install(DelegateCall.class)
                    ).to(AppendSpringBootJarLoaderAdvisor.class)
            ));
}
```

如果是ThinJar或者SpringMVC的，可以通过Agent的 instrumentation.appendToSystemClassLoaderSearch ，直接添加。

```java
for(PluginConfig pluginConfig : pluginConfigList){
    try{
        File f = new File(JarUtils.findJarUrl(pluginConfig.getConfigUrl()).getFile());
        instrumentation.appendToSystemClassLoaderSearch(new JarFile(f));
    }catch (Exception e){
        logger.error("append to system classlaoder error.", e);
    }
}
```

前面的两种方案，都是让Spring的ClassLoader能加载到Jar文件，接下来就是让Spring发现这些jar里的Bean。

**我们增强 org.springframework.context.support.AbstractApplicationContext.getBeanFactoryPostProcessors，这样SpringMVC和SpringBoot通用。**

```java
# 要求我们外部的Bean都必须实现BeanFactoryPostProcessor, 在IOC容器启动的第一轮，就被Sping所识别
private static AgentBuilder appendBeanPostProcessor(AgentBuilder agentBuilder){
        List<BeanFactoryPostProcessor> appendList = new ArrayList<>();
        appendList.add(new PrefAgentBeanPostProcessor(PluginRegistry.listSpringConfigurationList()));
        return agentBuilder.type(named("org.springframework.context.support.AbstractApplicationContext"))
                .transform((builder, typeDescription, classLoader, module) ->
                        builder.method(named("getBeanFactoryPostProcessors").and(isPublic())).intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .to(new AppendSpringBeanFactory(appendList))
                ));
}
```



[参考](https://www.cnblogs.com/learncat/p/16138124.html)







[springboot启动时是如何加载配置文件application.yml文件](https://cloud.tencent.com/developer/article/2144353)

- EnvironmentPostProcessorApplicationListener#onApplicationEnvironmentPreparedEvent
- SystemEnvironmentPropertySourceEnvironmentPostProcessor
- **ConfigDataEnvironmentPostProcessor**
- ConfigDataEnvironment#createContributors
- ConfigDataImporter#resolveAndLoad
- YamlPropertySourceLoader#load