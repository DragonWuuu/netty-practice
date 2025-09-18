package com.dragon.practice.protocol;

import com.dragon.practice.message.Message;
import com.dragon.practice.message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


/*
 * 该类是线程安全的，使用时需要确保传入的ByteBuf是完整的消息。
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    static final int MAGIC_NUMBER = 0x11224488;
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 魔数 4B
        out.writeInt(MAGIC_NUMBER);
        // 2. 版本号 1B
        out.writeByte(1);
        // 3. 序列化算法 1B 0表示JDK序列化，1表示JSON序列化
        out.writeByte(0);
        // 4. 指令类型 1B
        out.writeByte(msg.getType().ordinal());
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
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
// 编码的逆过程
        int magicNumber = in.readInt();
        int version = in.readByte();
        int serializer = in.readByte();
        MessageType type = MessageType.values()[in.readByte()];
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
                log.debug("{}, {}, {}, {}, {}, {}",
                        magicNumber, version, serializer, type, sequenceId, length);
                log.debug("message:{}, type:{}, sequenceId:{}",
                        message, message.getType(), message.getSequenceId());

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
