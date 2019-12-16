package com.ray.logic;

import com.ray.Util;
import com.ray.constants.Constant;
import com.ray.logic.model.MAModel;
import com.ray.logic.model.StockTuple;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DealLogic {

    private List<StockTuple> closePriceList = new LinkedList<>();

    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma60;
    private BigDecimal ma120;

    private Map<String, List<BigDecimal>> maMap = new HashMap<>();

    public void addClosePrice(StockTuple closePrice) {
        closePriceList.add(closePrice);
        if (closePriceList.size() >= 120) {
            ma10 = getMaAvg(10);
            ma20 = getMaAvg(20);
            ma60 = getMaAvg(60);
            ma120 = getMaAvg(120);
        }
        while (closePriceList.size() > 120) {
            closePriceList.remove(0);
        }
        dealAVGPriceList();
    }

    public BigDecimal getMA10() {
        return ma10;
    }

    public BigDecimal getMA20() {
        return ma20;
    }

    public BigDecimal getMA60() {
        return ma60;
    }

    public BigDecimal getMA120() {
        return ma120;
    }

    public StockTuple getClose() {
        return closePriceList.get(closePriceList.size() - 1);
    }

    public boolean isAllMARising() {
        return isMARising("MA10")
            && isMARising("MA20")
            && isMARising("MA60")
            && isMARising("MA120");
    }

    public boolean isDuoTou() {
        return isAllMARising()
            && ma10.compareTo(ma20) > 0
            && ma20.compareTo(ma60) > 0
            && ma60.compareTo(ma120) > 0;
    }

    // MA10<MA20<MA60<MA120  ;
    public boolean isCondition1() {
        return ma120.compareTo(ma60) > 0 && ma60.compareTo(ma20) > 0 && ma20.compareTo(ma10) > 0;
    }

    // MA10>MA20>MA60>MA120
    public boolean isCondition2() {
        return ma120.compareTo(ma60) < 0 && ma60.compareTo(ma20) < 0 && ma20.compareTo(ma10) < 0;
    }

    // MA20>MA10>MA60>MA120 MA60 120 向上  MA10 向下
    public boolean isCondition3() {
        return ma20.compareTo(ma10) > 0
            && ma10.compareTo(ma60) > 0
            && ma60.compareTo(ma120) > 0
            && isMARising("MA60")
            && isMARising("MA120")
            && !isMARising("MA10");
    }

    // MA60>MA20>MA10>MA120   MA60-120  向上 MA10 20向下
    public boolean isCondition4() {
        return ma60.compareTo(ma20) > 0
            && ma20.compareTo(ma10) > 0
            && ma10.compareTo(ma120) > 0
            && isMARising("MA60")
            && isMARising("MA120")
            && !isMARising("MA10")
            && !isMARising("MA20");
    }

    public boolean canStart() {
        return closePriceList.size() >= MAModel.MA120.getDayNum() - 1;
    }

    private BigDecimal getMaAvg(int n) {
        BigDecimal result = BigDecimal.ZERO;
        int num = 1;
        for (int i = closePriceList.size() - 1; i >= 0; i--) {
            result = result.add(closePriceList.get(i).getPrice());
            num++;
            if (num == n) {
                break;
            }
        }
        return Util.divide(result, new BigDecimal(n - 1));
    }

    private void dealAVGPriceList() {
        dealMAPrice("MA10", ma10);
        dealMAPrice("MA20", ma20);
        dealMAPrice("MA60", ma60);
        dealMAPrice("MA120", ma120);
    }

    private void dealMAPrice(String key, BigDecimal maPrice) {
        if (CollectionUtils.isEmpty(maMap.get(key))) {
            List<BigDecimal> priceList = new LinkedList<>();
            priceList.add(maPrice);
            maMap.put("key", priceList);
        } else {
            maMap.get("key").add(maPrice);
            if (maMap.get("key").size() > 10) {
                maMap.get("key").remove(0);
            }
        }
    }

    public boolean isMARising(String key) {
        List<BigDecimal> list = maMap.get(key);
        for (int i = 1; i < Constant.RISING_DAYS; i++) {
            if (list.get(i).compareTo(list.get(i - 1)) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isSatisfy() {
        return isAllMARising()
            && ma10.compareTo(ma20) > 0
            && ma20.compareTo(ma60) > 0
            && ma60.compareTo(ma120) > 0;
    }
}
