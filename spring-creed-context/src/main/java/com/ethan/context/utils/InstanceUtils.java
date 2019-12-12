package com.ethan.context.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class InstanceUtils {
  public static ObjectMapper getMapperInstance() {
    return ObjectMapperInstance.INSTANCE.getObjectMapper();
  }

  enum ObjectMapperInstance {
    INSTANCE;
    private ObjectMapper objectMapper;
    ObjectMapperInstance() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      // Don’t include null values
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      this.objectMapper = mapper;
    }

    public ObjectMapper getObjectMapper() {
      return objectMapper;
    }
  }
}
