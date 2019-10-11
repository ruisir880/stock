package com.ray.logic.model;

public class RemainHand {
    private int handSum = 0;

    private int share = 0;


    public int getHandSum() {
        return handSum;
    }

    public int getShare() {
        return share;
    }

    public int subHand(){
        int subNum = handSum/share;
        handSum = handSum - subNum;
        share--;
        return subNum;
    }

    public void addHand(int handNum){
        handSum = handNum+ handSum;
        share++;
    }

}
