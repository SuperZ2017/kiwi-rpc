package com.kiwi.client.interceptor;


import com.kiwi.client.invoke.Invocation;

public interface Interceptor {


    void beforeInvoke(Invocation invocation);


    Object intercept(Invocation invocation) throws InterruptedException;


    void afterInvoke(Invocation invocation);
}
