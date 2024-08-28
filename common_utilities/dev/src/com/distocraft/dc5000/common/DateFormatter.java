package com.distocraft.dc5000.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created on 5.7.2005
 * Class is wrapper class for Calendar. Extra features are added for timeparsing and certain time views.
 * @author vesterinen
 */
public class DateFormatter {
  private SimpleDateFormat sdf;

  private Calendar cal = Calendar.getInstance();

  /**
   * Formats date in correct format. e.g. HH.mm.ss.mm
   * @param format
   */
  public DateFormatter(String format) {
    //"HH.mm.ss.mm"
    sdf = new SimpleDateFormat(format);
  }

  /**
   * default constructor
   */
  public DateFormatter() {
    super();
  }

  /**
   * Returns Calendar object with current time.
   * @return
   */
  public Calendar getTime() {
    return cal;
  }

  /**
   * Returns current year as integer value
   * @return year
   */
  public int getCurrentYear() {
    return cal.get(Calendar.YEAR);
  }

  /**
   * Returns current day of month
   * @return current day
   */
  public int getCurrentDate() {
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Returns current month of year
   * @return current month
   */
  public int getCurrentMonth() {
    return cal.get(Calendar.MONTH);
  }

  /**
   * Shows time as wanted with certain delimeter. e.g time is wanted as '-' delimiting time -> 13-23-24 
   * @param delim
   * @return par
   */
  public String getCurrentTime(String delim) {
    int seconds = cal.getTime().getSeconds();
    int minutes = cal.getTime().getMinutes();
    String secFormat = String.valueOf(seconds);
    String minFormat = String.valueOf(minutes);
    if (seconds < 10)
      secFormat = "0" + seconds;
    if (minutes < 10)
      minFormat = "0" + minutes;
    return (cal.getTime().getHours() + delim + minFormat
        + delim + secFormat);
  }

  /**
   * Formats month and date with certain delimeter. e.g. 30.12 (mm.dd)
   * @param delim
   * @return
   */
  public String getCurrentDate(String delim) {
    int date = cal.getTime().getDate();
    String formatDate = String.valueOf(cal.getTime().getDate());
    int month = cal.getTime().getMonth() + 1;
    String formatMonth = String.valueOf(cal.getTime().getMonth() + 1);

    if (date < 10)
      formatDate = "0" + date;
    if (month < 10)
      formatMonth = "0" + month;
    return formatDate + delim + formatMonth;
  }

  /**
   * Formats year, month and date with certain delimeter. e.g. 2005.30.12 (yyyy.mm.dd)
   * @param delim
   * @return
   */
  public String getCurrentDateAndYear(String delim) {
    int date = cal.getTime().getDate();
    String formatDate = String.valueOf(cal.getTime().getDate());
    int month = cal.getTime().getMonth() + 1;
    String formatMonth = String.valueOf(cal.getTime().getMonth() + 1);

    if (date < 10)
      formatDate = "0" + date;
    if (month < 10)
      formatMonth = "0" + month;
    return (cal.getTime().getYear() + 1900) + delim + formatMonth + delim
        + formatDate;
  }

  /**
   * Sets time, object that is created is accessed via DateFormatter.
   * @param time
   */
  public void setCalendar(String time) {
    try {
      cal.setTime(sdf.parse(time));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reverses time in amount of months. Returns calendar instance representing new time of date.
   * @param amount - amount of months that is subtracted from current time
   * @return Calendar - represents subtracted time.
   */
  public Calendar reverseTime(int amount) {
    Calendar working;
    working = Calendar.getInstance();
    working.add(Calendar.MONTH, -(amount));
    return working;
  }

  //Example for testing and using Dateformatter and comparator.
  public static void main(String[] args) {
    DateFormatter df = new DateFormatter("yyyy-MM-dd hh:mm:ss.mm");
    df.setCalendar("2005-06-23 11:55:34.0");
    System.out.println(df.reverseTime(3).getTime());
    System.out.println(df.getTime().getTime());
    System.out.println(df.getTime().after(df.reverseTime(3)));
  }
}
