package com.example.spotifywrapped;

public class PastYears {
    private int year;
    private String user;
    private String month;

    public PastYears(String user, String month, int year) {
        this.user = user;
        this.month = month;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
