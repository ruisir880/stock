package com.ray.logic.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DealRecord {
    private String stockName;
    private List<Record> records = new ArrayList<>();
    private BigDecimal remainMoney;

    public DealRecord(String stockName) {
        this.stockName = stockName;
    }

    public void addDealRecord(
            DealType dealType,
            DateTime dateTime,
            BigDecimal price,
            int dealAmount,
            MAModel maModel,
            BigDecimal remainMoney,
            int stockNum,
            int share) {
        records.add(
                new Record(
                        dealType,
                        dateTime,
                        price,
                        dealAmount,
                        maModel,
                        remainMoney,
                        stockNum,
                        share));
    }

    public String getStockName() {
        return stockName;
    }

    public List<Record> getRecords() {
        return records;
    }

    public BigDecimal getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(BigDecimal remainMoney) {
        this.remainMoney = remainMoney;
    }

    public class Record {
        private DealType dealType;
        private DateTime dealTime;
        private BigDecimal price;
        private int dealAmount;
        private MAModel maModel;
        private BigDecimal remainMoney;
        private int remainStockNum;
        private int share;

        public Record(
                DealType dealType,
                DateTime dealTime,
                BigDecimal price,
                int dealAmount,
                MAModel maModel,
                BigDecimal remainMoney,
                int remainStockNum,
                int share) {
            this.dealType = dealType;
            this.dealTime = dealTime;
            this.price = price;
            this.dealAmount = dealAmount;
            this.maModel = maModel;
            this.remainMoney = remainMoney;
            this.remainStockNum = remainStockNum;
            this.share = share;
        }

        public DateTime getDealTime() {
            return dealTime;
        }

        public void setDealTime(DateTime dealTime) {
            this.dealTime = dealTime;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public int getDealAmount() {
            return dealAmount;
        }

        public void setDealAmount(int dealAmount) {
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

        public BigDecimal getRemainMoney() {
            return remainMoney;
        }

        public void setRemainMoney(BigDecimal remainMoney) {
            this.remainMoney = remainMoney;
        }

        public int getRemainStockNum() {
            return remainStockNum;
        }

        public void setRemainStockNum(int remainStockNum) {
            this.remainStockNum = remainStockNum;
        }

        public int getShare() {
            return share;
        }

        public void setShare(int share) {
            this.share = share;
        }
    }
}
