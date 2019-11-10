package com.ray.logic.model.stockShare;

import com.ray.Util;
import com.ray.constants.Constant;
import com.ray.logic.model.MAModel;
import com.ray.logic.model.StockTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/** Created by rui on 2019/11/9. */
public abstract class Share {
    private static final Logger log = LoggerFactory.getLogger(Share.class);

    protected int stockNum = 0;

    protected boolean canSellToday = false;

    protected boolean canBuyToday = true;

    public int getStockNum() {
        return stockNum;
    }

    public abstract MAModel getMaModel();

    public void dateChange() {
        if (stockNum > 0 && !canBuyToday) {
            canSellToday = true;
        }
    }

    public boolean isCanSellToday() {
        return canSellToday && stockNum > 0;
    }

    public boolean isCanBuyToday() {
        return canBuyToday;
    }

    public BigDecimal buyMoney(StockTuple tuple, BigDecimal avgPrice, MAModel model) {
        int buyNum = Util.divide(Constant.SHARE_MONEY, tuple.getPrice()).intValue();
        stockNum = stockNum + buyNum;
        canBuyToday = false;
        canSellToday = false;
        log.info(
                String.format(
                        "%s %5s 买入,价格：%6s %8s股, %8s价：%s ",
                        Util.getDate(tuple.getTime().toDate()),
                        getMaModel().name(),
                        tuple.getPrice(),
                        buyNum,
                        model.getCode(),
                        avgPrice));
        return tuple.getPrice().multiply(BigDecimal.valueOf(buyNum));
    }

    public BigDecimal sellMoney(StockTuple tuple, BigDecimal avgPrice, MAModel model) {
        if (!isCanSellToday()) {
            return BigDecimal.ZERO;
        }
        int dealNum = Util.divide(Constant.SHARE_MONEY, tuple.getPrice()).intValue();
        if (stockNum < dealNum) {
            dealNum = stockNum;
        }
        stockNum = stockNum - dealNum;
        if (!model.equals(MAModel.DOWN5)) {
            canBuyToday = true;
        }
        canSellToday = false;
        log.info(
                String.format(
                        "%s %5s 卖出,价格：%6s %8s股, %8s价：%s ",
                        Util.getDate(tuple.getTime().toDate()),
                        getMaModel().name(),
                        tuple.getPrice(),
                        dealNum,
                        model.getCode(),
                        avgPrice));
        return tuple.getPrice().multiply(BigDecimal.valueOf(dealNum));
    }

    public void setCanBuyTodayTrue() {
        canBuyToday = true;
    }
}
