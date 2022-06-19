package com.example.room8.database;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.MainActivity;
import com.example.room8.adapters.MessagesAdapter;
import com.example.room8.model.Message;
import com.example.room8.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatHandler {

    private WebSocket webSocket; // the socket
    private static final String PATH = "/messages";
    private static final String SERVER_PATH = "ws://" + NodeService.SERVER_BASE_URL + PATH; // the url of the server


    MainActivity activity;

    private final EditText messageEdit;  // the message input
    private final View sendBtn; // the btn of sending message and image

    private final RecyclerView recyclerView; // the recyclerView of the messages
    private final MessagesAdapter messageAdapter; // the recyclerView adapter


    public ChatHandler(MainActivity activity, EditText messageEdit, View sendBtn, RecyclerView recyclerView, MessagesAdapter messageAdapter) {
        this.activity = activity;
        this.messageEdit = messageEdit;
        this.sendBtn = sendBtn;
        this.recyclerView = recyclerView;
        this.messageAdapter = messageAdapter;

    }

    /**
     * initialize the socket connection for the messaging
     */
    public void initializeSocketConnection() {
        // get the jwt token from the shared preferences
        String jwtToken = activity.getSharedPreferences(MainActivity.JWT_SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(MainActivity.JWT_TOKEN, null);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder() //okhttp3
                .url(SERVER_PATH)
                .addHeader(NodeService.TOKEN_HEADER_KEY, jwtToken)
                .build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    /**
     * reset the btn and the messageEdit to default
     */
    private void resetMessageEdit() {
        messageEdit.setText("");
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
                sendMessageBtn();
            });
        }

        // socket input
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            activity.runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    if (jsonObject.has("insertId")) { // if we get back the message id from the data base
                        messageAdapter.setMessageIdByUUID(jsonObject.getInt("insertId"), jsonObject.getString("messageUUID"));
                    } else {
                        messageAdapter.addMessage(jsonObject);
                    }
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    /**
     * When the socket is open the method will run
     * initialize the component on the activity
     */
    private void sendMessageBtn() {
        User user = User.getInstance();
        sendBtn.setOnClickListener(v -> {
            Message message = new Message(user.getUserName(), messageEdit.getText().toString(), new Date(), user.getProfileIconId(), true);
            webSocket.send(message.toStringJsonFormat());
            messageAdapter.addMessage(message);
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            resetMessageEdit();
        });
    }

}
