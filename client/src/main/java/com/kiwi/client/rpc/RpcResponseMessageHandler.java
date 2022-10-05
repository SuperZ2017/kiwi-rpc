package com.kiwi.client.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {


    public static final Map<Integer, RpcFuture<Object>> futureMap = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) {
        log.info("msg : {}", msg);

        // 去掉不需要的
        RpcFuture<Object> future = futureMap.remove(msg.getSequenceId());

        if (future != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();

            if (exceptionValue != null) {
                future.setFailure(exceptionValue);
            } else {
                future.setSuccess(returnValue);
            }
        }

    }
}
