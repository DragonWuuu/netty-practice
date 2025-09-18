package com.dragon.practice;

import com.dragon.practice.message.RpcRequestMessage;
import com.dragon.practice.server.service.HelloRpcService;
import com.dragon.practice.server.service.SequenceIDGenerator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class HelloRPC {
    public static void main(String[] args) {
        HelloRpcService helloRpcService = getProxyService(HelloRpcService.class);
        helloRpcService.hello("zhangsan");
    }

    public static <T> T getProxyService(Class<T> serviceClass) {
        // 类加载器和接口集合
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};

        Object o = Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1. 构建消息RPCRequestMessage
                Integer sequenceId = SequenceIDGenerator.nextId();
                RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(
                        sequenceId,
                        serviceClass.getName(),
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        args
                );

                // 2. 获取Netty客户端
                Channel client = RpcClient.getClient();

                // 3. 准备空的 Promise，用来接收结果
                DefaultPromise<Object> promise = new DefaultPromise<>(client.eventLoop());
                RpcClient.putPromise(sequenceId, promise);

                // 4. 将消息发送出去
                client.writeAndFlush(rpcRequestMessage);

                // 5. 等待 Promise 结果并返回
                promise.await();
                if (promise.isSuccess()) {
                    return promise.get();
                } else {
                    throw new RuntimeException(promise.cause());
                }

            }
        });
        return (T) o;
    }
}
