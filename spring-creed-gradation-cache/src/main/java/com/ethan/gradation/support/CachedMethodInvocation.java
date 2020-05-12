package com.ethan.gradation.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CachedMethodInvocation implements Serializable {

  private static final long serialVersionUID = 7287154504020992151L;

  private Object key;
  private String targetBean;
  private String targetMethod;
  private List<Object> arguments;
  private List<String> parameterTypes = new ArrayList<>();

  public CachedMethodInvocation() {
  }

  public CachedMethodInvocation(Object key, Object targetBean, Method targetMethod, Class<?>[] parameterTypes, Object[] arguments) {
    this.key = key;
    this.targetBean = targetBean.getClass().getName();
    this.targetMethod = targetMethod.getName();
    if (arguments != null && arguments.length != 0) {
      this.arguments = Arrays.asList(arguments);
    }
    if (parameterTypes != null && parameterTypes.length != 0) {
      for (Class<?> clazz : parameterTypes) {
        this.parameterTypes.add(clazz.getName());
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CachedMethodInvocation that = (CachedMethodInvocation) o;

    return key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }
}
