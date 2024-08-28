package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * TODO intro <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class UpdateDIMSession extends SQLOperation {

  private Logger log;
  private Logger sqlLog;

  private SetContext sctx;
  private RockFactory rock;
  
  private String elem="";
  
  private boolean useROWSTATUS = false;
  
  public UpdateDIMSession(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx) throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);

    this.sctx = sctx;
    this.rock = rockFact;

    try {

      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);

      String techPack = collSet.getCollection_set_name();
      String set_type = collection.getSettype();
      String set_name = collection.getCollection_name();
      String logName = techPack + "." + set_type + "." + set_name;
      String where = trActions.getWhere_clause();
      Properties prop = new Properties();
      ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
      prop.load(bais);
      bais.close();
      this.elem = prop.getProperty("element","");
      this.useROWSTATUS = "true".equalsIgnoreCase(prop.getProperty("useRAWSTATUS","false"));
			
      this.log = Logger.getLogger("etl." + logName + ".loader.UpdateDIMSession");
      this.sqlLog = Logger.getLogger("sql." + logName + ".loader.UpdateDIMSession");

    } catch (Exception e) {
      throw new EngineMetaDataException("UpdateDIMSession initialization error", e, "init");
    }

  }

  public void execute() throws EngineException {
    
    Connection c = null;
    Statement s = null;

    try {
      
      log.fine("Executing...");
      
      SimpleDateFormat sdfIN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat sdfOUT = new SimpleDateFormat("yyyy-MM-dd");

      String measType = (String) sctx.get("MeasType");
     //String rawView = measType.trim() + "_RAW";

      
      RockFactory r = this.getConnection();      
      c = r.getConnection();
      s = c.createStatement();

      String sessionEndTime = String.valueOf(System.currentTimeMillis());
      
      if (elem==null) elem = "";
      if (!elem.equalsIgnoreCase("")) elem = ", "+elem;
      
      String sqlClause = "";
      
      List tableList = (List)sctx.get("tableList");
      log.fine("printing table list size---->"+tableList.size());
      Iterator tables = tableList.iterator();
      
      while(tables.hasNext()) {
      String tableName = (String)tables.next(); 
      log.fine("printing table list name---->"+tableName);
      
      if (useROWSTATUS){
      
        sqlClause = "SELECT COUNT(*) AS ROWCOUNT, SESSION_ID, BATCH_ID, TIMELEVEL, DATETIME_ID " + elem + " FROM "
        + tableName + " WHERE ROWSTATUS IS NULL OR ROWSTATUS = '' " + " GROUP BY SESSION_ID, BATCH_ID,  TIMELEVEL,  DATETIME_ID " + elem;
        
      } else {

        sqlClause = "SELECT COUNT(*) AS ROWCOUNT, SESSION_ID, BATCH_ID, TIMELEVEL, DATETIME_ID " + elem + " FROM "
        + tableName + " WHERE YEAR_ID IS NULL " + " GROUP BY SESSION_ID, BATCH_ID,  TIMELEVEL,  DATETIME_ID " + elem;
       
      }
           
      //log.info("Getting session information from raw table view " + rawView);
      log.info("Getting session information from the raw table " +tableName );
      sqlLog.finer(sqlClause);
      log.info("sqlClause in updateDimSession-->"+sqlClause);
      
      ResultSet resultSet = s.executeQuery(sqlClause);

      int totalRowCount;

      //Create the new collection...
      final Collection<Map<String, Object>> collection = new ArrayList<Map<String, Object>>();
           
     while (resultSet.next()) {

        totalRowCount = resultSet.getInt(1);
        
        if(totalRowCount <= 0) //No ROWS???
          return;
        
        String element = "";
        String sessionID = resultSet.getString(2);
        String batchID = resultSet.getString(3);
        String timeLevel = resultSet.getString(4);
        String dateTimeID = resultSet.getString(5);
        if (elem==null || elem.equalsIgnoreCase("")) {
        	element = null;
        }
        else {
        	element = resultSet.getString(6);
        }
        
        log.finest("sessionID: " + sessionID);
        log.finest("batchID: " + batchID);
        log.finest("rowCount: " + totalRowCount);
        log.finest("dateTimeID: " + dateTimeID);
        log.finest("timeLevel: " + timeLevel);
        log.finest("source: " + element);
        
        //Date time formatting needs this null checking so null values won't stop overall processing
        String formattedDatetime = (dateTimeID == null ? null : sdfOUT.format(sdfIN.parse(dateTimeID)));        

        
        Map sessionLogEntry = (Map)sctx.get("sessionLogEntry");
        
        sessionLogEntry.put("SESSION_ID", sessionID);
        sessionLogEntry.put("BATCH_ID", batchID);
        sessionLogEntry.put("DATE_ID", formattedDatetime);
        sessionLogEntry.put("TIMELEVEL", timeLevel);
        sessionLogEntry.put("DATATIME", dateTimeID);
        sessionLogEntry.put("DATADATE", formattedDatetime);
        sessionLogEntry.put("ROWCOUNT", String.valueOf(totalRowCount));
        sessionLogEntry.put("SESSIONENDTIME", sessionEndTime);
        sessionLogEntry.put("STATUS", "OK");
        sessionLogEntry.put("TYPENAME", measType);
        sessionLogEntry.put("SOURCE", element);
        
        //Need to dump what we have at this point to file.
        //This is to avoid any memory problems...
       final int bulkLimit = SessionHandler.getBulkLimit();
        if(collection.size() == bulkLimit){
            log.finest("The number of session is currently > "+bulkLimit+". Performing write to SessionLog.");
        	SessionHandler.bulkLog("LOADER",collection);
            log.finest("Finished Writing to the SessionLog.");
        	//Now clear the collection...
        	collection.clear();
        }
        //Write the session to the collection for bulk logging.
        collection.add(new HashMap<String, Object>(sessionLogEntry));
        log.fine("Saving session information. SessionID: " + sessionID + " BatchID: " + batchID);
      }
      log.finest("Writing to the SessionLog...");
      SessionHandler.bulkLog("LOADER", collection);
      log.finest("Finished Writing to the SessionLog.");

      resultSet.close();
            


      if("TABLE_NOT_FOUND".equals(tableName))
       continue;
        
      if (useROWSTATUS){
  
          sqlClause = "UPDATE " + tableName + " SET ROWSTATUS = 'LOADED' WHERE ROWSTATUS IS NULL or ROWSTATUS = ''";
          log.info("Updating ROWSTATUS from raw table " + tableName);
           
        } else {
          
          sqlClause = "UPDATE " + tableName + " SET YEAR_ID = datepart(yy, date_id) WHERE YEAR_ID IS NULL";
          log.info("Updating YEAR_IDs from raw table " + tableName);
          
        }
      
        sqlLog.finer(sqlClause);
        s.executeUpdate(sqlClause);
       
      } // end while
      
      log.fine("Succesfully updated...");
      
    } catch (Exception e) {
      log.log(Level.WARNING,"Update DIM session failed",e);
    } finally {
      
      if(s != null) {
        try {
          s.close();
        } catch(Exception e) {
          log.log(Level.WARNING,"error closing statement",e);
        }
      }
      
      if(c != null) {
        try {
          c.commit();
        } catch(Exception e) {
          log.log(Level.WARNING,"error finally committing",e);
        }
      }
      
    }

  }

}

