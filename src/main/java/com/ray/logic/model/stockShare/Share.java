package com.ray.logic.model.stockShare;

import com.ray.logic.model.MAModel;

/** Created by rui on 2019/11/9. */
public abstract class Share {

    protected int stockNum;

    protected boolean canDealToday;

    public int getStockNum() {
        return stockNum;
    }

    public abstract MAModel getMaModel();

    public void reset() {
        canDealToday = true;
    }

    public boolean isCanDealToday() {
        return canDealToday;
    }
}
