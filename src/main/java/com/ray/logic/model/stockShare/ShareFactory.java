package com.ray.logic.model.stockShare;

import com.ray.logic.model.MAModel;

/** Created by rui on 2019/11/10. */
public class ShareFactory {
    public static Share getShare(MAModel model) {
        switch (model) {
            case MA10:
                return new MA10_Share();
            case MA20:
                return new MA20_Share();
            case MA60:
                return new MA60_Share();
            case MA120:
                return new MA120_Share();
            default:
                return new MA10_Share();
        }
    }
}
