package com.example.room8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.database.ServerRequestsService;

import java.util.concurrent.atomic.AtomicBoolean;

public class NoConnectionActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        ((TextView) findViewById(R.id.failure_cause_textView)).setText(getIntent().getStringExtra("cause"));
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(this::tryNetwork);
    }


    @SuppressLint("SetTextI18n")
    private void tryNetwork() {
        if (!isNetworkAvailable()) {
            ((TextView) findViewById(R.id.failure_cause_textView)).setText("You don't have network connection");
            swipeRefreshLayout.setRefreshing(false);
        } else {
            // check if the user have connection to the server
            new Thread(() -> {
                if (ServerRequestsService.getInstance().isServerUp()) {
                    runOnUiThread(() -> {
                        startActivity(new Intent(this, MainActivity.class));
                    });
                }else{
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.failure_cause_textView)).setText("Server is down");
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }).start();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}