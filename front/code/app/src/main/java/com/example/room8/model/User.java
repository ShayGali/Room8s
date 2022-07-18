package com.example.room8.model;

import androidx.annotation.NonNull;

import com.example.room8.R;

import org.json.JSONException;
import org.json.JSONObject;


public final class User {
    private static User user;

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    private static final String MESSAGE_KEY = "msg";
    private static final String RESULT_KET = "result";
    private static final String ID_KEY = "ID";
    private static final String USER_NAME_KEY = "user_name";
    private static final String EMAIL_KEY = "email";
    private static final String USER_LEVEL_KEY = "user_level";
    private static final String MONTHLY_PAYMENT_KEY = "monthly_payment";
    private static final String ICON_ID_KEY = "profile_icon_id";


    private int id;
    private int apartmentId;
    private String userName;
    private String email;
    private int userLevel;

    private double monthlyPayment;
    private int profileIconId;

    private User() {
    }

    public static void parseDataFromJson(JSONObject userAsJson) throws JSONException {
        if (user == null)
            user = new User();
        if (userAsJson.has(MESSAGE_KEY) && "success".equals(userAsJson.getString("msg")) && userAsJson.has(RESULT_KET))
            userAsJson = userAsJson.getJSONObject("result");
        if (userAsJson.has(ID_KEY) && !userAsJson.isNull(ID_KEY))
            user.id = userAsJson.getInt(ID_KEY);
        if (userAsJson.has(USER_NAME_KEY))
            user.userName = userAsJson.getString(USER_NAME_KEY);
        if (userAsJson.has(EMAIL_KEY))
            user.email = userAsJson.getString(EMAIL_KEY);
        if (userAsJson.has(USER_LEVEL_KEY) && !userAsJson.isNull(USER_LEVEL_KEY))
            user.userLevel = userAsJson.getInt(USER_LEVEL_KEY);
        if (userAsJson.has(MONTHLY_PAYMENT_KEY) && !userAsJson.isNull(MONTHLY_PAYMENT_KEY))
            user.monthlyPayment = userAsJson.getDouble(MONTHLY_PAYMENT_KEY);
        if (userAsJson.has(ICON_ID_KEY) && !userAsJson.isNull(ICON_ID_KEY)) {
            int iconId = userAsJson.getInt(ICON_ID_KEY);
            if (iconId == 0)
                user.profileIconId = R.drawable.ic_launcher_foreground;
            else
                user.profileIconId = iconId;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", userLevel=" + userLevel +
                ", monthlyPayment=" + monthlyPayment +
                ", profileIconId=" + profileIconId +
                '}';
    }
}
