package com.distocraft.dc5000.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A static general purpose class that is currently used for handling session
 * IDs and logging adapter and logger events. This class shall not be
 * instantiated. Thread is extended because this class act also as shutdown hook
 * writing last used session information. <br>
 * <br>
 * Configuration: static.properties via common.StaticProperties-object. <br>
 * <br>
 * Database usage: No direct access. <br>
 * <br>
 * $id$ <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 *
 * @author lemminkainen
 */
public class SessionHandler extends Thread {

  private static final long SESSION_ID_MAX = 4294967290L;

  private static long DB_MAX_SESSION_ID = 0L;

  private static Object synO = new Object();

  private static HashMap sessions = null;

  private static HashMap logHandlers = null;

  private static Logger log = Logger.getLogger("etlengine.common.SessionHandler");

  /**
   * SessionHandler works statically, so there is no need to instatiate it.
   */
  private SessionHandler() {
  }

  public static int getBulkLimit(){
    int bl = 10000;
    try{
      bl = Integer.parseInt(StaticProperties.getProperty("session_handler_bulk_limit",
        System.getProperty("session_handler_bulk_limit", Integer.toString(bl))));
    } catch (NullPointerException e){
      //Ignore, use default
    }
    return bl;
  }


  /**
   * Logs one DC action. Registered LogHandlers are responsible of handling
   * actions.
   *
   * @param sessionType
   *          session type identifier. This is used to find handler for this
   *          event.
   * @param data
   *          the log data.
   */
  public static void log(final String sessionType, final Map data) {

    log.finest("log(" + sessionType + ")");

    final SessionLogger sl = (SessionLogger) logHandlers.get(sessionType);

    if (sl == null) {
      log.warning("No SessionLogger defined for " + sessionType);
    } else {
      sl.log(data);
    }

  }

  /**
   * Logs all DC action. Registered LogHandlers are responsible of handling
   * actions.
   *
   * @param sessionType
   *          session type identifier. This is used to find handler for this
   *          event.
   * @param data
   *          the log data.
   */
  public static void bulkLog(final String sessionType, final Collection<Map<String, Object>> data) {

    log.finest("log(" + sessionType + ")");

    final SessionLogger sl = (SessionLogger) logHandlers.get(sessionType);

    if (sl == null) {
      log.warning("No SessionLogger defined for " + sessionType);
    } else {
      sl.bulkLog(data);
    }

  }

  /**
   * Performs session log rotation. After rotation table files are ready for
   * loading into database.
   *
   * @param name
   *          Name of logger to be rotated.
   */
  public static void rotate(final String name) {

    final SessionLogger sl = (SessionLogger) logHandlers.get(name);

    if (sl == null) {
      log.fine("No SessionLogger defined for " + name);
    } else {
      try {
        sl.rotate();
      } catch (Exception e) {
        log.log(Level.WARNING, "Log rotation failed for " + name, e);
      }
    }

  }

  /**
   * Reserves sessionID for specified sessionType
   *
   * @param sessionType
   *          Name of session type
   * @return reserved sessionID
   * @throws Exception
   *           is thrown in case of failure
   */
  public static long getSessionID(final String sessionType) throws Exception {

    synchronized (synO) {

      SessionType st = (SessionType) sessions.get(sessionType);

      if (st == null) { // first use
        log.finest("First use of sessionType " + sessionType);
        reserve(sessionType);
        st = (SessionType) sessions.get(sessionType);
      }

      // Checking that engine session ID file is in synch with database
      if (sessionType == "engine" && SESSION_ID_MAX > st.current && DB_MAX_SESSION_ID >= st.current
          && DB_MAX_SESSION_ID < SESSION_ID_MAX) {
        st.current = DB_MAX_SESSION_ID;

        log.warning("Session ID mismatch between database and storage file for type " + sessionType
            + ". ID was be changed to match the database value.");
      }

      st.current++;

      // Handling for session ID overflow
      if (st.current >= SESSION_ID_MAX) {
        log.warning("SessionID overflow detected on type " + sessionType);

        st.current = 0;
        st.lastReserved = 0;

        persist();

        log.warning("SessionID counter of type " + sessionType + " was zeroed");

      }

      final long sessionid = st.current;

      if (st.current > st.lastReserved) {
        reserve(sessionType);
      }

      return sessionid;

    } // synch block

  }

  public static void setDBMaxSessionID(long maxDBSessionID) throws Exception {
    DB_MAX_SESSION_ID = maxDBSessionID;
  }

  /**
   * Do not touch. This method is runned as shutdown hook during shutdown of
   * JVM.
   */
  public void run() {

    boolean success = true;

    try {

      final Iterator sit = sessions.keySet().iterator();

      while (sit.hasNext()) {
        final SessionType st = (SessionType) sessions.get(sit.next());
        st.lastReserved = st.current;
      }

      persist();

    } catch (Exception e) {
      System.err.println("Shutdown hook failed on sessions");
      e.printStackTrace();
      success = false;
    }

    try {

      final Iterator i = logHandlers.keySet().iterator();

      while (i.hasNext()) {
        final String key = (String) i.next();
        final SessionLogger lh = (SessionLogger) logHandlers.get(key);
        lh.doFinish();
      }

    } catch (Exception e) {
      System.err.println("Shutdown hook failed on loggers");
      e.printStackTrace();
      success = false;
    }

    if (success) {
      System.out.println("Session state successfully persisted.");
    }

  }

