package com.example.spotifywrapped;

public class PastYears {
    private String user;
    private String month_year;

    public PastYears(String user, String month_year) {
        this.user = user;
        this.month_year = month_year;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMonth_year() {
        return month_year;
    }

    public void setMonth_year(String month_year) {
        this.month_year = month_year;
    }
}
