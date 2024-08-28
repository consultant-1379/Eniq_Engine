package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.ericsson.eniq.common.VelocityPool;

/**
 * 
 * 
 * 
 * @author savinen
 * 
 */
public class SQLJoin extends SQLOperation {

  private Logger log;

  private String storageid = null;

  private String basetablename = null;

  private String prevtablename = null;

  private String versionid = null;

  private String objName = null;

  private String emptyKey = "''";

  private String clause = null;

  private List ignoredKeysList = null;

  private RockFactory etlreprock = null;

  private RockFactory dwhreprock = null;

  private Exception exp = null;

  SetContext sctx;

  private long startTime = 0l;

  private long endTime = 0l;

  private boolean useROWSTATUS = false;

  protected SQLJoin() {
  }

  /**
   * Constructor
   * 
   * @param versionNumber
   *          metadata version
   * @param collectionSetId
   *          primary key for collection set
   * @param collectionId
   *          primary key for collection
   * @param transferActionId
   *          primary key for transfer action
   * @param transferBatchId
   *          primary key for transfer batch
   * @param connectId
   *          primary key for database connections
   * @param rockFact
   *          metadata repository connection object
   * @param connectionPool
   *          a pool for database connections in this collection
   * @param trActions
   *          object that holds transfer action information (db contents)
   * 
   */
  public SQLJoin(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);

    Map sessionLogEntry = new HashMap();

    long loadersetID = -1;
    try {
      loadersetID = SessionHandler.getSessionID("loader");
    } catch (Exception e) {
      throw new EngineMetaDataException("Error getting loaderSetID", e, "init");
    }

    sessionLogEntry.put("LOADERSET_ID", String.valueOf(loadersetID));
    sessionLogEntry.put("SESSION_ID", "");
    sessionLogEntry.put("BATCH_ID", "");
    sessionLogEntry.put("TIMELEVEL", "");
    sessionLogEntry.put("DATATIME", "");
    sessionLogEntry.put("DATADATE", "");
    sessionLogEntry.put("ROWCOUNT", "");
    sessionLogEntry.put("SESSIONSTARTTIME", String.valueOf(System.currentTimeMillis()));
    sessionLogEntry.put("SESSIONENDTIME", "");
    sessionLogEntry.put("STATUS", "");
    sessionLogEntry.put("TYPENAME", "");

    sctx.put("sessionLogEntry", sessionLogEntry);

    this.sctx = sctx;

    this.log = Logger.getLogger(clog.getName() + ".SQLJoin");

    String where = trActions.getWhere_clause();

    Properties prop = new Properties();

    this.etlreprock = rockFact;

