package com.dragon.practice.server.handler;

import com.dragon.practice.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    private static final Map<Integer, Promise> promises = new ConcurrentHashMap();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        // 从promises中取出并装填Promise。
        Promise promise = promises.remove(msg.getSequenceId());
        if (promise == null) return;
        if (msg.getSuccess()) {
            promise.setSuccess(msg.getReturnData());
        } else promise.setFailure(msg.getCaused());
    }

    public Promise put(Integer sequenceId, Promise promise) {
        return this.promises.put(sequenceId, promise);
    }

}
