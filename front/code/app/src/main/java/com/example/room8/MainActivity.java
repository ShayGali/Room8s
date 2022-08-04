package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.adapters.TasksAdapter;
import com.example.room8.database.HomePageHandler;
import com.example.room8.database.LoginHandler;
import com.example.room8.database.NodeService;
import com.example.room8.database.RegisterHandler;
import com.example.room8.dialogs.LoadingAlert;
import com.example.room8.dialogs.TaskDialog;
import com.example.room8.model.Task;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements TaskDialog.TaskDialogListener{

    public static final String JWT_SHARED_PREFERENCE = "jwt shared preference";
    public static final String JWT_TOKEN = "jwt token";


    public NodeService databaseService;

    LoadingAlert loadingAlert = new LoadingAlert(this);


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseService = new NodeService(this);
        loadingAlert.startLoadingDialog();
        // check if the user have internet connection
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "You don't have network connection", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NoConnectionActivity.class).putExtra("cause", "You don't have network connection"));
        }

        // check if the user have connection to the server
        new Thread(() -> {
            if (databaseService.isServerUp()) {
                runOnUiThread(() -> Toast.makeText(this, "Server is up", Toast.LENGTH_SHORT).show());
                if (checkIfJwtTokenExists()) { //TODO refresh token
                    runOnUiThread(()-> this.navigateFragment(R.id.action_loginFragment_to_homePageFragment));
                }
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Server is down", Toast.LENGTH_LONG).show());
                startActivity(new Intent(this, NoConnectionActivity.class).putExtra("cause", "Server is down"));
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
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }


    public void navigateFragment(int actionID) {
        Navigation.findNavController(this, R.id.main_nav_host_fragment).navigate(actionID);
    }

    // check if we have a token in the SharedPreferences
    public boolean checkIfJwtTokenExists() {
        SharedPreferences sp = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        return sp.getString(JWT_TOKEN, null) != null;
    }

    public String getJwtFromSharedPreference() {
        return getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE)
                .getString(MainActivity.JWT_TOKEN, null);
    }

    public void saveJwtToSharedPreference(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JWT_TOKEN, token);
        editor.apply();
    }

    public void logout(int actionId) {
        SharedPreferences sharedPreferences = getSharedPreferences(JWT_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JWT_TOKEN).apply();
        navigateFragment(actionId);
    }




    /**
     * makes a login attempt
     *
     * @param emailTextViewWeakReference    weak reference for the email input
     * @param passwordTextViewWeakReference weak reference for the password input
     */
    // get the Jwt token from the database, save it to the SharedPreferences
    public void login(WeakReference<TextView> emailTextViewWeakReference, WeakReference<TextView> passwordTextViewWeakReference) {
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new LoginHandler(mainActivityWeakReference, emailTextViewWeakReference, passwordTextViewWeakReference).execute();
    }

    /**
     * makes a register attempt
     *
     * @param userNameTextView              weak reference for the username input
     * @param emailTextViewWeakReference    weak reference for the email input
     * @param passwordTextViewWeakReference weak reference for the password input
     */
    public void register(WeakReference<TextView> userNameTextView, WeakReference<TextView> emailTextViewWeakReference, WeakReference<TextView> passwordTextViewWeakReference) {
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new RegisterHandler(mainActivityWeakReference, userNameTextView, emailTextViewWeakReference, passwordTextViewWeakReference).execute();
    }

    public void fetchUserData() {
        databaseService.getUserData();
    }

    public void fetchApartmentData(WeakReference<TextView> apartmentNameTextView, WeakReference<TextView> apartmentNumTextView, WeakReference<TextView> numberOfRoommatesTextview) {
        WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<>(this);
        new HomePageHandler(mainActivityWeakReference,apartmentNameTextView,apartmentNumTextView, numberOfRoommatesTextview).execute();
    }

    public void fetchTasks(WeakReference<TasksAdapter> tasksAdapterWeakReference) {
        databaseService.getAllTask(tasksAdapterWeakReference);
    }

    public void fetchRoom8(){
        databaseService.getRoommates();
    }

    @Override
    public void updateTask(Task t) {
        System.out.println(t);
    }
}