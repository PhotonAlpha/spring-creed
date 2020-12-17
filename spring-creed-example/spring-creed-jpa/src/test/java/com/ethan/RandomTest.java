package com.ethan;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class RandomTest {
  @Test
  void name() {

    Random ran = new Random();
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
    System.out.println(ran.nextInt(3));
  }
}
