package com.ray.logic;

import com.ray.logic.model.MAModel;
import com.ray.logic.model.StockInputModel;
import com.ray.logic.model.StockTuple;

import java.util.ArrayList;
import java.util.List;

public class StockDeal {

  private double ma10Amount;
  private double ma20Amount;
  private double ma60Amount;
  private double ma120Amount;

  public void process(StockInputModel input) {
    int index = calculateMa(input);
    List<StockTuple> stockTuples = input.getStockTuples();
    for (int i = index; i < stockTuples.size(); i++) {
        stockTuples.get(i)
    }
  }

  public int calculateMa(StockInputModel input) {
    List<Double> priceList = new ArrayList<>();

    List<StockTuple> stockTuples = input.getStockTuples();
    int index;
    for (index = 1; index < stockTuples.size(); index++) {
      if (stockTuples.get(index).getTime().getDayOfYear()
          != stockTuples.get(index - 1).getTime().getDayOfYear()) {
        priceList.add(stockTuples.get(index - 1).getPrice());
      }
      if (priceList.size() == MAModel.MA120.getDAYNUM() - 1) {
        break;
      }
    }

    double amount = 0;
    for (int i = 0; i < priceList.size(); i++) {
      amount = amount + priceList.get(i);

      if (i == MAModel.MA10.getDAYNUM() - 1) {
        ma10Amount = amount;
      } else if (i == MAModel.MA20.getDAYNUM() - 1) {
        ma20Amount = amount;
      } else if (i == MAModel.MA60.getDAYNUM() - 1) {
        ma60Amount = amount;
      } else if (i == MAModel.MA120.getDAYNUM() - 1) {
        ma120Amount = amount;
      }
    }
    return index;
  }
}
