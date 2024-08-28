package com.distocraft.dc5000.common;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created on 13.7.2005
 * Formatter class for logger.
 * @author vesterinen
 */
public class LogFormatter extends Formatter {

  public LogFormatter() {
    super();
  }

  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    
    DateFormatter df = new DateFormatter();
    buf.append(df.getCurrentDate("."));    
    buf.append(' ');
    buf.append(df.getCurrentTime(":"));
    buf.append(' ');
    buf.append(rec.getLevel());
    buf.append(' ');
    buf.append(rec.getLoggerName());
    buf.append(' ');
    buf.append(formatMessage(rec));
    if (rec.getThrown() != null){ 
      StackTraceElement[] stak = rec.getThrown().getStackTrace();
      buf.append('\n');
      buf.append(rec.getThrown().fillInStackTrace());
      buf.append('\n');
      for (int i=0; i < stak.length; i++){  
        buf.append('(');
        buf.append(stak[i]);
        buf.append(')');
        buf.append('\n');
      }
    }
    if (rec.getThrown() == null)
      buf.append("\n");
    return buf.toString();
  }
  
}
