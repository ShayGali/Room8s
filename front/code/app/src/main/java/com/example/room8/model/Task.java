package com.example.room8.model;

import androidx.annotation.NonNull;

import com.example.room8.database.NodeService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task implements Comparable<Task> {

    public static final String ID_KEY = "ID";
    public static final String APARTMENT_ID_KEY = "apartment_ID";
    public static final String CREATOR_ID_KEY = "creator_ID";
    public static final String TASK_TYPE_KEY = "task_type";
    public static final String CREATE_TIME_KEY = "create_time";
    public static final String EXPIRATION_DATE_KEY = "expiration_date";
    public static final String TITLE_KEY = "title";
    public static final String NOTE_KEY = "note";
    public static final String EXECUTORS_IDS = "executors_ids";
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
    List<Integer> executorsIds;

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
        this.executorsIds = t.executorsIds;

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
        if (o.executorsIds != null)
            this.executorsIds = new ArrayList<>(o.executorsIds);
    }

    public Task() {
    }

    /**
     * copy value from one task to another
     *
     * @param o
     */
    public void copyValues(Task o) {
        this.id = o.id;
        this.apartmentId = o.apartmentId;
        this.creatorId = o.creatorId;
        this.taskType = o.taskType;
        this.createDate = o.createDate;
        this.expirationDate = o.expirationDate;
        this.title = o.title;
        this.note = o.note;
        this.executorsIds = o.executorsIds;
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
        if (taskAsJson.has(EXPIRATION_DATE_KEY) && !taskAsJson.isNull(EXPIRATION_DATE_KEY))
            tempTask.setExpirationDate(NodeService.DATE_TIME_FORMAT.parse(taskAsJson.getString(EXPIRATION_DATE_KEY)));
        if (taskAsJson.has(TITLE_KEY) && !taskAsJson.isNull(TITLE_KEY))
            tempTask.setTitle(taskAsJson.getString(TITLE_KEY));
        if (taskAsJson.has(NOTE_KEY) && !taskAsJson.isNull(NOTE_KEY))
            tempTask.setNote(taskAsJson.getString(NOTE_KEY));
        if (taskAsJson.has(EXPIRATION_DATE_KEY)) {
            JSONArray executorsIdsJson = taskAsJson.getJSONArray(EXECUTORS_IDS);
            ArrayList<Integer> executorsIds = new ArrayList<>();
            for (int i = 0; i < executorsIdsJson.length(); i++) {
                executorsIds.add(executorsIdsJson.getInt(i));
            }
            tempTask.setExecutorsIds(executorsIds);
        }

        return tempTask;
    }

    public boolean shouldUpdateTask(Task other) {
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
        if (this.executorsIds == null) {
            if (other.executorsIds != null) {
                return true;
            }
        } else {
            if (other.executorsIds == null) {
                return false;
            }
            for (int id : other.executorsIds) {
                if (!this.executorsIds.contains(id)) return true;
            }
            for (int id : executorsIds) {
                if (!other.executorsIds.contains(id)) return true;
            }
        }

        return false;
    }

    public void updateTask(Task o) {
        this.taskType = o.taskType;
        this.expirationDate = o.getExpirationDate();
        this.title = o.title;
        this.note = o.note;
    }


    @NonNull
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
                ", executorsIds=" + executorsIds +
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

    public List<Integer> getExecutorsIds() {
        return executorsIds;
    }

    public void setExecutorsIds(List<Integer> executorsIds) {
        this.executorsIds = executorsIds;
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.getId(), o.getId());
    }


}
