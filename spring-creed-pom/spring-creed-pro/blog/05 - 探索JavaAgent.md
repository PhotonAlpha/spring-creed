1. [JavaAgent从入门到内存马](https://juejin.cn/post/6991844645147770917#heading-7)
2. [Java—JavaAgent探针](http://www.enmalvi.com/2022/05/16/java-javaagent/#Java_Agent_shi_xian_yuan_li)
3. [开源项目 proxy-agent](https://github.com/YingXinGuo95/proxy-agent) 以及使用[例子](https://blog.csdn.net/cs4290790/article/details/143230992)



## Java Agent 实现原理

在了解Java Agent的实现原理之前，需要对Java类加载机制有一个较为清晰的认知。一种是在main方法执行之前，通过`premain`来执行，另一种是程序运行中修改，需通过JVM中的Attach实现，`Attach`的实现原理是基于`JVMTI`。

主要是在类加载之前，进行拦截，对字节码修改

下面我们分别介绍一下这些关键术语：

**`JVMTI`** 就是`JVM Tool Interface`，是 JVM 暴露出来给用户扩展使用的接口集合，JVMTI 是基于事件驱动的，JVM每执行一定的逻辑就会触发一些事件的回调接口，通过这些回调接口，用户可以自行扩展

JVMTI是实现 Debugger、Profiler、Monitor、Thread Analyser 等工具的统一基础，在主流 Java 虚拟机中都有实现

**`JVMTIAgent`**是一个动态库，利用JVMTI暴露出来的一些接口来干一些我们想做、但是正常情况下又做不到的事情，不过为了和普通的动态库进行区分，它一般会实现如下的一个或者多个函数：

- `Agent_OnLoad`函数，如果agent是在启动时加载的，通过JVM参数设置
- `Agent_OnAttach`函数，如果agent不是在启动时加载的，而是我们先attach到目标进程上，然后给对应的目标进程发送load命令来加载，则在加载过程中会调用Agent_OnAttach函数
- `Agent_OnUnload`函数，在agent卸载时调用

**`javaagent`** 依赖于instrument的JVMTIAgent（Linux下对应的动态库是libinstrument.so），还有个别名叫JPLISAgent(Java Programming Language Instrumentation Services Agent)，专门为Java语言编写的插桩服务提供支持的

**`instrument`** 实现了`Agent_OnLoad`和`Agent_OnAttach`两方法，也就是说在使用时，`agent`既可以在启动时加载，也可以在运行时动态加载。其中启动时加载还可以通过类似-javaagent:jar包路径的方式来间接加载instrument agent，运行时动态加载依赖的是JVM的attach机制，通过发送load命令来加载agent

**`JVM Attach`** 是指 JVM 提供的一种进程间通信的功能，能让一个进程传命令给另一个进程，并进行一些内部的操作，比如进行线程 dump，那么就需要执行 jstack 进行，然后把 pid 等参数传递给需要 dump 的线程来执行



### Instrumentation

`java.lang.instrument`包下关键的类为：`java.lang.instrument.Instrumentation`。该接口提供一系列替换转化class定义的方法。接下来看一下该接口的主要方法进行以下说明：

### addTransformer

用于注册`transformer`。除了任何已注册的转换器所依赖的类的定义外，所有未来的类定义都将可以被`transformer`看到。当类被加载或被重新定义（`redefine`，可以是下方的`redefineClasses`触发）时，`transformer`将被调用。如果`canRetransform`为`true`，则表示当它们被`retransform`时（通过下方的`retransformClasses`），该`transformer`也会被调用。`addTransformer`共有如下两种重载方法：

```java
void addTransformer(ClassFileTransformer transformer,boolean canRetransform)
void addTransformer(ClassFileTransformer transformer)
```

### redefineClasses

```java
void redefineClasses(ClassDefinition... definitions)
                            throws ClassNotFoundException, UnmodifiableClassException
```

此方法用于替换**不引用现有**类文件字节的类定义，就像从源代码重新编译以进行修复并继续调试时所做的那样。该方法对一系列`ClassDefinition`进行操作，以便允许同时对多个类进行相互依赖的更改(类a的重新定义可能需要类B的重新定义)。**假如在`redifine`时，目标类正在执行中，那么执行中的行为还是按照原来字节码的定义执行，当对该类行为发起新的调用时，将会使用`redefine`之后的新行为。**

**注意：此`redefine`不会触发类的初始化行为**

当然`redefine`时，并不是随心所欲，我们可以重新定义方法体、常量池、属性、**但是不可以添加、移除、重命名方法和方法和入参，不能更改方法签名或更改继承**。当然，在未来的版本中，这些限制可能不复存在。

在转换之前，不会检查、验证和安装类文件字节，如果结果字节出现错误，此方法将抛出异常。而抛出异常将不会有类被重新定义

### retransformClasses

针对JVM**已经加载的类**进行转换，当类初始加载或重新定义类（`redefineClass`）时，可以被注册的`ClassFileTransformer`进行转化；但是针对那些已经加载完毕之后的类不会触发这个`transform`行为进而导致这些类无法被我们agent进行监听，所以可以通过`retransformClasses`触发一个事件，而这个事件可以被`ClassFileTransformer`捕获进而对这些类进行`transform`。

此方法将针对每一个通过`addTransformer`注册的且`canRetransform`是`true`的，进行调用其`transform`方法，转换后的类文件字节被安装成为类的新定义，从而拥有新的行为。

`redefineClasses`是自己提供字节码文件替换掉已存在的class文件，`retransformClasses`是在已存在的字节码文件上修改后再替换之。

### ClassFileTransformer

在我们的agent中，需要提供该接口的实现，以便在JVM定义类之前转换class字节码文件，该接口中就提供了一个方法,此方法的实现可以转换提供的类文件并返回一个新的替换类文件：

```java
byte[] transform(ClassLoader loader,
                 String className,
                 Class<?> classBeingRedefined,
                 ProtectionDomain protectionDomain,
                 byte[] classfileBuffer)
          throws IllegalClassFormatException
```

### Instrumentation接口源码

```java
public interface Instrumentation
{
    //添加ClassFileTransformer
    void addTransformer(ClassFileTransformer transformer, boolean canRetransform);

    //添加ClassFileTransformer
    void addTransformer(ClassFileTransformer transformer);

    //移除ClassFileTransformer
    boolean removeTransformer(ClassFileTransformer transformer);

    //是否可以被重新定义
    boolean isRetransformClassesSupported();

    // 重新转换提供的类集。此功能有助于检测已加载的类。
    // 最初加载类或重新定义类时，可以使用 ClassFileTransformer 转换初始类文件字节。
    // 此函数重新运行转换过程（无论之前是否发生过转换）。
    retransformClasses(Class<?>... classes) throws UnmodifiableClassException;

    // 返回当前 JVM 配置是否支持重新定义类。
    // 重新定义已加载的类的能力是 JVM 的可选能力。
    // 仅当代理 JAR 文件中的 Can-Redefine-Classes 清单属性设置为 true（如包规范中所述）并且 JVM 支持此功能时，才支持重新定义。
    boolean isRedefineClassesSupported();

    //重新定义Class文件
    void redefineClasses(ClassDefinition... definitions)
        throws ClassNotFoundException, UnmodifiableClassException;

    //是否可以修改Class文件
    boolean isModifiableClass(Class<?> theClass);

    //获取所有加载的Class
    @SuppressWarnings("rawtypes")
    Class[] getAllLoadedClasses();

    //获取指定类加载器已经初始化的类
    @SuppressWarnings("rawtypes")
    Class[] getInitiatedClasses(ClassLoader loader);

    //获取某个对象的大小
    long getObjectSize(Object objectToSize);

    //添加指定jar包到启动类加载器检索路径
    void appendToBootstrapClassLoaderSearch(JarFile jarfile);

    //添加指定jar包到系统类加载检索路径
    void appendToSystemClassLoaderSearch(JarFile jarfile);

    //本地方法是否支持前缀
    boolean isNativeMethodPrefixSupported();

    //设置本地方法前缀，一般用于按前缀做匹配操作
    void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix);
}
```

## `premain` JVM启动前的agent实现

Instrument是JDK5开始引入，在JDK5中Instrument要求在目标JVM程序运行之前通过命令行参数**javaagent**来设置代理类，在JVM初始化之前，Instrument启动在JVM中设置回调函数，检测特点类加载情况完成实际增强工作。

```shell
-javaagent: jarpath[ =options]
```

这里jarpath就是我们的agent jar的路径，agent jar必须符合jar文件规范。代理JAR文件的`manifest（META-INF/MANIFEST.MF）`必须包含属性`Premain-Class`。此属性的值是代理类的类名。代理类必须实现一个公共静态`premain`方法，该方法原则上与主应用程序入口点类似。在JVM初始化之后，将按照指定代理的顺序调用每个主方法（premain），然后将调用实际应用程序的主方法(main)。每个premain方法必须按照启动顺序返回。

premain方法可以有如下两种重载方法，**如果两者同时存在，则优先调用多参数的方法**：

```java
public static void premain(String agentArgs, Instrumentation inst);

public static void premain(String agentArgs);
```



## `agentmain ` JVM启动后的agent实现

DK6开始为`Instrument`增加很多强大的功能，其中要指出的就是在JDK5中如果想要完成增强处理，必须是在目标JVM程序启动前通过命令行指定`Instrument`,然后在实际应用中，目标程序可能是已经运行中，针对这种场景下如果要保证 JVM不重启得以完成我们工作，这不是我们想要的，于是JDK6中`Instrument`提供了在JVM启动之后指定设置java agent达到`Instrument`的目的。

该实现需要确保以下3点：

1. agent jar中manifest必须包含属性Agent-Class，其值为agent类名。
2. agent类中必须包含公有静态方法**agentmain**
3. system classload必须支持可以将agent jar添加到system class path。

`agent jar`将被添加到`system class path`，这个路径就是`SystemClassLoader`加载主应用程序的地方，`agent class`被加载后，JVM将会尝试执行它的`agentmain`方法，同样的，如果以下两个方法都存在，则优先执行多参数方法：

```java
public static void agentmain(String agentArgs, Instrumentation inst);

public static void agentmain(String agentArgs);
```

看到这里，结合JVM前启动前agent的实现和JVM启动后agent的实现，可能想问是否可以在一个`agent class`中同时包含`premain、agentmain`呢，答案是可以的，只不过在JVM启动前不会执行`agentmain`,同样的，JVM启动后不会执行`premain`。

**如果我们的agent无法启动（agent class无法被加载、agentmain出异常、agent class没有合法的agentmain方法等），JVM将不会终止！**

## 入门案例

### JVM启动前替换实现

我们已经知道通过配置-javaagent:文件.jar后，在java程序启动时候会执行premain方法。接下来我们使用javassist字节码增强的方式，来监控方法程序的执行耗时。

Javassist是一个开源的分析、编辑和创建Java字节码的类库。是由东京工业大学的数学和计算机科学系的 Shigeru Chiba （千叶 滋）所创建的。它已加入了开放源代码JBoss应用服务器项目，通过使用Javassist对字节码操作为JBoss实现动态"AOP"框架。



加入依赖

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.30.2-GA</version>
</dependency>
```

#### ASM、Javassist 系列

```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm-commons</artifactId>
    <version>9.7.1</version>
</dependency>
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>9.7.1</version>
</dependency>
```

#### Byte Buddy

```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.15.5</version>
</dependency>

<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-agent</artifactId>
    <version>1.15.5</version>
</dependency>
```















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

 

## 

[参考](https://www.cnblogs.com/learncat/p/16138124.html)







[springboot启动时是如何加载配置文件application.yml文件](https://cloud.tencent.com/developer/article/2144353)

- EnvironmentPostProcessorApplicationListener#onApplicationEnvironmentPreparedEvent
- SystemEnvironmentPropertySourceEnvironmentPostProcessor
- **ConfigDataEnvironmentPostProcessor**
- ConfigDataEnvironment#createContributors
- ConfigDataImporter#resolveAndLoad
- YamlPropertySourceLoader#load



1. 代理对象私有构造方法，创建自有构造方法，创建实例对象
2. 使用ASM拦截 字段 注解 类
3. 使用ByteBuddy拦截对象



Intellij Plugin
http://www.ideaplugin.com/idea-docs/Part%20I%20%E6%8F%92%E4%BB%B6/%E7%AC%AC%E4%B8%80%E4%B8%AA%E6%8F%92%E4%BB%B6/Using%20GitHub%20Template.html
https://github.com/JetBrains/intellij-platform-plugin-template

https://juejin.cn/post/6844904127990857742