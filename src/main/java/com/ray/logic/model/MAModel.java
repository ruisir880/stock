package com.ray.logic.model;

public enum  MAModel {
    MA10(10),
    MA20(20),
    MA60(60),
    MA120(120);

    private int DAYNUM;

    public int getDAYNUM() {
        return DAYNUM;
    }

    private MAModel(int dayNum){
        this.DAYNUM = dayNum;
    }
}
