package com.distocraft.dc5000.repository.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

/**
 * Cache implementation for checking active statuses
 * 
 * @author lemminkainen
 * 
 */
public class AggregationRuleCache {

  private static Logger log = Logger.getLogger("etlengine.repository.AggregationRuleCache");

  private static final String GET_ACTS = "select y.aggregation, y.target_type, y.target_level,y.source_type, y.source_level, y.aggregationscope from LOG_AGGREGATIONRULES y ";
    
  private String dburl = null;

  private String dbusr = null;

  private String dbpwd = null;

  // key is source_type+source_level
  private Map arc1 = null;
  
  // key is aggreagtion
  private Map arc2 = null;

  private static AggregationRuleCache acc = null;

  private AggregationRuleCache() {
  }

  public static void initialize(RockFactory rock) {

    acc = new AggregationRuleCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwh");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      acc.dburl = repdb.getConnection_string();
      acc.dbusr = repdb.getUsername();
      acc.dbpwd = repdb.getPassword();

      log.config("Repository: " + acc.dburl);

      acc.revalidate();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  public static AggregationRuleCache getCache() {
    return acc;
  }

  public List getAggregationRules(String source_type,String source_level) {
	  List rcoList = (List) arc1.get(source_type+source_level);
	  return rcoList;
  }

  public List getAggregationRules(String aggregation) {
	  List rcoList = (List) arc2.get(aggregation);
	  return rcoList;
  }

  
  public void revalidate() throws Exception {

    log.fine("Revalidating...");

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      con = DriverManager.getConnection(dburl, dbusr, dbpwd);

      ps = con.prepareStatement(GET_ACTS);

      rs = ps.executeQuery();

      HashMap hm1 = new HashMap();
      HashMap hm2 = new HashMap();

      while (rs.next()) {
    	  
        String aggregation = rs.getString(1);
        String target_type = rs.getString(2);
        String target_level = rs.getString(3);
        String source_type = rs.getString(4);
        String source_level = rs.getString(5);
        String aggregationscope = rs.getString(6);
        
        
        log.finest("Aggregation Rule: aggregation: "+aggregation+" target_type: "+target_type+" target_level: "+target_level+" source_type: "+source_type+" source_level: "+source_level+" aggregationscope: "+aggregationscope);
        AggregationRule rco = new AggregationRule(aggregation, target_type,target_level, source_type, source_level,  aggregationscope);
        
        if (!hm1.containsKey(source_type + source_level)){
        	List newList = new ArrayList();
        	hm1.put(source_type+source_level, newList);
        }

        if (!hm2.containsKey(aggregation)){
        	List newList = new ArrayList();
        	hm2.put(aggregation, newList);
        }

        
        ((List)hm1.get(source_type+source_level)).add(rco);
        ((List)hm2.get(aggregation)).add(rco);
        
      }

      arc1 = hm1;
      arc2 = hm2;

      log.info("Revalidation succesfully performed. " + arc1.size() + " techpacks found");

    } catch (SQLException sqle) {
      
      if ((sqle.getMessage().indexOf("ASA Error -141") == 0) || (sqle.getMessage().indexOf("SQL Anywhere Error -141") == 0)) // Table X not found
        log.log(Level.INFO, "Table LOG_AGGREGATIONRULES not found, Techpack DWH_MONITOR probably not installed.");  
      else   
       log.log(Level.SEVERE, "Fatal initialization error", sqle);

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
