package com.kiwi.client.invoke;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceIdGenerator {

    public static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
