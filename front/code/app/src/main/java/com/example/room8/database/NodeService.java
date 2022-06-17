package com.example.room8.database;

import com.example.room8.model.User;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NodeService {

    private static final String SERVER_IP_ADDRESS = "192.168.1.155";
    private static final int PORT = 3000;
    public static final String SERVER_BASE_URL = SERVER_IP_ADDRESS + ":" + PORT;
    public static final String HTTP_URL = "http://" + SERVER_BASE_URL;

    public static final String TOKEN_HEADER_KEY = "x-auth-token";
    public static final String TOKEN_BODY_KEY = "jwtToken";


    public boolean isServerUp() {
        OkHttpClient client = new OkHttpClient();
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
