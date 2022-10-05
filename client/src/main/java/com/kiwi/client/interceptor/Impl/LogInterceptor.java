package com.kiwi.client.interceptor.Impl;

import com.kiwi.client.interceptor.Interceptor;
import com.kiwi.client.invoke.Invocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements Interceptor {

    private long startTime;
    private long endTime;

    @Override
    public void beforeInvoke(Invocation invocation) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public Object intercept(Invocation invocation) throws InterruptedException {
        this.beforeInvoke(invocation);
        Object invoke = invocation.invoke();
        this.afterInvoke(invocation);
        return invoke;
    }

    @Override
    public void afterInvoke(Invocation invocation) {
        this.endTime = System.currentTimeMillis();
        log.info("======= call remote method : {}, take {} millis =======", invocation.getMethodName(), endTime - startTime);
    }
}
