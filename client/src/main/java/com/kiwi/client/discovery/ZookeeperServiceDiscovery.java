package com.kiwi.client.discovery;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import model.Service;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import registry.Discovery;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class ZookeeperServiceDiscovery implements Discovery {

    @Value("${zookeeper.address}")
    private String zookeeperAddress;

    @Value("${zookeeper.namespace}")
    private String zookeeperNamespace;

    public static List<Service> services = new ArrayList<>();

    // zookeeper 客户端
    private CuratorFramework client;


    @PostConstruct
    public void discovery() {
        log.info("start discovery rpc service...");

        client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .namespace(zookeeperNamespace)
                .build();

        //开启连接
        client.start();

        try {
            List<String> path = client.getChildren().forPath("/");
            Gson gson = new Gson();
            path.forEach(meta -> services.add(gson.fromJson(meta, Service.class)));

            log.info("the rpc server info is : {}", services);
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
