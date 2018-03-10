package com.university.rahim.softecapp.Pojos;

import java.util.List;

public class Leaderboard {
    private List<Points> pointsList;

    public Leaderboard(){}

    public Leaderboard(List<Points> pointsList) {
        this.pointsList = pointsList;
        sortList(pointsList);
    }

    public List<Points> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<Points> pointsList) {
        this.pointsList = pointsList;
    }

    private void sortList(List<Points> list){
        int n = list.size();
        Points temp;

        for(int i=0; i < n; i++){
            for(int j=1; j < (n-i); j++){
                if(list.get(j-1).getPoints() < list.get(j).getPoints()){

                    temp = list.get(j-1);
                    list.set(j-1, list.get(j));
                    list.set(j, temp);
                }

            }
        }

    }
}
