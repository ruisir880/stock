package com.ray.logic.model;

import java.util.List;

public class StockInputModel {
  private String name;
  private List<StockTuple> stockTuples;

  public StockInputModel(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void add(StockTuple tuple) {
    stockTuples.add(tuple);
  }

  public List<StockTuple> getStockTuples() {
    return stockTuples;
  }
}
