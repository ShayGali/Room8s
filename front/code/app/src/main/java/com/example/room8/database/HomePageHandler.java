package com.example.room8.database;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.model.Apartment;
import com.example.room8.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePageHandler extends AsyncTask<Void, Void, JSONObject> {

    private static final String REQUEST_URL = NodeService.HTTP_URL + "/apartments/data";
    WeakReference<MainActivity> activity;


    WeakReference<TextView> apartmentNameTextView;
    WeakReference<TextView> apartmentNumTextView;
    WeakReference<TextView> numberOfRoommatesTextview;

    public HomePageHandler(WeakReference<MainActivity> activity, WeakReference<TextView> apartmentNameTextView, WeakReference<TextView> apartmentNumTextView, WeakReference<TextView> numberOfRoommatesTextview) {
        this.activity = activity;
        this.apartmentNameTextView = apartmentNameTextView;
        this.apartmentNumTextView = apartmentNumTextView;
        this.numberOfRoommatesTextview = numberOfRoommatesTextview;

    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .addHeader(NodeService.TOKEN_HEADER_KEY, activity.get().getJwtFromSharedPreference())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(Objects.requireNonNull(response.body()).string());


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(JSONObject responseJson) {
        if (responseJson == null) {
            activity.get().showToast("Try again later");
            return;
        }
        try {
            if (responseJson.has(NodeService.MESSAGE_KEY) && "success" .equals(responseJson.getString(NodeService.MESSAGE_KEY)) && responseJson.has(NodeService.DATA_KEY)) {
                Apartment.parseDataFromJson(responseJson.getJSONObject(NodeService.DATA_KEY));
                Apartment apartment = Apartment.getInstance();
                apartmentNameTextView.get().setText("Apartment name: ".concat(apartment.getName()));
                apartmentNumTextView.get().setText("Apartment id: ".concat(String.valueOf(apartment.getId())));
                numberOfRoommatesTextview.get().setText("Number of people: ".concat(String.valueOf(apartment.getNumberOfPeople())));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
