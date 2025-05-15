package com.ethan.cache.redis.support;

import com.ethan.common.utils.json.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Deprecated
public interface KeyGenerator {
  ExpressionParser PARSER = new SpelExpressionParser();

  ThreadLocal<EvaluationContext> THREAD_LOCAL = new ThreadLocal<EvaluationContext>() {
    @Override
    protected EvaluationContext initialValue() {
      return new StandardEvaluationContext();
    }
  };

  /**
   * @author piaoruiqing
   *
   * @param joinPoint
   * @param prefix
   * @param argNames
   * @param argsAssociated
   * @return
   * @throws JsonProcessingException
   */
  default StringBuilder generate(ProceedingJoinPoint joinPoint, String prefix, String[] argNames, boolean argsAssociated) throws JsonProcessingException {

    MethodSignature signature = (MethodSignature)joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    StringBuilder builder = new StringBuilder();
    if (StringUtils.isBlank(prefix)) {
      builder = builder.append(joinPoint.getTarget().getClass().getName()).append(":").append(signature.getName());
    } else {
      builder = builder.append(prefix);
    }
    String[] parameterNames = signature.getParameterNames();
    if (!argsAssociated || parameterNames.length <= 0) {
      return builder;
    }
    String[] names;
    Object[] values;
    // argsNames为空时默认为全部参数
    if (null == argNames || argNames.length <= 0) {
      names = parameterNames;
      values = args;
    } else {
      Map<String, Object> argMap = new HashMap<>(parameterNames.length);
      for (int index = 0 ; index < parameterNames.length ; index++) {
        argMap.put(parameterNames[index], args[index]);
      }
      names = new String[argNames.length];
      values = new Object[argNames.length];
      for (int index = 0 ; index < argNames.length ; index++) {
        String[] expression = StringUtils.split(argNames[index], '.');
        names[index] = expression[expression.length - 1];
        String argName = expression[0];
        Object arg = argMap.get(argName);
        if (null == arg || expression.length == 1) {
          values[index] = arg;
          continue ;
        }
        // TODO
        EvaluationContext context = THREAD_LOCAL.get();
        context.setVariable(argName, arg);
        values[index] = PARSER.parseExpression("#" + argNames[index]).getValue(context);
      }
      THREAD_LOCAL.remove();
    }
    return builder.append(":").append(simpleJoinToBuilder(names, values, "=", "|"));
  }

  default StringBuilder simpleJoinToBuilder(String[] argNames, Object[] args, String separatorKV,
                                            String separator) {
    if (argNames == null || args == null) {
      return null;
    }
    if (argNames.length != args.length) {
      throw new IllegalArgumentException("inconsistent parameter length !");
    }
    if (argNames.length <= 0) {
      return new StringBuilder(0);
    }
    int bufSize = argNames.length * (argNames[0].toString().length()
            + Optional.ofNullable(args[0]).map(String::valueOf).map(String::length).orElse(4) + 2);
    StringBuilder builder = new StringBuilder(bufSize);
    for (int index = 0; index < argNames.length; index++) {
      if (index > 0) {
        builder.append(separator);
      }
      appendObject(builder, argNames[index], separatorKV, args[index]);
    }

    return builder;
  }

  default StringBuilder appendObject(StringBuilder builder, Object... object) {

    for (Object item : object) {
      if (item instanceof Number || item instanceof String || item instanceof Boolean
              || item instanceof Character) {
        builder.append(item);
      } else {
        builder.append(JacksonUtils.toJsonString(item));
      }
    }
    return builder;
  }

}
