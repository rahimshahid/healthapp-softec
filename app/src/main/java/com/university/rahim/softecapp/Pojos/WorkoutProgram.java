package com.university.rahim.softecapp.Pojos;

import java.util.List;

public class WorkoutProgram {

    List<Week> weeks;

    public WorkoutProgram(){}

    public WorkoutProgram(List<Week> weeks) {
        this.weeks = weeks;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    @Override
    public String toString() {

        String str = "Program{" +
                "weeks=";

        for(Week week : weeks)
            str = str + week.toString();

        return str;
    }
}
