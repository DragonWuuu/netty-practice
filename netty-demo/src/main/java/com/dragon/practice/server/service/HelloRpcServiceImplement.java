package com.dragon.practice.server.service;

public class HelloRpcServiceImplement implements HelloRpcService{
    @Override
    public String hello(String chat) {
        return "RPC response: "+ chat;
    }
}
