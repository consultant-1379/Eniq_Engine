package com.distocraft.dc5000.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SessionLogger {

  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  protected final Logger log;

  protected final String name;

  protected File inputTableDir = null;

  private final HashMap pws = new HashMap();

  protected int rowcount = 0;

  /* Acts like a Mutex for Thread safety */
  private static Object threadsafetyMutex = new Object();

  /**
   * Initialization of protected content this mus be done before accepting any
   * log entries
   *
   * @param name
   *          name of logger
   * @throws Exception
   *           is thrown in case of failure
   */
  SessionLogger(final String name) throws Exception {
    this.name = name;

    log = Logger.getLogger("etlengine.common.SessionLogger." + name);

    log.finer("init...");

    try {
      String loc = StaticProperties.getProperty("SessionHandling.log." + name + ".inputTableDir");
      if (loc.startsWith("${ETLDATA_DIR}")) {
        loc = System.getProperty("ETLDATA_DIR") + loc.substring(14);
      }
      inputTableDir = new File(loc);
    } catch (Exception e) {
      throw new Exception("SessionHandling.log." + name + ".inputTableDir needs to be defined.");
    }

    if (!inputTableDir.exists()) {
      inputTableDir.mkdirs();
    }

    if (!inputTableDir.exists() || !inputTableDir.isDirectory() || !inputTableDir.canWrite()) {
      throw new Exception("Can't access inputTableDir " + inputTableDir.getName());
    }

    log.finer("sucessfully initialized");

  }

  /**
   * Returns writer for certain day. If open file does not exists, it is
   * created.
   */
  protected synchronized PrintWriter getWriter(final String date) throws Exception {

    PrintWriter pw = null;

    //if (pw == null) {

      log.finest("Trying to open new writer " + date);

      final File newFile = new File(inputTableDir.getAbsolutePath() + File.separator + name + "." + date
          + ".unfinished");

      //final boolean exists = newFile.exists();

      pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(newFile, true)));

      //pws.put(date, pw);

      /* no headers needed in load files
      if (!exists) {
        writeFileHeader(pw);
      }*/

    //}

    return pw;

  }

  public String getName() {
    return name;
  }

  /**
   * Logs one sessionLog entry
   *
   * @param data
   *          SessionLog entry
   */
  public abstract void log(final Map<String, Object> data);

  /**
   * Logs all sessionLog entries
   *
   * @param data
   *          A collection of SessionLog entries
   */
  public abstract void bulkLog(final Collection<Map<String, Object>> data);

  /**
   * Finalization. Closes open streams.
   *
   * @see com.distocraft.dc5000.common.LogHandler#doFinish()
   */
  public void doFinish() {
    final Iterator i = pws.values().iterator();

    while (i.hasNext()) {
      try {
        final PrintWriter pw = (PrintWriter) i.next();
        pw.flush();
        pw.close();
      } catch (Exception e) {
        log.log(Level.WARNING, "doFinish failed", e);
      }
    }
  }

  /**
   * Rotation: closes open files.
   *
   * @throws Exception
   *           is thrown on error
   */
  public synchronized void rotate() throws Exception {

    log.fine("Rotating...");

    final Iterator pi = pws.values().iterator();
    while (pi.hasNext()) {
      final PrintWriter pw = (PrintWriter) pi.next();

      pw.close();
    }

    pws.clear();

    // List unfinished files
    final File[] fils = inputTableDir.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String fname) {
        return (fname.endsWith(".unfinished"));
      }
    });

    log.finest("Found " + fils.length + " unfinished files");

    // Mark unfinished to finished
    for (int i = 0; i < fils.length; i++) {
      final File uff = fils[i];

      String ffn = uff.getName().substring(0,uff.getName().lastIndexOf(".unfinished"));

      // KLUDGE: Ensure that upgrade does not crash to old unfinished files
      if(ffn.indexOf(".") < 0) {
        ffn += "." + sdf.format(new Date(System.currentTimeMillis()));
      }

      uff.renameTo(new File(uff.getParentFile(),ffn));
    }

  }
  protected void writeLog(final StringBuilder text , final String date) throws Exception {
    writeLog(text.toString(), date);
  }
  protected void writeLog(final StringBuffer text , final String date) throws Exception {
    writeLog(text.toString(), date);
  }

  private void writeLog(final String text, final String date) throws Exception {
	  try {
		  synchronized (threadsafetyMutex) {
			  final PrintWriter pw = getWriter(date);
			  pw.print(text);
			  pw.flush();
			  pw.close();
		}
	  }
	  catch (Exception e)
	  {
		 throw e;
	  }
  }
}
