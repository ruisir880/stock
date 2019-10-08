package com.ray.constants;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.regex.Pattern;

public class Constant {
    public static final double SELL_RISE_PERCENT=8;
    public static final double SELL_DOWN_PERCENT=5;
    public static final String DATE_SPLIT = "/";
    public static final String ROW_DATA_SPLIT = "   ";
    public static final Pattern DATE_PATTERN = Pattern.compile("yyyy/MM/dd");

    public static final  double START_FUND = 100000;
}
