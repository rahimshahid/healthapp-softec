package com.university.rahim.softecapp.Pojos;

import java.io.Serializable;

public class Workout implements Serializable {

    private String name;
    private String instructions;
    private double baseCalorieBurn;

    public Workout() {}

    public Workout(String name, String instructions, double baseCalorieBurn) {
        this.name = name;
        this.instructions = instructions;
        this.baseCalorieBurn = baseCalorieBurn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public double getBaseCalorieBurn() {
        return baseCalorieBurn;
    }

    public void setBaseCalorieBurn(double baseCalorieBurn) {
        this.baseCalorieBurn = baseCalorieBurn;
    }



    @Override
    public String toString() {
        return "Workout{" +
                "name='" + name + '\'' +
                ", instructions='" + instructions + '\'' +
                ", baseCalorieBurn=" + baseCalorieBurn +
                '}';
    }
}
