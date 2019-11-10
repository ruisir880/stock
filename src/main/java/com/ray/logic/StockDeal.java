package com.ray.logic;

import com.ray.Util;
import com.ray.exception.TradLogicException;
import com.ray.logic.model.DealRecord;
import com.ray.logic.model.MAModel;
import com.ray.logic.model.StockInputModel;
import com.ray.logic.model.StockTuple;
import com.ray.logic.model.stockShare.Share;
import com.ray.logic.model.stockShare.ShareFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.ray.constants.Constant.*;

public class StockDeal {
    private static final Logger log = LoggerFactory.getLogger(StockDeal.class);
    // private int HAND = 100;
    private BigDecimal remainMoney = START_FUND;

    private LinkedList<BigDecimal> priceList;
    private DealRecord dealRecord;

    private Share ma10_share = ShareFactory.getShare(MAModel.MA10);
    private Share ma20_share = ShareFactory.getShare(MAModel.MA20);
    private Share ma60_share = ShareFactory.getShare(MAModel.MA60);
    private Share ma120_share = ShareFactory.getShare(MAModel.MA120);
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma60;
    private BigDecimal ma120;

    private boolean hasSellRise8 = false;
    private boolean hasSellDown5 = false;

    // private RemainStock remainStock = new RemainStock();

    public void process(StockInputModel input) throws TradLogicException {
        dealRecord = new DealRecord(input.getName());
        int index = getMaList(input);
        setMaAvg();

        BigDecimal currentPrice;
        StockTuple currentStockTuple;

        // StockTuple lowPoint = input.getStockTuples().get(index);
        StockTuple highPoint = input.getStockTuples().get(index);
        StockTuple openOrCloseTuple = input.getStockTuples().get(index);
        List<Share> lastBuyShareList = new ArrayList<>();
        // List<Share> lastSellShareList = new ArrayList<>();
        Share down5SellShare = null;
        Share tempShare;
        for (int i = index; i < input.getStockTuples().size(); i++) {
            currentStockTuple = input.getStockTuples().get(i);
            currentPrice = currentStockTuple.getPrice();

            if (currentPrice.compareTo(highPoint.getPrice()) >= 0) {
                highPoint = currentStockTuple;
            }

            if (i != index && isAnotherDay(input.getStockTuples().get(i - 1), currentStockTuple)) {
                StockTuple closeTuple = input.getStockTuples().get(i - 1);
                dateChange(closeTuple);
                openOrCloseTuple =
                        closeTuple.getPrice().compareTo(currentStockTuple.getPrice()) > 0
                                ? currentStockTuple
                                : input.getStockTuples().get(i - 1);

                // 跌5卖出后，如果收盘价仍高于均值，买入
                if (hasSellDown5) {
                    if ((closeTuple.getPrice().compareTo(ma10) >= 0
                            || closeTuple.getPrice().compareTo(ma20) >= 0)) {
                        remainMoney =
                                remainMoney.subtract(
                                        down5SellShare.buyMoney(
                                                closeTuple, ma10, MAModel.DOWN5_CLOSE_BUY));
                        addToLastBuyList(lastBuyShareList, down5SellShare);
                    } else {
                        down5SellShare.setCanBuyTodayTrue();
                    }
                }
                hasSellDown5 = false;
                printRemainMoney(closeTuple);
            }

            // 判断8出
            if (currentStockTuple
                                    .getPrice()
                                    .compareTo(
                                            SELL_RISE_PERCENT.multiply(openOrCloseTuple.getPrice()))
                            >= 0
                    && lastBuyShareList.size() > 0
                    && !hasSellRise8) {

                tempShare = lastBuyShareList.get(lastBuyShareList.size() - 1);
                remainMoney =
                        remainMoney.add(
                                tempShare.sellMoney(
                                        currentStockTuple,
                                        openOrCloseTuple.getPrice(),
                                        MAModel.RISE8));
                hasSellRise8 = true;
            }

            // 正常交易
            normalBuy(currentStockTuple, lastBuyShareList);
            normalSell(currentStockTuple);

            // 判断5出
            tempShare = down5Deal(lastBuyShareList, currentStockTuple, highPoint);
            down5SellShare = tempShare == null ? down5SellShare : tempShare;

            // 手里仓位清0，最高仓位点，更改
            if (ma10_share.isCanBuyToday()
                    && ma20_share.isCanBuyToday()
                    && ma60_share.isCanBuyToday()
                    && ma120_share.isCanBuyToday()) {
                highPoint = currentStockTuple;
            }
        }
    }

