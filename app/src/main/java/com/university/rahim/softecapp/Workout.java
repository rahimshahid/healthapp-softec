package com.university.rahim.softecapp;


public class Workout {

    private String name;
    private int sets;
    private int reps;
    private double baseCalorieBurn;

    public Workout(String name, int sets, int reps, double baseCalorieBurn) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.baseCalorieBurn = baseCalorieBurn;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public double getBaseCalorieBurn() {
        return baseCalorieBurn;
    }
}
