package com.ethan.test;

import com.ethan.test.service.MyFunctionInterface;
import com.ethan.test.service.MyInterface;
import com.ethan.test.service.MyInterfaceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;

public class MyInterfaceTest {
  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }
    @InjectMocks
    MyInterfaceImpl myInterface;

  @Test
  void testInterFace() {
    myInterface.doSomething();
    MyInterface.doNothing();

    MyFunctionInterface mf = () -> System.out.println(this.getClass().getName());
    mf.test();
  }

  @Test
  void name() {

//    Files.copy()
  }
}
