package com.dragon.practice.server.handler;

import com.dragon.practice.entity.User;
import com.dragon.practice.message.*;
import com.dragon.practice.server.service.ChannelManager;
import com.dragon.practice.server.service.ChannelManagerSingleFactory;
import com.dragon.practice.server.service.UserService;
import com.dragon.practice.server.service.UserServiceMemoryImplement;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerServiceHandler extends SimpleChannelInboundHandler<Message> {

    static final ChannelManager channelManager = ChannelManagerSingleFactory.getChannelManagerMemoryImplement();
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelManager.unbind(ctx.channel());
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Channel channel = ctx.channel();
        switch (msg.getType()) {
            // 处理登录请求
            case LoginRequest: {
                User user = ((LoginRequestMessage) msg).getUser();
                UserService userService = new UserServiceMemoryImplement();
                boolean login = userService.Login(user.getUsername(), user.getPassword());
                if (login) {
                    channelManager.bind(user.getUsername(), channel);
                    channel.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
                } else
                    channel.writeAndFlush(new LoginResponseMessage(false, "登录失败，请检查用户名和密码"));
                break;
            }
            // 处理对话请求
            case SessionRequest: {
                SessionRequestMessage sessionRequestMessage = (SessionRequestMessage) msg;
                String targetName = sessionRequestMessage.getTargetName();
                String chat = sessionRequestMessage.getChat();
                Channel target = channelManager.get(targetName);
                if (target != null) {
                    target.writeAndFlush(new SessionResponseMessage(true, chat));
                    channel.writeAndFlush(new SessionResponseMessage(true, "消息发送成功"));
                }
                else {
                    channel.writeAndFlush(new SessionResponseMessage(false, "用户不在线"));
                }
                break;
            }
        }

    }
}
