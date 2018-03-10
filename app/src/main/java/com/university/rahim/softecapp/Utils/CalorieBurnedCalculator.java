package com.university.rahim.softecapp.Utils;

/**
 * Created by RAHIM on 3/10/2018.
 */

public class CalorieBurnedCalculator {
    private double weight; // kg

    private double height; // cm


    final static double walkingFactor = 0.57;

    static double CaloriesBurnedPerMile;

    static double strip;

    static double stepCountMile; // step/mile

    static double conversationFactor;

    private double CaloriesBurned;

    public CalorieBurnedCalculator(double _weight, double _height){
        weight=_weight;
        height=_height;
    }

    public double caloriesBurned(int stepsCount) {

        CaloriesBurnedPerMile = walkingFactor * (weight * 2.2);

        strip = height * 0.415;

        stepCountMile = 160934.4 / strip;

        conversationFactor = CaloriesBurnedPerMile / stepCountMile;

        CaloriesBurned = stepsCount * conversationFactor;

        return CaloriesBurned;

    }
}
