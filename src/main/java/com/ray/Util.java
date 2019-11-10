package com.ray;

import com.ray.logic.model.StockTuple;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    static SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat pattern2 = new SimpleDateFormat("MM/dd HH:mm:ss");

    public static BigDecimal divide(BigDecimal mother, BigDecimal subNum) {
        return mother.divide(subNum, 6, BigDecimal.ROUND_DOWN);
    }

    public static String getDate(Date date) {
        return pattern.format(date);
    }

    public static String getEndDate(Date date) {
        return pattern2.format(date);
    }

    public boolean isIn24h(StockTuple tuple1, StockTuple tuple2) {
        return Math.abs(tuple1.getTime().getMillis() - tuple2.getTime().getMillis()) <= 86400000;
    }
}
