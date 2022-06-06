package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.R;
import com.example.room8.database.DatabaseService;
import com.example.room8.database.LoginHandler;
import com.example.room8.database.NodeService;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    public static final String JWT_SHARED_PREFERENCE = "jwt shared preference";
    public static final String JWT_TOKEN = "jwt token";
    public DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseService = new NodeService();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void goToHomePage() {
        Navigation.findNavController(this, R.id.main_nav_host_fragment).navigate(R.id.action_loginFragment_to_homePageFragment);
    }

    // check if we have a token in the SharedPreferences
    public boolean checkIfJwtTokenExists() {
        SharedPreferences sp = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        return sp.getString(JWT_TOKEN, null) == null;
    }


    // get the Jwt token from the database, save it to the SharedPreferences
    // if the login failed the we will return false
    public void login() {
        WeakReference<TextView> emailTextViewWeakReference = new WeakReference<>(findViewById(R.id.login_email_EditText));
        WeakReference<TextView> passwordTextViewWeakReference = new WeakReference<>(findViewById(R.id.login_password_EditText));
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        LoginHandler loginHandler = new LoginHandler(mainActivityWeakReference, emailTextViewWeakReference, passwordTextViewWeakReference);
        loginHandler.execute();
    }

    public void register(String username, String email, String password) {

        databaseService.register(username, email, password);
    }

    String str = null;

    public void simple() {
        str = databaseService.simpleReq();
        if (str == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void simple2() {
        if (str == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}