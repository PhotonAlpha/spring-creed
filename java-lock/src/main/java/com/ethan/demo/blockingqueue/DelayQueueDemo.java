/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.demo.blockingqueue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/8/2022 3:56 PM
 */
public class DelayQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayTimer> timers = new DelayQueue<>();
        System.out.println("===1");
        timers.put(new DelayTimer("item1", 5 ,TimeUnit.SECONDS));
        timers.put(new DelayTimer("item2", 4 ,TimeUnit.SECONDS));
        timers.put(new DelayTimer("item3", 3 ,TimeUnit.SECONDS));
        System.out.println("===2");
        System.out.println("begin time:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        for (int i = 0; i < 3; i++) {
            DelayTimer take = timers.take();
            System.out.format("name:{%s}, time:{%s}\n",take.name, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }
}

class DelayTimer implements Delayed {
    /* 触发时间*/
    private long time;
    String name;

    public DelayTimer(String name, long time, TimeUnit unit) {
        this.name = name;
        this.time = System.currentTimeMillis() + (time > 0? unit.toMillis(time): 0);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayTimer item = (DelayTimer) o;
        long diff = this.time - item.time;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        }else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "DelayTimer{" +
                "time=" + time +
                ", name='" + name + '\'' +
                '}';
    }
}
