package com.dragon.practice.server.service;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceIDGenerator {
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static Integer nextId(){
        return atomicInteger.getAndIncrement();
    }
}
