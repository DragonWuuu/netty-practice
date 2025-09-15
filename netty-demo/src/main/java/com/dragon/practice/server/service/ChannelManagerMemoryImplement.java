package com.dragon.practice.server.service;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManagerMemoryImplement implements ChannelManager{
    Map<String, Channel> nameToChannelMap = new ConcurrentHashMap();
    Map<Channel, String> channelToNameMap = new ConcurrentHashMap();

    @Override
    public boolean bind(String name, Channel channel) {
        if (name == null || name.equals("") || channel == null) {
            return false;
        }
        nameToChannelMap.put(name, channel);
        channelToNameMap.put(channel, name);

        return true;
    }

    @Override
    public boolean unbind(String name) {
        if (name == null || name.equals("")) {
            return false;
        }
        Channel channel = nameToChannelMap.remove(name);
        channelToNameMap.remove(channel);

        return true;
    }

    @Override
    public boolean unbind(Channel channel) {
        if (channel == null) {
            return false;
        }
        String name = channelToNameMap.remove(channel);
        nameToChannelMap.remove(name);
        return true;
    }

    @Override
    public Channel get(String name) {
        return nameToChannelMap.get(name);
    }

    @Override
    public String get(Channel channel) {
        return channelToNameMap.get(channel);
    }
}
