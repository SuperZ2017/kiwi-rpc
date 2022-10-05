package com.kiwi.server.facade;

import annotation.RpcService;
import facade.HelloService;

import java.util.concurrent.TimeUnit;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        return String.format("hello : %s", name);
    }

    @Override
    public String hi(String name) {
        return String.format("hi : %s", name);
    }
}
