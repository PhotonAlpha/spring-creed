# 探索java agent

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

`java.lang.instrument`包下关键的类为：`java.lang.instrument.Instrumentation`。这是Java Agent技术的核心API，用于在运行时修改类的字节码。使用这个API，你可以实现自己的类加载器，并在类被加载到JVM时修改其字节码。该接口提供一系列替换转化class定义的方法。接下来看一下该接口的主要方法进行以下说明：

#### addTransformer

用于注册`transformer`。除了任何已注册的转换器所依赖的类的定义外，所有未来的类定义都将可以被`transformer`看到。当类被加载或被重新定义（`redefine`，可以是下方的`redefineClasses`触发）时，`transformer`将被调用。如果`canRetransform`为`true`，则表示当它们被`retransform`时（通过下方的`retransformClasses`），该`transformer`也会被调用。`addTransformer`共有如下两种重载方法：

```java
void addTransformer(ClassFileTransformer transformer,boolean canRetransform)
void addTransformer(ClassFileTransformer transformer)
```

#### redefineClasses

```java
void redefineClasses(ClassDefinition... definitions)
                            throws ClassNotFoundException, UnmodifiableClassException
```

此方法用于替换**不引用现有**类文件字节的类定义，就像从源代码重新编译以进行修复并继续调试时所做的那样。该方法对一系列`ClassDefinition`进行操作，以便允许同时对多个类进行相互依赖的更改(类a的重新定义可能需要类B的重新定义)。**假如在`redifine`时，目标类正在执行中，那么执行中的行为还是按照原来字节码的定义执行，当对该类行为发起新的调用时，将会使用`redefine`之后的新行为。**

**注意：此`redefine`不会触发类的初始化行为**

当然`redefine`时，并不是随心所欲，我们可以重新定义方法体、常量池、属性、**但是不可以添加、移除、重命名方法和方法和入参，不能更改方法签名或更改继承**。当然，在未来的版本中，这些限制可能不复存在。

在转换之前，不会检查、验证和安装类文件字节，如果结果字节出现错误，此方法将抛出异常。而抛出异常将不会有类被重新定义

#### retransformClasses

针对JVM**已经加载的类**进行转换，当类初始加载或重新定义类（`redefineClass`）时，可以被注册的`ClassFileTransformer`进行转化；但是针对那些已经加载完毕之后的类不会触发这个`transform`行为进而导致这些类无法被我们agent进行监听，所以可以通过`retransformClasses`触发一个事件，而这个事件可以被`ClassFileTransformer`捕获进而对这些类进行`transform`。

此方法将针对每一个通过`addTransformer`注册的且`canRetransform`是`true`的，进行调用其`transform`方法，转换后的类文件字节被安装成为类的新定义，从而拥有新的行为。

`redefineClasses`是自己提供字节码文件替换掉已存在的class文件，`retransformClasses`是在已存在的字节码文件上修改后再替换之。

#### ClassFileTransformer

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

### ❶JVM启动前替换实现 - premain

我们已经知道通过配置-javaagent:文件.jar后，在java程序启动时候会执行premain方法。接下来我们使用javassist字节码增强的方式，来监控方法程序的执行耗时。

#### Javassist

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.30.2-GA</version>
</dependency>
```

Javassist是一个开源的分析、编辑和创建Java字节码的库。它已经被许多其他的Java类库和工具使用，包括Hibernate和Spring。Javassist是分析字节码的工具，并且提供了一个简单的API来操作和生成字节码。

#### ASM

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

ASM是一种Java字节码操控框架，能够以二进制形式修改已有的类或是生成类，ASM可以直接生成二进制class文件也可以在类被加载入JVM之前动态改变类，只不过ASM在创建class字节码时说底层JVM的汇编指令，需要使用者对class组织结构和JVM汇编指令有一定的了解。由于Java 类存储在.class文件中，这些类文件中包含有：类名称、方法、属性及字节码，ASM从类文件中读入信息后改变类行为、分析类信息或者直接创建新的类。

著名的使用到ASM的案例便是lambda表达式、CGLIB动态代理类。

ASM框架核心类包含

- **ClassReader**：该类用来解析编译过的class字节码文件

- **ClassWriter**：该类用来重新构建编译后的类，比如修改类名、属性、方法或者根据要求创建新的字节码文件

- **ClassAdapter**：实现了ClassVisitor接口，将对它的方法调用委托给另一个ClassVisitor对象

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

Byte Buddy是一个更高级别的字节码生成库，它提供了更简洁的API来创建和修改**Java类**，而不需要直接与ASM或Java字节码指令打交道。Byte Buddy的目标是简化字节码操作，使得即使是没有深入了解字节码的开发者也能轻松使用。

#### ➀**基于java agent和byte-buddy组合简单使用**

##### **1)首先准备好java agent的premain**

```java
public class PreMainAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        //创建一个转换器，转换器可以修改类的实现
        //ByteBuddy对java agent提供了转换器的实现，直接使用即可
        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
                return builder
                        // 拦截任意方法
                        .method(ElementMatchers.<MethodDescription>any())
                        // 拦截到的方法委托给TimeInterceptor
                        .intercept(MethodDelegation.to(MyInterceptor.class));
            }
        };
         new AgentBuilder 
                .Default()
                // 根据包名前缀拦截类
                .type(ElementMatchers.nameStartsWith("com.agent"))
                // 拦截到的类由transformer处理
                .transform(transformer)
                .installOn(inst);
    }
}
```

##### **2)在pom.xml中新增premain的信息**

```xml
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
        <appendAssemblyId>false</appendAssemblyId>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <archive> <!--自动添加META-INF/MANIFEST.MF -->
            <manifest>
                <!-- 添加 mplementation-*和Specification-*配置项-->
                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
            </manifest>
            <manifestEntries>
                <!--指定premain方法所在的类-->
                <Premain-Class>com.example.demo.bytecode.PreMainAgent</Premain-Class>
                <!--添加这个即可-->
                <Agent-Class>com.example.demo.bytecode.PreMainAgent</Agent-Class>
                <Can-Redefine-Classes>true</Can-Redefine-Classes>
                <Can-Retransform-Classes>true</Can-Retransform-Classes>
            </manifestEntries>
        </archive>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```



##### **3）准备好拦截器**

```java
public class MyInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
            //执行原方法
            return callable.call();
        } finally {
            //打印调用时长
            System.out.println(method.getName() + ":" + (System.currentTimeMillis() - start)  + "ms");
        }
    }
}
```

#### ➁javaagent 动态注册Spring  boot  bean

涉及到Spring相关的，都用Bean解决，特别是利用Spring的AOP能力解决这类监控，或者利用中间件的扩展机制实现。剩下的搞不定的，再用字节码增强实现。好处举几个例子如下:

1、动态添加 Mybatis的Plugin，实现特定的SQL逻辑统计、拦截、监控。
2、动态添加Dubbo的Filter的逻辑，实现特定的RPC的统计、拦截、监控。
3、对OpenFeign，或者其他的Bean，直接声明AOP，进行调用拦截。

**如果我想动态的添加一些Bean，让Spring容器能感知到这些额外的Bean;然后再让这些Bean通过AOP、BeanFactory或者Aware接口来实现我们特定的监控逻辑，那么监控的逻辑开发就比字节码增强简单很多。**

因此我们可以在org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory阶段注入自定义的`BeanPostProcessor` `BeanFactoryPostProcessor`

 ```java
 public class ApplicationContextAdvice {
     public static final Logger log = LoggerFactory.getLogger(ApplicationContextAdvice.class);
     @Advice.OnMethodEnter // 在方法返回时执行
     public static void intercept(@Advice.This Object applicationContext) {
         // log.info("ApplicationContextAdvice:{}", applicationContext.getClass().getName());
         if (applicationContext instanceof ConfigurableApplicationContext context) {
             ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
             beanFactory.registerSingleton("creedBuddyAgentBeanPostProcessor", new CreedBuddyAgentBeanPostProcessor(context));
             beanFactory.registerSingleton("creedBuddyAgentBeanFactoryPostProcessor", new CreedBuddyAgentBeanFactoryPostProcessor(context));
             beanFactory.registerSingleton("applicationContextHolder", new ApplicationContextHolder(context));
             log.info("creedBuddyAgentBeanPostProcessor registered!");
         }
     }
 }
 
 
 new AgentBuilder 
         .Default()
         // 根据包名前缀拦截类
         .type(ElementMatchers.nameStartsWith("com.agent"))
         // 拦截到的类由transformer处理
         .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                   //MethodDelegation.withDefaultConfiguration().to()
                   // 完全忽略目标方法
                   //Advice.to() 允许在目标的执行方法前后插入逻辑，而不是完全接管方法
                   // 完全忽略目标方法
                   return builder.method(ElementMatchers.named("prepareBeanFactory")).intercept(
                           Advice.to(ApplicationContextAdvice.class));
         })
         .installOn(inst);
 
 ```

#### ➂基于java agent和ASM组合简单使用

在一些情况下，比如 修改  `PropertySource`#ignoreResourceNotFound(), 使其默认值为 true, 如果用byte buddy修改会导致属性缺失，[参考[How to remove/replace existing annotations?]](https://github.com/raphw/byte-buddy/issues/917#issuecomment-686726802), 因此我们可以使用以下实现方式：

```java
public class PropertyResourceTransformerEnhance implements ClassFileTransformer, Opcodes {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 只保留我们需要增强的类
        if ("org.springframework.context.annotation.PropertySource".equals(className.replace("/", "."))) {
            log.info("PropertyResourceTransformerEnhance:{} replaced:{}", className, className.replace("/", "."));
            ClassReader cr = null;
            try {
                cr = new ClassReader(classfileBuffer);
            } catch (Exception e) {
                log.error("Exception", e);
            }
            // ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            var cv = new PropertySourceClassVisitor(ASM9, cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return null;
    }
    static class PropertySourceAnnotationVisitor extends AnnotationVisitor {
        public PropertySourceAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            // log.debug("ModifyAnnotationVisitor name:" + name);
            log.debug("ModifyAnnotationVisitor value to true");
            // 将读取的默认值修改为true
            super.visit(name, true);
        }
    }

    static class PropertySourceMethodVisitor extends MethodVisitor {
        public PropertySourceMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }
        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            log.debug("ModifyMethodVisitor visitAnnotationDefault");
            // 访问annotation的默认值时，会执行此方法
            return new PropertySourceAnnotationVisitor(Opcodes.ASM9, super.visitAnnotationDefault());
        }
    }

    static class PropertySourceClassVisitor extends ClassVisitor {
        protected PropertySourceClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            //只修改 @PropertyResource ignoreResourceNotFound 方法
            log.info("PropertyResourceTransformerEnhance visitMethod name:{} descriptor:{}", name, descriptor);
            if ("ignoreResourceNotFound".equals(name) && "()Z".equals(descriptor)) {
                log.debug("visitMethod name:{} descriptor:{}" , name, descriptor);
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new PropertySourceMethodVisitor(Opcodes.ASM9, methodVisitor);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

}

public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {
  inst.addTransformer(new PropertyResourceTransformerEnhance());
}
```



### ❷JVM启动后替换实现 - `agentmain`

#### **1)首先准备好java agent的agentmain**

```java
public class RunTimeAgent {
    public static ResettableClassFileTransformer resettableClassFileTransformer;
    public static Instrumentation previousInstrumentation;
    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        // ref:https://github.com/raphw/byte-buddy/issues/1164
        // https://stackoverflow.com/questions/72078883/class-retransformation-with-bytebuddy-agent
        // > Normally, when retransforming, the Advice API is better suited for transformation. It supports most features of the delegation API but works slightly different.
        log.info("agentArgs{}", agentArgs);
        if (Objects.nonNull(resettableClassFileTransformer)) {
            log.info("resetting......{}", resettableClassFileTransformer.toString());
            resettableClassFileTransformer.reset(previousInstrumentation, AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
        }
        if ("exit".equals(agentArgs)) {
            log.info("exit......{}", resettableClassFileTransformer.toString());
            return;
        }
        // RedefinitionStrategy.REDEFINITION 修改现有的内容
        // RedefinitionStrategy.RETRANSFORMATION 可以动态增强类的定义或者添加新的字段或方法
        resettableClassFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with(new FlowListener())
                .type(ElementMatchers.named("org.example.controller.TestController"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    System.out.println("typeDescription:" + typeDescription);
                    try {
                        return builder
                                .visit(Advice.to(Class.forName("com.ethan.agent.adaptor.TestControllerInterceptor", true, classLoader)).on(ElementMatchers.named("testAgent")));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .installOn(inst);
        log.info(">>>>>>>>>>>>>>>>>>>>> INITIALIZED AGENT{}", resettableClassFileTransformer.toString());
        previousInstrumentation = inst;
    }

    public static class FlowListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith("org.example")) {
                log.info("onDiscovery:{} {} {} {}", typeName, classLoader, module, loaded);
            }
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            if (typeDescription.getName().contains("org.example")) {
                log.info("onTransformation:{}-{}-{}-{}-{}", typeDescription.getName(), classLoader, module, loaded, dynamicType);
                /* try {
                    dynamicType.saveIn(new File("/Users/venojk/Documents/bytebuddy/%s".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss")))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } */
            }
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeDescription.getName().contains("org.example")) {
                log.info("onIgnored:{}", typeDescription.getName());
            }
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            log.info("onError:{}", typeName, throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith("org.example")) {
                log.info("onComplete:{}", typeName);
            }
        }
    }
}
```

#### **2)在pom.xml中新增premain的信息**

```xml
<manifestEntries>
    <Can-Redefine-Classes>true</Can-Redefine-Classes>
    <Can-Retransform-Classes>true</Can-Retransform-Classes>
    <Agent-Class>com.ethan.agent.RunTimeAgent</Agent-Class>
