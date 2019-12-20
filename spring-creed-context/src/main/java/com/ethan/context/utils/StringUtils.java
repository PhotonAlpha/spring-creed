package com.ethan.context.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
  private static final ObjectMapper OBJECT_MAPPER;
  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  /**
   * @author piaoruiqing
   *
   * @param builder
   * @param object
   * @return
   * @throws JsonProcessingException
   */
  public static StringBuilder appendObject(StringBuilder builder, Object... object) throws JsonProcessingException {

    for (Object item : object) {
      if (item instanceof Number || item instanceof String || item instanceof Boolean
          || item instanceof Character) {
        builder.append(item);
      } else {
        builder.append(OBJECT_MAPPER.writeValueAsString(item));
      }
    }
    return builder;
  }

  /**
   * @author piaoruiqing
   *
   * @param argNames
   * @param args
   * @param separatorKV
   * @param separator
   * @return
   * @throws JsonProcessingException
   */
  public static StringBuilder simpleJoinToBuilder(String[] argNames, Object[] args, String separatorKV,
                                                  String separator) throws JsonProcessingException {

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
}
