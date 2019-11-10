package com.ray.logic.model.stockShare;

import com.ray.logic.model.MAModel;

/** Created by rui on 2019/11/10. */
public class MA10_Share extends Share {
    @Override
    public MAModel getMaModel() {
        return MAModel.MA10;
    }
}
