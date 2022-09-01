package com.example.room8.model;

import androidx.annotation.NonNull;

import com.example.room8.database.ServerRequestsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Expense {
    private static final String ID_KEY = "ID";
    private static final String USER_ID_KEY = "UserThatUploadID";
    private static final String TITLE_KEY = "title";
    private static final String TYPE_KEY = "expense_type";
    private static final String AMOUNT_KEY = "amount";
    private static final String PAYMENT_DATE_KEY = "payment_date";
    private static final String UPLOAD_DATE_KEY = "upload_date";
    private static final String NOTE_KEY = "note";

    private static final String[] TASK_TYPES = {"general expense", "electric bill", "water bill", "groceries"};


    private int id;
    private int userId;
    private String title;
    private String type;
    private double amount;
    private Date paymentDate;
    private Date uploadDate;
    private String note;

    public Expense(JSONObject expenseAsJson) throws ParseException, JSONException {
        if (expenseAsJson == null) return;

        if (expenseAsJson.has(ID_KEY) && !expenseAsJson.isNull(ID_KEY))
            this.id = expenseAsJson.getInt(ID_KEY);

        if (expenseAsJson.has(USER_ID_KEY) && !expenseAsJson.isNull(USER_ID_KEY))
            this.userId = expenseAsJson.getInt(USER_ID_KEY);

        if (expenseAsJson.has(TITLE_KEY))
            this.title = expenseAsJson.getString(TITLE_KEY);

        if (expenseAsJson.has(TYPE_KEY))
            this.type = expenseAsJson.getString(TYPE_KEY);

        if (expenseAsJson.has(AMOUNT_KEY) && !expenseAsJson.isNull(AMOUNT_KEY))
            this.amount = expenseAsJson.getDouble(AMOUNT_KEY);

        if (expenseAsJson.has(PAYMENT_DATE_KEY) && !expenseAsJson.isNull(PAYMENT_DATE_KEY))
            this.paymentDate =
                    ServerRequestsService.DATE_TIME_FORMAT.parse(expenseAsJson.getString(PAYMENT_DATE_KEY));

        if (expenseAsJson.has(UPLOAD_DATE_KEY) && !expenseAsJson.isNull(UPLOAD_DATE_KEY))
            this.uploadDate =
                    ServerRequestsService.DATE_TIME_FORMAT.parse(expenseAsJson.getString(UPLOAD_DATE_KEY));

        if (expenseAsJson.has(NOTE_KEY) && !expenseAsJson.isNull(NOTE_KEY))
            this.note = expenseAsJson.getString(NOTE_KEY);
    }


    public void update(Expense o) {
        this.userId = o.userId;
        this.title = o.title;
        this.type = o.type;
        this.amount = o.amount;
        this.paymentDate = o.paymentDate;
        this.note = o.note;
    }

    @NonNull
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", uploadDate=" + uploadDate +
                ", note='" + note + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
