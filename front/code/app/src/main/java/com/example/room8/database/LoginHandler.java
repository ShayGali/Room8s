package com.example.room8.database;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.model.User;

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

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String REQUEST_URL = NodeService.HTTP_URL + "/auth/login";

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
        RequestBody formBody = new FormBody.Builder()
                .add(EMAIL_KEY, emailTextView.get().getText().toString())
                .add(PASSWORD_KEY, passwordTextView.get().getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(REQUEST_URL)
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
        if (responseJson.has(NodeService.TOKEN_BODY_KEY)) {
            String token = null;
            try {
                token = responseJson.getString(NodeService.TOKEN_BODY_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            activity.get().saveJwtToSharedPreference(token);

            // save the user ID and his apartment ID
            this.setUserIdAndApartmentId(responseJson);

            // if he is in apartment we send him to the regular home page
            if (User.getInstance().getApartmentId() != 0) {
                activity.get().setIsInApartment(true);
                activity.get().navigateFragment(R.id.action_loginFragment_to_homePageFragment);
            }
            else {// if he is not in apartment we send him to the right home page
                activity.get().setIsInApartment(false);
                activity.get().navigateFragment(R.id.action_loginFragment_to_homePageUserWithoutApartmentFragment);
            }

        } else { // if the response don't have token key in the body
            try {
                activity.get().showToast(responseJson.getString("msg"));
            } catch (JSONException e) {
                activity.get().showToast("Try again later");
                e.printStackTrace();
            }
        }
    }

    private void setUserIdAndApartmentId(JSONObject responseJson) {
        User user = User.getInstance();
        if (responseJson.has("userId") && !responseJson.isNull("userId")) {
            try {
                user.setId(responseJson.getInt("userId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (responseJson.has("apartmentId") && !responseJson.isNull("apartmentId")) {
            try {
                user.setApartmentId(responseJson.getInt("apartmentId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
