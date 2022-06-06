package com.example.room8.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.room8.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginHandler extends AsyncTask<Void, Void, JSONObject> {

    WeakReference<MainActivity> activity;
    WeakReference<TextView> emailTextView;
    WeakReference<TextView> passwordTextView;

    public LoginHandler(WeakReference<MainActivity> activity, WeakReference<TextView> emailTextView, WeakReference<TextView> passwordTextView) {
        this.activity = activity;
        this.emailTextView = emailTextView;
        this.passwordTextView = passwordTextView;
}


    @Override
    protected JSONObject doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        String requestUrl = NodeService.SERVER_BASE_URL + "/auth/login";

        RequestBody formBody = new FormBody.Builder()
                .add("email", emailTextView.get().getText().toString())
                .add("password", passwordTextView.get().getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(Objects.requireNonNull(response.body()).string());


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject responseJson) {
        if (responseJson == null) {
            activity.get().showToast("Try again later");
            return;
        }
        System.out.println(responseJson.toString());
        if (responseJson.has("jwtToken")) {
            String token = null;
            try {
                token = responseJson.getString("jwtToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sp = activity.get().getSharedPreferences(MainActivity.JWT_SHARED_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(MainActivity.JWT_TOKEN, token);
            ed.apply();
            activity.get().goToHomePage();
        } else {
            try {
                activity.get().showToast(responseJson.getString("msg"));
            } catch (JSONException e) {
                activity.get().showToast("Try again later");
                e.printStackTrace();
            }
        }
    }
}
