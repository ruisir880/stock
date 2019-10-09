package com.ray.constants;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.regex.Pattern;

public class Constant {
    public static final double SELL_RISE_PERCENT=1.08;
    public static final double SELL_DOWN_PERCENT=0.95;
    public static final String DATE_SPLIT = "/";
    public static final String ROW_DATA_SPLIT = "\t";
    public static final Pattern DATE_PATTERN = Pattern.compile("yyyy/MM/dd");

    public static final  double START_FUND = 100000;
    public static final int SHARE_NUM = 5;
    public static final double SHARE_MONEY = START_FUND/SHARE_NUM;

    public static final double SHARE_10_MONEY = START_FUND*0.2;
    public static final double SHARE_20_MONEY = START_FUND*0.2;
    public static final double SHARE_60_MONEY = START_FUND*0.2;
    public static final double SHARE_120_MONEY = START_FUND*0.2;
}
