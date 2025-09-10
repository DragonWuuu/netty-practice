package com.dragon.practice.message;

public enum MessageType {

    NULL(0), LOGIN(1), REGISTER(2);

    int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
