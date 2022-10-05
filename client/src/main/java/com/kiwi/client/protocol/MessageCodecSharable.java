package com.kiwi.client.protocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import model.Message;
import org.springframework.beans.factory.annotation.Value;
import serializer.Serializer;
import serializer.SerializerFactory;
import serializer.SerializerStrategy;

import java.util.List;

/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Value("${serializer.algorithm}")
    private String serializerAlgorithm;


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) {
        try {
            ByteBuf out = ctx.alloc().buffer();
            // 1. 4 字节的魔数
            out.writeBytes(new byte[]{1, 2, 3, 4});
            // 2. 1 字节的版本,
            out.writeByte(1);
            // 3. 1 字节的序列化方式 jdk 0 , json 1
            out.writeByte(1);
            // 4. 1 字节的指令类型
            out.writeByte(msg.getMessageType());
            // 5. 4 个字节
            out.writeInt(msg.getSequenceId());
            // 无意义，对齐填充
            out.writeByte(0xff);
            // 6. 获取内容的字节数组

            Serializer serializer = new SerializerFactory(SerializerStrategy.getStrategy(serializerAlgorithm));

            byte[] bytes = serializer.serialize(msg);

            // 7. 长度
            out.writeInt(bytes.length);
            // 8. 写入内容
            out.writeBytes(bytes);
            outList.add(out);
        } catch (Exception e) {
            log.error("error : ", e);
        }

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        Object message = null;
        try {
            Serializer serializer = new SerializerFactory(SerializerStrategy.getStrategy(serializerAlgorithm));
            message = serializer.deserialize(Message.getMessageClass(messageType), bytes);
        } catch (Exception e) {
            log.error("error : ", e);
        }

        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
