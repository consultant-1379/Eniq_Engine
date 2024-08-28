package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * This action creates new Monitored types according the distinct (typename and
 * timelevel) rows in Log_Session_Loader table <br>
 * <br>
 * <br>
 * <br>
 * Usage: <br>
 * <br>
 * Execute action in ETLC. <br>
 * <br>
 * Used databases/tables <br>
 * <br>
 * DWH and DWHREP <br>
 * <br>
 * Properties used:<br>
 * startDateModifier: Start date of the search is today - 'startDateModifier'
 * -days (Default: 0)<br>
 * <br>
 * lookbackDays: End date of the search is start Date + 'lookbackDays' -days
 * (default: 7)<br>
 * <br>
 * status: Status of created monitored types (default: ACTIVE)<br>
 * <br>
 * <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author savinen
 */
public class UpdateMonitoredTypes extends SQLOperation {

  private Logger log;

  private Logger sqlLog;

  private RockFactory etlreprock;

  private RockFactory dwhreprock;

  private RockFactory dwhrock;

  private int startDateModifier = 0;

  private int lookbackDays = 0;

  private String status = "";

  private String tpname = "";

  public UpdateMonitoredTypes(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);

    etlreprock = rockFact;

    try {

      this.log = Logger.getLogger(clog.getName() + ".SQLExecute");
      this.sqlLog = Logger.getLogger("sql"+clog.getName().substring(4) + ".SQLExecute");
      
      Meta_databases md_cond = new Meta_databases(etlreprock);
      md_cond.setType_name("USER");
      Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);

      Vector dbs = md_fact.get();

      Iterator it = dbs.iterator();
      while (it.hasNext()) {
        Meta_databases db = (Meta_databases) it.next();

        if (db.getConnection_name().equalsIgnoreCase("dwhrep")) {
          dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
              .getDriver_name(), "DWHMgr", true);
        } else if (db.getConnection_name().equalsIgnoreCase("dwh")) {
          dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
              "DWHMgr", true);
        }

      } // for each Meta_databases

      if (dwhrock == null)
        throw new Exception("Database dwh is not defined in Meta_databases?!");

      if (dwhreprock == null)
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");

      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);

      String where = trActions.getWhere_clause();
      Properties prop = new Properties();
      ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
      prop.load(bais);
      bais.close();

      String sdmStr = prop.getProperty("startDateModifier","0");
      try {
        this.startDateModifier = Integer.parseInt(sdmStr);
      } catch(NumberFormatException nfe) {
        log.warning("Parameter startDateModifier \"" + sdmStr +"\"  is invalid. Using default 0.");
        this.startDateModifier = 0;
      }
      
      String lbdStr = prop.getProperty("lookbackDays","7");
      try {
        this.lookbackDays = Integer.parseInt(lbdStr);
      } catch(NumberFormatException nfe) {
        log.warning("Parameter lookbackDays \"" + lbdStr +"\"  is invalid. Using default 7.");
        this.lookbackDays = 7;
      }
      
      this.status = prop.getProperty("status", "ACTIVE");

    } catch (Exception e) {
      throw new EngineMetaDataException("UpdateMonitoredTypes initialization error", e, "init");
    }

  }

  public void execute() throws EngineException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Connection dwhrepc = null;
    Connection dwhc = null;
    Statement dwhs = null;
    Statement dwhs2 = null;
    Statement dwhreps = null;
    String startDateString = "";
    String endDateString = "";
    String nowDateString = "";

    try {

      GregorianCalendar start = new GregorianCalendar();
      GregorianCalendar end = new GregorianCalendar();
      GregorianCalendar now = new GregorianCalendar();

      now.setTimeInMillis(System.currentTimeMillis());

      end.setTimeInMillis(System.currentTimeMillis());
      end.add(GregorianCalendar.DATE, -startDateModifier);

      start.setTimeInMillis(end.getTimeInMillis());
      start.add(GregorianCalendar.DATE, -lookbackDays);

      startDateString = sdf.format(start.getTime());
      endDateString = sdf.format(end.getTime());
      nowDateString = sdf.format(now.getTime());

      log.fine("Executing...");

      dwhc = dwhrock.getConnection();
      dwhs = dwhc.createStatement();
      dwhs2 = dwhc.createStatement();

      dwhrepc = dwhreprock.getConnection();
      dwhreps = dwhrepc.createStatement();

      String sqlClause = "select typename, timelevel  from LOG_Session_LOADER ses where typename not in (select typename from LOG_MonitoredTypes s where ses.timelevel = s.timelevel) and ses.datadate between '"
          + startDateString + "' and '" + endDateString + "' group by typename,timelevel";

      log.info("");
      sqlLog.finer(sqlClause);

      ResultSet resultSet = dwhs.executeQuery(sqlClause);

      while (resultSet.next()) {

        String typename = resultSet.getString("typename");
        String timelevel = resultSet.getString("timelevel");

        log.finest("typename: " + typename);
        log.finest("timelevel: " + timelevel);

        // fetch tecpack name from TPActivations
        sqlClause = "select t.techpack_name from dataFormat d, tpactivation t  where d.typeid like '%" + typename
            + "' and d.VERSIONID = t.VERSIONID";
        sqlLog.finer(sqlClause);
        ResultSet rSet = dwhreps.executeQuery(sqlClause);

        if (typename.length()>0 && timelevel.length()>0){
          if (rSet.next()) {

            tpname = rSet.getString("techpack_name");

            sqlClause = "insert into dc.LOG_MonitoredTypes (TYPENAME," + " TIMELEVEL, " + " STATUS, " + " MODIFIED, "
                + " ACTIVATIONDAY," + " TECHPACK_NAME) values (" + "     '" + typename + "', " + "     '" + timelevel
                + "', " + "     '" + status + "', " + "     '" + nowDateString + "', " + "     '" + startDateString + "', "
                + "     '" + tpname + "')";

            log.info("Inserting new monitored type ( typename: " + typename + ", timelevel: " + timelevel
                + ", teckpack_name: " + tpname + " ) to monitoredtypes table");

            sqlLog.finer(sqlClause);
            dwhs2.executeUpdate(sqlClause);
            log.info("Inserted OK.");

          } else {
            log.info("Techpackname not found from TPactivations. ( typename: " + typename + " )");
          } 
        } else {
          
          if (typename.length()<1) log.info("Typename is empty. No monitored type added.");
          if (timelevel.length()<1) log.info("Timelevel is empty. No monitored type added.");
          
          
        }
        


      }

      dwhrepc.commit();
      dwhc.commit();

      log.fine("Succesfully updated.");

    } catch (Exception e) {
      log.log(Level.WARNING, "Update monitored types failed", e);
    } finally {

      if (dwhreps != null) {
        try {
          dwhreps.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "error closing statement", e);
        }
      }

      if (dwhs != null) {
        try {
          dwhs.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "error closing statement", e);
        }
      }

      if (dwhrepc != null) {
        try {
          dwhrepc.commit();
        } catch (Exception e) {
          log.log(Level.WARNING, "error finally committing", e);
        }
      }

      if (dwhc != null) {
        try {
          dwhc.commit();
        } catch (Exception e) {
          log.log(Level.WARNING, "error finally committing", e);
        }
      }
           
      try {
        dwhreprock.getConnection().close();
      } catch (Exception se) {
      }

      try {
        dwhrock.getConnection().close();
      } catch (Exception ze) {
      }
    }

  }

}
