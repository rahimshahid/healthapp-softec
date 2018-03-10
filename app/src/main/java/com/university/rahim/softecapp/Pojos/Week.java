package com.university.rahim.softecapp.Pojos;

import java.util.List;

public class Week {

    private List<Day> days;

    public Week(){}

    public Week(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return days;
    }

    @Override
    public String toString() {
        String str = "Week{" +
                "Days=";

        for(Day day : days)
            str = str + day.toString();

        return str;
    }
}
