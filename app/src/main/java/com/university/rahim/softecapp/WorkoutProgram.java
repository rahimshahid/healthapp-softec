package com.university.rahim.softecapp;

import java.util.ArrayList;

public class WorkoutProgram {

    public void addWorkouts(int week, int day) {
        // TODO: get workouts from db
        workouts.add( new Workout("Pushups",3, 12,34.7));

    }

    int week;
    int day;

    ArrayList<Workout> workouts;

    public WorkoutProgram(int week, int day){
        this.week = week;
        this.day = day;

        workouts = new ArrayList<>();

        addWorkouts(week, day);
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }
}
