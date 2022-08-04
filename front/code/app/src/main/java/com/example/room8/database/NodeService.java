package com.example.room8.database;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.room8.MainActivity;
import com.example.room8.adapters.TasksAdapter;
import com.example.room8.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
    public static final String TASKS_PATH = "/tasks";

    public static final String TOKEN_HEADER_KEY = "x-auth-token";
    public static final String TOKEN_BODY_KEY = "jwtToken";

    public static final String MESSAGE_KEY = "msg"; // if the response is good
    public static final String DATA_KEY = "data"; // the data

    // formatters for the date and time
    @SuppressLint("SimpleDateFormat") // for parse date time from the server
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat") // for format date object to time string
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    @SuppressLint("SimpleDateFormat") // for format date object to date string
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    static { // for initial the timezone
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        TIME_FORMAT.setTimeZone(TimeZone.getDefault());
    }

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

                        if (responseJOSN.has(MESSAGE_KEY) && "success".equals(responseJOSN.getString("msg"))
                                && responseJOSN.has(DATA_KEY))
                            User.parseDataFromJson(responseJOSN.getJSONObject(DATA_KEY));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

    public void getAllTask(WeakReference<TasksAdapter> taskAdapter) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/all")
                .addHeader(TOKEN_HEADER_KEY, activity.getJwtFromSharedPreference())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.showToast("fetch tasks failed");
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

                        if (responseJOSN.has(MESSAGE_KEY) && "success".equals(responseJOSN.getString("msg"))
                                && responseJOSN.has(DATA_KEY)){
                            JSONArray tasks = responseJOSN.getJSONArray(DATA_KEY);
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject task = tasks.getJSONObject(i);
                                activity.runOnUiThread(()-> {
                                    try {
                                        taskAdapter.get().addTask(task);
                                    } catch (JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
