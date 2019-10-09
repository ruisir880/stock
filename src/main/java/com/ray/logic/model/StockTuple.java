package com.ray.logic.model;


import org.joda.time.DateTime;

public class StockTuple {
    private DateTime time;
    private double price;
    private StockTuple pre;
    private StockTuple next;

    public StockTuple(DateTime time, double price) {
        this.time = time;
        this.price = price;
    }

    public StockTuple getPre() {
        return pre;
    }

    public void setPre(StockTuple pre) {
        this.pre = pre;
    }

    public StockTuple getNext() {
        return next;
    }

    public void setNext(StockTuple next) {
        this.next = next;
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
