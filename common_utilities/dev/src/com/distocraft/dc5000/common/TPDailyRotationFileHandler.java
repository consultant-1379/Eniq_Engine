package com.distocraft.dc5000.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class TPDailyRotationFileHandler extends Handler {

  private static final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");

  private String dirpattern = null;

  private HashMap logs = new HashMap();

  private boolean deb = false;

  public TPDailyRotationFileHandler() throws IOException, SecurityException {

    LogManager manager = LogManager.getLogManager();

    String cname = TPDailyRotationFileHandler.class.getName();

    dirpattern = manager.getProperty(cname + ".dirpattern");
    if (dirpattern == null)
      throw new IOException("Parameter \"" + cname + ".dirpattern\" is not defined.");

    if (dirpattern.startsWith("$log")) {
      String end = dirpattern.substring(4);
      String log_dire = System.getProperty("LOG_DIR");

      if (log_dire == null)
        throw new IOException("System property \"LOG_DIR\" not defined");

      dirpattern = log_dire + end;
    }

    String xlevel = manager.getProperty(cname + ".level");
    if (xlevel != null)
      setLevel(getLevel(xlevel));
    else
      setLevel(Level.FINEST);

    setFormatter(getFormatter(cname + ".formatter"));

    String filt = manager.getProperty(cname + ".filter");

    if (filt != null && filt.trim().length() > 0)
      setFilter(new PrefixFilter(filt));

    String xdeb = manager.getProperty(cname + ".debug");
    if (xdeb != null && xdeb.length() > 0)
      deb = true;

  }

  /**
   * Does nothing because publish will handle flush after writing
   */
  public synchronized void flush() {

    if (deb)
      System.err.println("TPDRFH.flush()");

  }

  public synchronized void close() {

    if (deb)
      System.err.println("TPDRFH.close()");

    Iterator i = logs.keySet().iterator();

    while (i.hasNext()) {

      try {

        String key = (String) i.next();

        OutputDetails od = (OutputDetails) logs.get(key);
        od.out.close();
        od.out = null;

        i.remove();

      } catch (Exception e) {
      }

    }

  }

  public synchronized void publish(LogRecord record) {

    if (deb)
      System.err.println("TPDRFH.publish(" + record.getLoggerName() + ")");

    // Determine that level is loggable and filter passes
    if (!isLoggable(record)) {
      return;
    }

    String tp = "NA";
    String type = "engine";

    try {

      String logname = record.getLoggerName();

      // Special handling fot these loggers
      if (logname.startsWith("etl.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix)); // TP name is
                                                              // second word
      } else if (logname.startsWith("sql.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix)); // TP name is
                                                              // second word
        type = "sql";
      } else if (logname.startsWith("file.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix)); // TP name is
                                                              // second word
        type = "file";
      } else if (logname.startsWith("sqlerror.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix)); // TP name is
                                                              // second word
        type = "sqlerror";
      }

    } catch (Exception e) {
    }

    if (deb)
      System.err.println("TechPackName is \"" + tp + "\" type is \"" + type + "\"");

    OutputDetails od = (OutputDetails) logs.get(tp + "_" + type);

    Date dat = new Date(record.getMillis());

    String dstamp = form.format(dat);

    if (od == null || !dstamp.equals(od.dat))
      od = rotate(tp, type, dstamp);

    try {
      od.out.write(getFormatter().format(record));
      od.out.flush();

      if (deb)
        System.err.println("Written: " + record.getMessage());
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  private OutputDetails rotate(String tp, String type, String timestamp) {

    if (deb)
      System.err.println("Initialized DRFH.rotate(" + tp + " " + type + " " + timestamp + ")");

    OutputDetails od = null;

    try {

      od = (OutputDetails) logs.get(tp);

      if (od == null)
        od = new OutputDetails();
      else if (od.out != null) // a file is already open
        od.out.close();

      String dirx = null;

      if (!"NA".equals(tp))
        dirx = dirpattern + File.separator + tp;
      else
        dirx = dirpattern + File.separator;

      File dir = new File(dirx);
      if (!dir.exists())
        dir.mkdirs();

      File f = new File(dir, type + "-" + timestamp + ".log");

      if (deb)
        System.err.println("FileName is " + f.getCanonicalPath());

      od.out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f, true)));
      od.dat = timestamp;

      od.out.write(getFormatter().getHead(this));

      logs.put(tp + "_" + type, od);

    } catch (Exception e) {
      System.err.println("LogRotation failed");
      e.printStackTrace();
    }

    return od;

  }

  public class OutputDetails {

    public Writer out = null;

    public String dat = null;

  };

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
