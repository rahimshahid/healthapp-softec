package com.university.rahim.softecapp.Pojos;


public class Points {
    private String uid;
    private String name;
    private double points;

    public Points() {}

    public Points(String uid, String name, double points) {
        this.uid = uid;
        this.name = name;
        this.points = points;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
