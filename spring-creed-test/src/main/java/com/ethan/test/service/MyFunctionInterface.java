package com.ethan.test.service;

@FunctionalInterface
public interface MyFunctionInterface {
    void test();

    default void doSomething() {
        System.out.println("do MyFunctionInterface");
        privateMethod();
        MyFunctionInterface.privateStaticMethod();
    }

    private void privateMethod() {
        System.out.println("privateMethod");
    }

    private static void privateStaticMethod() {
        System.out.println("privateMethod");
    }
}
