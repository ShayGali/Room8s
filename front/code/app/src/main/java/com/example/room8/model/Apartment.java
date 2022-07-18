package com.example.room8.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

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
    private int number_of_people;

    public static void parseDataFromJson(JSONObject apartmentAsJson) throws JSONException {
        instance = getInstance(); // for create if not exists
        if (apartmentAsJson.has(ID_KEY) && !apartmentAsJson.isNull(ID_KEY))
            instance.setId(apartmentAsJson.getInt(ID_KEY));
        if (apartmentAsJson.has(NAME_KEY))
            instance.setName(apartmentAsJson.getString(NAME_KEY));
        if (apartmentAsJson.has(NUMBER_OF_PEOPLE_KEY) && !apartmentAsJson.isNull(NUMBER_OF_PEOPLE_KEY))
            instance.setNumber_of_people(apartmentAsJson.getInt(NUMBER_OF_PEOPLE_KEY));
    }


    public Apartment() {

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

    public int getNumber_of_people() {
        return number_of_people;
    }

    public void setNumber_of_people(int number_of_people) {
        this.number_of_people = number_of_people;
    }

    @NonNull
    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number_of_people=" + number_of_people +
                '}';
    }
}
