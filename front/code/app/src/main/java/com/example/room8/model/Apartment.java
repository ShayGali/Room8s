package com.example.room8.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class Apartment {

    private static volatile Apartment instance;

    public static Apartment getInstance() {
        if (instance == null) {
            synchronized (Apartment.class) {
                if (instance == null)
                    instance = new Apartment();
            }
        }
        return instance;
    }

    private static final String ID_KEY = "ID";
    private static final String NAME_KEY = "apartment_name";
    private static final String NUMBER_OF_PEOPLE_KEY = "number_of_people";

    private int id;
    private String name;
    private int numberOfPeople;

    private final ArrayList<Roommate> roommates;
    private final ArrayList<Task> tasks;
    private final ArrayList<Expense> expenses;


    public static void parseDataFromJson(JSONObject apartmentAsJson) throws JSONException {
        instance = getInstance(); // for create if not exists
        if (apartmentAsJson.has(ID_KEY) && !apartmentAsJson.isNull(ID_KEY))
            instance.setId(apartmentAsJson.getInt(ID_KEY));
        if (apartmentAsJson.has(NAME_KEY))
            instance.setName(apartmentAsJson.getString(NAME_KEY));
        if (apartmentAsJson.has(NUMBER_OF_PEOPLE_KEY) && !apartmentAsJson.isNull(NUMBER_OF_PEOPLE_KEY))
            instance.setNumberOfPeople(apartmentAsJson.getInt(NUMBER_OF_PEOPLE_KEY));

    }

    private Apartment() {
        this.roommates = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public static void resetData() {
        instance = null;
    }

    public String getRoom8NameById(int id) {
        for (Roommate r : roommates) {
            if (r.getId() == id) return r.getUserName();
        }
        return "";
    }

    public void addRoommate(Roommate r) {
        roommates.removeIf(roommate -> roommate.getId() == r.getId());
        roommates.add(r);
    }

    public void addTask(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId()) {
                t.updateTask(task);
                return;
            }
        }
        tasks.add(task);
    }

    public void addExpense(Expense expense) {
        for (Expense e : expenses) {
            if (e.getId() == expense.getId()) {
                e.update(expense);
                return;
            }
        }
        expenses.add(expense);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public ArrayList<Roommate> getRoommates() {
        return roommates;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    @NonNull
    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", roommates=" + roommates +
                ", tasks=" + tasks +
                '}';
    }


}
