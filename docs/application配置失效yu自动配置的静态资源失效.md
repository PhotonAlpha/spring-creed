# `@EnableWebMvc`使用坑点解析: 导致
- Spring Boot在application文件中的配置失效
- 在Spring Boot的自定义配置类加上@EnableWebMvc后，发现自动配置的静态资源路径（classpath:/META/resources/，classpath:/resources/，classpath:/static/，classpath:/public/）资源无法访问。  

https://blog.csdn.net/zxc123e/article/details/84636521