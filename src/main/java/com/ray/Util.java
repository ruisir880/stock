package com.ray;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    static SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static BigDecimal divide(BigDecimal mother, BigDecimal subNum) {
        return mother.divide(subNum, 6, BigDecimal.ROUND_DOWN);
    }

    public static String getDate(Date date) {
        return pattern.format(date);
    }
}
