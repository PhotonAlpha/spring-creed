package com.ethan.cache.redis.expression;

import org.springframework.expression.EvaluationException;

class VariableNotAvailableException extends EvaluationException {

  private final String name;

  public VariableNotAvailableException(String name) {
    super("Variable '" + name + "' is not available");
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