</manifestEntries>
```

#### **3）准备好拦截器**

```java
@Advice.OnMethodExit // 在方法返回时执行
public static void intercept(@Advice.Origin String methodName, @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned) {
    String versionStr = "version11";
    log.info("OnMethodExit JWTVerifierAdvice Name:{} hello world【{}]", methodName, versionStr);
    returned = "hello world【" + versionStr + "]";
}
```

#### 存在的问题 TODO

虽然解决了重复挂在jar的问题，但是当jar更新之后，静态类无法调用最新的class类。



### ❸[springboot启动时是如何加载配置文件application.yml文件](https://cloud.tencent.com/developer/article/2144353)

- EnvironmentPostProcessorApplicationListener#onApplicationEnvironmentPreparedEvent
- SystemEnvironmentPropertySourceEnvironmentPostProcessor
- **ConfigDataEnvironmentPostProcessor**
- ConfigDataEnvironment#createContributors
- ConfigDataImporter#resolveAndLoad
- YamlPropertySourceLoader#load



1. 代理对象私有构造方法，创建自有构造方法，创建实例对象
2. 使用ASM拦截 字段 注解 类
3. 使用ByteBuddy拦截对象



# BytBuddy完整入门学习

- [Byte Buddy 官网](https://bytebuddy.net/#/tutorial-cn)

特点：

（1）使用Byte Buddy，不需要深入了解Java字节码或类文件格式。

（2）Byte Buddy的API的设计是非侵入性的，因此Byte Buddy不会在其创建的类中留下如何痕迹，所以生成的类不需要依附Byte Buddy去使用。这就是Byte Buddy的吉祥物（logo）是一个幽灵的原因。

（3）Byte Buddy使用Java 5编写，但可以支持任何Java版本。

（4）Byte Buddy是轻量级的库，只依赖于Java字节码解析库ASM。

### Hello World

动态生成一个类，它的`toString()`方法返回`"Hello World!"`

```java
Class<?> dynamicType = new ByteBuddy()
  .subclass(Object.class)	// 指定父类
  .method(ElementMatchers.named("toString"))	// 指定重写的方法
  .intercept(FixedValue.value("Hello World!"))	// 指定方法拦截
  .make()	// 生成class
  .load(getClass().getClassLoader())	// 指定类加载器，加载class
  .getLoaded();
 
assertThat(dynamicType.newInstance().toString(), is("Hello World!"));
```

- `ElementMatcher`中提供了预定义的一些匹配器，如果需要自定义需要去实现`ElementMatcher`接口。
- 客户端通过实现`Implementation`接口，可以自定义拦截器。

### Method Delegation

把方法`Function::apply`委派给自定义的`GreetingInterceptor::greet`方法

```java
public class GreetingInterceptor {
    public Object greet(Object argument) {
        return "Hello from " + argument;
    }
}

Class<? extends java.util.function.Function> dynamicType = new ByteBuddy()
  .subclass(java.util.function.Function.class)
  .method(ElementMatchers.named("apply"))
  .intercept(MethodDelegation.to(new GreetingInterceptor()))	// 委派
  .make()
  .load(getClass().getClassLoader())
  .getLoaded();
assertThat((String) dynamicType.newInstance().apply("Byte Buddy"), is("Hello from Byte Buddy"));
```

- 可以发现`intercept`方法没有指定`GreetingInterceptor`中的方法，`byte-buddy`会根据出入参数类型去匹配拦截的方法。如果发现有多个方法的出入参数都可以匹配`Function::apply`方法的实现，则会报错！

将拦截器定义成可以匹配更宽泛的出入参类型，以拦截任何方法。示例如下：

```java
public class GeneralInterceptor {
  @RuntimeType
  public Object intercept(@AllArguments Object[] allArguments,
                          @Origin Method method) {
    // intercept any method of any signature
  }
}
```

当`byte-buddy`类库解析到这些注解时，会把对应需要的参数依赖进行注入。

`@RuntimeType`注解用于：运行时，方法被拦截后，出参的类型转换。

Byte Buddy还提供了其他预定义的注解，比如：用于`Runnable`或`Callable`类型的注解`@SuperCall`，ByteBuddy会注入父类的代理对象，以提供父类的非抽象方法调用。

### Changing existing classes

Byte Buddy不仅限于创建子类，也提供了“运行时重定义已存在代码”的能力。Byte Buddy为“重定义”提供了一套简便的API，使用了JDK5引入的Java agent机制。（Java agent用来在运行时 修改已经存在的Java应用中的代码。）

下面是一个使用例子，我们对所有类名以`Timed`结尾的类的全部方法增加统计方法耗时的功能。

首先定义一个计时拦截器`TimingInterceptor`

```java
public class TimingInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {
        long start = System.currentTimeMillis();
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        } finally {
            System.out.println(method + " took " + (System.currentTimeMillis() - start));
        }
    }
}
```

接着使用Java agent来应用`TimingInterceptor`到所有以`Timed`结尾的类的方法中。

```java
public class TimerAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(ElementMatchers.nameEndsWith("Timed"))
                .transform(
                        (builder, type, classLoader, module, protectionDomain) ->
                                builder.method(ElementMatchers.any())
                                    .intercept(MethodDelegation.to(TimingInterceptor.class))
                )
                .installOn(instrumentation);
    }
}
```

- `premain`方法类似于Java中的`main`方法，它是任何去执行重定义的Java agent的一个入口方法，它先于`main`方法被调用。

- Java agent接收了一个`Instrumentation`接口的实例对象，这个接口允许Byte Buddy来连接到JVM到标准API，从而进行运行时类型重定义。

- [package java.lang.instrument 官方文档](#)：java.lang.instrument包提供了一些服务来允许Java agents在JVM中“检测（instrument）”程序运行，检测机制是修改方法的字节码。

- 该程序与一个manifest文件打包在一起，该manifest文件的`Premain-Class`属性指向 `TimerAgent`。 现在可以通过设置 `-javaagent:timingagent.jar`将生成的 jar 文件添加到任何 Java 应用程序，类似于将 jar 添加到类路径。 当代理被激活，所有以 `Timed` 结尾的类现在都会将其执行时间打印到控制台。

## 类

### 类命名策略

```java
DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
  .subclass(Object.class)
  .name("example.Type")	// 显式指定类的命名，可选
  .make();
