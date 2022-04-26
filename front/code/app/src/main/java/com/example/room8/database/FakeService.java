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
    public boolean login(String email, String password) {
        Optional<User> optionalUser = demoUsers.stream()
                .filter(
                        user -> email.equals(user.getEmail())
                )
                .findFirst();

        return optionalUser
                .map(
                        user -> user.getPassword().equals(password)
                ).orElse(false);

    }
}
