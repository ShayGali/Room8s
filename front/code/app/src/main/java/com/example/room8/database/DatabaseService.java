package com.example.room8.database;

import com.example.room8.model.User;

public interface DatabaseService {


    User getUserData();

    void setToken(String token);



    boolean isServerUp();
}