```

命名是定义一个类时必须的。如果用户没有显式指定类的命名，Byte Buddy遵循 **“约定优于配置原则”** 。

Byte Buddy的默认配置提供了一个`NamingStrategy（命名策略）`，它可以根据动态类的超类名称随机生成一个名称。例如，子类化一个名为`example.Foo`的类，生成的类名称就像`example.Foo$$ByteBuddy$$1376591271`，其中的数字序列是随机的。这条规则的例外是：子类化的类型来自`java.lang`包，Java的安全模型不允许自定义的类在这个命名空间。因此，在默认的命名策略中，这种类型以`net.bytebuddy.renamed`前缀命名。

注意！！如果定义的类名中的包 和 超类相同，直接父类的包私有方法对于动态类是可见的。

用户可以自定义类命名策略，示例如下：

```java
DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
  .with(new NamingStrategy.AbstractBase() {
    @Override
    protected String name(TypeDescription superClass) {
        return "i.love.ByteBuddy." + superClass.getSimpleName();
    }
  })
  .subclass(Object.class)
  .make();
```

此匿名类被实现为简单地将`i.love.ByteBuddy`与父类的类名拼接起来。 当子类化`Object`类时，动态类型就会被命名为`i.love.ByteBuddy.Object`。

注意！！ Java 虚拟机用名称来区分不同的类型，要避免名称冲突。如果需要自定义类名，建议使用Byte Buddy 内置的`NamingStrategy.SuffixingRandom`，这样用户只需要自定义一个有意义的前缀名称。

### 类型重定义与类型变基

ByteBuddy不仅可以创建现有类的子类，也可以增强现有类。

- 类型重定义(**redefine**)：Byte Buddy 可以添加字段、方法 或者 替换已存在方法的实现。

  类型重定义的效果是这样的：

  ```java
  class Foo {
    String bar() { return "bar"; }
  }
  	|			|			|
  	V			V			V
  class Foo {
    String bar() { return "qux"; }
  }
  ```

  

- 类型变基(**rebase**)：Byte Buddy会用兼容的签名复制私有方法的实现为私有的重命名过的方法，允许用户继续调用原始方法。

  类型变基的效果是这样的：

  ```java
  class Foo {
    String bar() { return "bar"; }
  }
  	|			|			|
  	V			V			V
  class Foo {
    String bar() { return "foo" + bar$original(); }
    private String bar$original() { return "bar"; }
  }
  ```

- 两种增强方式的区别：<u>类型重定义方法时**丢弃**覆写的方法</u>，而类型变基会时对原始方法进行**保留**。

任何变基，重定义或者子类化都是用相同的API执行的，它是由`DynamicType.Builder`接口定义的。

```java
new ByteBuddy().subclass(Foo.class)
new ByteBuddy().redefine(Foo.class)
new ByteBuddy().rebase(Foo.class)
```

如何使用这个动态类型，具体见后续内容（reloading a class）

### 类加载

#### Unloaded实例

Byte Buddy 创建的类型是通过`DynamicType.Unloaded`的一个实例来表示的。

> ByteBuddyAPI一般的使用流程中的类型转换：
>
> ByteBuddy => DynamicType.Builder => DynamicType. Unloaded => DynamicType. Loaded => Class<?>

对于`DynamicType.Unloaded`实例，代表的是Java的class二进制字节码。对于它的使用，用户可以让JVM去加载，也可以自行构建脚本运行字节码等等。该类型提供了`saveIn(File)`方法，允许用户把类保存到给定文件中，此外也提供了`inject(File)`方法将类注入到已存在的jar文件中。

#### 类加载器

系统类加载器负责加载Java应用程序路径里的类，但它不感知动态创建的类。ByteBuddy提供的几个解决方案如下：

- 创建一个新的`ClassLoader`，它用于加载动态创建的类。它的父类加载器（聚合非继承，与双亲委派中的“父亲”概念相同）指定为程序中已经存在的类加载器，这样程序中的所有类对于新类加载器都是可见的。

- 通常，子类加载器在加载类之前会先询问父类加载器（双亲委派）。ByteBuddy提供类加载器，其遵从“孩子优先创建”，这样同一个类可能既被子加载器加载又被父加载器加载。

- 最后，ByteBuddy利用反射将一个类注入到已经存在的类加载器。类加载器中以类的全限定名来一一标识每个不同的类，ByteBuddy利用这条规则，反射调用受保护的方法，将新类注入了类加载器。

  > 具体实现在`net.bytebuddy.dynamic.loading.ClassInjector`中，反射调用的`protected`方法是`ClassLoader::defineClass`，即最终依旧使用`ClassLoader`的本地方法进行类定义。

上面的方法各自有缺点：

- 每个ClassLoader的命名空间是独立的，父类加载器和子类加载器中就算加载的是同一个类，JVM也会把它们当成不一样的类。带来的问题：同一个包下不同类加载器加载的类之间不能互调包私有方法，如果这两个类之间还有继承关系则会导致包私有方法覆写失效，运行时类转换异常等问题。

  > 这意味着，如果不是用相同的类加载器加载， `example.Foo`类无法访问`example.Bar`类的包私有方法。此外， 如果`example.Bar`继承`example.Foo`，任何被覆写的包私有方法都将变为无效，但会委托给原始实现。

- 如果使用注入类加载器等方法，则可能难以解决循环依赖的问题，因为当类加载器注入类A时，类A依赖类B，故类加载器尝试去寻找类B（类的加载—链接—解析），此时会找不到类B，因为类B还没被动态创建。

  > 幸运的是，大多数JVM的实现在第一次使用时都会延迟解析引用类， 这就是类注入通常在没有这些限制的时候正常工作的原因。此外，实际上，由 Byte Buddy 创建的类通常不会受这样的循环影响。

#### 类加载策略

创建一个`DynamicType.Unloaded`后，这个类型可以用`ClassLoadingStrategy`加载。如果没有提供这个策略，Byte Buddy 会基于提供的类加载器推测出一种策略，并且仅为启动类加载器创建一个新的类加载器， 该类加载器不能用反射的方式注入任何类。

Byte Buddy 提供了几种开箱即用的类加载策略， 每一种都遵循上述概念中的其中一个。这些策略在`net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default`中定义。

```java
public interface ClassLoadingStrategy<T extends ClassLoader> {
  enum Default implements Configurable<ClassLoader> {
    /** 创建一个新的、经过包装的ClassLoader */
    WRAPPER(...),
    /** 
     * 和WRAPPER类似，
     * 但是会通过ClassLoader.getResourceAsStream(String)暴露代表类的byte arrays。
     * 这意味着类的字节码始终保留在ClassLoader中，一直占用Java堆空间
     */
    WRAPPER_PERSISTENT(...),
    /** 创建一个类似的具有孩子优先语义的ClassLoader */
    CHILD_FIRST(...),
    /** 根据名称，可同理类推 */
    CHILD_FIRST_PERSISTENT(...),
    /** 用反射注入一个动态类型 */
    INJECTION(...);
  }
}
```

类加载代码示例：

```java
Class<?> type = new ByteBuddy()
  .subclass(Object.class)
  .make()
  .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
  .getLoaded();

// net.bytebuddy.dynamic.loading.ByteArrayClassLoader@bccb269
System.out.println(type.getClassLoader());
```

> 注意，当加载类时，预定义的类加载策略是通过应用执行上下文的`ProtectionDomain`来执行的。或者， 所有默认的策略通过调用`withProtectionDomain`方法来提供明确地保护域规范。

#### 重新加载类(Reloading)

Byte Buddy 可以用来重新定义或变基一个已存在的类。然而，在执行 Java 程序时，通常无法保证给定的类没有加载。由于 Java 虚拟机的HotSwap功能， 即使在类被加载之后，他们也可以被重新定义。这个功能可以通过 Byte Buddy 的`ClassReloadingStrategy`使用。

> Java虚拟机的HotSwap功能：
>
> 在不停止程序运行的情况下，动态地替换程序的某一部分，以做到程序热更新的目的。
>
> HotSwap功能有两种类型：
>
> 1. Source-Level HotSwap：可以在不重新编译整个类的情况下，修改类中的方法或代码块。但是，只有在修改的代码块不改变类的结构时，才可以使用这种方式进行HotSwap。
> 2. Class-File HotSwap：可以在程序运行时，替换已经被加载到虚拟机中的类文件。这种方式可以用来进行更广泛的代码修改，但是需要保证新的类文件与旧的类文件具有相同的结构。
>
> HotSwap只能用Java agent访问。这样的代理可以通过在虚拟机启动时使用`-javaagent`参数指定它来安装，其中 javaagent 的参数需要是 Byte Buddy 的代理jar，它可以从[从 Maven 仓库下载](https://link.juejin.cn?target=https%3A%2F%2Fsearch.maven.org%2Fsearch%3Fq%3Da%3Abyte-buddy-agent)。 然而，当 Java 应用从虚拟机的一个JDK运行时，即使应用启动后，Byte Buddy 也可以通过`ByteBuddyAgent.install()`加载 Java 代理。

把Bar重定义为Foo，代码示例：

```java
class Foo {
  String m() { return "foo"; }
}
 
class Bar {
  String m() { return "bar"; }
}


ByteBuddyAgent.install();
Foo foo = new Foo();
new ByteBuddy()
  .redefine(Bar.class)
  .name(Foo.class.getName())
  .make()
  .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
Assert.assertEquals("bar", foo.m());
```

可以发现，对Bar进行重定义，同时影响了Foo，因为类加载器是根据类的名称来查找类型，所以Bar覆盖了原本的Foo类型。

HotSwap 功能的一个巨大的缺陷：HotSwap的当前实现要求重定义的类在重定义前后应用相同的类模式，类的结构不能改变。 这意味着当重新加载类时，不允许添加方法或字段。

Byte Buddy 为任何变基的类定义了原始方法的副本， 因此类的变基不适用于`ClassReloadingStrategy`。

此外，类重定义不适用于具有显式的类初始化程序的方法(类中的静态块)的类， 因为该初始化程序也需要复制到额外的方法中。如何理解：静态代码块是在“类加载—初始化”阶段执行的，不是在运行时随着方法被调用动态执行的，所以如果需要对其重定义，需要复制初始化程序。

不幸的是， OpenJDK已经退出了[扩展HotSwap的功能](https://openjdk.org/jeps/159)， 因此，无法使用HotSwap的功能解决此限制。同时，Byte Buddy 的HotSwap支持可用于某些看起来有用的极端情况。 否则，当(例如，从构建脚本)增强存在的类时，变基和重定义可能是一个便利的功能。

#### 使用未加载的类

由于HotSwap的局限性，最好在类被加载前去对一个类进行重定义或变基。ByteBuddy提供了类似反射的机制可以简单处理未加载的类。ByteBuddy用`TypeDescription`来表示一个`Class`实例，Byte Buddy 使用`TypePool(类型池)`，提供了一种标准的方式来获取类的`TypeDescription(类描述)`。Byte Buddy也提供了类型池的默认实现`TypePool.Default`。`TypePool.Default`解析类的二进制格式并将其表示为需要的`TypeDescription`。

```kotlin
package foo;
class Bar { }

