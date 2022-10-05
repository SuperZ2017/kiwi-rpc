package com.kiwi.client.invoke;

import model.RpcRequestMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokeProxy implements InvocationHandler {

    private long timeout;
    private String loadBalance;


    public InvokeProxy(long timeout, String loadBalance) {
        this.timeout = timeout;
        this.loadBalance = loadBalance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int sequenceId = SequenceIdGenerator.nextId();

        RpcRequestMessage message = new RpcRequestMessage(
                sequenceId,
                method.getDeclaringClass().getName(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes(),
                args
        );

        Invocation invocation = new Invocation(message, this.timeout, this.loadBalance);
        return invocation.invoke();
    }
}
