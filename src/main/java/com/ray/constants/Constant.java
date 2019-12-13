package com.ray.constants;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Constant {
    public static final BigDecimal SELL_RISE_PERCENT = BigDecimal.valueOf(1.08);
    public static final BigDecimal SELL_DOWN_PERCENT = BigDecimal.valueOf(0.95);
    public static final String DATE_SPLIT = "-";
    public static final String ROW_DATA_SPLIT = "\t";
    public static final Pattern DATE_PATTERN = Pattern.compile("yyyy/MM/dd");

    public static final BigDecimal START_FUND = new BigDecimal(100000);
    public static final BigDecimal SHARE_NUM = new BigDecimal(5);
    public static final BigDecimal SHARE_MONEY = START_FUND.divide(SHARE_NUM);

    public static final BigDecimal SHARE_10_MONEY = START_FUND.multiply(new BigDecimal(0.2));
    public static final BigDecimal SHARE_20_MONEY = START_FUND.multiply(new BigDecimal(0.2));
    public static final BigDecimal SHARE_60_MONEY = START_FUND.multiply(new BigDecimal(0.2));
    public static final BigDecimal SHARE_120_MONEY = START_FUND.multiply(new BigDecimal(0.2));

    public static final int RISING_DAYS = 3;

    public static void main(String[] args) {
        System.out.println(String.format("%10s", 11));
        System.out.println(String.format("%8s", "ss"));
    }
}
