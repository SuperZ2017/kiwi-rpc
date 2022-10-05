package com.kiwi.server.registry;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import model.Service;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import registry.Registry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ZookeeperServiceRegistry implements Registry {

    @Value("${zookeeper.address}")
    private String zookeeperAddress;


    @Value("${rpc.server.address}")
    private String rpcServerAddress;


    @Value("${service.name}")
    private String serviceName;


    @Value("${zookeeper.namespace}")
    private String zookeeperNamespace;


    @Value("${zookeeper.path}")
    private String zookeeperPath;

    // zookeeper 客户端
    private CuratorFramework client;

    @Override
    @PostConstruct
    public void registry() {
        log.info("start register rpc service...");

        client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .namespace(zookeeperNamespace)
                .build();

        //开启连接
        client.start();

        Service service = new Service();
        String[] ip = rpcServerAddress.split(":");
        service.setHost(ip[0]);
        service.setPort(Integer.valueOf(ip[1]));
        service.setName(serviceName);

        String meta = new Gson().toJson(service);

        try {
            // 将服务器地址作为目录，创建临时节点
            client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath("/" + meta);
        } catch (Exception e) {
            log.error("创建 zookeeper 节点出错了", e);
        }
    }


    @PreDestroy
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
