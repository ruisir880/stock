package com.ray.logic.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class DealRecord {
    private String stockName;
    private List<Record> records = new ArrayList<>();

    public DealRecord(String stockName) {
        this.stockName = stockName;
    }

    public void addDealRecord(DealType dealType,DateTime dateTime, double price, double dealAmount, MAModel maModel ){
        records.add(new Record(dealType,dateTime,price,dealAmount,maModel));
    }

    class Record{
        private DealType dealType;
        private DateTime dealTime;
        private double price;
        private double dealAmount;
        private MAModel maModel;

        public Record(DealType dealType, DateTime dealTime, double price, double dealAmount, MAModel maModel) {
            this.dealType = dealType;
            this.dealTime = dealTime;
            this.price = price;
            this.dealAmount = dealAmount;
            this.maModel = maModel;
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

        public DealType getDealType() {
            return dealType;
        }

        public void setDealType(DealType dealType) {
            this.dealType = dealType;
        }

        public MAModel getMaModel() {
            return maModel;
        }

        public void setMaModel(MAModel maModel) {
            this.maModel = maModel;
        }
    }
}
