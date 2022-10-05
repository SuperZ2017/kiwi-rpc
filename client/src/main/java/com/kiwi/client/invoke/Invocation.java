package com.kiwi.client.invoke;

import com.kiwi.client.discovery.ZookeeperServiceDiscovery;
import com.kiwi.client.interceptor.Impl.LogInterceptor;
import com.kiwi.client.interceptor.Interceptor;
import com.kiwi.client.loadBalance.LoadBalance;
import com.kiwi.client.protocol.MessageCodecSharable;
import com.kiwi.client.protocol.ProtocolFrameDecoder;
import com.kiwi.client.rpc.RpcFuture;
import com.kiwi.client.rpc.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequestMessage;
import model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Invocation {

    private List<Interceptor> interceptors = new ArrayList<>();

    private int index = 0;

    private RpcRequestMessage requestMessage;

    private long timeout;

    private String loadBalanceName;


    public String getMethodName() {
        return this.requestMessage.getMethodName();
    }

    public Invocation(RpcRequestMessage requestMessage, long timeout, String loadBalanceName) {
        this.requestMessage = requestMessage;
        this.loadBalanceName = loadBalanceName;
        this.timeout = timeout;
        this.interceptors.add(new LogInterceptor());
    }


    public Object invoke() throws InterruptedException {
        if (index == interceptors.size()) {
            return this.sendRequest(this.requestMessage);
        }

        Interceptor interceptor = interceptors.get(index++);
        return interceptor.intercept(this);
    }


    private Object sendRequest(RpcRequestMessage requestMessage) throws InterruptedException {

        LoadBalance loadBalance = getLoadBalance();
        Service service = loadBalance.route(ZookeeperServiceDiscovery.services);

        Channel channel = this.getChannel(service);
        channel.writeAndFlush(requestMessage);

        RpcFuture<Object> rpcFuture = new RpcFuture<>();
        RpcResponseMessageHandler.futureMap.put(requestMessage.getSequenceId(), rpcFuture);

        // 等待 promise 结果
        boolean ready = rpcFuture.await(this.timeout, TimeUnit.SECONDS);
        if (!ready) {
            throw new RuntimeException("请求超时");
        }

        if (rpcFuture.isSuccess()) {
            // 调用正常
            return rpcFuture.getNow();
        } else {
            // 调用异常
            throw new RuntimeException(rpcFuture.cause());
        }

    }

    public Channel getChannel(Service service) {
        Channel channel = null;
        NioEventLoopGroup group = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtocolFrameDecoder())
                                .addLast(loggingHandler)
                                .addLast(messageCodec)
                                .addLast(rpcHandler);
                    }
                });

        try {
            channel = bootstrap.connect(service.getHost(), service.getPort()).sync().channel();

            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("connect exception : ", e);
        }

        return channel;
    }


    public LoadBalance getLoadBalance() {
        ServiceLoader<LoadBalance> loadBalances = ServiceLoader.load(LoadBalance.class);

        log.info("loadBalanceName : {}", loadBalanceName);
        for (LoadBalance loader : loadBalances) {
            annotation.LoadBalance balance = loader.getClass().getAnnotation(annotation.LoadBalance.class);
            log.info("balance : {}", balance.value());
            if (loadBalanceName.equals(balance.value())) {
                return loader;
            }
        }

        throw new RuntimeException("loadBalance no exist");
    }

}
