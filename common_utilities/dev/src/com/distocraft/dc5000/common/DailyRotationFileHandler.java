package com.distocraft.dc5000.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class DailyRotationFileHandler extends StreamHandler {

  private static final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");

  private String pattern = null;

  private String openStream = null;

  private BufferedOutputStream out = null;

  public DailyRotationFileHandler() throws IOException, SecurityException {

    LogManager manager = LogManager.getLogManager();

    String cname = DailyRotationFileHandler.class.getName();

    pattern = manager.getProperty(cname + ".pattern");
    if (pattern == null)
      throw new IOException("Parameter \"" + cname + ".pattern\" is not defined.");
    
    if(pattern.startsWith("$log")) {
      String end = pattern.substring(4);
      
      String log_dire = System.getProperty("LOG_DIR");

      if (log_dire == null)
        throw new IOException("System property \"LOG_DIR\" not defined");

      pattern = log_dire + end;
    }

    setLevel(getLevel(cname + ".level"));

    setFormatter(getFormatter(cname + ".formatter"));

    String filt = manager.getProperty(cname + ".filter");

    if (filt != null && filt.trim().length() > 0)
      setFilter(new PrefixFilter(filt));

    Date dat = new Date();
    String dstamp = form.format(dat);
    rotate(dstamp);

  }

  public synchronized void publish(LogRecord record) {
    
    if (!isLoggable(record)) {
      return;
    }

    Date dat = new Date(record.getMillis());

    String dstamp = form.format(dat);

    if (!dstamp.equals(openStream))
      rotate(dstamp);

    super.publish(record);
    flush();
  }

  private void rotate(String timestamp) {
    
    try {

      if (out != null) // a file is already open
        out.close();

      File f = new File(pattern + timestamp + ".log");
      FileOutputStream fos = new FileOutputStream(f,true);
      out = new BufferedOutputStream(fos);

      setOutputStream(out);

      openStream = timestamp;

    } catch (Exception e) {
      System.err.println("LogRotation failed");
      e.printStackTrace();
    }

  }

  private Formatter getFormatter(String prop) {

    try {
      Formatter formatter = (LogFormatter) (Class.forName(prop).newInstance());
      return formatter;
    } catch (Exception e) {
      return new OneLinerLogFormatter();
    }

  }

  private Level getLevel(String prop) {

    if (prop.equalsIgnoreCase("SEVERE"))
      return Level.SEVERE;
    else if (prop.equalsIgnoreCase("WARNING"))
      return Level.WARNING;
    else if (prop.equalsIgnoreCase("CONFIG"))
      return Level.CONFIG;
    else if (prop.equalsIgnoreCase("INFO"))
      return Level.INFO;
    else if (prop.equalsIgnoreCase("FINE"))
      return Level.FINE;
    else if (prop.equalsIgnoreCase("FINER"))
      return Level.FINER;
    else if (prop.equalsIgnoreCase("FINEST"))
      return Level.FINEST;
    else if (prop.equalsIgnoreCase("OFF"))
      return Level.OFF;
    else
      return Level.ALL;

  }

}
