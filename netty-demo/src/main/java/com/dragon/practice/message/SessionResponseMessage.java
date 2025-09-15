package com.dragon.practice.message;

public class SessionResponseMessage extends AbstractResponseMessage{
    public SessionResponseMessage(Boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public MessageType getType() {
        return MessageType.SessionResponse;
    }
}
