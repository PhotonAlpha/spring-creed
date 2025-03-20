package com.ethan.identity.core.segment.dal.entity;

import lombok.Data;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 双buffer
 */
@Data
public class SegmentBuffer {
    private String key;
    private Segment[] segments; //双buffer
    private volatile int currentPos; //当前的使用的segment的index
    private volatile boolean nextReady; //下一个segment是否处于可切换状态
    private volatile boolean initOk; //是否初始化完成
    private final AtomicBoolean threadRunning; //线程是否在运行中
    private final ReadWriteLock lock;

    private volatile int step;
    private volatile int minStep;
    private volatile long updateTimestamp;

    public SegmentBuffer() {
        segments = new Segment[]{new Segment(this), new Segment(this)};
        currentPos = 0;
        nextReady = false;
        initOk = false;
        threadRunning = new AtomicBoolean(false);
        lock = new ReentrantReadWriteLock();
    }


    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    public Segment getCurrent() {
        return segments[currentPos];
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }
    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }

    @Override
    public String toString() {
        return "SegmentBuffer{" + "key='" + key + '\'' +
                ", segments=" + Arrays.toString(segments) +
                ", currentPos=" + currentPos +
                ", nextReady=" + nextReady +
                ", initOk=" + initOk +
                ", threadRunning=" + threadRunning +
                ", step=" + step +
                ", minStep=" + minStep +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }
}
