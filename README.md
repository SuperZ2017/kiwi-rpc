# kiwi-rpc

使用 SpringBoot、Netty、Zookeeper 实现的一个 rpc demo。

演示：
1. 本地启动 Zookeeper 服务端，默认地址为 127.0.0.1:2181
2. 启动 Zookeeper 客户端，创建路径 /service
3. 启动 server 包中 ServerApplication
4. 启动 client 包中 ClientApplication
5. 调用 client 包中 HelloController，hello 方法

支持的功能有：
1. 序列化：java、json、protostuff
2. 负载均衡，支持 spi
3. 拦截器
4. 超时
5. 记录请求耗时

基本原理
服务端 (Server)：
1. 在 SpringBoot 启动时，使用异步线程启动 Netty 服务端 (RpcServer)，并将 Netty 连接的信息保存到 Zookeeper (ZookeeperServiceRegistry)；
2. 在提供远程调用的类上加上注解 @RpcService，参考 HelloServiceImpl，SpringBoot 在启动过程中，会将该注解标记的类的信息保存到 Map 中，以便被远程调用时找到该类。
3. 当被远程调用时，Netty 中业务相关的处理器主要是 RpcRequestMessageHandler，该类 channelRead0 方法通过反射或 cglib 调用请求的方法，然后将返回值包装返回。

客户端 (Client)：
1. 将 @RpcReference 标记在需要远程调用的类和对象上，可以指定调用超时时间和负载均衡算法；
2. 在 SpringBoot 启动时，会将 Zookeeper 中 /service 路径下的路径全部保存下来，作为需要连接的 Netty 服务端地址。
3. 在 SpringBoot 启动时，会将 @RpcReference 标记的对象替换为动态代理生成的对象 (RpcClient)，后续调用该对象的方法都是调用动态代理生成的对象 invoke 方法。
4. 当调用远程方法时 (HelloController hello())，将会调用动态代理对象的方法，生成 Netty Client 连接到 Netty Server，并发起调用，直到结果返回。

参考的文章：
1. https://my.oschina.net/huangyong/blog/361751
2. https://www.cnblogs.com/itoak/p/13370031.html
