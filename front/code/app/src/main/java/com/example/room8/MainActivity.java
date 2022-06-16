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
import com.example.room8.dialogs.LoadingAlert;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    public static final String JWT_SHARED_PREFERENCE = "jwt shared preference";
    public static final String JWT_TOKEN = "jwt token";
    public DatabaseService databaseService;

    LoadingAlert loadingAlert = new LoadingAlert(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseService = new NodeService();
        loadingAlert.startLoadingDialog();

        if (!isNetworkAvailable()) {//TODO - לשלוח אותו למסך יעודי
            Toast.makeText(this, "You don't have network connection", Toast.LENGTH_SHORT).show();
        }
        new Thread(() -> {
            if (isServerUp()) { // TODO - לשלוח אותו למסך יעודי
                runOnUiThread(() -> Toast.makeText(this, "Server is up", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Server is down", Toast.LENGTH_LONG).show());
            }
            loadingAlert.dismissDialog();
        }).start();
    }

    private boolean isServerUp() {
        return databaseService.isServerUp();
    }

    public void func() {
        new Thread(() -> {
            if (isServerUp()) { // TODO - לשלוח אותו למסך יעודי
                runOnUiThread(() -> Toast.makeText(this, "Server is up", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Server is down", Toast.LENGTH_LONG).show());
            }
            loadingAlert.dismissDialog();
        }).start();
    }

    /**
     * Detect whether there is an Internet connection available
     *
     * @return whether there is an Internet connection available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public void register(WeakReference<TextView> userNameTextView, WeakReference<TextView> emailTextViewWeakReference, WeakReference<TextView> passwordTextViewWeakReference) {
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new RegisterHandler(mainActivityWeakReference, userNameTextView, emailTextViewWeakReference, passwordTextViewWeakReference).execute();
    }


}