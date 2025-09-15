package com.dragon.practice.server.service;

public class ChannelManagerSingleFactory {
    private static ChannelManagerMemoryImplement channelManagerMemoryImplement;
    private static Object LOCK = new Object();
    public static ChannelManager getChannelManagerMemoryImplement() {
        if (channelManagerMemoryImplement != null) {
            return channelManagerMemoryImplement;
        }
        synchronized (LOCK) {
            if (channelManagerMemoryImplement != null) {
                return channelManagerMemoryImplement;
            }
            channelManagerMemoryImplement = new ChannelManagerMemoryImplement();
        }
        return channelManagerMemoryImplement;
    }
}
