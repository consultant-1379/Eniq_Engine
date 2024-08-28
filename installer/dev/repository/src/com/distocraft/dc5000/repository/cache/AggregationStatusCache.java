package com.distocraft.dc5000.repository.cache;

import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.common.Constants;

public class AggregationStatusCache {

  private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

  private static Connection con;

  private static String dburl;

  private static String dbuser;

  private static String passwd;

  private static Hashtable cache = new Hashtable();

  private static long read = 0L;

  private static long write = 0L;

  private static long hits = 0L;

  private static long hardmiss = 0L;

  private static long softmiss = 0L;

  private static long dbmiss = 0L;

  private static Logger log = Logger.getLogger("etlengine.AggregationStatusCache");

  private static Timer timer = null;

  public static void init(String _dburl, String _dbuser, String _passwd, String drvname) throws Exception {

    Class.forName(drvname);

    dburl = _dburl;
    dbuser = _dbuser;
    passwd = _passwd;

    con = DriverManager.getConnection(dburl, dbuser, passwd);

    int speriod = -1;

    try {
      speriod = Integer.parseInt(StaticProperties.getProperty("AggregationStatusCache.StatisticsPeriod", "-1"));
    } catch (Exception e) {
    }

    if (speriod > 0) {
      timer = new Timer();
      timer.schedule(new StatisticsTask(), speriod * 60000, speriod * 60000);
    }

    log.fine("Initialized");

  }

  public static class StatisticsTask extends TimerTask {

    public void run() {
      AggregationStatusCache.logStatistics();
    }

  };

  public static AggregationStatus getStatus(String aggregation, long datadate) {

    read++;

    synchronized (cache) {

      String key = key(aggregation, datadate);

      SoftReference sr = (SoftReference) cache.get(key);

      AggregationStatus ags = null;

      if (sr != null) { // Found cached entry for key

        ags = (AggregationStatus) sr.get();

        if (ags != null) { // Softreference was effective
          hits++;
        } else { // Softreference was already broken
          softmiss++;

          ags = readDatabase(aggregation, datadate);

          if (ags != null) {
            SoftReference jr = new SoftReference(ags);
            cache.put(key(ags.AGGREGATION, ags.DATADATE), jr);
          }

        }

      } else { // Cache miss

        hardmiss++;

        ags = readDatabase(aggregation, datadate);

        if (ags != null) {
          SoftReference jr = new SoftReference(ags);
          cache.put(key(ags.AGGREGATION, ags.DATADATE), jr);
        }

      }

      return ags;

    }

  }

  public static void setStatus(AggregationStatus as) {
    write++;

    synchronized (cache) {
      if (checkThresholdReset(as.STATUS) == true) {
        as.THRESHOLD = 0;
      }
      SoftReference sr = new SoftReference(as);
      cache.put(key(as.AGGREGATION, as.DATADATE), sr);
      writeDatabase(as);

    }

  }
  
  /**
   * Checks if the threshold value should be reset to 0 (null in database).
   * @param   currentStatus     The current status as a string.
   * @return  resetThreshold    True if the threshold value should be reset.
   */
  protected static boolean checkThresholdReset(final String currentStatus) {
    boolean resetThreshold = false;  
    
    // MANUAL, IGNORED, ERROR, FAILEDDEPENDENCY, LATE DATA or AGGREGATED,
    // threshold should be reset:
    if (currentStatus.equalsIgnoreCase(Constants.AGG_MANUAL_STATUS) ||
        currentStatus.equalsIgnoreCase(Constants.AGG_IGNORED_STATUS) ||
        currentStatus.equalsIgnoreCase(Constants.AGG_FAILED_STATUS) ||
        currentStatus.equalsIgnoreCase(Constants.AGG_FAILED_DEPENDENCY_STATUS) || 
        currentStatus.equalsIgnoreCase(Constants.AGG_LATE_DATA_STATUS) ||
        currentStatus.equalsIgnoreCase(Constants.AGG_AGGREGATED_STATUS)){
      resetThreshold = true;
    }
    return resetThreshold;
  }

  public static void update(String sql) throws Exception {
    synchronized (cache) {

      PreparedStatement stmt = null;

      try {

        try {

          stmt = con.prepareStatement(sql);
          int rcount = stmt.executeUpdate();

          if (rcount > 0)
            cache.clear();

        } catch (SQLException e) {

          String msg = e.getMessage();

          if (msg.indexOf("Connection is already closed") >= 0) { // KILL-IDLE is teasing us

            log.info("Connection was already closed. Trying again...");

            con = DriverManager.getConnection(dburl, dbuser, passwd);

            stmt = con.prepareStatement(sql);
            int rcount = stmt.executeUpdate();
            
            if (rcount > 0)
              cache.clear();
          }
        }

      } finally {
        if (stmt != null) {
          try {
            stmt.close();
          } catch (Exception e) {
          }
        }
      }
    }
  }

