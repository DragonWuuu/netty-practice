package com.dragon.practice.server.service;

import io.netty.channel.Channel;

public interface ChannelManager {

    boolean bind(String name, Channel channel);

    boolean unbind(String name);

    boolean unbind(Channel channel);

    Channel get(String name);

    String get(Channel channel);

}
