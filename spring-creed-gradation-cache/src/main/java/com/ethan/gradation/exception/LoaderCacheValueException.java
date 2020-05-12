package com.ethan.gradation.exception;

public class LoaderCacheValueException extends RuntimeException {
  private final Object key;

  public LoaderCacheValueException(Object key, Throwable ex) {
    super(String.format("The key %s occur some exception", key, ex));
    this.key = key;
  }

  public Object getKey() {
    return this.key;
  }
}
