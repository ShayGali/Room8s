package com.example.room8.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Roommate {

    private static final String ID_KEY = "ID";
    private static final String USER_NAME_KEY = "user_name";
    private static final String USER_LEVEL_KEY = "user_level";
    private static final String LEVEL_NAME_KEY = "level_name";
    private static final String ICON_ID_KEY = "profile_icon_id";

    public static final String[] LEVELS = new String[]{"basic user", "apartment owner", "admin"};

    private int id;
    private String userName;
    private int userLevel;
    private String levelName;
    private int iconId;

    public Roommate(JSONObject roommateAsJson) throws JSONException {
        if (roommateAsJson.has(ID_KEY) && !roommateAsJson.isNull(ID_KEY))
            this.id = roommateAsJson.getInt(ID_KEY);
        if (roommateAsJson.has(USER_NAME_KEY))
            this.userName = roommateAsJson.getString(USER_NAME_KEY);
        if (roommateAsJson.has(USER_LEVEL_KEY) && !roommateAsJson.isNull(USER_LEVEL_KEY))
            this.userLevel = roommateAsJson.getInt(USER_LEVEL_KEY);
        if (roommateAsJson.has(LEVEL_NAME_KEY))
            levelName = roommateAsJson.getString(LEVEL_NAME_KEY);

        if (roommateAsJson.has(ICON_ID_KEY) && !roommateAsJson.isNull(ICON_ID_KEY))
            iconId = roommateAsJson.getInt(ICON_ID_KEY);
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


    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Roommate{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userLevel=" + userLevel +
                ", levelName='" + levelName + '\'' +
                '}';
    }
}
