package com.ray.logic;

import com.ray.logic.model.DealRecord;
import com.ray.logic.model.StockInputModel;
import com.ray.logic.model.StockTuple;
import org.joda.time.DateTime;

import java.io.*;

import static com.ray.constants.Constant.DATE_SPLIT;
import static com.ray.constants.Constant.ROW_DATA_SPLIT;

public class Reader {

    public static StockInputModel readSourceFile(String path){

        StockInputModel result = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "GBK"));

            String lineTxt = null;
            String[] strs = null;
            String[] dates = null;
            result = new StockInputModel(br.readLine());
            br.readLine();//读取头
            while ((lineTxt = br.readLine()) != null) {
                strs = lineTxt.split(ROW_DATA_SPLIT);
                dates = strs[0].split(DATE_SPLIT);
                DateTime dateTime = new DateTime(
                        Integer.valueOf(dates[0]),
                        Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[2]),
                        Integer.valueOf(strs[1].substring(0,2)),
                        Integer.valueOf(strs[1].substring(2)));
                result.add(new StockTuple(dateTime,Double.valueOf(strs[2])));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void write(DealRecord dealRecord) {
        try{
            File file = new File("C:\\Users\\rrui\\Desktop\\"+dealRecord.getStockName());
            file.deleteOnExit();
            file.createNewFile();

            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            br.write("最终结余："+String.valueOf(dealRecord.getRemainMoney()));
            br.newLine();
            for(DealRecord.Record record : dealRecord.getRecords()){
        br.write(
            String.format(
                "%s  %s  %s  %s  %s  %s  %s",
                record.getDealTime(),
                record.getDealType(),
                record.getPrice(),
                record.getDealAmount(),
                record.getMaModel(),
                record.getRemainMoney(),
                record.getRemainHandSum()));
                br.newLine();
            }
            br.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

  public static void main(String[] args) {
      StockInputModel model = readSourceFile("C:\\Users\\rrui\\Desktop\\SH#600000.txtd");
      System.out.println(model.getName());

      StockDeal stockDeal = new StockDeal();
      stockDeal.process(model);
      stockDeal.getDealRecord();
      write(stockDeal.getDealRecord());
  }
}
