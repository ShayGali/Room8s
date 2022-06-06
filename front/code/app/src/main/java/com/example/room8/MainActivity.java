package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.database.DatabaseService;
import com.example.room8.database.LoginHandler;
import com.example.room8.database.NodeService;
import com.example.room8.database.RegisterHandler;

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

        if (!isNetworkConnected()) {//TODO - לשלוח אותו למסך יעודי
            Toast.makeText(this, "You don't have network connection", Toast.LENGTH_SHORT).show();
        }
        if (!isServerUp()) { // TODO - לשלוח אותו למסך יעודי
            Toast.makeText(this, "You don't have network connection", Toast.LENGTH_SHORT).show();

        }

    }

    private boolean isServerUp() {
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        return cm.getActiveNetworkInfo() != null;
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void goToHomePage() {
        Navigation.findNavController(this, R.id.main_nav_host_fragment).navigate(R.id.action_loginFragment_to_homePageFragment);
    }

    public void goToHomePageWithOutApartment() {
        Navigation.findNavController(this, R.id.main_nav_host_fragment).navigate(R.id.action_signupFragment_to_homePageUserWithoutApartmentFragment);
    }

    // check if we have a token in the SharedPreferences
    public boolean checkIfJwtTokenExists() {
        SharedPreferences sp = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        return sp.getString(JWT_TOKEN, null) == null;
    }


    // get the Jwt token from the database, save it to the SharedPreferences
    public void login(WeakReference<TextView> emailTextViewWeakReference, WeakReference<TextView> passwordTextViewWeakReference) {
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new LoginHandler(mainActivityWeakReference, emailTextViewWeakReference, passwordTextViewWeakReference).execute();
    }

    public void register(WeakReference<TextView> userNameTextView,WeakReference<TextView> emailTextViewWeakReference, WeakReference<TextView> passwordTextViewWeakReference){
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new RegisterHandler(mainActivityWeakReference, userNameTextView,emailTextViewWeakReference,passwordTextViewWeakReference).execute();
    }



}