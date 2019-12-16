package com.ray.logic;

import com.ray.Util;
import com.ray.constants.Constant;
import com.ray.exception.TradLogicException;
import com.ray.logic.model.MAModel;
import com.ray.logic.model.RemainStock;
import com.ray.logic.model.StockInputModel;
import com.ray.logic.model.StockTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.ray.constants.Constant.*;

public class StockDeal {
    private static final Logger log = LoggerFactory.getLogger(StockDeal.class);
    private BigDecimal remainMoney = START_FUND;

    private boolean hasBuyMa10 = false;
    private boolean hasBuyMa20 = false;
    private boolean hasBuyMa60 = false;
    private boolean hasBuyMa120 = false;
    private boolean hasSellRise8 = false;
    private boolean hasSellDown5 = false;

    private RemainStock remainStock = new RemainStock();
    private DealLogic dealLogic;
    private boolean todaySell = false;
    private boolean startEnter = true;
    private List<MAModel> decreaseHis = new ArrayList<>();

    private void dateChange(StockTuple close) throws TradLogicException {
        dealLogic.addClosePrice(close);
        todaySell = false;
        hasSellRise8 = false;
        hasSellDown5 = false;
        remainStock.dateChange();
        decreaseHis.clear();
//多头趋势  CLOSE>MA10   （0  2%） 现价开仓   大于2%不开仓
        if (!hasBuyMa10 && dealLogic.isSatisfy()
            && dealLogic.getMA10().compareTo(close.getPrice().multiply(new BigDecimal(0.98))) > 0
            && dealLogic.getMA10().compareTo(close.getPrice()) < 0) {
            hasBuyMa10 = buy(close, MAModel.MA10);
        }

        // 跌5卖出后，如果收盘价仍高于均值，买入
        if (hasSellDown5 && close.getPrice().compareTo(dealLogic.getMA10()) >= 0) {
            buy(close, MAModel.DOWN5_CLOSE_BUY);
            hasSellDown5 = true;
        }
    }

    public void process(StockInputModel input) throws TradLogicException {
        dealLogic = new DealLogic();
        int index = getStartIndex(input, dealLogic);
        BigDecimal currentPrice;
        StockTuple currentStockTuple;
        StockTuple lowPoint = input.getStockTuples().get(index);
        StockTuple highPoint = input.getStockTuples().get(index);

        for (int i = index; i < input.getStockTuples().size(); i++) {
            currentStockTuple = input.getStockTuples().get(i);
            currentPrice = currentStockTuple.getPrice();

            if (currentPrice.compareTo(lowPoint.getPrice()) <= 0) {
                lowPoint = currentStockTuple;
            } else if (currentPrice.compareTo(highPoint.getPrice()) >= 0) {
                highPoint = currentStockTuple;
            }

            if (i != index && isAnotherDay(input.getStockTuples().get(i - 1), currentStockTuple)) {
                dateChange(input.getStockTuples().get(i - 1));
            }
            enter();

            // 判断8出
            sellWhenRise8(currentStockTuple, lowPoint);
            // 判断5出
            sellWhenDown5(currentStockTuple, highPoint);
            // 金叉买入
            normalBuy(currentStockTuple);
            // 死叉卖出
            normalSell(currentStockTuple);

            // 手里仓位清0，最高仓位点，更改
            if (todaySell && remainStock.getTotalStockNum() == 0) {
                highPoint = currentStockTuple;
                lowPoint = currentStockTuple;
            }
        }
        remainMoney = remainMoney.add(input.getStockTuples().get(input.getStockTuples().size() - 1)
            .getPrice().multiply(new BigDecimal(remainStock.getTotalStockNum())));
    }

    private void enter() throws TradLogicException {
        if (!startEnter) {
            return;
        }
        logicBuy1();
        logicBuy2();
        logicBuy3();
    }

