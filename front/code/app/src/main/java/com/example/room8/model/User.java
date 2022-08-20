package com.example.room8.model;

import com.example.room8.R;

import org.json.JSONException;
import org.json.JSONObject;


public final class User {
    private static User instance;

    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null)
                    instance = new User();
            }
        }
        return instance;
    }


    private static final String ID_KEY = "ID";
    private static final String USER_NAME_KEY = "user_name";
    private static final String EMAIL_KEY = "email";
    private static final String USER_LEVEL_KEY = "user_level";
    private static final String ICON_ID_KEY = "profile_icon_id";
    private static final String LEVEL_NAME_KEY = "level_name";


    private int id;
    private int apartmentId;
    private String userName;
    private String email;
    private int userLevel;
    private String levelName;

    private int profileIconId;

    private User() {
    }

    public static void parseDataFromJson(JSONObject userAsJson) throws JSONException {
        instance = getInstance(); // for create if not exists

        if (userAsJson.has(ID_KEY) && !userAsJson.isNull(ID_KEY))
            instance.setId(userAsJson.getInt(ID_KEY));
        if (userAsJson.has(USER_NAME_KEY))
            instance.setUserName(userAsJson.getString(USER_NAME_KEY));
        if (userAsJson.has(EMAIL_KEY))
            instance.setEmail(userAsJson.getString(EMAIL_KEY));
        if (userAsJson.has(USER_LEVEL_KEY) && !userAsJson.isNull(USER_LEVEL_KEY))
            instance.setUserLevel(userAsJson.getInt(USER_LEVEL_KEY));
        if(userAsJson.has(LEVEL_NAME_KEY))
            instance.setLevelName(userAsJson.getString(LEVEL_NAME_KEY));
        if (userAsJson.has(ICON_ID_KEY) && !userAsJson.isNull(ICON_ID_KEY)) {
            int iconId = userAsJson.getInt(ICON_ID_KEY);
            if (iconId == 0)
                instance.setProfileIconId(R.drawable.ic_launcher_foreground);
            else
                instance.setProfileIconId(iconId);
        }
    }

    public static void resetData() {
        instance = null;}


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



    public int getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(int profileIconId) {
        this.profileIconId = profileIconId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", apartmentId=" + apartmentId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", userLevel=" + userLevel +
                ", levelName='" + levelName + '\'' +
                ", profileIconId=" + profileIconId +
                '}';
    }
}
