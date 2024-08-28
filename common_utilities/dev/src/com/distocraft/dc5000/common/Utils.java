/**
 * 
 */
package com.distocraft.dc5000.common;


/**
 * @author eheijun
 *
 */
public class Utils {
  
  private Utils() {
  }

  public static Integer replaceNull(final Integer value) {
    if (value == null) {
      return Integer.valueOf(0);
    }
    return value;
  }

  public static String replaceNull(final String value) {
    if (value == null) {
      return "";
    }
    return value;
  }

  public static Double replaceNull(final Double value) {
    if (value == null) {
      return new Double(0);
    }
    return value;
  }

  public static Long replaceNull(final Long value) {
    if (value == null) {
      return Long.valueOf(0);
    }
    return value;
  }

  
}
