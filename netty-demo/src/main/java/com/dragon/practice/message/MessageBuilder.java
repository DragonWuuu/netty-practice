package com.dragon.practice.message;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Setter
public class MessageBuilder implements Serializable {
    MessageType type = MessageType.NULL;
    Object data = null;
    int sequence = 0;

    private MessageBuilder(){}

    public static MessageBuilder builder(){
        return new MessageBuilder();
    }

    public Message build(){
        return new Message() {
            @Override
            public Object getData() {
                return data;
            }

            @Override
            public int getType() {
                return type.getType();
            }

            @Override
            public int getSequenceId() {
                return sequence;
            }
        };
    }
}
