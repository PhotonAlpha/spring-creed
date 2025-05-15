/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common.utils.thread;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class ThreadUtils {
    public boolean sleep(int millis) {
        if (millis > 0L) {
            try {
                TimeUnit.MILLISECONDS.sleep(millis);
            } catch (InterruptedException var3) {
                return false;
            }
        }
        return true;
    }

}