    private boolean buy(StockTuple tuple, MAModel maModel) throws TradLogicException {
        startEnter = false;
        if (remainStock.getShare() >= 5) {
            throw new TradLogicException("Stock shrare is bigger than 5. please check code." + maModel.name());
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
        log.info(String.format("%s %s 买入, 有%s份,ma10:%s ma20:%s ma60:%s ma120:%s ",
            Util.getDate(tuple.getTime().toDate()), maModel.name(), remainStock.getShare(), hasBuyMa10, hasBuyMa20, hasBuyMa60, hasBuyMa120));
        return true;
    }

    private boolean sell(StockTuple tuple, MAModel maModel) throws TradLogicException {
        if (remainStock.getCanSellNum() == 0) {
            return false;
        }
        //todo Q：只正针对于MA10？
        if ((decreaseHis.contains(MAModel.DOWN5) || decreaseHis.contains(MAModel.RISE8)) && decreaseHis.contains(MAModel.MA10)) {
            return false;
        }
        int sellStockNum = remainStock.subStockNum(tuple);
        remainMoney = remainMoney.add(tuple.getPrice().multiply(BigDecimal.valueOf(sellStockNum)));
        decreaseHis.add(maModel);
        log.info(String.format("%s %s 卖出 %s, 有%s份,ma10:%s ma20:%s ma60:%s ma120:%s",
            Util.getDate(tuple.getTime().toDate()), maModel.name(), sellStockNum, remainStock.getShare(), hasBuyMa10, hasBuyMa20, hasBuyMa60, hasBuyMa120));
        return true;
    }

    private void normalBuy(StockTuple currentStockTuple) throws TradLogicException {
        //满足MA10>MA20>MA60>MA120 并且向上
        if (!dealLogic.isSatisfy()) {
            return;
        }
        if (dealLogic.isMARising(MAModel.MA10.name())) {
            hasBuyMa10 = buySingleMA(hasBuyMa10, currentStockTuple, dealLogic.getMA10(), MAModel.MA10);
        }
        if (dealLogic.isMARising(MAModel.MA20.name())) {
            hasBuyMa20 = buySingleMA(hasBuyMa20, currentStockTuple, dealLogic.getMA20(), MAModel.MA20);
        }
        if (dealLogic.isMARising(MAModel.MA60.name())) {
            hasBuyMa60 = buySingleMA(hasBuyMa60, currentStockTuple, dealLogic.getMA60(), MAModel.MA60);
        }
        if (dealLogic.isMARising(MAModel.MA120.name())) {
            hasBuyMa120 = buySingleMA(hasBuyMa120, currentStockTuple, dealLogic.getMA120(), MAModel.MA120);
        }
    }

    private void normalSell(StockTuple currentStockTuple) throws TradLogicException {
        hasBuyMa10 = sellSingleMA(hasBuyMa10, currentStockTuple, dealLogic.getMA10(), MAModel.MA10);
        hasBuyMa20 = sellSingleMA(hasBuyMa20, currentStockTuple, dealLogic.getMA20(), MAModel.MA20);
        hasBuyMa60 = sellSingleMA(hasBuyMa60, currentStockTuple, dealLogic.getMA60(), MAModel.MA60);
        hasBuyMa120 = sellSingleMA(hasBuyMa120, currentStockTuple, dealLogic.getMA120(), MAModel.MA120);
    }

    private boolean buySingleMA(boolean hasBuy, StockTuple currentStockTuple, BigDecimal maPrice, MAModel model) throws TradLogicException {
        if (!hasBuy && currentStockTuple.getPrice().compareTo(maPrice) > 0) {
            if (buy(currentStockTuple, model)) {
                return true;
            }
        }
        return false;
    }

    private boolean sellSingleMA(boolean hasBuy, StockTuple currentStockTuple, BigDecimal maPrice, MAModel model) throws TradLogicException {
        if (hasBuy && currentStockTuple.getPrice().compareTo(maPrice) < 0) {
            if (sell(currentStockTuple, model)) {
                todaySell = true;
                return false;
            } else if (remainStock.getTotalStockNum() == 0) {
                return false;
            }
        }
        return false;
    }

    private void sellWhenRise8(StockTuple currentStockTuple, StockTuple lowPoint) throws TradLogicException {
        if (currentStockTuple.getPrice().compareTo(SELL_RISE_PERCENT.multiply(lowPoint.getPrice())) >= 0) {
            if (isIn24h(currentStockTuple, lowPoint)) {
                // 卖出;
                if (!hasSellRise8 && sell(currentStockTuple, MAModel.RISE8)) {
                    hasSellRise8 = true;
                    todaySell = true;
                }
            } else {
                lowPoint = currentStockTuple;
                StockTuple temp = currentStockTuple;
                while (isIn24h(temp, currentStockTuple)) {
                    if (lowPoint.getPrice().compareTo(temp.getPrice()) > 0) {
                        lowPoint = temp;
                    }
                    temp = temp.getPre();
                }
            }
        }
    }

    private void sellWhenDown5(StockTuple currentStockTuple, StockTuple highPoint)
        throws TradLogicException {
        if (currentStockTuple.getPrice().compareTo(SELL_DOWN_PERCENT.multiply(highPoint.getPrice())) <= 0 && remainStock.getCanSellNum() > 0) {
            // 卖出;
            if (!hasSellDown5 && sell(currentStockTuple, MAModel.DOWN5)) {
                hasSellDown5 = true;
                todaySell = true;
            }
        }
    }

    private boolean isIn24h(StockTuple tuple1, StockTuple tuple2) {
        return Math.abs(tuple1.getTime().getMillis() - tuple2.getTime().getMillis()) <= 86400000;
    }

    private boolean bigger(BigDecimal par1, BigDecimal par2) {
        return par1.compareTo(par2) > 0;
    }

    private void logicBuy1() throws TradLogicException {
        if (!dealLogic.isCondition1()) {
            return;
        }
        BigDecimal comparePrice = dealLogic.getClose().getPrice();
        if (bigger(dealLogic.getMA10(), comparePrice)) {
            hasBuyMa10 = buySingleMA(hasBuyMa10, dealLogic.getClose(), dealLogic.getMA10(), MAModel.MA10);
            hasBuyMa20 = buySingleMA(hasBuyMa20, dealLogic.getClose(), dealLogic.getMA20(), MAModel.MA20);
            hasBuyMa60 = buySingleMA(hasBuyMa60, dealLogic.getClose(), dealLogic.getMA60(), MAModel.MA60);
            hasBuyMa120 = buySingleMA(hasBuyMa120, dealLogic.getClose(), dealLogic.getMA120(), MAModel.MA120);
        }
        if (bigger(dealLogic.getMA20(), comparePrice) && bigger(comparePrice, dealLogic.getMA10())) {
            //todo
        }
    }

    private void logicBuy2() throws TradLogicException {
        if (!dealLogic.isCondition2()) {
            return;
        }
        BigDecimal comparePrice = dealLogic.getClose().getPrice().multiply(new BigDecimal(0.98));
        if (bigger(dealLogic.getMA10(), comparePrice)) {
            buy(dealLogic.getClose(), MAModel.MA10);
        }
    }

    private void logicBuy3() {
        if (!dealLogic.isCondition3()) {
            return;
        }
        BigDecimal comparePrice = dealLogic.getClose().getPrice();
        if (bigger(dealLogic.getMA10(), comparePrice)) {
            // todo buy() 40%
        }
        if (bigger(dealLogic.getMA20(), comparePrice)) {
            // todo buy() 20%
        }
    }

    private int getStartIndex(StockInputModel input, DealLogic dealLogic) {
        List<StockTuple> stockTuples = input.getStockTuples();
        int index;
        for (index = 1; index < stockTuples.size(); index++) {
            if (isAnotherDay(stockTuples.get(index), stockTuples.get(index - 1))) {
                dealLogic.addClosePrice(stockTuples.get(index - 1));
            }
            if (dealLogic.canStart()) {
                break;
            }
        }
        return index;
    }

    private boolean isAnotherDay(StockTuple tuple1, StockTuple tuple2) {
        return tuple1.getTime().getDayOfYear() != tuple2.getTime().getDayOfYear();
    }
}
