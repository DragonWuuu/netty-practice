package com.dragon.practice.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
public abstract class Message implements Serializable {
    // 请求序号
    private int sequenceId;
    // 消息的类型
    public abstract MessageType getType();
}
