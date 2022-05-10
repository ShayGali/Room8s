package com.example.room8.database;

import androidx.annotation.NonNull;

import com.example.room8.model.User;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Http2;

public class NodeService implements DatabaseService {

    OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://10.113.4.225:";
    private static final int PORT = 3000;
    private static final String URL = BASE_URL + PORT;


    @Override
    public String login(String email, String encryptPassword) {
        return null;
    }

    @Override
    public User getUserData() {
        return null;
    }

    @Override
    public void setToken(String token) {

    }

    @Override
    public void register(String username, String email, String password) {

        String requestUrl = URL + "/users/register";
        RequestBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("email",email)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(formBody)
                .build();
        final String[] res = {null};
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if(response.code() == 201)
//                if(response.code() == 409) //email is registered
//                if (response.code() == 500) // internal
                    res[0] = Objects.requireNonNull(response.body()).string();
                    System.out.println("res - " + response);
                    System.out.println("body - " + response.body());
                    System.out.println("str - " + res[0]);
            }
        });
    }

    public String simpleReq(){
        RequestBody formBody = new FormBody.Builder()
                .add("email","shaygali100@gmail.com")
                .add("password","123")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();
        final String[] res = {null};
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    res[0] = Objects.requireNonNull(response.body()).string();
                    System.out.println("res - " + response);
                    System.out.println("body - " + response.body());
                    System.out.println("str - " + res[0]);
                }
            }
        });
        return res[0];
    }
}
