/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class TestData {
    public int x;
    public int y;
    public static final VarHandle X;
    public static final VarHandle Y;
    static {
        try {
            X = MethodHandles.lookup().findVarHandle(TestData.class, "x", int.class);
            Y = MethodHandles.lookup().findVarHandle(TestData.class, "y", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
