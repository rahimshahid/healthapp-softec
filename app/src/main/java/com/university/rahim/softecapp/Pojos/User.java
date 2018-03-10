package com.university.rahim.softecapp.Pojos;

import java.util.Date;

public class User {

    private String name;
    private double height;
    private double weight;
    private Date DOB;

    User(){}

    public User(String name, double height, double weight, Date DOB) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.DOB = DOB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }
}
