package com.dragon.practice.message;

import com.dragon.practice.entity.User;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    User user;

    public LoginRequestMessage(String username, String password) {
        user = new User();
        user.setUsername(username);
        user.setPassword(password);
    }

    @Override
    public MessageType getType() {
        return MessageType.LoginRequest;
    }

    @Override
    public int getSequenceId() {
        return 0;
    }
}
