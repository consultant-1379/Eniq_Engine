package com.distocraft.dc5000.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Java.util.Logging implementation for formatting log output. <br>
 * <br>
 * Configuration: none <br>
 * <br>
 * $id$ <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * 
 * @author lemminkainen
 */
public class OneLinerLogFormatter extends Formatter {

  public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm:ss");
  
  /**
   * Formats one log entry.
   * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
   */
  public String format(LogRecord lr) {

    StringBuffer res = new StringBuffer();

    res.append(sdf.format(new Date(lr.getMillis())));
    res.append(" ");
    res.append(lr.getLevel().getName());
    res.append(" ");
    res.append(lr.getLoggerName());
    res.append(" : ");
    res.append(lr.getMessage());
    res.append("\n");

    Throwable t = lr.getThrown();
    int inten = 3;

    while (t != null) {
      appendException(t, inten, res);
      inten += 3;

      t = t.getCause();
    }

    return res.toString();
  }

  private void appendException(Throwable t, int inten, StringBuffer res) {
    if (t != null) {

      for (int i = 0; i < inten; i++)
        res.append(" ");

      res.append(t.getClass().getName());
      res.append(": ");
      res.append(t.getMessage());
      res.append("\n");

      StackTraceElement[] ste = t.getStackTrace();

      for (int j = 0; j < ste.length; j++) {

        for (int i = 0; i < inten + 5; i++)
          res.append(" ");

        res.append(ste[j].getClassName());
        res.append(".");
        res.append(ste[j].getMethodName());
        res.append("(");
        if (ste[j].getFileName() == null) {
          res.append("Unknown Source");
        } else {
          res.append(ste[j].getFileName());
          res.append(":");
          res.append(ste[j].getLineNumber());
        }
        res.append(")");
        res.append("\n");
        
      }
      
      ste = null;

    }

  }

}
