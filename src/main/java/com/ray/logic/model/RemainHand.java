package com.ray.logic.model;

public class RemainHand {
    private int todayHand;

    private int handSum = 0;

    private int share = 0;


    public int getHandSum() {
        return handSum;
    }

    public int getShare() {
        return share;
    }

    public int getTodayHand() {
        return todayHand;
    }

    public void dateChange(){
        todayHand = 0;
    }

    public int subHand(){
        int subNum = handSum/share;
        handSum = handSum - subNum;
        share--;
        return subNum;
    }

    public void addHand(int handNum){
        todayHand = todayHand + handNum;
        handSum = handNum+ handSum;
        share++;
    }

}
