package com.university.rahim.softecapp.Pojos;

import java.util.List;

public class Day {

    private List<Workout> exercises;

    public Day(){}

    public Day(List<Workout> exercises) {
        this.exercises = exercises;
    }

    public List<Workout> getExercises() {
        return exercises;
    }

    @Override
    public String toString() {
        String str = "Day{" +
                "exercises=";

        for(Workout workout : exercises)
            str = str + exercises.toString();

        return str;
    }
}
