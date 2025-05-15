/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan;

import com.ethan.common.TestData;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VarHandleTest {
    private final TestData testData = new TestData();
    @Test
    void plainOrderTester() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                testData.x = finalI;
                System.out.println("X"+Thread.currentThread().getName() + " x:" + testData.x + " y:" + testData.y);
            });
        }
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                testData.y = finalI;
                System.out.println("Y"+Thread.currentThread().getName() + " x:" + testData.x + " y:" + testData.y);
            });
        }
        executorService.shutdown();
    }
    @Test
    void opaqueOrderTester() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println("X"+Thread.currentThread().getName() + " x:" + testData.X.getOpaque(testData) + " y:" + testData.Y.getOpaque(testData));
                testData.X.setOpaque(testData, finalI);
            });
        }
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println("Y"+Thread.currentThread().getName() + " x:" + testData.X.getOpaque(testData) + " y:" + testData.Y.getOpaque(testData));
                testData.Y.setOpaque(testData, finalI);
            });
        }
        executorService.shutdown();
    }
    @Test
    void raOrderTester() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println("X"+Thread.currentThread().getName() + " x:" + testData.X.getAcquire(testData) + " y:" + testData.Y.getAcquire(testData));
                testData.X.setRelease(testData, finalI);
            });
        }
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println("Y"+Thread.currentThread().getName() + " x:" + testData.X.getAcquire(testData) + " y:" + testData.Y.getAcquire(testData));
                testData.Y.setRelease(testData, finalI);
            });
        }
        executorService.shutdown();
    }
}
