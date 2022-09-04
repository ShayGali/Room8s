package com.example.room8.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.room8.model.Apartment;
import com.example.room8.model.User;

public class SharedPreferenceHandler {

    @SuppressLint("StaticFieldLeak")
    public static volatile SharedPreferenceHandler instance;
    private static final Object lock = new Object();

    public static SharedPreferenceHandler getInstance() {
        SharedPreferenceHandler r = instance;
        if (r == null) {
            synchronized (lock) {    // While we were waiting for the lock, another
                r = instance;        // thread may have instantiated the object.
                if (r == null) {
                    r = new SharedPreferenceHandler();
                    instance = r;
                }
            }
        }
        return r;
    }


    private static final String AUTH_PREFERENCE = "auth_preference";
    private static final String JWT_ACCESS_TOKEN = "access_token";
    private static final String JWT_REFRESH_TOKEN = "refresh_token";
    private static final String IS_IN_APARTMENT = "is_in_apartment";

    Activity activity;
    SharedPreferences sp;

    private SharedPreferenceHandler() {
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        sp = activity.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void saveJwtAccessToken(String token) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(JWT_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getAccessJwt() {
        return sp.getString(JWT_ACCESS_TOKEN, null);
    }

    public void saveJwtRefreshToken(String token) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(JWT_REFRESH_TOKEN, token);
        editor.apply();
    }

    public String getRefreshJwt() {
        return sp.getString(JWT_REFRESH_TOKEN, null);
    }

    public boolean checkIfJwtAccessTokenExists() {
        return getAccessJwt() != null;
    }

    public void setIsInApartment(boolean isInApartment) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_IN_APARTMENT, isInApartment);
        editor.apply();
    }

    public boolean isInApartment() {
        return sp.getBoolean(IS_IN_APARTMENT, false);
    }

    public void deleteSaveData() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(JWT_ACCESS_TOKEN).apply();
        editor.remove(IS_IN_APARTMENT).apply();
        User.resetData();
        Apartment.resetData();
    }

}
