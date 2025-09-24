package com.dragon.practice.message;

import lombok.Data;

@Data
public class RpcResponseMessage extends AbstractResponseMessage{

    private Throwable caused = null;
    Object returnData;

    public RpcResponseMessage(int sequenceId, String reason, Object returnData) {
        super(true, reason);
        super.setSequenceId(sequenceId);
        this.returnData = returnData;
    }

    public RpcResponseMessage(int sequenceId, String reason, Throwable caused) {
        super(false, reason);
        super.setSequenceId(sequenceId);
        this.caused = caused;
    }

    @Override
    public MessageType getType() {
        return MessageType.RPCResponse;
    }
}
