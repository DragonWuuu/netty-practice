package com.dragon.practice.message;

import lombok.Data;

@Data
public class RpcResponseMessage extends AbstractResponseMessage{

    private Throwable caused = null;
    Object returnData;

    public RpcResponseMessage(String reason, Object returnData) {
        super(true, reason);
        this.returnData = returnData;
    }

    public RpcResponseMessage(String reason, Throwable caused) {
        super(false, reason);
        this.caused = caused;
    }

    @Override
    public MessageType getType() {
        return MessageType.RPCResponse;
    }
}
