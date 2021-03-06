package com.example.room8.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    private List<Task> tasks;

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

    @NonNull
    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number_of_people=" + numberOfPeople +
                '}';
    }
}
