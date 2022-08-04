package com.example.room8.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Roommate {

    private static final String ID_KEY = "ID";
    private static final String USER_NAME_KEY = "user_name";
    private static final String USER_LEVEL_KEY = "user_level";

    int id;
    String userName;
    int userLevel;

    public Roommate(int id, String userName, int userLevel) {
        this.id = id;
        this.userName = userName;
        this.userLevel = userLevel;
    }

    public Roommate(JSONObject roommateAsJson) throws JSONException {
        if (roommateAsJson.has(ID_KEY) && !roommateAsJson.isNull(ID_KEY))
            this.id = roommateAsJson.getInt(ID_KEY);
        if (roommateAsJson.has(USER_NAME_KEY))
            this.userName = roommateAsJson.getString(USER_NAME_KEY);
        if (roommateAsJson.has(USER_LEVEL_KEY) && !roommateAsJson.isNull(USER_LEVEL_KEY))
            this.userLevel = roommateAsJson.getInt(USER_LEVEL_KEY);
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

    @Override
    public String toString() {
        return "Roommate{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userLevel=" + userLevel +
                '}';
    }
}
