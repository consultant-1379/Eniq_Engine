package com.distocraft.dc5000.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author ejarsok
 * 
 */

public class DateFormatterTest {

  private Calendar cal = Calendar.getInstance();
  
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.HH.mm.ss.mm");
  
  private static DateFormatter dF;

  @BeforeClass
  public static void init() {
    dF = new DateFormatter("yyyy.MM.HH.mm.ss.mm");
    dF.setCalendar("2000.10.01.01.01.01");
  }
  
  @Test
  public void testGetTime() {
    Calendar c = dF.getTime();
    assertEquals("2000.10.01.01.01.01", sdf.format(c.getTime()));
  }
  
  @Test
  public void testGetCurrentYear() {
    assertEquals(2000, dF.getCurrentYear());
  }
  
  @Test
  public void testGetCurrentDate() {
    assertEquals(1, dF.getCurrentDate());
  }
  
  @Test
  public void testGetCurrentMonth() {
    assertEquals(9, dF.getCurrentMonth());
  }
  
  @Test
  public void testGetCurrentTime2() {
    assertEquals("1.01.01", dF.getCurrentTime("."));
  }
  
  @Test
  public void testGetCurrentDate2() {
    assertEquals("01.10", dF.getCurrentDate("."));
  }
  
  @Test
  public void testGetCurrentDateAndYear() {
    assertEquals("2000.10.01", dF.getCurrentDateAndYear("."));
  }
  
  @Test
  public void testReverseTime() {
    Calendar reversed = dF.reverseTime(2);
    Calendar current = Calendar.getInstance();
    System.out.println(sdf.format(reversed.getTime()));
    current.roll(Calendar.MONTH, -2);
    current.get(Calendar.MONTH);
    assertEquals(current.get(Calendar.MONTH), reversed.get(Calendar.MONTH));
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(DateFormatterTest.class);
  }
}
