package com.dragon.practice.message;

import java.io.Serializable;


public interface Message extends Serializable {
    // 消息的内容
    Object getData();
    // 消息的类型
    int getType();
    // 请求序号
    int getSequenceId();
}
