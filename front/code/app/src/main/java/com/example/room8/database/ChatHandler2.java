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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatHandler {

    
    private static final String PATH = "/messages";
    private static final String SERVER_PATH = "ws://" + ServerRequestsService.SERVER_BASE_URL + PATH; // the url of the server

    private WebSocket webSocket; // the socket
    Consumer<JSONObject> onMessageFunction;
    /**
     * initialize the socket connection for the messaging
     */
    public ChatHandler(Consumer<JSONObject> onMessageFunction) {

        this.onMessageFunction = onMessageFunction;

        String token = SharedPreferenceHandler.getInstance().getAccessJwt();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder() 
                .url(SERVER_PATH)
                .addHeader(ServerRequestsService.TOKEN_HEADER_KEY, token)
                .build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    private sendMsg(Message msg){
        webSocket.send(msg.toStringJsonFormat());
    }

    public void closeConnection(){
        webSocket.close(1000,"end of socket");
    }
    /**
     * Listen to socket opened and inputs
     */
    private class SocketListener extends WebSocketListener {

        // what will happen when the socket will open
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            
        }

        // socket input
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
                try {
                    onMessageFunction.accept(new JSONObject(text))
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

        }
    }

}
