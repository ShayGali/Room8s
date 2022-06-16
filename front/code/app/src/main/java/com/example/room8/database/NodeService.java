package com.example.room8.database;

import androidx.annotation.NonNull;

import com.example.room8.model.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NodeService implements DatabaseService {

    OkHttpClient client = new OkHttpClient();
    private static final String IP_ADDRESS = "10.113.4.116";
    private static final int PORT = 3000;
    public static final String SERVER_BASE_URL = IP_ADDRESS + ":" + PORT;
    public static final String HTTP_URL = "http://" + SERVER_BASE_URL;


    @Override
    public User getUserData() {
        return null;
    }

    @Override
    public void setToken(String token) {

    }

    public boolean isServerUp() {
        Request request = new Request.Builder()
                .url(HTTP_URL + "/isAlive")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