  /**
   * Reserves a range of sessionIDs
   *
   * @param sessionType
   */
  private static void reserve(final String sessionType) throws Exception {

    log.finer("Reserving id range for " + sessionType);

    final long incrementStep = Long.parseLong(StaticProperties.getProperty("SessionHandling.incrementStep", "50"));

    if (sessions.containsKey(sessionType)) {
      final SessionType st = (SessionType) sessions.get(sessionType);
      st.lastReserved = st.lastReserved + incrementStep;
    } else {
      final SessionType st = new SessionType();
      st.current = 0;
      st.lastReserved = incrementStep;
      sessions.put(sessionType, st);
    }

    log.finest(incrementStep + " sessions reserved");

    persist();

  }

  /**
   * Reads the sessionID list from the storage file and initializes LogHandlers
   *
   * @throws Exception
   *           is thrown in case of failure
   */
  public static void init() throws Exception {

    log.fine("initializing...");

    String fil = StaticProperties.getProperty("SessionHandling.storageFile");
    if (fil == null) {
      throw new Exception("SessionHandling.storageFile must be defined");
    }

    if (fil.startsWith("${ETLDATA_DIR}")) {
      fil = System.getProperty("ETLDATA_DIR") + fil.substring(14);
    }

    final File f = new File(fil);

    sessions = new HashMap();

    if (f.exists() && (!f.canRead() || !f.canWrite())) {
      throw new Exception("SessionHandler storage file " + fil + " exists, but cannot be read or written.");
    }

    if (f.exists()) {

      FileInputStream fi = null;
      java.util.Properties p = null;

      try {

        fi = new FileInputStream(f);

        p = new java.util.Properties();

        p.load(fi);

      } finally {
        if (fi != null) {
          try {
            fi.close();
          } catch (Exception e) {
            log.log(Level.WARNING, "Error closing file", e);
          }
        }
      }

      final Enumeration e = p.keys();

      while (e.hasMoreElements()) {
        final String key = (String) e.nextElement();

        final SessionType st = new SessionType();
        st.current = Integer.parseInt(p.getProperty(key));
        st.lastReserved = st.current;

        log.finest("Init found type " + key + " current id " + st.current);

        sessions.put(key, st);

      }

    } else {
      log.info("SessionHandler storage file does not exist. Session IDs will start from zero.");
    }

    log.fine("SessionID facility initialized. " + sessions.size() + " existing counters.");

    logHandlers = new HashMap();

    final String slogs = StaticProperties.getProperty("SessionHandling.log.types");

    final StringTokenizer tz = new StringTokenizer(slogs, ",");
    while (tz.hasMoreTokens()) {
      final String token = tz.nextToken().trim();

      if (token.length() <= 0) {
        continue;
      }

      log.finest("Found loggerType " + token);

      final String logClass = StaticProperties.getProperty("SessionHandling.log." + token + ".class");

      if (logClass == null) {
        log.warning("No logger class found for " + logClass);
        continue;
      }

      try {

        final SessionLogger al = (SessionLogger) Class.forName(logClass).newInstance();
        logHandlers.put(token, al);

        log.info("SessionLogger was initialized for type " + token);

      } catch (Throwable e) {
        log.log(Level.WARNING, "SessionLogger " + logClass + "failed to initialize. Ignored.", e);
      }

    }

    log.fine("Logger facility initialized. " + logHandlers.size() + " loggers.");

    final SessionHandler sh = new SessionHandler();
    Runtime.getRuntime().addShutdownHook(sh);

    log.fine("Shutdown Hook added");

  }

  /**
   * Persists sessionID list to file
   *
   * @throws Exception
   *           is thrown in case of failure.
   */
  private static void persist() throws Exception {
    log.finer("Persisting state to StorageFile");

    String fil = StaticProperties.getProperty("SessionHandling.storageFile");
    if (fil == null) {
      throw new Exception("SessionHandling.storageFile must be defined");
    }

    if (fil.startsWith("${ETLDATA_DIR}")) {
      fil = System.getProperty("ETLDATA_DIR") + fil.substring(14);
    }

    final File f = new File(fil);

    FileOutputStream fo = null;

    try {

      fo = new FileOutputStream(f);

      final java.util.Properties p = new java.util.Properties();

      final Iterator i = sessions.keySet().iterator();

      while (i.hasNext()) {
        final String key = (String) i.next();

        final SessionType st = (SessionType) sessions.get(key);

        p.put(key, String.valueOf(st.lastReserved));

      }

      p.store(fo, "Reserved sessionID information. DO NOT TOUCH!");

      log.fine("Successfully persisted to StorageFile");

    } finally {
      if (fo != null) {
        try {
          fo.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Error closing file", e);
        }
      }
    }

  }
}