  public static void logStatistics() {
    log.info("Cache statistics " + read + " reads " + write + " writes");
    double hitrate = hits / read * 100;
    log.info("Cache hit rate " + hitrate + " %");
    double smissrate = softmiss / read * 100;
    log.info("Soft reference miss " + smissrate);
    double dbmissrate = dbmiss / read * 100;
    log.info("Database misses " + dbmissrate);
  }

  private static AggregationStatus readDatabase(String aggregation, long datadate) {

    log.finest("Reading database " + aggregation + "," + sdf.format(new Date(datadate)));

    PreparedStatement stmt = null;
    ResultSet rs = null;

    AggregationStatus ax = null;

    try {

      try {
        stmt = con.prepareStatement("SELECT * FROM LOG_AGGREGATIONSTATUS WHERE AGGREGATION = ? AND DATADATE = ?");
        stmt.setString(1, aggregation);
        stmt.setDate(2, new Date(datadate));

        rs = stmt.executeQuery();

      } catch (SQLException e) {

        String msg = e.getMessage();

        if (msg.indexOf("Connection is already closed") >= 0) { // KILL-IDLE
          // is
          // teasing
          // us

          log.info("Connection was already closed. Trying again...");

          con = DriverManager.getConnection(dburl, dbuser, passwd);

          stmt = con.prepareStatement("SELECT * FROM LOG_AGGREGATIONSTATUS WHERE AGGREGATION = ? AND DATADATE = ?");

          stmt.setString(1, aggregation);
          stmt.setDate(2, new Date(datadate));

          rs = stmt.executeQuery();

        }
      }

      while (rs.next()) {

        if (ax != null)
          log.warning("AggregationStatus not unique for aggregation=" + aggregation + " datadate="
              + sdf.format(new Date(datadate)));

        ax = new AggregationStatus();
        ax.AGGREGATION = rs.getString("AGGREGATION");
        ax.TYPENAME = rs.getString("TYPENAME");
        ax.TIMELEVEL = rs.getString("TIMELEVEL");
        ax.DATADATE = rs.getDate("DATADATE").getTime();
        if (rs.getTimestamp("INITIAL_AGGREGATION") != null)
          ax.INITIAL_AGGREGATION = rs.getTimestamp("INITIAL_AGGREGATION").getTime();
        ax.STATUS = rs.getString("STATUS");
        ax.DESCRIPTION = rs.getString("DESCRIPTION");
        ax.ROWCOUNT = rs.getInt("ROWCOUNT");
        ax.AGGREGATIONSCOPE = rs.getString("AGGREGATIONSCOPE");
        if (rs.getTimestamp("LAST_AGGREGATION") != null)
          ax.LAST_AGGREGATION = rs.getTimestamp("LAST_AGGREGATION").getTime();
        //TR:HN57054 - EEIKBE (START).
        ax.LOOPCOUNT = rs.getInt("LOOPCOUNT");
        //ax.ROWCOUNT = rs.getInt("LOOPCOUNT");
        //TR:HN57054 - EEIKBE (FINISH).
        if (rs.getTimestamp("THRESHOLD") != null) {
          ax.THRESHOLD = rs.getTimestamp("THRESHOLD").getTime();       
        }
      }

    } catch (Exception e) {
      log.log(Level.WARNING, "readDatabase failed", e);
    } finally {
      try {
        rs.close();
      } catch (Exception e) {
      }
      try {
        stmt.close();
      } catch (Exception e) {
      }
    }

    if (ax == null)
      dbmiss++;

    return ax;
  }
  
