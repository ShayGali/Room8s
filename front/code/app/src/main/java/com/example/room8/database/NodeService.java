package com.example.room8.database;

import androidx.annotation.NonNull;

import com.example.room8.MainActivity;
import com.example.room8.model.Apartment;
import com.example.room8.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NodeService {

    private static final String SERVER_IP_ADDRESS = "192.168.1.155";
    private static final int PORT = 3000;
    public static final String SERVER_BASE_URL = SERVER_IP_ADDRESS + ":" + PORT;
    public static final String HTTP_URL = "http://" + SERVER_BASE_URL;

    public static final String USERS_PATH = "/users";
    public static final String APARTMENTS_PATH = "/apartments";

    public static final String TOKEN_HEADER_KEY = "x-auth-token";
    public static final String TOKEN_BODY_KEY = "jwtToken";

    public static final String MESSAGE_KEY = "msg"; // if the response is good
    public static final String DATA_KEY = "data"; // the data

    MainActivity activity;

    public NodeService(MainActivity activity) {
        this.activity = activity;
    }

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

    public void getUserData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/findById")
                .addHeader(TOKEN_HEADER_KEY, activity.getJwtFromSharedPreference())
                .get()
                .build();

        client.newCall(request).enqueue((new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.showToast("fetch data failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                String stringBody = responseBody != null ? responseBody.string() : null;
                if (stringBody == null) {
                    activity.showToast("fetch data went wrong");
                    activity.showToast("responseBody is null");
                    System.err.println("fetch data went wrong");
                    System.err.println("responseBody is null");
                } else if (!response.isSuccessful()) {

                    activity.showToast("fetch data went wrong");
                    activity.showToast("response code: " + response.code());
                    activity.showToast("response body: " + stringBody);

                    System.err.println("response code: " + response.code());
                    System.err.println("response body: " + stringBody);
                } else {
                    try {

                        JSONObject responseJOSN = new JSONObject(stringBody);

                        if (responseJOSN.has(MESSAGE_KEY) && "success".equals(responseJOSN.getString("msg")) && responseJOSN.has(DATA_KEY))
                            User.parseDataFromJson(responseJOSN.getJSONObject(DATA_KEY));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }
}
