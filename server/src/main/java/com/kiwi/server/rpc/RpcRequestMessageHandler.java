package com.kiwi.server.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequestMessage;
import model.RpcResponseMessage;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.Map;


@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private final Map<String, Object> handlerMap;

    public RpcRequestMessageHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage request) {
        log.debug("msg : {}", request);

        AllChannelHandler.channelRead(() -> {
            RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
            rpcResponseMessage.setSequenceId(request.getSequenceId());
            try {
                String interfaceName = request.getInterfaceName();
                Object serviceBean = handlerMap.get(interfaceName);
                Class<?> serviceBeanClass = serviceBean.getClass();
                String methodName = request.getMethodName();
                Class[] parameterTypes = request.getParameterTypes();
                Object[] parameterValue = request.getParameterValue();

                FastClass fastClass = FastClass.create(serviceBeanClass);
                FastMethod method = fastClass.getMethod(methodName, parameterTypes);
                Object invoke = method.invoke(serviceBean, parameterValue);

                log.debug("invoke value : {}", invoke);
                rpcResponseMessage.setReturnValue(invoke);
            } catch (Exception e) {
                log.error("error ",e);
                rpcResponseMessage.setExceptionValue(new Exception(e.getCause().getMessage()));
            }
            ctx.writeAndFlush(rpcResponseMessage);
        });
    }

}
