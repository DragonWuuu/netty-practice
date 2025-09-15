package com.dragon.practice.message;

import lombok.Data;

@Data
public class SessionRequestMessage extends Message {
    String targetName;
    String chat;

    public SessionRequestMessage(String chat, String targetName) {
        this.chat = chat;
        this.targetName = targetName;
    }

    @Override
    public MessageType getType() {
        return MessageType.SessionRequest;
    }
}
