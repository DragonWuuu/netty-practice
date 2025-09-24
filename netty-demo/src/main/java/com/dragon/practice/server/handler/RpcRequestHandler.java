package com.dragon.practice.server.handler;

import com.dragon.practice.message.RpcRequestMessage;
import com.dragon.practice.message.RpcResponseMessage;
import com.dragon.practice.server.service.HelloRpcService;
import com.dragon.practice.server.service.HelloRpcServiceImplement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        String className = msg.getClassName();
        switch (className) {
            case "com.dragon.practice.server.service.HelloRpcService": {
                try {
                    HelloRpcService helloRpcService = new HelloRpcServiceImplement();
                    Method method = helloRpcService.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
                    Object invoke = method.invoke(helloRpcService, msg.getArgs());
                    log.info("invoke: {}", invoke);
                    ctx.channel().writeAndFlush(new RpcResponseMessage(msg.getSequenceId(), "调用成功", invoke));
                } catch (NoSuchMethodException e) {
                    ctx.channel().writeAndFlush(new RpcResponseMessage(msg.getSequenceId(), "调用异常", new RuntimeException(e.getMessage())));
                }
                break;
            }
        }
    }
}
