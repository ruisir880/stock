package com.ray.logic.model;

public enum MAModel {
    MA10(10, "MA10"),
    MA20(20, "MA20"),
    MA60(60, "MA60"),
    MA120(120, "MA120"),
    RISE8(8, "Rise_8%"),
    DOWN5(5, "Down_5%"),
    DOWN5_CLOSE_BUY(0, "Down_5_Close_price");

    private int dayNum;

    private String code;

    private MAModel(int dayNum, String code) {
        this.dayNum = dayNum;
        this.code = code;
    }

    public int getDayNum() {
        return dayNum;
    }

    private MAModel(int dayNum) {
        this.dayNum = dayNum;
    }

    public String getCode() {
        return code;
    }
}
