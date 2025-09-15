package com.dragon.practice.server.service;

public interface UserService {

    // 登录接口，返回是否登录成功
    boolean Login(String username, String password);

}
