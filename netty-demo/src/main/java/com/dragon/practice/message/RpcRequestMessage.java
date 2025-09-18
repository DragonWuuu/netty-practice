package com.dragon.practice.message;

import lombok.Data;

@Data
public class RpcRequestMessage extends Message{

    // 全限定类名
    private String className;
    // 方法名
    private String methodName;
    // 返回值类型
    private Class returnType;
    // 参数类型
    private Class[] parameterTypes;
    // 参数集合
    private Object[] args;

    public RpcRequestMessage(int sequenceId, String className, String methodName, Class returnType, Class[] parameterTypes, Object[] args) {
        super.setSequenceId(sequenceId);
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    @Override
    public MessageType getType() {
        return MessageType.RPCRequest;
    }
}
