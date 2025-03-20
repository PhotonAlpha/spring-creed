# 日常杂项问题

## 1. Spring Boot Logback 加载[问题](https://stackoverflow.com/questions/43937031/spring-boot-ignoring-logback-spring-xml) 及源码

- [使用TransmittableThreadLocal实现异步场景日志链路追踪](https://juejin.cn/post/6981831233911128072#heading-5)

## 2. Spring Boot Active profiles 打印

 `11:24:41.885 [main] INFO  com.ethan.creed.MyApplication - The following 5 profiles are active:xxxx` 

org.springframework.boot.SpringApplication#logStartupProfileInfo

```java
--1.--
protected void logStartupProfileInfo(ConfigurableApplicationContext context) {
		Log log = getApplicationLog(); //1.获取日志类型
		if (log.isInfoEnabled()) {
			List<String> activeProfiles = quoteProfiles(context.getEnvironment().getActiveProfiles());
			if (ObjectUtils.isEmpty(activeProfiles)) {
				List<String> defaultProfiles = quoteProfiles(context.getEnvironment().getDefaultProfiles());
				String message = String.format("%s default %s: ", defaultProfiles.size(),
						(defaultProfiles.size() <= 1) ? "profile" : "profiles");
				log.info("No active profile set, falling back to " + message
						+ StringUtils.collectionToDelimitedString(defaultProfiles, ", "));
			}
			else {
				String message = (activeProfiles.size() == 1) ? "1 profile is active: "
						: activeProfiles.size() + " profiles are active: ";
				log.info("The following " + message + StringUtils.collectionToDelimitedString(activeProfiles, ", "));
			}
		}
	}
--2.--
protected Log getApplicationLog() {
		if (this.mainApplicationClass == null) {
			return logger;
		}
		return LogFactory.getLog(this.mainApplicationClass);//2. 寻找适配的log factory
}
--3.--
  final class LogAdapter {
    private static final boolean log4jSpiPresent = isPresent("org.apache.logging.log4j.spi.ExtendedLogger");
    private static final boolean log4jSlf4jProviderPresent = isPresent("org.apache.logging.slf4j.SLF4JProvider");
    private static final boolean slf4jSpiPresent = isPresent("org.slf4j.spi.LocationAwareLogger");
    private static final boolean slf4jApiPresent = isPresent("org.slf4j.Logger");
    private static final Function<String, Log> createLog;

    private LogAdapter() {
    }

    public static Log createLog(String name) {
        return (Log)createLog.apply(name);
    }
  }
--4.--
 //比如如果发现apache.log4j,添加下面的配置就可以打印启动日志
org.apache.logging.log4j.level=info 
```



## 3. TODO