    try {

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
        }

      } // for each Meta_databases

      if (dwhreprock == null)
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");

      if (where != null && where.length() > 0) {
        ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
        prop.load(bais);
        bais.close();
      }

      basetablename = prop.getProperty("typeName");
      prevtablename = prop.getProperty("prevTableName");
      versionid = prop.getProperty("versionid");
      objName = prop.getProperty("objName");
      String ignoredKeys = prop.getProperty("ignoredKeys", "");
      emptyKey = prop.getProperty("emptyKey", "''");

      sctx.put("MeasType", objName);

      ignoredKeysList = new ArrayList();
      String[] iKeys = ignoredKeys.split(",");

      for (int i = 0; i < iKeys.length; i++) {
        ignoredKeysList.add(iKeys[i]);
        log.finer("Added to ignored keys: " + iKeys[i]);
      }

      log.finer("objName: " + objName);
      log.finer("basetablename: " + basetablename);
      log.finer("versionid: " + versionid);

      if (basetablename == null)
        throw new Exception("Parameter basetablename must be defined");

      Dwhtype dt = new Dwhtype(dwhreprock);
      dt.setBasetablename(basetablename);
      DwhtypeFactory dtf = new DwhtypeFactory(dwhreprock, dt);
      Dwhtype dtr = dtf.getElementAt(0);

      if (dtr == null)
        throw new Exception("Basetablename " + basetablename + " Not found from DWHType");

      storageid = dtr.getStorageid();

      log.finer("storageid: " + storageid);

      clause = trActions.getAction_contents();

      log.finer("SQLclause: " + clause);

      if (clause == null)
        throw new Exception("Parameter clause must be defined");

    } catch (Exception e) {

      // Failure cleanup connections
      try {
        dwhreprock.getConnection().close();
      } catch (Exception se) {
      }

      throw new Exception("Error while initializing joiner ", e);
    }

    try {

      Meta_transfer_actions whereDim = new Meta_transfer_actions(rockFact);
      whereDim.setCollection_id(collection.getCollection_id());
      whereDim.setCollection_set_id(collection.getCollection_set_id());
      whereDim.setVersion_number(collection.getVersion_number());
      whereDim.setAction_type("UpdateDimSession");
      
      try {
      
      Meta_transfer_actions updateDIMSession = new Meta_transfer_actions(rockFact, whereDim);
      String DIMSessionWhere = updateDIMSession.getWhere_clause();

      Properties DIMSessionProperties = null;

      if (DIMSessionWhere != null && DIMSessionWhere.length() > 0) {
        DIMSessionProperties = new Properties();
        final ByteArrayInputStream bais = new ByteArrayInputStream(DIMSessionWhere.getBytes());
        DIMSessionProperties.load(bais);
        bais.close();
      }

      this.useROWSTATUS = "true".equalsIgnoreCase(DIMSessionProperties.getProperty("useRAWSTATUS", "false"));
      
      } catch(Exception e) {
          log.finer("No UpdateDIMsession action was found from this set.");
      }
    } catch (Exception e) {
      log.log(java.util.logging.Level.WARNING, "Error while trying to fetch UpdateDimSessions ROWSTATUS property\n", e);
    }
  }

  public void execute() throws Exception {

    VelocityEngine ve = null;

    try {

      String sClause = "select min(date_id) min, max(date_id) max from " + prevtablename;

      log.finest("sql :" + sClause);

      ResultSet rSet = null;
      try {

        rSet = executeSQLQuery(sClause);
        final List doublicateList = new ArrayList();

        if (rSet != null) {
          while (rSet.next()) {

            if (rSet != null && rSet.getDate("min") != null) {
              startTime = rSet.getDate("min").getTime();
            }

            if (rSet != null && rSet.getDate("max") != null) {
              endTime = rSet.getDate("max").getTime();
            }

          }
        }
      } finally {
        if (rSet != null) {
          rSet.close();
        }
      }

      log.finer("StartTime: " + startTime);
      log.finer("EndTime: " + endTime);

      ve = VelocityPool.reserveEngine();

      VelocityContext ctx = getContext(versionid, prevtablename, objName, ve);

      StringWriter writer = new StringWriter();

      ve.evaluate(ctx, writer, "", clause);

      String sqlClause = writer.toString();

      log.finer("Trying to execute: " + sqlClause);

      this.getConnection().executeSql(sqlClause);

      if (exp != null) {
        log.severe("Exception occured during execution. Failing set.");
        throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { this.getTrActions()
            .getAction_contents() }, exp, this, this.getClass().getName(), EngineConstants.ERR_TYPE_EXECUTION);

      }

      if (useROWSTATUS) {

    	  List tableListCreated = createTableList();
    
    	  if(tableListCreated.isEmpty()){
    		  // use the tableList that is already in set context
    		  log.fine("Created table list is empty. Using the loaded table list.");
    	  } else {
    		  // use the created tableList which has the tables where rowstatus is null or empty
    		  sctx.put("tableList", tableListCreated);	
    	  }
      }

    } finally {

      VelocityPool.releaseEngine(ve);

      try {
        dwhreprock.getConnection().close();
      } catch (Exception se) {
      }
    }
  }

  private VelocityContext getContext(String versionid, String prevtablename, String objName, VelocityEngine ve)
      throws Exception {

    List tableNameList = new ArrayList();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    VelocityContext context = new VelocityContext();

    List measkeyList = new ArrayList();
    List meascounterList = new ArrayList();

    Measurementtype mt = new Measurementtype(dwhreprock);
    mt.setVersionid(versionid);
    mt.setObjectname(objName);
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhreprock, mt);

    Iterator iVer = mtf.get().iterator();

    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();

      // counters
      Measurementcounter mc = new Measurementcounter(dwhreprock);
      mc.setTypeid(types.getTypeid());
      MeasurementcounterFactory mcf = new MeasurementcounterFactory(dwhreprock, mc);
      Iterator iTable = mcf.get().iterator();

      while (iTable.hasNext()) {

        Measurementcounter counter = (Measurementcounter) iTable.next();
        meascounterList.add(counter);
      }

      // keys
      Measurementkey mk = new Measurementkey(dwhreprock);
      mk.setTypeid(types.getTypeid());
      MeasurementkeyFactory mkf = new MeasurementkeyFactory(dwhreprock, mk);
      iTable = mkf.get().iterator();

      while (iTable.hasNext()) {

        Measurementkey keys = (Measurementkey) iTable.next();
        // if (!ignoredKeysList.contains(keys.getDataname())){
        measkeyList.add(keys);
        // }
      }
    }

    PhysicalTableCache ptc = PhysicalTableCache.getCache();

    List tables = ptc.getActiveTables(storageid);

    Iterator i = tables.iterator();

    List tableList = new ArrayList();

    while (i.hasNext()) {

      try {

        String table = (String) i.next();

        long sTime = ptc.getStartTime(table);
        long eTime = ptc.getEndTime(table);

        // 
        if ((sTime <= endTime && eTime > endTime) || (sTime <= startTime && eTime > startTime)
            || (sTime >= startTime && eTime <= endTime)) {

          Map m = new HashMap();
          m.put("table", table);
          m.put("startDate", "'" + sdf.format(new Date(sTime)) + "'");
          m.put("endDate", "'" + sdf.format(new Date(eTime)) + "'");
          tableList.add(m);
          tableNameList.add(table);

        }

      } catch (Exception e) {
        log.log(Level.WARNING, "Error figuring out partitions for " + storageid, e);
        exp = e;
      }

    } // foreach table

    context.put("SourceTable", prevtablename);
    context.put("MeasurementKeyList", measkeyList);
    context.put("MeasurementCounterList", meascounterList);
    context.put("TargetTableList", tableList);
    context.put("IgnoredKeysList", ignoredKeysList);
    context.put("EmptyKey", emptyKey);

    sctx.put("tableList", tableNameList);

    return context;

  }

  /**
   * Executes a SQL query
   * 
   */
  protected ResultSet executeSQLQuery(String sqlClause) throws Exception {

    RockFactory c = this.getConnection();
    Connection con = c.getConnection();
    ResultSet rSet = null;

    try {
      // get max value from DB
      Statement stmtc = con.createStatement();
      stmtc.getConnection().commit();
      rSet = stmtc.executeQuery(sqlClause);
      stmtc.getConnection().commit();

    } catch (Exception e) {
      log.severe(e.getStackTrace() + "\r\n" + new String[] { this.getTrActions().getAction_contents() });
      throw new Exception(e);

    }

    return rSet;
  }

  /**
   * Creating a list of tables which include null values in their rowstatus
   * column.
   * 
   * @return List of tables to be loaded.
   * @throws Exception
   */
  private List createTableList() throws Exception {

    final List tableList = new ArrayList();

    PhysicalTableCache ptc = PhysicalTableCache.getCache();

    //final String storageID = tablename.trim() + ":RAW";

    List activeTables = new ArrayList();
    activeTables = ptc.getActiveTables(storageid);
    
    String sqlClause = "";
    final String selectPart = "\n SELECT DISTINCT date_id ";
    final String fromPart = "\n FROM ";
    final String wherePartForNulls = "\n WHERE rowstatus IS NULL ";
    final String wherePartForEmpties = "\n WHERE rowstatus = '' ";
    final String unionPart = "\n UNION ";
    final int activeTablesSize = activeTables.size();
    
    if (activeTablesSize > 0) {

		for (int i = 0; i < activeTablesSize; i++) {
			final String partitionTable = (String) activeTables.get(i);
			            
			//Example:
			//SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus IS NULL
			//UNION 
			//SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus = ''
			//...
			            
			sqlClause += selectPart + fromPart + partitionTable + wherePartForNulls;
			sqlClause += unionPart;
			sqlClause += selectPart + fromPart + partitionTable + wherePartForEmpties;
			            
			if(i<activeTablesSize-1) {
				sqlClause += unionPart;
			}			            
		}
		sqlClause += ";";
          
    } else {
    	log.fine("No active tables found for storageID: " + storageid);
    }
    
    final RockFactory r = this.getConnection();
    final Connection c = r.getConnection();
    final Statement s = c.createStatement();
    ResultSet resultSet = null;
    
    if(sqlClause.length() > 0){
    	resultSet = s.executeQuery(sqlClause);
    }

    if (activeTablesSize > 0 && null != resultSet) {

      while (resultSet.next()) {

        for (int i = 0; i < activeTablesSize; i++) {

          Long startTime = ptc.getStartTime((String) activeTables.get(i));
          Long endTime = ptc.getEndTime((String) activeTables.get(i));
          Date tableDate = resultSet.getDate(1);

          if (tableDate.getTime() >= startTime && tableDate.getTime() < endTime) {

            if (!tableList.contains(activeTables.get(i)))
              tableList.add((String) activeTables.get(i));
          }
        }
        
      }
    }

    try {
    	if (null != resultSet){
    		resultSet.close();
    	}
    } catch (Exception e) {
    	
    }
    
    try {
    	if (null != s) {
    		s.close();
    	}
    } catch (Exception e) {
    	
    }
    
    return tableList;
  }

}