    public void setMaAvg() {
        ma10 = getMaAvg(10);
        ma20 = getMaAvg(20);
        ma60 = getMaAvg(60);
        ma120 = getMaAvg(120);
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

    // 获取平均值
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

    /* public boolean buy(StockTuple tuple, MAModel maModel, BigDecimal avgPrice)
            throws TradLogicException {
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
                        "%s %5s 买入,价格：%6s %8s股, %8s价：%s ",
                        Util.getDate(tuple.getTime().toDate()),
                        maModel.name(),
                        tuple.getPrice(),
                        buyNum,
                        maModel.getCode(),
                        avgPrice));
        return true;
    }

    public boolean sell(StockTuple tuple, MAModel maModel, BigDecimal avgPrice)
            throws TradLogicException {
        if (remainStock.getCanSellNum() == 0) {
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
                        "%s %5s 卖出,价格：%6s %8s股, %8s价：%s ",
                        Util.getDate(tuple.getTime().toDate()),
                        maModel.name(),
                        tuple.getPrice(),
                        sellStockNum,
                        maModel.getCode(),
                        avgPrice));
        return true;
    }*/

    public DealRecord getDealRecord() {
        return dealRecord;
    }

    private void printRemainMoney(StockTuple input) {
        int totalStock =
                ma10_share.getStockNum()
                        + ma20_share.getStockNum()
                        + ma60_share.getStockNum()
                        + ma120_share.getStockNum();
        BigDecimal totalMoney =
                remainMoney.add(input.getPrice().multiply(new BigDecimal(totalStock)));
        log.info(
                String.format(
                        "%s 收盘价：%s 最后总额：%s ma10:%s %s ma20:%s %s ma60:%s %s m120:%s %s",
                        Util.getEndDate(input.getTime().toDate()),
                        input.getPrice(),
                        totalMoney,
                        ma10_share.getStockNum(),
                        ma10,
                        ma20_share.getStockNum(),
                        ma20,
                        ma60_share.getStockNum(),
                        ma60,
                        ma120_share.getStockNum(),
                        ma120));
    }

    private void dateChange(StockTuple tuple) {
        ma10_share.dateChange();
        ma20_share.dateChange();
        ma60_share.dateChange();
        ma120_share.dateChange();

        hasSellRise8 = false;
        // remainStock.dateChange();
        priceList.removeFirst();
        priceList.addLast(tuple.getPrice());

        setMaAvg();
    }

    public void normalBuy(StockTuple currentStockTuple, List<Share> lastBuyShareList) {
        BigDecimal currentPrice = currentStockTuple.getPrice();
        Share lastBuy = null;
        // 金叉买入
        if (currentPrice.compareTo(ma10) > 0 && ma10_share.isCanBuyToday()) {
            remainMoney =
                    remainMoney.subtract(
                            ma10_share.buyMoney(currentStockTuple, ma10, MAModel.MA10));
            lastBuy = ma10_share;
        }
        if (currentPrice.compareTo(ma20) > 0 && ma20_share.isCanBuyToday()) {
            remainMoney =
                    remainMoney.subtract(
                            ma20_share.buyMoney(currentStockTuple, ma20, MAModel.MA20));
            lastBuy = ma20_share;
        }
        if (currentPrice.compareTo(ma60) > 0 && ma60_share.isCanBuyToday()) {
            remainMoney =
                    remainMoney.subtract(
                            ma60_share.buyMoney(currentStockTuple, ma60, MAModel.MA60));
            lastBuy = ma60_share;
        }
        if (currentPrice.compareTo(ma120) > 0 && ma120_share.isCanBuyToday()) {
            remainMoney =
                    remainMoney.subtract(
                            ma120_share.buyMoney(currentStockTuple, ma120, MAModel.MA120));
            lastBuy = ma120_share;
        }
        addToLastBuyList(lastBuyShareList, lastBuy);
    }

    private void addToLastBuyList(List<Share> lastBuyShareList, Share share) {
        if (share != null) {
            if (lastBuyShareList.size() == 4) {
                lastBuyShareList.remove(0);
            }
            lastBuyShareList.add(share);
        }
    }

    public void normalSell(StockTuple currentStockTuple) {
        BigDecimal currentPrice = currentStockTuple.getPrice();
        // 死叉卖出
        Share lastSell = null;
        if (currentPrice.compareTo(ma10) < 0 && ma10_share.isCanSellToday()) {
            remainMoney =
                    remainMoney.add(ma10_share.sellMoney(currentStockTuple, ma10, MAModel.MA10));
            lastSell = ma10_share;
        }
        if (currentPrice.compareTo(ma20) < 0 && ma20_share.isCanSellToday()) {
            remainMoney =
                    remainMoney.add(ma20_share.sellMoney(currentStockTuple, ma20, MAModel.MA20));
            lastSell = ma20_share;
        }
        if (currentPrice.compareTo(ma60) < 0 && ma60_share.isCanSellToday()) {
            remainMoney =
                    remainMoney.add(ma60_share.sellMoney(currentStockTuple, ma60, MAModel.MA60));
            lastSell = ma60_share;
        }
        if (currentPrice.compareTo(ma120) < 0 && ma120_share.isCanSellToday()) {
            remainMoney =
                    remainMoney.add(ma120_share.sellMoney(currentStockTuple, ma120, MAModel.MA120));
            lastSell = ma120_share;
        }
        /* if (lastSell != null) {
            if (lastSellShareList.size() == 4) {
                lastSellShareList.remove(0);
            }
            lastSellShareList.add(lastSell);
        }*/
    }

    private Share getLastCanSellShare(List<Share> lastBuyShareList) {
        if (lastBuyShareList.size() == 0) {
            return null;
        } else {
            for (int i = lastBuyShareList.size() - 1; i >= 0; i--) {
                if (lastBuyShareList.get(i).isCanSellToday()) {
                    return lastBuyShareList.get(i);
                }
            }
        }
        return null;
    }

    // 判断5卖出
    public Share down5Deal(
            List<Share> lastBuyShareList, StockTuple currentStockTuple, StockTuple highPoint) {
        Share last = getLastCanSellShare(lastBuyShareList);
        if (currentStockTuple.getPrice().compareTo(SELL_DOWN_PERCENT.multiply(highPoint.getPrice()))
                        <= 0
                && last != null) {
            // 卖出;
            if (!hasSellDown5) {
                remainMoney =
                        remainMoney.add(
                                last.sellMoney(
                                        currentStockTuple, highPoint.getPrice(), MAModel.DOWN5));
                hasSellDown5 = true;
                return last;
            }
        }
        return null;
    }
}
