package com.ray.logic;

import com.ray.logic.model.*;

import java.util.LinkedList;
import java.util.List;

import static com.ray.constants.Constant.*;

public class StockDeal {
    private int HAND =100;
    private double remainMoney =START_FUND;

    private LinkedList<Double> priceList;
    private DealRecord dealRecord;

    private boolean hasBuyMa10 = false;
    private boolean hasBuyMa20 = false;
    private boolean hasBuyMa60 = false;
    private boolean hasBuyMa120 = false;

    private int hand10= 0;
    private int hand20= 0;
    private int hand60= 0;
    private int hand120= 0;

    public void process(StockInputModel input) {
        dealRecord  = new DealRecord(input.getName());
        int index = getMaList(input);

        double currentPrice;
        StockTuple currentStockTuple;
        double ma10 = getMaAvg(10);
        double ma20 = getMaAvg(20);
        double ma60 = getMaAvg(60);
        double ma120 = getMaAvg(120);

        StockTuple temp;
        StockTuple lowPoint = input.getStockTuples().get(index);
        StockTuple highPoint = input.getStockTuples().get(index);

        for (int i = index; i < input.getStockTuples().size(); i++) {
            currentStockTuple = input.getStockTuples().get(i);
            currentPrice = currentStockTuple.getPrice();

            if(currentPrice <= lowPoint.getPrice()){
                lowPoint = currentStockTuple;
            }else if(currentPrice >= highPoint.getPrice()){
                highPoint = currentStockTuple;
            }

            if (i != index && isAnotherDay(input.getStockTuples().get(i - 1), currentStockTuple)) {
                priceList.removeFirst();
                priceList.add(currentPrice);

                ma10 = getMaAvg(10);
                ma20 = getMaAvg(20);
                ma60 = getMaAvg(60);
                ma120 = getMaAvg(120);
            }

            //判断8出
            if(currentStockTuple.getPrice() >= SELL_RISE_PERCENT*lowPoint.getPrice()){
                if(isIn24h(currentStockTuple,lowPoint)){
                    //卖出;
                    sell(currentStockTuple,MAModel.RISE8);
                }else {
                    lowPoint = currentStockTuple;
                    temp = currentStockTuple;
                    while (isIn24h(temp,currentStockTuple)){
                        if(lowPoint.getPrice() > temp.getPrice()){
                            lowPoint = temp;
                        }
                        temp = temp.getPre();
                    }
                }
            }
            //判断5出
            if(currentStockTuple.getPrice() <= SELL_DOWN_PERCENT*highPoint.getPrice()){
                if(isIn24h(highPoint,currentStockTuple)){
                    //卖出;
                    sell(currentStockTuple,MAModel.DOWN5);
                }else {
                    highPoint = currentStockTuple;
                    temp = currentStockTuple;
                    while (isIn24h(temp,currentStockTuple)){
                        if(temp.getPrice() > highPoint.getPrice()){
                            highPoint = temp;
                        }
                        temp = temp.getPre();
                    }
                }
            }

            //金叉买入
            if(!hasBuyMa10 && currentPrice >= ma10){
                buy(currentStockTuple, MAModel.MA10);
                hasBuyMa10 = true;
            }
            if(!hasBuyMa20 && currentPrice >= ma20){
                buy(currentStockTuple, MAModel.MA20);
                hasBuyMa20 = true;
            }
            if(!hasBuyMa60 && currentPrice >= ma60){
                buy(currentStockTuple, MAModel.MA60);
                hasBuyMa60 = true;
            }
            if(!hasBuyMa120 && currentPrice >= ma120){
                buy(currentStockTuple, MAModel.MA120);
                hasBuyMa120 = true;
            }

            //死叉卖出
            if(hasBuyMa10 && currentPrice <= ma10){
                sell(currentStockTuple, MAModel.MA10);
                hasBuyMa10 = false;
            }
            if(hasBuyMa20 && currentPrice <= ma20){
                sell(currentStockTuple, MAModel.MA20);
                hasBuyMa20=false;
            }
            if(hasBuyMa60 && currentPrice <= ma60){
                sell(currentStockTuple, MAModel.MA60);
                hasBuyMa60 = false;
            }
            if(hasBuyMa120 && currentPrice <= ma120){
                sell(currentStockTuple, MAModel.MA120);
                hasBuyMa120 = false;
            }

            //8卖
        }
    }

    public int getMaList(StockInputModel input) {
        priceList = new LinkedList<>();

        List<StockTuple> stockTuples = input.getStockTuples();
        int index;
        for (index = 1; index < stockTuples.size(); index++) {
            if (isAnotherDay(stockTuples.get(index), stockTuples.get(index - 1))) {
                priceList.add(stockTuples.get(index - 1).getPrice());
            }
            if (priceList.size() == MAModel.MA120.getDayNum() - 1) {
                break;
            }
        }
        return index;
    }

    public double getMaAvg(int n) {
        double result = 0;
        int num = 1;
        for (int i = priceList.size() - 1; i >= 0; i--) {
            result = result + priceList.get(i);
            num++;
            if (num == n) {
                break;
            }
        }
        return result/n-1;
    }

    public boolean isAnotherDay(StockTuple tuple1, StockTuple tuple2) {
        return tuple1.getTime().getDayOfYear() != tuple2.getTime().getDayOfYear();
    }

    public void buy(StockTuple tuple,MAModel maModel){
        int handNum = 0;
        switch (maModel){
            case MA10:
                handNum =hand10 = (int) (SHARE_10_MONEY/(tuple.getPrice()*HAND));
                break;
            case MA20:
                handNum = hand20=(int) (SHARE_20_MONEY/(tuple.getPrice()*HAND));
                break;
            case MA60:
                handNum = hand60=(int) (SHARE_60_MONEY/(tuple.getPrice()*HAND));
                break;
            case MA120:
                handNum = hand120=(int) (SHARE_120_MONEY/(tuple.getPrice()*HAND));
                break;
        }
        remainMoney = remainMoney - tuple.getPrice()*HAND* handNum;
        if(remainMoney <0){
            //todo log error;
        }
        dealRecord.addDealRecord(DealType.BUY, tuple.getTime(), tuple.getPrice(), handNum, maModel);
    }

    public void sell(StockTuple tuple,MAModel maModel){
        int handNum= 0;
        switch (maModel){
            case MA10:
                handNum =hand10 ;
                hand10=0;
                break;
            case MA20:
                handNum=  hand20;
                hand20=0;
                break;
            case MA60:
                handNum =hand60;
                hand60=0;
                break;
            case MA120:
                handNum =hand120;
                hand120=0;
                break;
            case DOWN5:
            case RISE8:
                handNum =hand120;
                hand120=0;
                break;

        }
        remainMoney = remainMoney + tuple.getPrice()*HAND* handNum;
        dealRecord.addDealRecord(DealType.SELL, tuple.getTime(), tuple.getPrice(), handNum, maModel);
    }

    public boolean isIn24h(StockTuple tuple1,StockTuple tuple2){
         return Math.abs(tuple1.getTime().getMillis()-tuple2.getTime().getMillis())<=86400000;
    }

    public DealRecord getDealRecord() {
        return dealRecord;
    }
}
