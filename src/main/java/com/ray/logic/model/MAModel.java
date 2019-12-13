package com.ray.logic.model;

public enum MAModel {
    MA10(10),
    MA20(20),
    MA60(60),
    MA120(120),
    RISE8(8),
    DOWN5(5),
    DOWN5_CLOSE_BUY(0);

    private int dayNum;

    public int getDayNum() {
        return dayNum;
    }

    private MAModel(int dayNum) {
        this.dayNum = dayNum;
    }
}