  private static void writeDatabase(AggregationStatus as) {

    log.finest("Writing database: " + as.AGGREGATION + "," + sdf.format(new Date(as.DATADATE)) + ",status=" + as.STATUS
        + ",loopcount=" + as.LOOPCOUNT);

    PreparedStatement stmt = null;

    try {

      try {

        String tablename = PhysicalTableCache.getCache().getTableName("LOG_AggregationStatus:PLAIN",as.DATADATE);                
        
        if (tablename!=null){
          
          stmt = con
          .prepareStatement("UPDATE "+tablename+" SET AGGREGATION=?,TYPENAME=?,TIMELEVEL=?,DATADATE=?,INITIAL_AGGREGATION=?,STATUS=?,DESCRIPTION=?,ROWCOUNT=?,AGGREGATIONSCOPE=?,LAST_AGGREGATION=?, LOOPCOUNT=?, THRESHOLD=? WHERE AGGREGATION=? AND DATADATE=?");

          stmt.setString(1, as.AGGREGATION);
          stmt.setString(2, as.TYPENAME);
          stmt.setString(3, as.TIMELEVEL);
          stmt.setDate(4, new Date(as.DATADATE));
    
          if (as.INITIAL_AGGREGATION == 0) {
            stmt.setTimestamp(5, null);
          } else {
            stmt.setTimestamp(5, new Timestamp(as.INITIAL_AGGREGATION));
          }
          stmt.setString(6, as.STATUS);
          stmt.setString(7, as.DESCRIPTION);
          stmt.setInt(8, as.ROWCOUNT);
          stmt.setString(9, as.AGGREGATIONSCOPE);
          if (as.LAST_AGGREGATION == 0) {
            stmt.setTimestamp(10, null);
          } else {
            stmt.setTimestamp(10, new Timestamp(as.LAST_AGGREGATION));
          }
          stmt.setInt(11, as.LOOPCOUNT);
          
          if (as.THRESHOLD == 0) {
            stmt.setTimestamp(12, null);
          } else {
            stmt.setTimestamp(12, new Timestamp(as.THRESHOLD));
          }
          
          stmt.setString(13, as.AGGREGATION);
          stmt.setDate(14, new Date(as.DATADATE));
    
          stmt.executeUpdate();
          
        } else {
          log.warning("LOG_AggregationStatus partition not found at "+sdf.format(new Date(as.DATADATE)));
        }
        


      } catch (SQLException e) {

        String msg = e.getMessage();

        if (msg.indexOf("Connection is already closed") >= 0) { // KILL-IDLE
          // is
          // teasing
          // us

          log.info("Connection was already closed. Trying again...");

          con = DriverManager.getConnection(dburl, dbuser, passwd);
          
          String tablename = PhysicalTableCache.getCache().getTableName("LOG_AggregationStatus:PLAIN",as.DATADATE);

          if (tablename!=null){
        
              stmt = con
                  .prepareStatement("UPDATE "+tablename+" SET AGGREGATION=?,TYPENAME=?,TIMELEVEL=?,DATADATE=?,INITIAL_AGGREGATION=?,STATUS=?,DESCRIPTION=?,ROWCOUNT=?,AGGREGATIONSCOPE=?,LAST_AGGREGATION=?, LOOPCOUNT=?, THRESHOLD=? WHERE AGGREGATION=? AND DATADATE=?");
                
              stmt.setString(1, as.AGGREGATION);
              stmt.setString(2, as.TYPENAME);
              stmt.setString(3, as.TIMELEVEL);
              stmt.setDate(4, new Date(as.DATADATE));
    
              if (as.INITIAL_AGGREGATION == 0) {
                stmt.setTimestamp(5, null);
              } else {
                stmt.setTimestamp(5, new Timestamp(as.INITIAL_AGGREGATION));
              }
              stmt.setString(6, as.STATUS);
              stmt.setString(7, as.DESCRIPTION);
              stmt.setInt(8, as.ROWCOUNT);
              stmt.setString(9, as.AGGREGATIONSCOPE);
              if (as.LAST_AGGREGATION == 0) {
                stmt.setTimestamp(10, null);
              } else {
                stmt.setTimestamp(10, new Timestamp(as.LAST_AGGREGATION));
              }
              stmt.setInt(11, as.LOOPCOUNT);
              if (as.THRESHOLD == 0) {
                stmt.setTimestamp(12, null);
              } else {
                stmt.setTimestamp(12, new Timestamp(as.THRESHOLD));
              }
              stmt.setString(13, as.AGGREGATION);
              stmt.setDate(14, new Date(as.DATADATE));
    
              stmt.executeUpdate();
          
          } else {
            log.warning("LOG_AggregationStatus partition not found at "+sdf.format(new Date(as.DATADATE)));
          }

        } else {
          log.log(Level.WARNING, "writeDatabase failed", e);
        }
      }

    } catch (Exception e) {
      log.log(Level.WARNING, "writeDatabase failed", e);
    } finally {
      try {
        stmt.close();
      } catch (Exception e) {
      }
      try {
        con.commit();
      } catch (Exception e) {
      }
    }

  }

  private static String key(String aggregation, long datadate) {
    return aggregation + "_" + datadate;
  }

}