package com.university.rahim.softecapp.Utils;

/**
 * Created by RAHIM on 3/10/2018.
 */

public class DistanceCalculator {
    double height;
    private double getStepLength(){
        return 0.01*0.42*height;
    }
    public DistanceCalculator(double _height){
        height=_height;
    }
    public double distanceWalkedinMeters(int _stepsTaken){
        return getStepLength()*_stepsTaken;
    }
}
