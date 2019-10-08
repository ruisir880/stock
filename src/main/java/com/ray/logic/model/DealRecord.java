package com.ray.logic.model;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public class DealRecord {
    private String stockName;
    private List<Record> records;

    public DealRecord(String stockName) {
        this.stockName = stockName;
    }

    public void addDealRecord(DateTime dateTime, double price, double dealAmount, Map<MAModel,Double> maPrice ){
        records.add(new Record(dateTime,price,dealAmount));
    }

    class Record{
        private DateTime dealTime;
        private double price;
        private double dealAmount;
        private Map<MAModel,Double> maPrice;

        public Record(DateTime dealTime, double price, double dealAmount) {
            this.dealTime = dealTime;
            this.price = price;
            this.dealAmount = dealAmount;
        }

        public Record(DateTime dealTime, double price, double dealAmount, Map<MAModel, Double> maPrice) {
            this.dealTime = dealTime;
            this.price = price;
            this.dealAmount = dealAmount;
            this.maPrice = maPrice;
        }

        public DateTime getDealTime() {
            return dealTime;
        }

        public void setDealTime(DateTime dealTime) {
            this.dealTime = dealTime;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getDealAmount() {
            return dealAmount;
        }

        public void setDealAmount(double dealAmount) {
            this.dealAmount = dealAmount;
        }

        public Map<MAModel, Double> getMaPrice() {
            return maPrice;
        }

        public void setMaPrice(Map<MAModel, Double> maPrice) {
            this.maPrice = maPrice;
        }
    }
}
