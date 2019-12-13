package com.ray.logic;

import com.ray.Util;
import com.ray.constants.Constant;
import com.ray.exception.TradLogicException;
import com.ray.logic.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static com.ray.constants.Constant.*;

public class StockDeal {
    private static final Logger log = LoggerFactory.getLogger(StockDeal.class);
    // private int HAND = 100;
    private BigDecimal remainMoney = START_FUND;

    private LinkedList<BigDecimal> priceList;
    private DealRecord dealRecord;

    private boolean hasBuyMa10 = false;
    private boolean hasBuyMa20 = false;
    private boolean hasBuyMa60 = false;
    private boolean hasBuyMa120 = false;
    private boolean hasSellRise8 = false;
    private boolean hasSellDown5 = false;

    private RemainStock remainStock = new RemainStock();

    public void process(StockInputModel input) throws TradLogicException {
        dealRecord = new DealRecord(input.getName());
        int index = getMaList(input);

        BigDecimal currentPrice;
        StockTuple currentStockTuple;
        BigDecimal ma10 = getMaAvg(10);
        BigDecimal ma20 = getMaAvg(20);
        BigDecimal ma60 = getMaAvg(30);
        BigDecimal ma120 = getMaAvg(40);

        StockTuple temp;
        StockTuple lowPoint = input.getStockTuples().get(index);
        StockTuple highPoint = input.getStockTuples().get(index);
        boolean toadySell = false;

        for (int i = index; i < input.getStockTuples().size(); i++) {
            currentStockTuple = input.getStockTuples().get(i);
            currentPrice = currentStockTuple.getPrice();

            if (currentPrice.compareTo(lowPoint.getPrice()) <= 0) {
                lowPoint = currentStockTuple;
            } else if (currentPrice.compareTo(highPoint.getPrice()) >= 0) {
                highPoint = currentStockTuple;
            }

            if (i != index && isAnotherDay(input.getStockTuples().get(i - 1), currentStockTuple)) {
                toadySell = false;
                hasSellRise8 = false;
                hasSellDown5 = false;
                remainStock.dateChange();
                priceList.removeFirst();
                priceList.add(input.getStockTuples().get(i - 1).getPrice());

                ma10 = getMaAvg(10);
                ma20 = getMaAvg(20);
                ma60 = getMaAvg(60);
                ma120 = getMaAvg(120);

                // 跌5卖出后，如果收盘价仍高于均值，买入
                if (hasSellDown5
                        && input.getStockTuples().get(i - 1).getPrice().compareTo(ma10) >= 0) {
                    buy(input.getStockTuples().get(i - 1), MAModel.DOWN5_CLOSE_BUY);
                    hasSellDown5 = true;
                }
            }

            // 判断8出
            if (currentStockTuple
                            .getPrice()
                            .compareTo(SELL_RISE_PERCENT.multiply(lowPoint.getPrice()))
                    >= 0) {
                if (isIn24h(currentStockTuple, lowPoint)) {
                    // 卖出;
                    if (!hasSellRise8 && sell(currentStockTuple, MAModel.RISE8)) {
                        hasSellRise8 = true;
                        toadySell = true;
                    }
                } else {
                    lowPoint = currentStockTuple;
                    temp = currentStockTuple;
                    while (isIn24h(temp, currentStockTuple)) {
                        if (lowPoint.getPrice().compareTo(temp.getPrice()) > 0) {
                            lowPoint = temp;
                        }
                        temp = temp.getPre();
                    }
                }
            }
            // 判断5出
            if (currentStockTuple
                                    .getPrice()
                                    .compareTo(SELL_DOWN_PERCENT.multiply(highPoint.getPrice()))
                            <= 0
                    && remainStock.getCanSellNum() > 0) {
                // 卖出;
                if (!hasSellDown5 && sell(currentStockTuple, MAModel.DOWN5)) {
                    hasSellDown5 = true;
                    toadySell = true;
                }
            }

            // 金叉买入
            if (!hasBuyMa10 && currentPrice.compareTo(ma10) > 0) {
                if (buy(currentStockTuple, MAModel.MA10)) {
                    hasBuyMa10 = true;
                }
            }
            if (!hasBuyMa20 && currentPrice.compareTo(ma20) > 0) {
                if (buy(currentStockTuple, MAModel.MA20)) {
                    hasBuyMa20 = true;
                }
            }
            if (!hasBuyMa60 && currentPrice.compareTo(ma60) > 0) {
                if (buy(currentStockTuple, MAModel.MA60)) {
                    hasBuyMa60 = true;
                }
            }
            if (!hasBuyMa120 && currentPrice.compareTo(ma120) > 0) {
                if (buy(currentStockTuple, MAModel.MA120)) {
                    hasBuyMa120 = true;
                }
            }

            // 死叉卖出
            if (hasBuyMa10 && currentPrice.compareTo(ma10) < 0) {
                if (sell(currentStockTuple, MAModel.MA10)) {
                    hasBuyMa10 = false;
                    toadySell = true;
                } else if (remainStock.getTotalStockNum() == 0) {
                    hasBuyMa10 = false;
                }
            }
            if (hasBuyMa20 && currentPrice.compareTo(ma20) < 0) {
                if (sell(currentStockTuple, MAModel.MA20)) {
                    hasBuyMa20 = false;
                    toadySell = true;
                } else if (remainStock.getTotalStockNum() == 0) {
                    hasBuyMa20 = false;
                }
            }
            if (hasBuyMa60 && currentPrice.compareTo(ma60) < 0) {
                if (sell(currentStockTuple, MAModel.MA60)) {
                    hasBuyMa60 = false;
                    toadySell = true;
                } else if (remainStock.getTotalStockNum() == 0) {
                    hasBuyMa60 = false;
                }
            }
            if (hasBuyMa120 && currentPrice.compareTo(ma120) < 0) {
                if (sell(currentStockTuple, MAModel.MA120)) {
                    hasBuyMa120 = false;
                    toadySell = true;
                } else if (remainStock.getTotalStockNum() == 0) {
                    hasBuyMa120 = false;
                }
            }

            // 手里仓位清0，最高仓位点，更改
            if (toadySell && remainStock.getTotalStockNum() == 0) {
                highPoint = currentStockTuple;
                lowPoint = currentStockTuple;
            }
        }
        remainMoney =
                remainMoney.add(
                        input.getStockTuples()
                                .get(input.getStockTuples().size() - 1)
                                .getPrice()
                                .multiply(new BigDecimal(remainStock.getTotalStockNum())));

        dealRecord.setRemainMoney(remainMoney);
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

    public BigDecimal getMaAvg(int n) {
        BigDecimal result = BigDecimal.ZERO;
        int num = 1;
        for (int i = priceList.size() - 1; i >= 0; i--) {
            result = result.add(priceList.get(i));
            num++;
            if (num == n) {
                break;
            }
        }
        return Util.divide(result, new BigDecimal(n - 1));
    }

    public boolean isAnotherDay(StockTuple tuple1, StockTuple tuple2) {
        return tuple1.getTime().getDayOfYear() != tuple2.getTime().getDayOfYear();
    }

    public boolean buy(StockTuple tuple, MAModel maModel) throws TradLogicException {
        if (remainStock.getShare() >= 5) {
            throw new TradLogicException(
                    "Stock shrare is bigger than 5. please check code." + maModel.name());
        }
        int buyNum;
        if (remainMoney.compareTo(Constant.SHARE_MONEY) > 0) {
            buyNum = Util.divide(Constant.SHARE_MONEY, tuple.getPrice()).intValue();
        } else {
            buyNum = Util.divide(remainMoney, tuple.getPrice()).intValue();
        }

        remainStock.addStockNum(buyNum);
        remainMoney = remainMoney.subtract(tuple.getPrice().multiply(BigDecimal.valueOf(buyNum)));
        if (remainMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new TradLogicException("Remain money less than 0." + maModel.name());
        }
        dealRecord.addDealRecord(
                DealType.BUY,
                tuple.getTime(),
                tuple.getPrice(),
                buyNum,
                maModel,
                remainMoney,
                remainStock.getTotalStockNum(),
                remainStock.getShare());
        log.info(
                String.format(
                        "%s %s 买入, 有%s份,ma10:%s ma20:%s ma60:%s ma120:%s ",
                        Util.getDate(tuple.getTime().toDate()),
                        maModel.name(),
                        remainStock.getShare(),
                        hasBuyMa10,
                        hasBuyMa20,
                        hasBuyMa60,
                        hasBuyMa120));
        return true;
    }

    public boolean sell(StockTuple tuple, MAModel maModel) throws TradLogicException {
        if (remainStock.getCanSellNum() == 0) {

            /* log.warn(
            Util.getDate(tuple.getTime().toDate())
                    + " warning!, There is no stock can sell. "
                    + maModel.name());*/
            return false;
        }
        if (remainStock.getCanSellNum() == 0) {
            return false;
        }

        int sellStockNum = remainStock.subStockNum(tuple);

        remainMoney = remainMoney.add(tuple.getPrice().multiply(BigDecimal.valueOf(sellStockNum)));
        dealRecord.addDealRecord(
                DealType.SELL,
                tuple.getTime(),
                tuple.getPrice(),
                sellStockNum,
                maModel,
                remainMoney,
                remainStock.getTotalStockNum(),
                remainStock.getShare());
        log.info(
                String.format(
                        "%s %s 卖出 %s, 有%s份,ma10:%s ma20:%s ma60:%s ma120:%s",
                        Util.getDate(tuple.getTime().toDate()),
                        maModel.name(),
                        sellStockNum,
                        remainStock.getShare(),
                        hasBuyMa10,
                        hasBuyMa20,
                        hasBuyMa60,
                        hasBuyMa120));
        return true;
    }

    public boolean isIn24h(StockTuple tuple1, StockTuple tuple2) {
        return Math.abs(tuple1.getTime().getMillis() - tuple2.getTime().getMillis()) <= 86400000;
    }

    public DealRecord getDealRecord() {
        return dealRecord;
    }
}