TypePool typePool = TypePool.Default.ofSystemLoader();
Class bar = new ByteBuddy()
  .redefine(typePool.describe("foo.Bar").resolve(),
            ClassFileLocator.ForClassLoader.ofSystemLoader())
  .defineField("qux", String.class)
  .make()
  .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
  .getLoaded();
Assert.asserNotNull(bar.getDeclaredField("qux"));
```

使用`"foo.Bar"`来描述类，而不是直接引用这个类，避免JVM提前加载了这个类。

请注意， 当处理未加载的类时，我们还需要指定一个`ClassFileLocator(类文件定位器)`，它允许定位类的类文件。

#### 创建Java Agent

JavaAgent可以拦截 Java 应用中进行的任何类加载活动。Java 代理被实现为一个简单的带有入口点的 jar 文件，其入口点在 jar 文件的 manifest(清单) 文件中指定。

假定我们之前定义了一个名为`@ToString`的注解，通过实现代理的`premain`方法， 对所有带该注解的类实现`toString`方法，示例如下：

```java
class ToStringAgent {
  public static void premain(String arguments, Instrumentation instrumentation) {
    new AgentBuilder.Default()
        .type(isAnnotatedWith(ToString.class))
        .transform(new AgentBuilder.Transformer() {
      @Override
      public DynamicType.Builder transform(DynamicType.Builder builder,
                                              TypeDescription typeDescription,
                                              ClassLoader classloader) {
        return builder.method(named("toString"))
                      .intercept(FixedValue.value("transformed"));
      }
    }).installOn(instrumentation);
  }
}
```

Bootstrap类加载器在Java代码中表现为`null`，所以无法通过反射在这个加载器加载类。但是 Byte Buddy 可以创建 jar 文件并且将这些文件添加到启动类的加载路径中。然而，这么做需要将这些类保存到磁盘上。

如果你不想写premain方法，那么也可以使用ByteBuddy帮忙写的premain方法：

```java
new AgentBuilder.Default()
        .type(isAnnotatedWith(ToString.class))
        .transform(new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder transform(DynamicType.Builder builder,
                                                 TypeDescription typeDescription,
                                                 ClassLoader classloader) {
                return builder.method(named("toString"))
                        .intercept(FixedValue.value("transformed"));
            }
        }).installOn(ByteBuddyAgent.install());
```

写完之后，在你需要拦截的时机执行这段代码（比如挂载在Spring启动流程中）。

建议程序全局只调用一次`ByteBuddyAgent.install()`，之后需要instrumentation时只调用`ByteBuddyAgent.getInstrumentation()`方法获取。因为`install`方法内部会进行同步，有一定性能损失。

#### 使用泛型

Byte Buddy 在处理 Java 程序语言中定义的泛型类型。Java 运行时不考虑泛型，只处理泛型的擦除。然而， 泛型仍然会嵌入在任何 Java 文件中并且被反射 API 暴露。因此，有时在生成的类中包含泛型信息是有意义的， 因为泛型信息能影响其他类库和框架的行为。当编译器将一个类作为类库进行处理和持久化时，嵌入的泛型信息也很重要。

ByteBuddy可以帮忙把泛型元数据带入class文件中。

> 当子类化一个类、实现一个接口，或声明一个字段或方法时，由于上述原因，Byte Buddy 接受一个 Java `Type`而不是一个擦除泛型的`类`。 泛型也可以用`TypeDescription.Generic.Builder`被显式的定义。Java 泛型与类型擦除一个重要的不同是类型变量的上下文含义。 当另一种类型以相同的名称声明相同类型的变量时，通过某种类型定义的具有特定名称的类型变量不一定表示相同类型。因此， 当将一个`类型`实例交给库时，Byte Buddy 会重新绑定所有泛型类型，这些泛型类型在生成的类型或方法的上下文中表示类型变量。
>
> 当一个类型被创建时，Byte Buddy 还会透明的插入[ *桥接方法*](https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html)。桥接方法被`MethodGraph.Compiler`处理，它是`ByteBuddy`实例的一个属性。 默认方法图编译器行为像 Java 编译器一样处理任何类文件的泛型信息。但是，对于 Java 以外的语言，不同的方法图编译器可能是合适的。

## 字段和方法

### 创建动态类的主要目的是定义新的逻辑

下面是一个简单的例子：覆写`toString`方法，使其返回`"Hello World!"`

```java
String toString = new ByteBuddy()
  .subclass(Object.class)
  .method(ElementMatchers.named("toString"))
  .intercept(FixedValue.value("Hello World!"))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance()	// Java reflection API
  .toString();
```

这个例子中包含了ByteBuddy领域特定语言的两条指令：

- `method`：匹配覆写方法

  - `ElementMatcher`中提供了预定义的一些匹配器，如果需要自定义需要去实现`ElementMatcher`接口。

  - 注意：预定义的方法匹配器是可以组合的，通过组合可以实现对方法更精确的匹配。

    例如：`named("toString").and(returns(String.class).and(takesArguments(0)))`

- `intercept`：实现覆写方法
  - ByteBuddy提供了一些预定义拦截器，如果需要自定义需要去实现`Implementation`接口。

### ByteBuddy以栈的形式组织覆写方法的规则。

让我们看一个这种场景的示例：

```java
class Foo {
  public String bar() { return null; }
  public String foo() { return null; }
  public String foo(Object o) { return null; }
}
 
