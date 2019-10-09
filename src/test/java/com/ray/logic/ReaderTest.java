package com.ray.logic;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderTest {

  @Test
  public void readSourceFile() {
      DateTime dateTime1 = new DateTime(2019,10,2,9,15);
      DateTime dateTime2 = new DateTime(2019,10,3,9,15);
      System.out.println(Math.abs(dateTime1.getMillis()-dateTime2.getMillis()));
  }
}