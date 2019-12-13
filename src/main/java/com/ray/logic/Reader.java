package com.ray.logic;

import com.ray.exception.TradLogicException;
import com.ray.logic.model.DealRecord;
import com.ray.logic.model.StockInputModel;
import com.ray.logic.model.StockTuple;
import org.joda.time.DateTime;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static com.ray.constants.Constant.DATE_SPLIT;

public class Reader {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static StockInputModel readSourceFile(String path) {

        StockInputModel result = null;
        try {
            File sourceFile = new File(path);
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"));

            String lineTxt = null;
            String[] strs = null;
            String[] dates = null;
            String[] time = null;
            result = new StockInputModel(sourceFile.getName());
            // br.readLine();//读取头
            while ((lineTxt = br.readLine()) != null) {
                strs = lineTxt.split("\\s+");
                dates = strs[0].split(DATE_SPLIT);
                time = strs[1].split(":");
                DateTime dateTime =
                        new DateTime(
                                Integer.valueOf(dates[0]),
                                Integer.valueOf(dates[1]),
                                Integer.valueOf(dates[2]),
                                Integer.valueOf(time[0]),
                                Integer.valueOf(time[1]),
                                Integer.valueOf(time[2]));
                result.add(new StockTuple(dateTime, new BigDecimal(strs[2])));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void write(DealRecord dealRecord) {
        try {
            File file = new File("C:\\Users\\rrui\\Desktop\\" + dealRecord.getStockName());
            file.deleteOnExit();
            file.createNewFile();

            BufferedWriter br =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            br.write("最终结余：" + String.valueOf(dealRecord.getRemainMoney()));
            br.newLine();
            for (DealRecord.Record record : dealRecord.getRecords()) {
                br.write(
                        String.format(
                                "%s  %4s  %8s  %6s  %6s  %10s  %8s %2s",
                                formatter.format(record.getDealTime().toDate()),
                                record.getDealType(),
                                record.getPrice(),
                                record.getDealAmount(),
                                record.getMaModel(),
                                record.getRemainMoney(),
                                record.getRemainStockNum(),
                                record.getShare()));
                br.newLine();
            }
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StockInputModel model = readSourceFile("C:\\Users\\rrui\\Desktop\\stock\\taget\\SH600017");
        System.out.println(model.getName());

        StockDeal stockDeal = new StockDeal();
        try {
            stockDeal.process(model);
        } catch (TradLogicException e) {
            e.printStackTrace();
        }
        stockDeal.getDealRecord();
        write(stockDeal.getDealRecord());
    }
}
