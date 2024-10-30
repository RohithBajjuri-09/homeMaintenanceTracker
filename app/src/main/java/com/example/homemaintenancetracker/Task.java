package com.example.homemaintenancetracker;


public class Task {
    private int id;
    private String description;
    private String date;
    private String provider;

    public Task() {}

    public Task(int id, String description, String date, String provider) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.provider = provider;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
