package com.ethan.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class CommonTestCase {
  @Test
  void testJackson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Student student = new Student("xiao");
    String res = mapper.writeValueAsString(student);
    System.out.println(res);
  }

  @Getter
  static class Student {
    private String name;
    private final String lastName = "ming";

    @JsonCreator
    public Student(String name) {
      this.name = name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Test
  void testUUID() {
  }
}
