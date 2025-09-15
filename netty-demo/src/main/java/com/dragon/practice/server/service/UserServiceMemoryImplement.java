package com.dragon.practice.server.service;

import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImplement implements UserService{
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap();

    {
        map.put("admin", "123456");
        map.put("zhangsan", "three");
    }

    @Override
    public boolean Login(String username, String password) {
        String psw = map.get(username);
        if (psw != null && !psw.equals("")) {
            if (psw.equals(password)) {
                return true;
            }
        }
        return false;
    }
}
