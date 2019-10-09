package com.ray.logic.model;

import java.util.ArrayList;
import java.util.List;

public class StockInputModel {
  private String name;
  private List<StockTuple> stockTuples = new ArrayList<>();

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
      StockTuple current = getCurrent();
      stockTuples.add(tuple);
      if(current == null){
          return;
      }
      current.setNext(tuple);
      tuple.setPre(current);

  }

  public List<StockTuple> getStockTuples() {
    return stockTuples;
  }

  public StockTuple getCurrent(){
      if(stockTuples.size()==0){
          return null;
      }
      return stockTuples.get(stockTuples.size()-1);
  }
}
