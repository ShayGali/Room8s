package com.example.room8.model;

import com.example.room8.database.NodeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Task {

    private static final String ID_KEY = "ID";
    private static final String APARTMENT_ID_KEY = "apartment_ID";
    private static final String CREATOR_ID_KEY = "creator_ID";
    private static final String TASK_TYPE_KEY = "task_type";
    private static final String CREATE_TIME_KEY = "create_time";
    private static final String EXPIRATION_DATE_KEY = "expiration_date";
    private static final String TITLE_KEY = "title";
    private static final String NOTE_KEY = "";
    private static final String ICON_PATH_KEY = "icon_path";

    int id;
    int apartmentId;
    int creatorId;
    String taskType;
    Date createDate;
    Date expirationDate;
    String title;
    String note;

    public Task(JSONObject taskAsJson) throws JSONException, ParseException {
        Task t = parseDataFromJson(taskAsJson);
        this.id = t.id;
        this.apartmentId = t.apartmentId;
        this.creatorId = t.creatorId;
        this.taskType = t.taskType;
        this.createDate = t.createDate;
        this.expirationDate = t.expirationDate;
        this.title = t.title;
        this.note = t.note;

    }

    public Task() {
    }

    public static Task parseDataFromJson(JSONObject taskAsJson) throws JSONException, ParseException {
        if (taskAsJson == null) return null;

        Task tempTask = new Task();

        if (taskAsJson.has(ID_KEY) && !taskAsJson.isNull(ID_KEY))
            tempTask.setId(taskAsJson.getInt(ID_KEY));
        if (taskAsJson.has(APARTMENT_ID_KEY) && !taskAsJson.isNull(APARTMENT_ID_KEY))
            tempTask.setApartmentId(taskAsJson.getInt(APARTMENT_ID_KEY));
        if (taskAsJson.has(CREATOR_ID_KEY) && !taskAsJson.isNull(CREATOR_ID_KEY))
            tempTask.setCreatorId(taskAsJson.getInt(CREATOR_ID_KEY));
        if (taskAsJson.has(TASK_TYPE_KEY))
            tempTask.setTaskType(taskAsJson.getString(TASK_TYPE_KEY));
        if (taskAsJson.has(CREATE_TIME_KEY))
            tempTask.setCreateDate(NodeService.DATE_TIME_FORMAT.parse(taskAsJson.getString(CREATE_TIME_KEY)));
        if (taskAsJson.has(EXPIRATION_DATE_KEY))
            tempTask.setExpirationDate(NodeService.DATE_TIME_FORMAT.parse(taskAsJson.getString(EXPIRATION_DATE_KEY)));
        if (taskAsJson.has(TITLE_KEY))
            tempTask.setTitle(taskAsJson.getString(TITLE_KEY));
        if (taskAsJson.has(NOTE_KEY))
            tempTask.setNote(taskAsJson.getString(NOTE_KEY));

        return tempTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
