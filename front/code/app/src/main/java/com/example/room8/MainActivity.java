package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.room8.R;
import com.example.room8.database.DatabaseService;
import com.example.room8.database.FakeService;

public class MainActivity extends AppCompatActivity {

    public static final String JWT_SHARED_PREFERENCE = "jwt shared preference";
    public static final String JWT_TOKEN = "jwt token";
    public DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseService = new FakeService();
    }

    // check if we have a token in the SharedPreferences
    public boolean checkIfJwtTokenExists() {
        SharedPreferences sp = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        return sp.getString(JWT_TOKEN, null) == null;
    }


    // get a string token and save it to the SharedPreferences
    public void saveJwtToken(String token) {
        SharedPreferences sp = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(JWT_TOKEN, token);
        ed.apply();
    }


    // get the Jwt token from the database, save it to the SharedPreferences
    // if the login failed the we will return false
    public boolean login(String email, String password) {

        // TODO : hash the password
        String encryptPassword = encryptPassword(password);
        String token = databaseService.login(email, encryptPassword);
        if (token == null)
            return false;

        saveJwtToken(token);

        return true;
    }

    private String encryptPassword(String password){
        //TODO: implement the function
        return password;
    }

}