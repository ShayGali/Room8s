package com.example.room8.model;

public final class User {
    private static User user;

    private Integer id;
    private String email;
    private String password;
//    private double monthlyPayment;
//    private String profileIconPath;

    private User(Integer id, String email, String password, double monthlyPayment, String profileIconPath) {
        this.id = id;
        this.email = email;
        this.password = password;
//        this.monthlyPayment = monthlyPayment;
//        this.profileIconPath = profileIconPath;
    }

    public static User getInstance() {
        if (user == null) {
            user = new User(null, null, null, 0, "");
        }
        return user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
