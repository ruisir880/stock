package com.ray.logic.model;


import org.joda.time.DateTime;

public class StockTuple {
    private DateTime time;
    private double price;

    public StockTuple(DateTime time, double price) {
        this.time = time;
        this.price = price;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
