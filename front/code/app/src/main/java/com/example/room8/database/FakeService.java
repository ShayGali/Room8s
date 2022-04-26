package com.example.room8.database;

import com.example.room8.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeService implements DatabaseService {

    final List<User> demoUsers = new ArrayList<>();

    public FakeService() {
        demoUsers.add(new User("a@a.com", "123"));
    }

    @Override
    public String login(String email, String encryptPassword) {
        // TODO : make post method to the server with the cardinals and return the token from the header if success;
        // TODO : If the login fails (status code 401), we will return null

        if (email.equals("a@a.com") && encryptPassword.equals("123"))
            return "demo-token";

        return null;
    }

}
