package com.ray.logic.model;

import com.ray.Util;
import com.ray.constants.Constant;

public class RemainStock {
    private int canSellNum;

    private int share = 0;

    // 当天买入的，不允许卖
    private int todayBuyNum = 0;

    public int getShare() {
        return share;
    }

    public int getCanSellNum() {
        return canSellNum;
    }

    public int getTodayBuyNum() {
        return todayBuyNum;
    }

    // 日期改变
    public void dateChange() {
        canSellNum = canSellNum + todayBuyNum;
        todayBuyNum = 0;
    }

    public int subStockNum(StockTuple tuple) {
        int needSellNum = Util.divide(Constant.SHARE_MONEY, tuple.getPrice()).intValue();
        if (canSellNum < needSellNum) {
            needSellNum = canSellNum;
        }
        canSellNum = canSellNum - needSellNum;
        share--;
        return needSellNum;
    }

    public void addStockNum(int stockNum) {
        todayBuyNum = todayBuyNum + stockNum;
        share++;
    }

    public int getTotalStockNum() {
        return todayBuyNum + canSellNum;
    }
}
