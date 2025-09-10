package com.dragon.practice.protocol;

import com.dragon.practice.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    static final int MAGIC_NUMBER = 0x11224488;

    @Override
    // 出站 编码
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 魔数 4B
        out.writeInt(MAGIC_NUMBER);
        // 2. 版本号 1B
        out.writeByte(1);
        // 3. 序列化算法 1B 0表示JDK序列化，1表示JSON序列化
        out.writeByte(0);
        // 4. 指令类型 1B
        out.writeByte(msg.getType());
        // 5. 请求序号 4B
        out.writeInt(msg.getSequenceId());
        // 填充 1B凑够16B
        out.writeByte(0xFF);

        // 获取数据对象的字节数组 Object -> ObjectStream -> ByteArrayStream -> byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(msg);
        byte[] dataBytes = byteArrayOutputStream.toByteArray();
        int length = dataBytes.length;
        // 6. 正文长度 4B
        out.writeInt(length);
        // 消息正文
        out.writeBytes(dataBytes);
    }

    @Override
    // 入站 解码
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 编码的逆过程
        int magicNumber = in.readInt();
        int version = in.readByte();
        int serializer = in.readByte();
        int command = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] dataBytes = new byte[length];
        in.readBytes(dataBytes);
        // 检查魔数
        if (magicNumber != MAGIC_NUMBER) {
            return; // 快速结束
        }

        switch (serializer) {
            // jdk序列化
            case 0 : {
                // dataBytes -> ByteArrayStream -> ObjectStream -> Object -> Message
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object data = objectInputStream.readObject();
                Message message = ((Message) data);
                log.info("{}, {}, {}, {}, {}, {}",
                        magicNumber, version, serializer, command, sequenceId, length);
                log.info("message:{}, type:{}, sequenceId:{}, data:{}",
                        message, message.getType(), message.getSequenceId(), message.getData());

                // 传出数据
                out.add(message);
                break;
            }
            // json序列化
            case 1 : {
                // todo json序列化
                break;
            }
        }
    }
}
