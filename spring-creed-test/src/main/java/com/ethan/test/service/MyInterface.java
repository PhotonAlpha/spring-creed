package com.ethan.test.service;

public interface MyInterface {
    int MAX_VALUE = 100;

    void test();

    default void doSomething() {
        System.out.println("do something");
        privateMethod();
    }

    static void doNothing() {
        System.out.println("doNothing");
        privateStaticMethod();
    }
    private void privateMethod() {
        System.out.println("privateMethod");
    }
    private static void privateStaticMethod() {
        System.out.println("privateStaticMethod");
    }

}
