package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.example.room8.database.ServerRequestsService;
import com.example.room8.database.SharedPreferenceHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    public ServerRequestsService databaseService;
    public SharedPreferenceHandler sharedPreferenceHandler;

    public static boolean isStrongPassword(String password) {
        return password.length() >= 6;
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferenceHandler = SharedPreferenceHandler.getInstance();
        sharedPreferenceHandler.setActivity(this);

        databaseService = ServerRequestsService.getInstance();
        databaseService.setActivity(this);

        String connectionFailCause = checkConnections();
        if (connectionFailCause != null) {
            Toast.makeText(this, connectionFailCause, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NoConnectionActivity.class).putExtra("cause", connectionFailCause));
        } else {
            if (SharedPreferenceHandler.getInstance().checkIfJwtAccessTokenExists()) {
                if (SharedPreferenceHandler.getInstance().isInApartment())
                    this.navigateFragment(R.id.action_loginFragment_to_homePageFragment);
                else
                    this.navigateFragment(R.id.action_loginFragment_to_homePageUserWithoutApartmentFragment);
            }
        }
    }

    String checkConnections() {
        if (!isNetworkAvailable()) {
            return "You don't have network connection";
        }

        AtomicBoolean isUp = new AtomicBoolean(true);
        // check if the user have connection to the server
        Thread t = new Thread(() -> {
            if (!databaseService.isServerUp()) {
                isUp.set(false);
                runOnUiThread(() -> Toast.makeText(this, "Server is down", Toast.LENGTH_LONG).show());
            }

        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isUp.get())
            return "server is down";

        return null;
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


    public void navigateFragment(int actionID) {
        runOnUiThread(() -> {
                    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                    if (navHostFragment != null)
                        navHostFragment.getNavController().navigate(actionID);
                }

        );
    }


    public void logout(int actionId) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        navigateFragment(actionId);
    }


    // get the Jwt token from the database, save it to the SharedPreferences
    public void login(String email, String password) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        databaseService.login(email, password, isInApartment -> {
            if (isInApartment) {
                navigateFragment(R.id.action_loginFragment_to_homePageFragment);
            } else {
                navigateFragment(R.id.action_loginFragment_to_homePageUserWithoutApartmentFragment);
            }
        });
    }

    public void register(String username, String email, String password) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        databaseService.register(username, email, password, () ->
                navigateFragment(R.id.action_signupFragment_to_homePageUserWithoutApartmentFragment)
        );
    }

    public void fetchTasks(Runnable notifyFunction) {
        databaseService.getAllTask(notifyFunction);
    }

    public void createApartment(String name) {
        databaseService.createApartment(name, () -> navigateFragment(R.id.action_homePageUserWithoutApartmentFragment_to_homePageFragment));
    }

    public void leaveApartment() {
        databaseService.leave(() -> navigateFragment(R.id.action_profileFragment_to_homePageUserWithoutApartmentFragment));
    }

    public void changePassword(String password) {
        databaseService.changePassword(password);
    }

    public void removeRoom8s(Integer id) {
        databaseService.removeRoom8(id);
    }
}