Foo dynamicFoo = new ByteBuddy()
  .subclass(Foo.class)
  .method(isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
  .method(named("foo")).intercept(FixedValue.value("Two!"))
  .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance();
```

在这个示例中，定义了三条规则，而且三条规则覆盖的方法范围是交织的，第一条规则拦截`Foo`类的全部方法，第二条只拦截方法名称为foo的方法，第三条拦截的方法不仅名称是foo，而且需要一个参数。

当调用`String foo(Object o) { ... }`方法时，它会命中哪条规则呢？

ByteBuddy对于规则以压栈的方式存储，意味着所有的方法都是先去尝试匹配第三条规则，如果没匹配上则再匹配第二条规则，最后尝试匹配第一条规则。

因此，客户端应该始终最后注册更具体的方法匹配器。

注意，`ByteByddy`设置允许定义一个`ignoreMethod`属性，与该方法匹配器成功匹配的方法永远不会被覆写。 默认，Byte Buddy 不会覆写任何`synthetic`方法。

> synthetic（合成）：源代码没有的字段、方法、构造器，但是在编译后被合成了，这些字段、方法、构造器会被synthetic修饰。

### 定义新的方法、字段

可以用`defineMethod`来定义一个方法签名，定义之后需要提供一个实现`Implementation`。如果在定义完方法之后又添加了一些方法匹配器，那么刚刚定义的方法实现可能会因为被方法匹配器命中，而被取代。

定义方法的修饰符：使用`ForMethod`的实现类

- `Visibility`：可见性：public、private、包私有、protected

- `Ownership`：拥有类型：成员、static

- `SynchronizationState`：同步状态：普通、synchronized

- `SyntheticState`：合成状态：普通、synthetic

- `MethodStrictness`：方法严格：普通、strictfp

- `MethodManifestation`：方法表现：普通、native、abstract、final、final native、final bridge

- `MethodArguments`：方法参数类型：普通、包含不定参数

- `Mandate`：是否强制：普通、mandated强制方法

```java
@Test
public void testDefineMethod() throws Exception {
    Class<? extends Foo> loaded = new ByteBuddy()
            .subclass(Foo.class)
            .defineMethod("qux", String.class,
                    Visibility.PUBLIC, Ownership.MEMBER, SynchronizationState.PLAIN, SyntheticState.PLAIN)
            .withParameter(String.class, "name", MethodManifestation.FINAL.getMask())
            .intercept(FixedValue.value("hello"))
            .make()
            .load(getClass().getClassLoader())
            .getLoaded();
    Method qux = loaded.getDeclaredMethod("qux", String.class);
    Object ret = qux.invoke(loaded.newInstance(), "a");
    Assert.assertEquals(ret, "hello");
}
```

可以用`defineField`为给定的类定义字段，字段不会覆写，只能被隐藏，因此不存在字段匹配器。

- `Visibility`
- `Ownership`
- `Mandate`
- `SyntheticState`
- `FieldPersistence`：普通、transient
- `FieldManifestation`：普通、final、volatile
- `EnumerationState`：普通、枚举

```java
@Test
public void testDefineField() throws NoSuchFieldException {
    Class<?> newFoo = new ByteBuddy()
            .subclass(Foo.class)
            .defineField("name", String.class, Visibility.PUBLIC, Ownership.MEMBER, FieldManifestation.VOLATILE)
            .make()
            .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
            .getLoaded();

    Field name = newFoo.getDeclaredField("name");
    Assert.assertEquals(name.getType(), String.class);
    Assert.assertNotSame(name.getModifiers() & Modifier.VOLATILE, 0);
}
```

类修饰符`ModifierContributor.ForType`：

- `Visibility`
- `Ownership`
- `EnumerationState`
- `SyntheticState`
- `TypeManifestation`：普通、final、abstract、interface、annotation

> 关键字：**strictfp**
>
> 使用对象：字段，类
>
> 介绍：自Java2以来，Java语言增加了一个关键字strictfp
>
> strictfp的意思是FP-strict，也就是说精确浮点的意思。在Java虚拟机进行浮点运算时，如果没有指定strictfp关键字时，Java的编译器以及运行环境在对浮点运算的表达式是采取一种近似于我行我素的行为来完成这些操作，以致于得到的结果往往无法令你满意。而一旦使用了strictfp来声明一个类、接口或者方法时，那么所声明的范围内Java的编译器以及运行环境会完全依照浮点规范IEEE-754来执行。因此如果你想让你的浮点运算更加精确，而且不会因为不同的硬件平台所执行的结果不一致的话，那就请用关键字strictfp。
>
> 你可以将一个类、接口以及方法声明为strictfp，但是不允许对接口中的方法以及构造函数声明strictfp关键字，例如下面的代码：
>
> 1. 合法的使用关键字strictfp：
>
> strictfp interface A {}
>
> public strictfp class FpDemo1 { strictfp void f() {} }
>
> 1. 错误的使用方法：
>
> interface A { strictfp void f(); }
>
> public class FpDemo2 { strictfp FpDemo2() {} }

> bridge method：
>
> 示例：public synthetic bridge accept(Ljava/lang/Object;)V
>
> 桥接方法是 JDK 1.5 引入泛型后，为了使Java的泛型方法生成的字节码和 1.5 版本前的字节码相兼容，由编译器自动生成的方法。

> mandated方法：标识说明这个方法收到JVM规范强制约束，比如`Object::equals`

### 深究fixed values

`FixedValue`实现的方法会简单返回一个给定的对象，每次给的对象是否相同？

一个类能以两种不同的方式记住这个对象：

- 把固定值写入**类的常量池**。常量池是java class文件的一部分，包含大量无状态的值：类名称、方法名称等等，以及类中方法或者字段中用到的任何字符串或基本类型的值、其他类型的引用。
- 把固定值保存在静态字段中。注意：当这个类被加载到JVM中时，需要给这个字段赋给定的值，否则这个字段总会是`null`。因此，每个动态创建的类都附带一个`TypeInitializer`，它可以配置执行这样的显式初始化。一般的，当`Unloaded`被load时，bytebuddy会自动执行`TypeInitializer#onload(Class<?> type)`，但是如果在bytebuddy之外加载类则需要显式进行初始化，可以调用`TypeInitializer#isAlive()`查询初始化器的存活状态。

> @see class ForStaticField implements LoadedTypeInitializer, Serializable

> 例外：FixedValue返回`null`值，既不会写入常量池也不会保存在静态字段中。

拦截方法时，如果指定的返回值和返回类型不匹配，则在创建`Unloaded`对象时就会直接抛出`IllegalArgumentException`。

客户端可以实现`net.bytebuddy.implementation.bytecode.assign`来自定义赋值器，在`FixedValue`中用`withAssigner()`方法指定赋值器。

### 委托方法调用

#### 默认绑定策略

为了灵活性，Byte Buddy提供了`MethodDelegation`（方法委托）实现，它在对方法调用做出反应时提供最大程度的自由。

使用`MethodDelegation`示例如下：

```java
class Source {
  public String hello(String name) { return null; }
}

class Target {
  public static String intercept(String s) { return "String intercept(String s)"; }
  public static String intercept(int i) { return "String intercept(int i)"; }
  public static String intercept(Object o) { return "String intercept(Object o)"; }
}

@Test
public void testMethodDelegationDecision() throws Exception {
  String helloWorld = new ByteBuddy()
    .subclass(Source.class)
    .method(named("hello")).intercept(MethodDelegation.to(Target.class))
    .make()
    .load(getClass().getClassLoader())
    .getLoaded()
    .newInstance()
    .hello("World");
  Assert.assertEquals(helloWorld, "String intercept(String s)");
}
```

ByteBuddy默认的方法绑定算法：`String intercept(int i)`的入参类型与原方法的入参类型不匹配，所以不会被视为可能的匹配。`String intercept(Object o)`的参数类型可以与原方法匹配，但是`String intercept(String s)`的参数类型匹配更精确，所以最终`String intercept(String s)`被选择。

#### 委派与注解

ByteBuddy支持方法委派与注解协调工作：

注解决定了ByteBuddy将会为方法分配什么值，如果没有发现注解，效果等同于添加了@Argument注解，且注解参数值为方法参数索引值。下面示例的两个方法对于ByteBuddy来说是等价的。

```java
/** 隐式：不用注解 */
void foo(Object o1, Object o2) {...}
/** 显式：使用注解 */
void foo(@Argument(0) Object o1, @Argument(1) Object o2) {...}
```

其他注解：

- `@AllArguments`：用于数组入参，ByteBuddy尝试把源方法入参都塞到一个数组里传递给拦截方法
- `@This`：用于获取被拦截的源对象，一般用于通过这个对象访问其字段，如果访问被拦截方法会导致死循环
- `@Origin`：用于`Method`, `Constructor`, `Executable`, `Class`, `MethodHandle`, `MethodType`, `String`, `int`，可以获取原始方法、构造器、类等引用，如果被注解的参数类型是`String`，则分配`Method#toString()`，如果被注解的是参数类型是`int`，则会被分配检测方法的修饰符。
- `@SuperCall`：用于`Callable`或`Runnable`，可以给被拦截方法添加“切面”，bytebuddy会生成`AuxiliaryType`辅助类型，用于拦截器使用，这个辅助类型也可以通过DynamicType接口直接访问。

```java
class MemoryDatabase {
  public String load(String info) {
  	return info + ": foo";
  }
}

class LoggerInterceptor {
  public static String log(@SuperCall Callable<String> zuper)
    throws Exception {
    // zuper is generated dynamically by bytebuddy
    // zuper has fields which represent origin method parameters
    System.out.println("Calling database");
    try {
    	return zuper.call();
    } finally {
    	System.out.println("Returned from database");
    }
  }
}

@Test
public void testSuperCall() throws InstantiationException, IllegalAccessException {
  DynamicType.Loaded<MemoryDatabase> dynamicType = new ByteBuddy()
          .subclass(MemoryDatabase.class)
          .method(named("load"))
          .intercept(MethodDelegation.to(LoggerInterceptor.class))
          .make()
          .load(getClass().getClassLoader());

  String mysql = dynamicType.getLoaded()
  				.newInstance().load("mysql");
  System.out.println(mysql); // watch output text

  // 辅助类型`AuxiliaryType`
  Class<?> superCallClass = dynamicType.getLoadedAuxiliaryTypes()
    			.values().iterator().next();
  Class<?>[] interfaces = superCallClass.getInterfaces();
  Assert.assertTrue(Arrays.asList(interfaces).contains(Runnable.class));
  Assert.assertTrue(Arrays.asList(interfaces).contains(Callable.class));
}
```

`@SuperCall`实现原理类似于以下java代码：

```java
class LoggingMemoryDatabase extends MemoryDatabase {
 
  private class LoadMethodSuperCall implements Callable {
 
    private final String info;
    private LoadMethodSuperCall(String info) {
      this.info = info;
    }
 
    @Override
    public Object call() throws Exception {
      return LoggingMemoryDatabase.super.load(info);
    }
  }
 
  @Override
  public List<String> load(String info) {
    return LoggerInterceptor.log(new LoadMethodSuperCall(info));
  }
}
```

`@Super`：作用于被拦截对象，可以给被拦截方法添加切面，且可以修改入参。d

```java
class ChangingLoggerInterceptor {
  public static List<String> log(String info, @Super MemoryDatabase zuper) {
    System.out.println("Calling database");
    try {
      return zuper.load(info + " (logged access)");
    } finally {
      System.out.println("Returned from database");
    }
  }
}
```

`@Super`详解：

传入的`zuper`实例是ByteBuddy生成的，与原实例不同，所以如果通过`zuper`实例直接操作实例变量或者调用`final`方法可能会造成意料外的结果。如果被`@Super`修饰的参数类型与拦截的对象不匹配，则该方法不会被认为是其任何方法的绑定目标。

一般情况下，ByteBuddy会使用默认构造器来改造类对象。由于`@Super`可以修饰任何类型，所以存在不能使用默认构造器的情况，此时可以使用`@Super`的`constructorParameters`属性来识别不同构造器，然后分配相应的默认值进行调用，也可以在参数中指定`Super.Instantiation.UNSAFE`策略，这就不需要指定任何构造器了。

注意：拦截的目标方法所可能抛出的所检异常将被编译器忽视，运行时可能抛出。

- `@RuntimeType`：解决拦截的几个方法返回值不兼容，增强代码复用

严格的类型会限制代码的复用，为了克服这个限制，Byte Buddy 允许给方法和方法参数添加`@RuntimeType`注解， 它指示 Byte Buddy 终止严格类型检查以支持运行时类型转换。

```java
class Loop {
  public String loop(String value) { return value; }
  public int loop(int value) { return value; }
}

class Interceptor {
  @RuntimeType
  public static Object intercept(@RuntimeType Object value) {
    System.out.println("Invoked method with: " + value);
    return value;
  }
}
```

`@RuntimeType`放弃了类型安全，如果拦截方法编写不当，可能导致`ClassCastException`。

- `@DefaultCall`：用于调用接口默认方法，而不是超类方法。类似`@SuperCall`

```java
interface Flyable {
    default String fly() {
        return "i can fly";
    }

    void sayHi(String name);
}

class FoxInterceptor {
    public static String intercept(@DefaultCall Callable<String> callable) throws Exception {
        return "intercepted: " + callable.call();
    }
}

Flyable flyable = new ByteBuddy()
  .subclass(Flyable.class)
  .method(ElementMatchers.named("fly"))
  .intercept(MethodDelegation.to(FoxInterceptor.class))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance();
String ret = flyable.fly();
Assert.assertEquals(ret, "intercepted: i can fly");
try {
  flyable.sayHi("bob");
  Assert.fail();
} catch (Throwable e) {
  Assert.assertTrue(e instanceof AbstractMethodError);
}
```

- `@Default`：类似`@Super`，ByteBuddy会生成代理对象并注入

  ```java
  interface Fox {
      default String fox() { return "fox"; }
  }
  
  class DefaultInterceptor {
      public static String intercept(@Default Fox fox) {
          System.out.println("proxy class: " + fox.getClass());
          return "intercepted default: " + fox.fox();
      }
  }
  
  Fox fox = new ByteBuddy()
    .subclass(Fox.class)
    .method(named("fox"))
    .intercept(MethodDelegation.to(DefaultInterceptor.class))
    .make()
    .load(getClass().getClassLoader())
    .getLoaded()
    .newInstance();
  String ret = fox.fox();
  Assert.assertEquals(ret, "intercepted default: fox");
  ```

`@Pipe`：Byte Buddy自带的一个可以使用但需要显示安装和注册的注解。通过`@Pipe`注解，可以将拦截的方法调用转发到另一个对象。

- `@Pipe`注解为什么不能预先注解来给客户端使用？因为Java8之前Java类库没有合适函数式接口来用。

```java
class MemoryDatabase {
  public String load(String info) {
    return info + ": foo";
  }
}

class ForwardingLoggerInterceptor {
  private final MemoryDatabase memoryDatabase; // constructor omitted

  public String log(@Pipe Function<MemoryDatabase, String> pipe) {
    System.out.println("Calling database");
    try {
      return pipe.apply(memoryDatabase);
    } finally {
      System.out.println("Returned from database");
    }
  }
}

MemoryDatabase loggingDatabase = new ByteBuddy()
  .subclass(MemoryDatabase.class)
  .method(ElementMatchers.named("load"))
  .intercept(MethodDelegation.withDefaultConfiguration()
             .withBinders(Pipe.Binder.install(Function.class))
             .to(new ForwardingLoggerInterceptor(new MemoryDatabase())))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance();
loggingDatabase.load("hello world");
```

在上面的示例中，我们只转发了我们本地创建的实例的调用。然而，通过子类化一个类型来拦截一个方法的优势在于，这种方法允许增强一个存在的实例。 此外，你通常会在实例级别注册拦截器，而不是在类级别注册一个静态拦截器。

#### 歧义解析器与方法绑定策略

在 Byte Buddy 确定有资格绑定到给定源方法的候选方法后，它将解析委托给`AmbiguityResolver(歧义解析器)`链。同样，你可以自由实现自己的歧义解析器， 它可以补充甚至替代 Byte Buddy 的默认解析器。

如果没有此类更改，歧义解析器链会尝试通过应用下面具有相同顺序的规则来识别一个唯一的目标方法：

- 可以通过添加`@BindingPriority`注解，给方法分配明确的优先级，高优先级的方法总是优先被识别为目标方法。
- `@IgnoreForBinding`注解的方法永远不会被视为目标方法。
- 如果源方法和目标方法名称相同，则优先级高于其他名称不同的方法。
- 参数与源方法类型兼容，参数类型更确切的方法将被优先考虑。如果在解析阶段，参数的分配不应该考虑参数类型，则通过设置注解的`bindingMechanic`属性为`BindingMechanic.ANONYMOUS`。
- 如果参数类型都明确，则绑定参数更多的方法被视为目标方法。

一般的，我们会把方法委派给静态方法，但也可以委派给**实例方法、字段和构造器**

- 通过调用`MethodDelegation.to(new Target())`，可以将方法调用委托给`Target`类的任何实例方法。注意， 这包含实例的类继承层次中任何位置定义的方法，包含`Object`类中定义的方法，但是不会优先选用父类方法。
- 通过调用`MethodDelegation.toField("fieldName")`，可以将方法调用委派给当前类的任何实例或静态字段，即直接返回这个字段的值。
- 通过调用`MethodDelegation.toConstructor(ArrayList.class)`，可以将方法调用委派给指定类的构造器，即返回这个构造器改造的实例。

> `MethodDelegation`会检查注解以调整它的绑定逻辑。这些注解对于 Byte Buddy 是确定的， 但这并不意味着带注解的类以任何形式依赖 Byte Buddy。相反，Java 运行时只是忽略当加载类时在类路径找不到的注解类型。 这意味着在动态类创建后不再需要 Byte Buddy，同时意味着，即使 Byte Buddy 没有在类路径上，你也可以在另一个JVM进程中加载动态类和委托其方法调用的类。

> 这里有几个预定义的注解可以和我们只想简要命名的`MethodDelegation`一起使用。如果你想要阅读更多关于这些注解的信息， 你可以在代码内的文档中找到更多的信息。这些注解是：
>
> - `@Empty`：应用此注解，Byte Buddy会注入参数(parameter)类型的默认值。对于基本类型，这相当于零值，对于引用类型， 值为`null`。使用该注解是为了避免拦截器的参数。
> - `@StubValue`：使用此注解，注解的参数将注入拦截方法的存根值。对于reference-return-types(返回引用类型)和`void`的方法， 会注入`null`。对于返回基本类型的方法，会注入相等的`0`的包装类型。 当使用`@RuntimeType`注解定义一个返回`Object`类型的通用拦截器时，结合使用可能会非常有用。通过返回注入的值， 该方法在合适地被视为基本返回类型时充当从根。
> - `@FieldValue`：此注解在检测类的类层次结构中定位一个字段并且将字段值注入到注解的参数中。如果没有找到注解参数兼容的可见字段， 则目标方法不会被绑定。
> - `@FieldProxy`：使用此注解，Byte Buddy 会为给定字段注入一个accessor(访问器)。如果拦截的方法表示此类方法， 被访问的字段可以通过名称显式地指定，也可以从getter或setter方法名称派生。在这个注解被使用之前，需要显式地安装和注册，类似于`@Pipe`注解。
> - `@Morph`：这个注解的工作方式与`@SuperCall`注解非常相似。然而，使用这个注解允许指定用于调用超类方法参数。注意， 仅当你需要调用具有与原始调用不同参数的超类方法时，才应该使用此注解，因为使用`@Morph`注解需要对所有参数装箱和拆箱。如果过你想调用一个特定的超类方法， 请考虑使用`@Super`注解来创建类型安全的代理。在这个注解被使用之前，需要显式地安装和注册，类似于`@Pipe`注解。
> - `@SuperMethod`：此注解只能用于可从`Method`分配的参数类型。分配的方法被设置为允许原始代码调用的综合的访问器方法。 注意，使用此注解会导致为代理类创建一个公共访问器，该代理类允许不通过security manager(安全管理器)在外部调用超类方法。
> - `@DefaultMethod`：`@SuperMethod`，但用于默认方法调用。如果默认方法调用只有一种可能性， 则该默认方法在唯一类型上被调用。否则，可以将类型显式地指定为注解属性。

除了使用预定义的注解，Byte Buddy还允许通过注册一个或几个`ParameterBinder`来定义自己的注解。



### 调用超类方法

`ConstructorStrategy(构造器策略)`负责为任何给定的类创建一组预定义的构造器。

```java
/** 复制直接父类的构造器（默认使用） */
ConstructorStrategy.Default.IMITATE_SUPER_TYPE
/** 不创建任何构造器 */
ConstructorStrategy.Default.NO_CONSTRUCTORS
/** 只创建默认构造器 */
ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR
```

下面两段代码效果完全一致：

```java
new ByteBuddy()
  .subclass(Object.class)
  .make()
  
new ByteBuddy()
  .subclass(Object.class, ConstructorStrategy.Default.IMITATE_SUPER_TYPE)
  .make()  
```

对于类变基和重定义，构造器当然只是简单地保留，这使得`ConstructorStrategy`的规范过时了。相反，对于复制这些保留的构造器(和方法)的实现， 需要指定一个`ClassFileLocator(类文件定位器)`，它允许查找包含了这些构造器定义的源类。Byte Buddy 会尽最大努力识别源类文件的位置， 例如，通过查询对应的`ClassLoader`或者通过查看应用的类路径。然而，当处理自定义的类加载器时，查看可能仍然会失败。然后， 就要提供一个自定义`ClassFileLocator`。

### 调用默认方法

创建一个动态类实现若干个接口，这些接口中有可能存在默认接口方法的方法签名冲突，这时默认方法的调用变得不明确。相应地，Byte Buddy的`DefaultMethodCall`实现采用了优先接口列表，当拦截到方法时，会采用列表上第一个提到的接口的默认方法。

```java
interface First {
  default String qux() { return "FOO"; }
}
 
interface Second {
  default String qux() { return "BAR"; }
}

Class<?> dynamicClass = new ByteBuddy(ClassFileVersion.JAVA_V8)
        .subclass(Object.class)
        .implement(First.class)
        .implement(Second.class)
        .method(named("qux"))
  			.intercept(DefaultMethodCall.prioritize(First.class))
        .make()
        .load(ClassLoader.getSystemClassLoader())
        .getLoaded();
Object ret = dynamicClass.getDeclaredMethod("qux")
        .invoke(dynamicClass.newInstance());
Assert.assertEquals("FOO", ret);
```

> 注意，Java8 之前定义在类文件中的任何 Java 类都不支持默认方法。此外，你应该意识到相比于 Java 编程语言，Byte Buddy 强制弱化对默认方法可调用性的需求。 Byte Buddy 只需要有类型继承结构中最具体的类来实现默认方法的接口。除了 Java 编程语言，它不要求这个接口是任何超类实现的最具体的接口。最后， 如果你不想期望一个不明确的默认方法定义，你可以每次都使用`DefaultMethodCall.unambiguousOnly()`用于接收在发现不明确的默认方法调用时抛出异常的实现。 通过优先化`DefaultMethodCall`显示相同的行为，其中，在没有优先化的接口中调用默认方法是不明确的，并且没有找到优先化的接口来定义具有兼容的签名的方法。

### 调用特定方法

在一些场景中，上面的`Implementation`不能满足实现更多自定义的行为。例如，有人可能想实现一个有显式行为的自定义类。例如， 我们或许想要实现下面的 Java 类，它有一个和超类构造器参数不同的构造器：

```java
public class SampleClass {
  public SampleClass(int unusedValue) { super(); }
}
```

解法：指定调用超类构造器来定义子类构造器

```java
new ByteBuddy()
  .subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
  .defineConstructor(Visibility.PUBLIC)
  .withParameter(Integer.TYPE)
  .intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()));
```

`MethodCall`实现传递参数时也可以被使用。这些参数要么作为值显式的传递，要么作为需要手动设置的实例字段的值或者作为给定的参数值。 此外，这个实现允许在被检测的实例之外的其他实例上调用方法。此外，它允许新实例的创建从拦截方法返回。`MethodCall`的类文档提供了这些功能的详情。

### 访问字段

用`FieldAccessor`字段访问器，可以把getter、setter方法的实现绑定到对应变量上。

为了与这个实现兼容，方法必须：

- 有一个类似于`void setBar(Foo f)`的签名用来定义字段的setter。作为[Java bean specification(Java Bean规范) ](https://www.oracle.com/java/technologies/javase/javabeans-spec.html)中的惯例，通常这个setter将访问名为`bar`的字段。在此上下文中，参数类型Foo必须是这个字段类型的子类。
- 有一个类似于`Foo getBar()`的签名来定义字段的 getter。作为 Java Bean 规范中的惯例，通常这个 getter 将访问名为`bar`的字段。为此，方法返回的类型`Foo`必须是字段类型的超类。

使用方式：调用`FieldAccessor.ofBeanProperty()`。

- 如果不期望自动根据方法名称绑定字段，也可以选择通过`FiledAccessor.ofField(String)`来显式指定字段名称。
- 如果需要，允许程序员在这个字段不存在的情况下定义一个新字段来用于实现getter、setter方法。
- 当访问一个现有字段，可以通过`in`方法来指定定义的字段类型。
- 在Java中，允许在类继承结构的多个类定义同一个字段，父类的字段将被子类隐藏。如果没有显式的字段类定位，Byte Buddy将从最具体的类开始遍历类继承结构，直到遇见满足条件的第一个字段。

下面是一个使用示例，我们将创造一个工厂对象`InstanceCreator`，这个工厂对象用于创建可以动态替换`doSomething()`方法实现的`UserType`子类。

```java
class UserType {
  public String doSomething() { return null; }
}
 
interface Interceptor {
  String doSomethingElse();
}
 
interface InterceptionAccessor {
  Interceptor getInterceptor();
  void setInterceptor(Interceptor interceptor);
}
 
interface InstanceCreator {
  Object makeInstance();
}

Class<? extends UserType> dynamicUserType = new ByteBuddy()
        .subclass(UserType.class)
        .method(not(isDeclaredBy(Object.class)))
        .intercept(MethodDelegation.toField("interceptor"))
        .defineField("interceptor", Interceptor.class, Visibility.PRIVATE)
        .implement(InterceptionAccessor.class)
        .intercept(FieldAccessor.ofBeanProperty())
        .make()
        .load(getClass().getClassLoader())
        .getLoaded();

InstanceCreator factory = new ByteBuddy()
        .subclass(InstanceCreator.class)
        .method(not(isDeclaredBy(Object.class)))
        .intercept(MethodDelegation.toConstructor(dynamicUserType))
        .make()
        .load(dynamicUserType.getClassLoader())
        .getLoaded()
        .newInstance();

class HelloWorldInterceptor implements Interceptor {
    @Override
    public String doSomethingElse() {
        return "Hello World!";
    }
}

UserType userType = (UserType) factory.makeInstance();
((InterceptionAccessor) userType).setInterceptor(new HelloWorldInterceptor());

Assert.assertEquals("Hello World!", userType.doSomething());
```

### 杂项

除了目前我们讨论过的`Implementation`外，Byte Buddy还包含其他的实现：

- `StubMethod`实现了一个方法，只需返回方法返回类型的默认值，而无需任何进一步的操作。这样，一个方法的调用可以被静默地抑制。 例如，这种方式可以实现模拟(mock)类型。任何基本类型的默认值分别为零或者零字符。返回引用类型的方法将返回`null`作为默认值。
- `ExceptionMethod`能用来实现一个只抛出异常的方法。如前所述，可以从任何方法抛出已检查异常，即使这个方法没有声明这个异常。
- `Forwarding`实现允许简单地将方法调用转发到另一个与拦截方法的声明类型相同类型的实例。用`MethodDelegation`可以达到相同的效果。 然而，通过`Forwarding`，应用更简单的委托模型，该模型可以覆盖不需要目标方法发现的用例。
- `InvocationHandlerAdapter`允许使用 Java 类库自带的[代理类](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Proxy.html)中现有的`InvocationHandler`。
- `InvokeDynamic`实现允许用[bootstrap方法](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/package-summary.html)运行时动态绑定一个方法，这个方法从 Java7 可以访问。

## 注解

### annotationType

目前为止，Byte Buddy 不是唯一一个基于注解 API 的 Java 应用。 为了在这样的应用中集成动态创建的类型，Byte Buddy 允许为它创建的类和成员定义注解。

来看一个例子：

```java
@Retention(RetentionPolicy.RUNTIME)
@interface RuntimeDefinition { }
 
class RuntimeDefinitionImpl implements RuntimeDefinition {
  @Override
  public Class<? extends Annotation> annotationType() {
    return RuntimeDefinition.class;
  }
}
 
new ByteBuddy()
  .subclass(Object.class)
  .annotateType(new RuntimeDefinitionImpl())
  .make();
```

就像 Java 的`@interface`关键字所暗示的，注解在内部代表的是接口类型。因此，注解可以像普通的接口一样被实现。 与实现接口的唯一不同就是注解的隐式`annotationType`方法，它决定类表示的注解类型。这一种方法通常返回实现的注解类型的类字面量。 除此之外，任何注解属性会被实现，就像是一个接口方法一样。注意，一个注解的默认值需要通过注解方法的实现被重复。

定义方法和字段的注解类似于上面的类型注解。

```java
new ByteBuddy()
  .subclass(Object.class)
    .annotateType(new RuntimeDefinitionImpl())
  .method(named("toString"))
    .intercept(SuperMethodCall.INSTANCE)
    .annotateMethod(new RuntimeDefinitionImpl())
  .defineField("foo", Object.class)
    .annotateField(new RuntimeDefinitionImpl())
```

### 横切关注点

当一个类应该作为另一个类的子类代理时，为动态创建的类定义注解将非常重要。子类代理通常被用于实现[cross-cutting concerns(横切关注点)](https://link.juejin.cn?target=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCross-cutting_concern)，其中子类应该尽可能透明地模仿源类。然而，一个类上的注解不会为它的子类保留， 除非这个注解被[`@Inherited`](https://link.juejin.cn?target=http%3A%2F%2Fdocs.oracle.com%2Fjavase%2F8%2Fdocs%2Fapi%2Fjava%2Flang%2Fannotation%2FInherited.html)注解修饰。

> 在Spring Boot中，使用Spring提供javaAop或Cglib对Controller Bean进行AOP，会导致Bean的注解丢失。带来的影响是：当RequestMappingHandlerMapping扫描Handler时，会丢失@RequestMapping等注解信息，导致接口映射失败。
>
> 但是，Spring在ClassUtils中提供了一个分支判断，如果Bean的名称中包含特定字符串（"$$"），则会被认定为是Cglib代理的Bean，对此，Bean的注解信息都将从其父类中提取。
>
> 个人认为这不是一个好的设计。

默认情况下，`ByteBuddy`配置对于动态创建的类或类成员不会预定义任何注解。但是，可以通过提供一个默认的`TypeAttributeAppender`， `MethodAttributeAppender`或者`FieldAttributeAppender`来改变这个行为。注意，这样的默认追加器不是附加的，而是替换它们之前的值。

有时，在定义一个类时，最好不要加载注解类型或其它任何属性的类型。为此，可以使用`AnnotationDescription.Builder`提供的流式接口来定义注解而不触发类的加载， 但这是以类型安全为代价的。然而，所有注解属性都是在运行时评估的。

默认情况下，Byte Buddy 将注解的任何属性都包含到类文件里，包括通过`default`值隐式指定的默认属性。但是， 可以通过为`ByteBuddy`实例提供一个`AnnotationFilter`来自定义这个行为。

### 类型注解TYPE_USE

> Byte Buddy 暴露并写入类型注解，因为它们是作为 Java 8的一部分被引入的。类型注解作为被声明的注解可以通过任何`TypeDescription.Generic`实例被访问。 如果一个类型注解应该被加到一个泛型类型的字段或方法上，这个被注解的类就可以用`TypeDescription.Generic.Builder`生成。

### 属性追加器

Java 类文件可以包含任何自定义信息作为所谓的属性（注解元数据）。对于一个类，字段或方法，可以用 Byte Buddy 提供的`AttributeAppender`来追加属性。属性追加器也可以用于 基于通过拦截的类型，字段或方法提供的信息来定义方法。例如，当覆写子类中的方法时，可以复制拦截方法的所有注解：

```java
class AnnotatedMethod {  
  @SomeAnnotation void bar() { }
}

new ByteBuddy()
  .subclass(AnnotatedMethod.class)
  .method(ElementMatchers.named("bar"))
  .intercept(StubMethod.INSTANCE)
  .attribute(MethodAttributeAppender.ForInstrumentedMethod.INSTANCE)
```

上面的代码覆写`AnnotatedMethod`类的`bar`方法，同时也复制了被覆盖方法的所有注解，包含参数或类型上的注解。

当一个类被重定义或变基时，相同的规则可能不适用。默认情况下，`ByteBuddy`被配置为保留变基或重定义方法的任何注解，即使这个方法像上面一样被拦截。 但是，可以通过设置`AnnotationRetention`策略为`DISABLED`，来改变这个行为，以便丢弃任何预先存在的注解。

## 自定义扩展

### 创建自定义方法体

前面介绍的都是Byte Buddy的标准API，在这一节介绍Byte Buddy对ASM的封装，需要阅读者对字节码知识有一定了解。

任何 Java 类都由下面几部分组成。核心的部分大致可以分为如下几类：

- **基础数据**：一个类文件引用类名及超类名和它实现的接口。另外类文件引用不同的元数据，比如类的 Java 版本号、注解或者编译器为创建类文件而处理的原文件名。
- **常量池**：类常量池是由一些值组成的集合，这些值被类的成员或注解所引用。在这些值中，常量池存储如基本类型的值和类的源代码中由一些字面量表达式创建的字符串。
- **字段列表**：Java 类文件中包含了在这个类中声明的所有字段的一个列表。除了字段的类型、名称和修饰符，类文件还保存了每个字段的注解。
- **方法列表**：类似于字段列表，Java 类文件包含一个所有声明方法的列表。除了字段之外，非抽象方法还由描述主体方法的字节编码指令数组描述。 这些指令代表所谓的 Java 字节码。

下面以一个计算`10+50`方法为例，首先我们来看怎么用字节码指令实现

```java
LDC     10  // stack contains 10
LDC     50  // stack contains 10, 50
IADD        // stack contains 60
IRETURN     // stack is empty
```

假定`10`在常量池的索引为`1`，`50`在常量池的索引为`2`，那么这个方法的字节码表示为

```java
12 00 01
12 00 02
60
AC
```

用Byte Buddy，我们可以为`IADD`指令实现一个`StackManipulation`：

```java
public enum IntegerSum implements StackManipulation {

    INSTANCE; // singleton

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor,
                      Implementation.Context implementationContext) {
        methodVisitor.visitInsn(Opcodes.IADD);	// 使用IADD指令
      	// -1 代表操作对栈内元素的影响：弹出栈顶两个元素相加并把结果存入栈顶，栈深度-1
      	// 0 代表操作对栈最大深度的影响：IADD不会影响最大栈深度，所以为0
        return new Size(-1, 0);
    }
}
```

当java代码编译后，操作数栈的深度就被固定下来类。返回的Size对象用于BytebBuddy计算出方法所需要的操作数栈深度。

任何把指令进行组合，实现这个方法

```java
public enum SumMethod implements ByteCodeAppender {

    INSTANCE; // singleton

    @Override
    public Size apply(MethodVisitor methodVisitor,
                      Implementation.Context implementationContext,
                      MethodDescription instrumentedMethod) {
        if (!instrumentedMethod.getReturnType().asErasure().represents(int.class)) {
            throw new IllegalArgumentException(instrumentedMethod + " must return int");
        }
        StackManipulation.Size operandStackSize = new StackManipulation.Compound(
                IntegerConstant.forValue(10),	// stack push 10
                IntegerConstant.forValue(50),	// stack push 50
                IntegerSum.INSTANCE,					// pop 10,50;add 10,50;push 60
                MethodReturn.INTEGER					// return top of stack
        ).apply(methodVisitor, implementationContext);
      	// 方法操作数栈深度：取operandStackSize的操作数栈最大深度
      	// 方法局部变量表大小：由于这个方法没有涉及自定义的局部变量，所以只取决于方法入参、this对象引用
        return new Size(operandStackSize.getMaximalSize(),
                instrumentedMethod.getStackSize());
    }
}
```

现在为这个方法提供一个自定义`Implementation(实现)`

```java
public enum SumImplementation implements Implementation {

    INSTANCE; // singleton

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        // 可以对需要增强的类型进行修改或增加新的元素，比如添加字段、方法或接口等
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        // 负责将字节码附加到方法中
        return SumMethod.INSTANCE;
    }
}
```

注意，Byte Buddy 在任何类的一次创建过程中，无论一个`Implementation`实例使用了多少次，每个实例的`prepare`和`appender`方法只调用一次。这样可以避免`Implementation`为了幂等而重复检验字段、方法是否已经定义。

让我们实际创建一个带有自定义方法的类：

```java
abstract class SumExample {
  public abstract int calculate();
}

int ret = new ByteBuddy()
        .subclass(SumExample.class)
        .method(ElementMatchers.named("calculate"))
        .intercept(SumImplementation.INSTANCE)
        .make()
        .load(getClass().getClassLoader())
        .getLoaded()
        .newInstance()
        .calculate();
Assert.assertSame(60, ret);
```

在我们深入定制 Byte Buddy 组件之前，有必要对 Java 跳转指令及其相关的栈帧概念进行简要回顾。自 Java 6 起，为了优化 JVM 的验证过程，所有的跳转指令——这些指令通常用于构建如 `if` 或 `while` 等控制流语句——均要求包含附加信息，即*stack map frame(栈映射帧)* 。一个栈映射帧携带了跳转目标位置当前执行栈上所有值的类型信息。有了这些信息，JVM 验证器就可以减少我们编写字节码时的复杂性。然而，为复杂的跳转逻辑提供精确的栈映射帧仍是一个挑战，一些代码生成库在创建正确的栈映射帧时会遇到难题。

那我们该如何处理这一问题呢？实际上，Byte Buddy 选择了回避的策略。Byte Buddy 遵循的原则是：代码生成应当主要用于在编译时未知的类型层次结构与为这些类型注入的自定义代码之间的桥梁。因此，它生成的代码量尽可能少。只要可行，条件逻辑都应当使用你所熟悉的 JVM 语言进行实现和编译，随后再将其以最简洁的方式绑定到相应方法上。这一做法带来的额外好处是，Byte Buddy 的使用者可以编写标准 Java 代码，并利用他们所熟悉的工具，如调试器和集成开发环境的代码导航器。如果没有源代码形式的生成代码，这些优势将无法实现。但是，如果你确实需要使用跳转指令直接创建字节码，请确保借助 ASM 库手动添加正确的栈映射帧，因为 Byte Buddy 不会自动生成它们。

### 创建自定义分配器

为了展示自定义赋值器的应用，我们接下来将实现一个特殊的赋值器。这个赋值器将负责处理任何接收到的值，通过调用该值的 `toString` 方法，只将其赋值给类型为 `String` 的变量。

```java
public enum ToStringAssigner implements Assigner {

    INSTANCE; // singleton

    @Override
    public StackManipulation assign(TypeDescription.Generic source,
                                    TypeDescription.Generic target,
                                    Assigner.Typing typing) {
        if (!source.isPrimitive() && !source.isArray() && target.represents(String.class)) {
            MethodDescription toStringMethod = new TypeDescription.ForLoadedType(Object.class)
                    .getDeclaredMethods()
                    .filter(ElementMatchers.named("toString"))
                    .getOnly();
            return MethodInvocation.invoke(toStringMethod).virtual(source.asErasure());
        } else {
            return StackManipulation.Illegal.INSTANCE;
        }
    }
}
```

我们可以将这个自定义的`Assigner`和比如`FixedValue`实现集成，如下：

```java
String string = new ByteBuddy()
        .subclass(Object.class)
        .method(ElementMatchers.named("toString"))
        .intercept(FixedValue.value(42)
                .withAssigner(new PrimitiveTypeAwareAssigner(ToStringAssigner.INSTANCE),
                        Assigner.Typing.STATIC))
        .make()
        .load(getClass().getClassLoader())
        .getLoaded()
        .newInstance()
        .toString();
Assert.assertEquals("42", string);
ToStringAssigner`会把属于`Integer`类型的42通过`toString()`方法转为`String`的值`"42"
```

### 创建自定义参数绑定器 

定义一个注解，目的是简单地向注解的参数中注入一个固定的字符串。首先，我们定义一个`StringValue`注解：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface StringValue {
    String value();
}
```

用我们自定义的注解，需要创建一个对应的`ParameterBinder`，它能够创建一个表示此参数绑定的`StackManipulation`。 每次调用这个参数绑定器， 它的对应注解在参数上通过`MethodDelegation`会被发现。为我们的实例注解实现一个自定义参数绑定器很简单：

```java
public enum StringValueBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<StringValue> {

    INSTANCE; // singleton

    @Override
    public Class<StringValue> getHandledType() {
        return StringValue.class;
    }

    @Override
    public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<StringValue> annotation,
                                                           MethodDescription source,
                                                           ParameterDescription target,
                                                           Implementation.Target implementationTarget,
                                                           Assigner assigner,
                                                           Assigner.Typing typing) {
        if (!target.getType().asErasure().represents(String.class)) {
            throw new IllegalStateException(target + " makes illegal use of @StringValue");
        }
        StackManipulation constant = new TextConstant(annotation.load().value());
        return new MethodDelegationBinder.ParameterBinding.Anonymous(constant);
    }
}
```

我们的参数绑定器首先验证`target(目标)`参数是否确实为`String`类型；如果不是，将抛出异常，提醒用户注解放置不当。 接下来，我们构造一个`TextConstant`实例，该实例负责将字符串常量从常量池推送到执行栈上。 随后，`StackManipulation`策略被封装成一个匿名`ParameterBinding(参数绑定)`实例，以便能够从相应方法返回所需信息。

> 您可以指定一个`Unique(唯一的)`或`Illegal(非法的)`参数绑定来增强绑定精准度。唯一绑定的优势在于，它允许通过`AmbiguityResolver(不明确解析器)`识别出绑定的特定对象，确保其唯一性。这种解析器在处理步骤中会验证参数绑定是否已按照唯一标识进行注册，从而可以确定该绑定是否比其他成功的绑定方案更为适合。而通过设定`Illegal(非法的)`绑定，您可以明确告知 Byte Buddy 框架，某一对`source`和`target`方法相互不兼容，避免将它们错误地绑定至一起。

这些步骤构成了与`MethodDelegation`结合使用自定义注解所必需的全部流程。一旦接收到`ParameterBinding`，系统将确认其值是否正确绑定到了预期参数；如果不正确，当前的`source`和`target`方法配对将被视为不可绑定并被排除。同时，此机制也会启用`AmbiguityResolver`来辨别是否存在唯一的有效绑定。

最终让我们把这个自定义注解付诸实践：

```java
public class ToStringInterceptor {
    public static String makeString(@StringValue("Hello!") String value) {
        return value;
    }
}
String string = new ByteBuddy()
        .subclass(Object.class)
        .method(ElementMatchers.named("toString"))
        .intercept(MethodDelegation.withDefaultConfiguration()
                .withBinders(StringValueBinder.INSTANCE)
                .to(ToStringInterceptor.class))
        .make()
        .load(getClass().getClassLoader())
        .getLoaded()
        .newInstance()
        .toString();
Assert.assertEquals("Hello!", string);
```

在`ToStringInterceptor`拦截器中，`toString`方法是唯一可能被动态类拦截的目标方法，并且其调用将与后续方法的调用进行绑定。在目标方法被激活时，Byte Buddy 会自动将注解中指定的字符串值分配给目标方法的唯一参数。





Intellij Plugin构建
http://www.ideaplugin.com/idea-docs/Part%20I%20%E6%8F%92%E4%BB%B6/%E7%AC%AC%E4%B8%80%E4%B8%AA%E6%8F%92%E4%BB%B6/Using%20GitHub%20Template.html
https://github.com/JetBrains/intellij-platform-plugin-template

https://juejin.cn/post/6844904127990857742







# 参考文档

1. [BytBuddy完整入门学习](https://juejin.cn/post/7394790053140529187)
1. [Java—JavaAgent探针](http://www.enmalvi.com/2022/05/16/java-javaagent/#Java_Agent_shi_xian_yuan_li)

