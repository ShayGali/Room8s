package com.example.room8.model;

import org.json.JSONObject;

public final class User {
    private static User user;

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        User.user = user;
    }

    public static void parseFromJson(JSONObject userAsJson){

    }

    private int id;
    private String userName;
    private String email;
    private int userLevel;

    private double monthlyPayment;
    private int profileIconId;

    public User(int id, String userName, String email, int userLevel, double monthlyPayment, int profileIconId) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.userLevel = userLevel;
        this.monthlyPayment = monthlyPayment;
        this.profileIconId = profileIconId;
    }

    public User() {
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public int getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(int profileIconId) {
        this.profileIconId = profileIconId;
    }
}
