package com.example.room8.model;

import com.example.room8.database.NodeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {

    public static final String ID_KEY = "ID";
    public static final String APARTMENT_ID_KEY = "apartment_ID";
    public static final String CREATOR_ID_KEY = "creator_ID";
    public static final String TASK_TYPE_KEY = "task_type";
    public static final String CREATE_TIME_KEY = "create_time";
    public static final String EXPIRATION_DATE_KEY = "expiration_date";
    public static final String TITLE_KEY = "title";
    public static final String NOTE_KEY = "";
    public static final String ICON_PATH_KEY = "icon_path";

    public static final String[] TASK_TYPES = {"general task", "something"};


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

    /**
     * copy constructor
     *
     * @param o
     */
    public Task(Task o) {
        this.id = o.id;
        this.apartmentId = o.apartmentId;
        this.creatorId = o.creatorId;
        this.taskType = o.taskType;
        if (o.getCreateDate() != null)
            this.createDate = new Date(o.getCreateDate().getTime());
        if (o.expirationDate != null)
            this.expirationDate = new Date(o.getExpirationDate().getTime());
        this.title = o.title;
        this.note = o.note;
    }

    public Task() {
    }

    /**
     * copy value frrom one task to another
     *
     * @param o
     */
    public void setValues(Task o) {
        this.id = o.id;
        this.apartmentId = o.apartmentId;
        this.creatorId = o.creatorId;
        this.taskType = o.taskType;
        this.createDate = o.getCreateDate();
        this.expirationDate = o.getExpirationDate();
        this.title = o.title;
        this.note = o.note;
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

    public boolean shouldUpdateTask(Task other) {
        if (this.creatorId != other.creatorId)
            return true;

        if (this.expirationDate == null) {
            if (other.expirationDate != null) {
                return true;
            }
        } else {
            if (!this.expirationDate.equals(other.expirationDate)) {
                return true;
            }
        }

        if (this.taskType == null) {
            if (other.taskType != null) {
                return true;
            }
        } else {
            if (!this.taskType.equals(other.taskType))
                return true;
        }

        if (this.title == null) {
            if (other.title != null) {
                return true;
            }
        } else {
            if (!this.title.equals(other.title))
                return true;
        }

        if (this.note == null) {
            if (other.note != null) {
                return true;
            }
        } else {
            if (!this.note.equals(other.note))
                return true;
        }

        return false;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", apartmentId=" + apartmentId +
                ", creatorId=" + creatorId +
                ", taskType='" + taskType + '\'' +
                ", createDate=" + createDate +
                ", expirationDate=" + expirationDate +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                '}';
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
