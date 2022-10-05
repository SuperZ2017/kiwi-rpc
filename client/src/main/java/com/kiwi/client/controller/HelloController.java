package com.kiwi.client.controller;

import annotation.RpcReference;
import facade.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RpcReference
@RestController
@RequestMapping("/hello")
public class HelloController {

    @RpcReference(timeout = 6, loadBalance = "Random")
    public HelloService helloService;

    @GetMapping("1")
    public void hello() throws InterruptedException {
        String name = "张三";
        log.info(helloService.hello(name));
    }

    @GetMapping("2")
    public void hi() {
        String name = "张三";
        log.info(helloService.hi(name));
    }
}
