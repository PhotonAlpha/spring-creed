package com.ethan.e2e;

import com.ethan.e2e.service.LookupSymmetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = E2EApplication.class)
public class E2EApplicationTest {
  @Autowired
  private LookupSymmetric lookupSymmetric;
  @Test
  void testGetSymmetric() {
    String res1 = lookupSymmetric.getSymmetricKeyPair("abc");
    String res2 = lookupSymmetric.getSymmetricKeyPair("abc");
    String res3 = lookupSymmetric.getSymmetricKeyPair("abc");
    String res4 = lookupSymmetric.getSymmetricKeyPair("abc");
    String res5 = lookupSymmetric.getSymmetricKeyPair("abc");
    String res6 = lookupSymmetric.getSymmetricKeyPair("abc");
    System.out.println(res1);
    System.out.println(res2);
    System.out.println(res3);
    System.out.println(res4);
    System.out.println(res5);
    System.out.println(res6);
  }
}
