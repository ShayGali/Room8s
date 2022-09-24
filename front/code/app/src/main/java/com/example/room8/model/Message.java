package com.example.room8.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This class represent a message for the chat
 */
public class Message implements Comparator<Message> {

    // the keys of the json object
    private static final String MESSAGE_ID_KEY = "messageId";
    private static final String UUID_KEY = "UUID";
    public static final String USER_NAME_KEY = "user_name";
    public static final String MESSAGE_CONTENT_KEY = "message";
    public static final String DATE_KEY = "timestamp";
    public static final String ICON_ID_KEY = "profile_icon_id";
    public static final String IS_SENT_KEY = "isSent";

    int messageId;
    UUID UUID; // for identify when the message id came back from the server
    String userName;
    String msgContent;
    Date date;
    int iconID; // the ID of the img in the drawable dir
    boolean isSent;

    public Message(String userName, String msgContent, Date date, int iconID, boolean isSent) {
        this.UUID = java.util.UUID.randomUUID();
        this.userName = userName;
        this.msgContent = msgContent;
        this.date = date;
        this.iconID = iconID;
        this.isSent = isSent;
    }

    /**
     * Make message object from json object. <br>
     * the key are: <br>
     * [<br>
     * {@value MESSAGE_ID_KEY}:int,<br>
     * {@value USER_NAME_KEY}:string,<br>
     * {@value MESSAGE_CONTENT_KEY}:string,<br>
     * {@value DATE_KEY}:string(in "yyyy-MM-dd HH:mm:ss" pattern),<br>
     * {@value ICON_ID_KEY}:int,<br>
     * {@value IS_SENT_KEY}:boolean<br>
     * ]<br>
     *
     * @param jsonMessage message in json format
     * @throws JSONException  get the keys from the json object
     * @throws ParseException parse the date
     */
    public Message(JSONObject jsonMessage) throws JSONException, ParseException {
        if (jsonMessage.has(MESSAGE_ID_KEY) && !jsonMessage.isNull(MESSAGE_ID_KEY))
            this.messageId = jsonMessage.getInt(MESSAGE_ID_KEY);

        if (jsonMessage.has(USER_NAME_KEY))
            this.userName = jsonMessage.getString(USER_NAME_KEY);
        else
            this.userName = "user name not found";

        if (jsonMessage.has(MESSAGE_CONTENT_KEY))
            this.msgContent = jsonMessage.getString(MESSAGE_CONTENT_KEY);
        else
            this.msgContent = "";

        if (jsonMessage.has(DATE_KEY)) {
            this.date = ServerRequestsService.DATE_TIME_PARSER.parse(jsonMessage.getString(DATE_KEY));
        } else
            this.date = new Date();
        if (jsonMessage.has(ICON_ID_KEY) && !jsonMessage.isNull(ICON_ID_KEY))
            this.iconID = jsonMessage.getInt(ICON_ID_KEY);
        else
            this.iconID = R.drawable.ic_launcher_foreground;
        if (jsonMessage.has(IS_SENT_KEY))
            this.isSent = jsonMessage.getBoolean(IS_SENT_KEY);
        else
            this.isSent = false;
    }


    public String toStringJsonFormat() {
        String str = "{\"" +
                UUID_KEY + "\":\"" + this.UUID.toString() + "\",\"" +
                USER_NAME_KEY + "\":\"" + this.userName + "\",\"" +
                MESSAGE_CONTENT_KEY + "\":\"" + this.msgContent + "\",\"" +
                ICON_ID_KEY + "\":\"" + this.iconID + "\",\"" +
                IS_SENT_KEY + "\":" + this.isSent;
        return str + "}";
    }

    /**
     * @return Only the date from the DateTime object
     */
    public String getDateFormat() {
        return ServerRequestsService.DATE_FORMAT.format(this.date);
    }


    /**
     * @return Only the time from the DateTime object
     */
    public String getTimeFormat() {
        return ServerRequestsService.TIME_FORMAT.format(this.date);
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", UUID=" + UUID +
                ", userName='" + userName + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", date=" + date +
                ", iconID=" + iconID +
                ", isSent=" + isSent +
                '}';
    }

    public String getUserName() {
        return userName;
    }


    public String getMsgContent() {
        return msgContent;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIconID() {
        return iconID;
    }


    public boolean isSent() {
        return isSent;
    }


    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public UUID getUUID() {
        return UUID;
    }


    @Override
    public int compare(Message o1, Message o2) {
        return o1.date.compareTo(o2.date);
    }
}
