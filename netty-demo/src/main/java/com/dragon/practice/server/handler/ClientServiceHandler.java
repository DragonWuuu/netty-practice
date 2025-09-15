package com.dragon.practice.server.handler;

import com.dragon.practice.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ClientServiceHandler extends SimpleChannelInboundHandler<Message> {

    CountDownLatch scannerThreadLatch = new CountDownLatch(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx){

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名");
            String username = scanner.nextLine();
            System.out.println("请输入密码");
            String password = scanner.nextLine();
            ctx.channel().writeAndFlush(new LoginRequestMessage(username, password));

            try {
                scannerThreadLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                System.out.println("""
                        ============================
                        请输入您要执行的命令：
                        退出: quit
                        发送消息: send [message] [username]
                        ============================
                        """);
                String line = scanner.nextLine();
                if (line.equals("quit")) {
                    System.out.println("退出");
                    break;
                }
                String[] elements = line.split(" ");
                switch (elements[0]) {
                    case "send":
                        log.debug("send message");
                        ctx.channel().writeAndFlush(new SessionRequestMessage(elements[1], elements[2]));
                        break;
                    case "other":

                        break;
                }
            }
            ctx.close();

        }, "Scanner").start();
    }

    // 处理服务器结果
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg){
        switch (msg.getType()) {
            // 登录结果
            case LoginResponse:
                log.debug(msg.toString());
                Boolean success = ((AbstractResponseMessage) msg).getSuccess();
                log.debug(success.toString());
                if (success) {
                    log.debug("登录成功");
                    scannerThreadLatch.countDown();
                } else {
                    log.debug("登录失败");
                    ctx.close();
                }
                break;
            case NULL:
                break;
                // 对话结果
            case SessionResponse:
                SessionResponseMessage sessionResponseMessage = (SessionResponseMessage) msg;
                Boolean responseMessageSuccess = sessionResponseMessage.getSuccess();
                if (responseMessageSuccess) {
                    log.info(sessionResponseMessage.getReason());
                }
                break;
        }
    }
}
