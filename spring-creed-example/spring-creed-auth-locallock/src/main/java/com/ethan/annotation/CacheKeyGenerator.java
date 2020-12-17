package com.ethan.annotation;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@FunctionalInterface
public interface CacheKeyGenerator {
  /**
   * 获取AOP参数,生成指定缓存Key
   *
   * @param pjp PJP
   * @return 缓存KEY
   */
  String getLockKey(ProceedingJoinPoint pjp);

  /**
   * can not be overwrite
   * @return
   */
  static CacheKeyGenerator simple() {
    return (pjp) -> {
      MethodSignature signature = (MethodSignature) pjp.getSignature();
      Method method = signature.getMethod();
      CacheLock lockAnnotation = method.getAnnotation(CacheLock.class);
      final Object[] args = pjp.getArgs();
      final Parameter[] parameters = method.getParameters();
      StringBuilder builder = new StringBuilder();
      // TODO 默认解析方法里面带 CacheParam 注解的属性,如果没有尝试着解析实体对象中的
      for (int i = 0; i < parameters.length; i++) {
        final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
        if (annotation == null) {
          continue;
        }
        builder.append(lockAnnotation.delimiter()).append(args[i]);
      }
      if (StringUtils.isEmpty(builder.toString())) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
          final Object object = args[i];
          final Field[] fields = object.getClass().getDeclaredFields();
          for (Field field : fields) {
            final CacheParam annotation = field.getAnnotation(CacheParam.class);
            if (annotation == null) {
              continue;
            }
            field.setAccessible(true);
            builder.append(lockAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
          }
        }
      }
      return lockAnnotation.prefix() + builder.toString();
    };
  }
}

