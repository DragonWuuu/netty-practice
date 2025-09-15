package com.dragon.practice.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LengthFrameDecoder extends LengthFieldBasedFrameDecoder {
    public LengthFrameDecoder(){
        super(1024 * 1024, 12, 4, 0, 0, true);
    }
}
