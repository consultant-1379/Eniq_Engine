package com.distocraft.dc5000.repository.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

public class PhysicalTableCache {

  private static Logger log = Logger.getLogger("etlengine.repository.PhysicalTableCache");

  private static final String GET_ACTIVE_TABLES = "SELECT dwhp.storageid, dwhp.tablename, dwhp.starttime, dwhp.endtime, dwhp.status FROM DWHPartition dwhp, DWHType dwht, TPActivation tpa, TypeActivation ta  where dwht.storageid = dwhp.storageid and dwht.techpack_name = tpa.techpack_name and dwht.techpack_name = ta.techpack_name and dwht.typename = ta.typename and dwht.tablelevel = ta.tablelevel and tpa.status = 'ACTIVE' and ta.status='ACTIVE'";

  private static final String ACTIVE = "ACTIVE";
  
  private String dburl = null;

  private String dbusr = null;

  private String dbpwd = null;

  private Map si_map = null;
  
  // date: 9999-01-01 00:00:00
  private static final long ENDOFTHEWORLD = 253370757600000L; 

  private static PhysicalTableCache ptc = null;

  public class PTableEntry implements Comparable {
    public String storageID;

    public String tableName;

    public long startTime;

    public long endTime;
    
    public String status;

    public int compareTo(Object o) {
      if (o instanceof PTableEntry) {
        PTableEntry k = (PTableEntry) o;
        if (k.endTime > k.endTime) {
          return -1;
        } else if (k.endTime > k.endTime) {
          return 1;
        }
      }
      return 0;
    }
  };

  private PhysicalTableCache() {
  }

  public static void initialize(RockFactory rock) {

    ptc = new PhysicalTableCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwhrep");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      ptc.dburl = repdb.getConnection_string();
      ptc.dbusr = repdb.getUsername();
      ptc.dbpwd = repdb.getPassword();

      log.config("Repository: " + ptc.dburl);

      ptc.revalidate();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }
  
  public static void initialize(RockFactory rock, String dburl, String dbusr, String dbpwd) {

    ptc = new PhysicalTableCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwhrep");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      ptc.dburl = dburl;
      ptc.dbusr = dbusr;
      ptc.dbpwd = dbpwd;
      
      log.config("Repository: " + ptc.dburl);

      ptc.revalidate();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  
  public List getActiveTables(String storageID) {
    List l = new ArrayList();
    
    List ptes = (List)si_map.get(storageID);
    
    if(ptes != null) {
      Iterator pti = ptes.iterator();
      while(pti.hasNext()) {
        PTableEntry pte = (PTableEntry)pti.next();
        
        if(pte.status.equalsIgnoreCase("ACTIVE"))
          l.add(pte.tableName);
      }
    }
    
    return l;
  }

  public long getEndTime(String tablename){
    
    Iterator iter = si_map.keySet().iterator();
    while (iter.hasNext()){
      
      String key = (String) iter.next();
      List list = (List)si_map.get(key);
      Iterator i = list.iterator();

      while (i.hasNext()) {
        
        PTableEntry pte = (PTableEntry)i.next();
        if (pte.status.equalsIgnoreCase(ACTIVE) && pte.tableName.equalsIgnoreCase(tablename))
          return pte.endTime;      
      }      
    }
     
    return -1;   
  }
  

  public long getStartTime(String tablename){
    
    Iterator iter = si_map.keySet().iterator();
    while (iter.hasNext()){
      
      String key = (String) iter.next();
      List list = (List)si_map.get(key);
      Iterator i = list.iterator();

      while (i.hasNext()) {
        
        PTableEntry pte = (PTableEntry)i.next();
        if (pte.status.equalsIgnoreCase(ACTIVE) && pte.tableName.equalsIgnoreCase(tablename))
          return pte.startTime;       
      }      
    }
     
    return -1;   
  }
  

  
  public String getTableName(String storageID, long dataTime) {
    List l = (List) si_map.get(storageID);

    if (l != null) {
      Iterator i = l.iterator();

      while (i.hasNext()) {
        PTableEntry pte = (PTableEntry) i.next();

        if(pte.status.equalsIgnoreCase(ACTIVE) && pte.endTime > dataTime && pte.startTime <= dataTime) {
          return pte.tableName; 
        }
      }

    }
    return null;

  }

  public List getTableName(String storageID, long s, long e) {
    
    return getTableName(ACTIVE,storageID,s,e);

  }

  public List getTableName(String statuses,String storageID, long s, long e) {
    
    List result = new ArrayList();
    List l = (List) si_map.get(storageID);
    
    List statusList = new ArrayList();
    StringTokenizer st = new StringTokenizer(statuses,",");
    while (st.hasMoreTokens()){
      statusList.add(st.nextElement());
    }
   
    if (l != null) {
      Iterator i = l.iterator();

      while (i.hasNext()) {
        PTableEntry pte = (PTableEntry) i.next();
        long startTime = pte.startTime;
        long endTime = pte.endTime;

        if (endTime == 0) endTime = ENDOFTHEWORLD;
               
        if (statusList.contains(pte.status) && s<endTime && e>=startTime) {
            result.add(pte.tableName); 
        }             
      }
    }
    
    return result;
  }
  
  public static PhysicalTableCache getCache() {
    return ptc;
  }
  
  public static void setCache(final PhysicalTableCache cache) {
    ptc = cache;
  }

  public void revalidate() throws Exception {

    log.fine("Revalidating...");

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      con = DriverManager.getConnection(dburl, dbusr, dbpwd);

      ps = con.prepareStatement(GET_ACTIVE_TABLES);
      
      rs = ps.executeQuery();
      
      Map si_map2 = new HashMap();

      while (rs.next()) {
        PTableEntry pte = new PTableEntry();

        pte.storageID = rs.getString(1);
        pte.tableName = rs.getString(2);
        
        Timestamp sts = rs.getTimestamp(3);
        if(sts != null) {
          pte.startTime = sts.getTime();
        } else {
          log.finest("Start time not defined -> Partition " + rs.getString(2) + " not in use");
          continue;
        }
        
        pte.startTime = rs.getTimestamp(3).getTime();
        
        Timestamp ets = rs.getTimestamp(4);
        if(ets != null)
          pte.endTime = ets.getTime();
        else
          pte.endTime = ENDOFTHEWORLD;

        pte.status = rs.getString(5);
        
        
        List l = (List) si_map2.get(pte.storageID);
        if (l == null) {
          l = new ArrayList(10);
          si_map2.put(pte.storageID, l);
        }

        log.finest("Table "+pte.storageID+" -> "+pte.tableName+" "+pte.startTime+"..."+pte.endTime);
        
        l.add(pte);

        Collections.sort(l);

      }
      
      si_map = si_map2;

      log.info("Revalidation succesfully performed. " + si_map.size() + " tables found");

    } finally {

      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (con != null) {
        try {
          con.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

    }
  }

}
