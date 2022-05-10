package com.example.room8.database;

import com.example.room8.model.User;

public interface DatabaseService {

    String login(String email, String encryptPassword);

    User getUserData();

    void setToken(String token);

    String register(String username, String email, String password);

    String simpleReq();
}
