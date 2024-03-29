package com.example.room8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.database.ServerRequestsService;
import com.example.room8.database.SharedPreferenceHandler;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
                ServerRequestsService.getInstance().refreshToken(null, "", i -> forceLogout(), i -> forceLogout());

                if (SharedPreferenceHandler.getInstance().isInApartment())
                    this.navigateFragment(R.id.action_loginFragment_to_homePageFragment);
                else
                    this.navigateFragment(R.id.action_loginFragment_to_homePageUserWithoutApartmentFragment);
            }
        }
//        this.refreshData();
//        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

    }


    @SuppressLint("SetTextI18n")
    public void refreshData() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ServerRequestsService.getInstance().getApartmentId(() -> {
                    ServerRequestsService.getInstance().getUserData();
                    if (SharedPreferenceHandler.getInstance().isInApartment()) {
                        ServerRequestsService.getInstance().getAllTask(null);
                        ServerRequestsService.getInstance().getRoom8s();
                        ServerRequestsService.getInstance().getExpenses();
                        ServerRequestsService.getInstance().getApartmentData();

                    }
//                    else {
//                        try {
//                            Navigation.findNavController(view).navigate(R.id.action_homePageUserWithoutApartmentFragment_to_profileFragment);
//                        } catch (IllegalArgumentException ignored) {
//                        }
//                    }
//                    swipeRefreshLayout.setRefreshing(false);
                });

            }
        }, 0, 5000);

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


    /**
     * delete the save data and navigate to the login screen
     *
     * @param actionId navigate to login screen
     */
    public void logout(int actionId) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        navigateFragment(actionId);
    }


    /**
     * get the Jwt token from the database, save it to the SharedPreferences
     */
    public void login(String email, String password, WeakReference<TextView> errorMsgWeakReference) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        databaseService.login(email, password,
                isInApartment -> {
                    if (isInApartment) {
                        navigateFragment(R.id.action_loginFragment_to_homePageFragment);
                    } else {
                        navigateFragment(R.id.action_loginFragment_to_homePageUserWithoutApartmentFragment);
                    }
                },
                failMsg -> runOnUiThread(() -> errorMsgWeakReference.get().setText(failMsg)));
    }


    public void register(String username, String email, String
            password, Consumer<String> displayError) {
        SharedPreferenceHandler.getInstance().deleteSaveData();
        databaseService.register(
                username, email, password,
                () -> navigateFragment(R.id.action_signupFragment_to_homePageUserWithoutApartmentFragment),
                displayError
        );
    }

    public void createApartment(String name) {
        databaseService.createApartment(name, () -> navigateFragment(R.id.action_homePageUserWithoutApartmentFragment_to_homePageFragment));
    }

    public void leaveApartment() {
        databaseService.leave(() -> navigateFragment(R.id.action_profileFragment_to_homePageUserWithoutApartmentFragment));
    }


    /**
     * logout the user when cant refresh his token
     */
    public void forceLogout() {
        SharedPreferenceHandler.getInstance().deleteSaveData();

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_homePageUserWithoutApartmentFragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_homePageFragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_profileFragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_tasksFragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_message_Fragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
                        if (navHostFragment != null)
                            navHostFragment.getNavController().navigate(R.id.action_walletFragment_to_loginFragment);
                    }
            );
            return;
        } catch (IllegalArgumentException ignored) {
        }

        // if we cant navigate him to login we close the app
        this.finish();
    }
}
