package com.example.room8.database;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.adapters.MessagesAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatHandler implements TextWatcher {
    private String name; // the name of the user

    private WebSocket webSocket; // the socket
    private static final String PATH = "/messages";
    private static final String SERVER_PATH = "ws://" + NodeService.SERVER_BASE_URL + PATH; // the url of the server  //demo echo socket - ws://echo.websocket.org


    Activity activity;

    private EditText messageEdit;  // the message input
    private View sendBtn; // the btn of sending message and image

    private RecyclerView recyclerView; // the recyclerView of the messages
    private MessagesAdapter messageAdapter; // the recyclerView adapter


    public ChatHandler(Activity activity,String name, EditText messageEdit, View sendBtn, RecyclerView recyclerView, MessagesAdapter messageAdapter) {
        this.activity = activity;
        this.name = name;
        this.messageEdit = messageEdit;
        this.sendBtn = sendBtn;
        this.recyclerView = recyclerView;
        this.messageAdapter = messageAdapter;
    }
    public void initializeSocketConnection() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder() //okhttp3
                .url(SERVER_PATH)
                .addHeader("x-auth-token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjcsImFwYXJ0bWVudElkIjoxLCJpYXQiOjE2NTM4OTU5OTYsImV4cCI6MTY4NTQzMTk5Nn0.3_J8E0g-3QD2Ho3zeE7NwHyCORHV3PjqpHW2Js2HdfA")
                .build();
        webSocket = client.newWebSocket(request, new SocketListener());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    // check if the message edit text have text, if true we set the btn to be sent and not the image picker
    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().isEmpty())
            resetMessageEdit();
    }

    /**
     * reset the btn and the messageEdit to default
     */
    private void resetMessageEdit() {
        messageEdit.removeTextChangedListener(this);
        messageEdit.setText("");
        messageEdit.addTextChangedListener(this);
    }

    /**
     * Listen to socket opened and inputs
     */
    private class SocketListener extends WebSocketListener {

        // what will happen when the socket will open
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Connection", Toast.LENGTH_SHORT).show();
                initializeView();
            });
        }

        // socket input
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);

            activity.runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);

                    messageAdapter.addMessage(jsonObject);

                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    /**
     * When the socket is open the method will run
     * initialize the component on the activity
     */
    private void initializeView() {

        messageEdit.addTextChangedListener(this); // add listener to text changes

        sendBtn.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("message", messageEdit.getText().toString());
                jsonObject.put("isSent", true);

                webSocket.send(jsonObject.toString());

                messageAdapter.addMessage(jsonObject);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

                resetMessageEdit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

}
