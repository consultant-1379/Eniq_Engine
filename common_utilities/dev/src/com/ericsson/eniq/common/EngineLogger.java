package com.ericsson.eniq.common;

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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.distocraft.dc5000.common.ConsoleLogFormatter;

public class EngineLogger extends Handler {

  private static final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");

  private String logdir = null;

  private HashMap logs = new HashMap();

  private boolean deb = false;

  public EngineLogger() throws IOException, SecurityException {

    logdir = System.getProperty("LOG_DIR");
    if (logdir == null)
      throw new IOException("System property \"LOG_DIR\" not defined");

    logdir = logdir + File.separator + "engine";
    
    setLevel(Level.ALL);
    setFormatter(new ConsoleLogFormatter());

    String xdeb = System.getProperty("EngineLogger.debug");
    if (xdeb != null && xdeb.length() > 0)
      deb = true;

  }

  /**
   * Does nothing because publish will handle flush after writing
   */
  public synchronized void flush() {

    if (deb)
      System.err.println("EL.flush()");

  }

  public synchronized void close() {

    if (deb)
      System.err.println("EL.close()");

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

  /**
   * Publish a LogRecord
   */
  public synchronized void publish(LogRecord record) {

    if (deb)
      System.err.println("EL.publish(" + record.getLoggerName() + ")");

    // Determine that level is loggable and filter passes
    if (!isLoggable(record)) {
      return;
    }

    String tp = "NA";
    String type = "engine";

    try {

      String logname = record.getLoggerName();

      // Special handling for these loggers
      if (logname.startsWith("etl.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix));
      } else if (logname.startsWith("sql.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix));
        type = "sql";
      } else if (logname.startsWith("file.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix));
        type = "file";
      } else if (logname.startsWith("sqlerror.")) {
        int ix = logname.indexOf(".") + 1;
        tp = logname.substring(ix, logname.indexOf(".", ix));
        type = "sqlerror";
      }

    } catch (Exception e) {
      if(deb)
        e.printStackTrace();
    }

    if (deb)
      System.err.println("EL: TechPackName is \"" + tp + "\" type is \"" + type + "\"");

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
    
    int levelInt = record.getLevel().intValue();
    if (levelInt >= Level.WARNING.intValue()) {
      if(deb)
        System.err.println("EL: Logging error");
      
      OutputDetails odw = (OutputDetails)logs.get("WARN_error");
      
      if (odw == null || !dstamp.equals(odw.dat))
        odw = rotate("WARN", "error", dstamp);

      try {
        odw.out.write(getFormatter().format(record));
        odw.out.flush();

        if (deb)
          System.err.println("EL: Written to error: " + record.getMessage());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      
    }

  }

  private OutputDetails rotate(String tp, String type, String timestamp) {

    if (deb)
      System.err.println("EL.rotate(" + tp + " " + type + " " + timestamp + ")");

    OutputDetails od = null;

    try {

      od = (OutputDetails) logs.get(tp + "_" + type);

      if (od == null)
        od = new OutputDetails();
      else if (od.out != null) // a file is already open
        od.out.close();

      String dirx = null;

      if ("NA".equals(tp))
        dirx = logdir + File.separator;
      else if ("WARN".equals(tp))
        dirx = logdir + File.separator;
      else
        dirx = logdir + File.separator + tp;

      File dir = new File(dirx);
      if (!dir.exists())
        dir.mkdirs();

      File f = new File(dir, type + "-" + timestamp + ".log");

      if (deb)
        System.err.println("EL: FileName is " + f.getCanonicalPath());

      od.out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f, true)));
      od.dat = timestamp;

      od.out.write(getFormatter().getHead(this));

      logs.put(tp + "_" + type, od);

    } catch (Exception e) {
      System.err.println("EL: LogRotation failed");
      e.printStackTrace();
    }

    return od;

  }

  public class OutputDetails {

    public Writer out = null;

    public String dat = null;

  };

}
