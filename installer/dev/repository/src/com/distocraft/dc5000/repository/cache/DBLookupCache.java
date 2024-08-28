package com.distocraft.dc5000.repository.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.system.ETLCEventListener;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

public class DBLookupCache  implements ETLCEventListener {

  private static Logger log = Logger.getLogger("etlengine.repository.DBLookupCache");

  private String dburl = null;

  private String dbusr = null;

  private String dbpwd = null;

  private Map clause_map = null;

  private Map tableToClauseMap = null;

  private static DBLookupCache dblc = null;

  private DBLookupCache() {
  }

  public void triggerEvent(String key) {

    try {
      final DBLookupCache dblc = DBLookupCache.getCache();
      
      dblc.refresh(key);
      

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal event triggering error", e);
    }

  }
  
  public static void initialize(RockFactory rock) {

    dblc = new DBLookupCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwh");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwh database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      dblc.dburl = repdb.getConnection_string();
      dblc.dbusr = repdb.getUsername();
      dblc.dbpwd = repdb.getPassword();

      log.config("Repository: " + dblc.dburl);

      dblc.refresh();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  
  public static void initialize(RockFactory rock, String dburl, String dbusr, String dbpwd) {

    dblc = new DBLookupCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwh");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwh database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      dblc.dburl = dburl;
      dblc.dbusr = dbusr;
      dblc.dbpwd = dbpwd;

      log.config("Repository: " + dblc.dburl);

      dblc.refresh();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }
  
  
  public void add(String sql, boolean update) throws Exception {
    add(sql, null, update);
  }

  public void add(String sql, String tableName, boolean update) throws Exception {

    clause_map.put(sql, null);

    if (tableName != null) {
      if (!tableToClauseMap.containsKey(tableName))
        tableToClauseMap.put(tableName, new ArrayList());

      if (!((ArrayList) tableToClauseMap.get(tableName)).contains(sql)){
      ((ArrayList) tableToClauseMap.get(tableName)).add(sql);
      }
    }

    if (update)
      update(sql);
  }

  public void add(String sql) throws Exception {
    add(sql, null);
  }

  public void add(String sql, String tableName) throws Exception {

    if (tableName != null) {
      if (!tableToClauseMap.containsKey(tableName))
        tableToClauseMap.put(tableName, new ArrayList());
      if (!((ArrayList) tableToClauseMap.get(tableName)).contains(sql)){
      ((ArrayList) tableToClauseMap.get(tableName)).add(sql);
      }
    }

    if (!clause_map.containsKey(sql)) {

      clause_map.put(sql, null);
      update(sql);
    }
  }

  public Map remove(String sql) {

    return (Map) clause_map.remove(sql);
  }

  public Map get(String sql) {

    return (Map) clause_map.get(sql);
  }

  public static DBLookupCache getCache() {
    return dblc;
  }

  private void update(String sql) throws Exception {

    log.fine("Updating sql " + sql);

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rest = null;

    try {

      if (clause_map == null)
        clause_map = new HashMap();

      con = DriverManager.getConnection(dburl, dbusr, dbpwd);

      Map rs = (Map) clause_map.get(sql);

      if (rs == null)
        rs = new HashMap();

      if (!rs.isEmpty())
        rs.clear();

      ps = con.prepareStatement(sql);
      rest = ps.executeQuery();
      int i = 0;
      
      if (rest != null)
        while (rest.next()) {
          i++;
          rs.put(rest.getString(1), rest.getString(2));
        }

      clause_map.put(sql, rs);

      log.fine("Refreshed " + i + " mapping rows for " + sql);

      log.info("Refresh succesfully performed. " + clause_map.size() + " lookups found");

    } finally {

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

  public void refresh() throws Exception {

    refresh(null);
  }



  public void refresh(String tableName) throws Exception {

    if (tableName == null)
      log.fine("Refreshing all");
    else
      log.fine("Refreshing lookups for table " + tableName + "");

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rest = null;
    int updated = 0;

    try {

      if (tableToClauseMap == null)
        tableToClauseMap = new HashMap();

      if (clause_map == null)
        clause_map = new HashMap();

      if (clause_map.isEmpty())
        return;


      Iterator iter = null;

      if (tableName == null)
        iter = clause_map.keySet().iterator();
      else {
        ArrayList list = (ArrayList) tableToClauseMap.get(tableName);
        if (list == null) {
          log.finest("No lookups found for table " + tableName);
          return;
        }
        iter = list.iterator();
      }
     
      if (iter != null) {
      
        con = DriverManager.getConnection(dburl, dbusr, dbpwd);
        
        while (iter.hasNext()) {

          String sql = (String) iter.next();

          Map rs = new HashMap();

          ps = con.prepareStatement(sql);
          rest = ps.executeQuery();
          int i = 0;

          if (rest != null)
            while (rest.next()) {
              i++;
              rs.put(rest.getString(1), rest.getString(2));
            }

          clause_map.put(sql, rs);
          updated++;
          log.fine("Refreshed " + i + " mapping rows for " + sql);
        }
      }

      log.info("Refresh succesfully performed. " + updated + " lookups refreshed");

    } finally {

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
