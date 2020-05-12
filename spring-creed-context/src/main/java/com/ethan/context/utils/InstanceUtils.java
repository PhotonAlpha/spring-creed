package com.ethan.context.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class InstanceUtils {
  private InstanceUtils() {
  }

  public static ObjectMapper getMapperInstance() {
    return ObjectMapperInstance.INSTANCE.getObjectMapper();
  }
  public static ObjectMapper getNonFinalMapperInstance() {
    return ObjectMapperNonFinalInstance.INSTANCE.getObjectMapper();
  }

  enum ObjectMapperInstance {
    INSTANCE;
    private ObjectMapper objectMapper;
    ObjectMapperInstance() {
      ObjectMapper mapper = newObjectMapper();
      mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      // Don’t include null values
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      this.objectMapper = mapper;
    }

    public ObjectMapper getObjectMapper() {
      return objectMapper;
    }
  }
  enum ObjectMapperNonFinalInstance {
    INSTANCE;
    private ObjectMapper objectMapper;
    ObjectMapperNonFinalInstance() {
      ObjectMapper mapper = newObjectMapper();
      mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      // Don’t include null values
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      this.objectMapper = mapper;
    }

    public ObjectMapper getObjectMapper() {
      return objectMapper;
    }
  }

  private static ObjectMapper newObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }

}
