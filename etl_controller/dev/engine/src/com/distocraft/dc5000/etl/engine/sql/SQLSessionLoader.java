
package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * TODO intro 
 * TODO usage
 * TODO used databases/tables
 * TODO used properties
 *
 * @author melantie
 * Copyright Distocraft 2005
 * 
 * $id$
 */

public class SQLSessionLoader extends SQLOperation {

  public static final String ADAPTER_LOAD_STATEMENT = "LOAD TABLE LOG_SESSION_ADAPTER (SESSION_ID ';', BATCH_ID ';', FILENAME ';', SESSIONENDTIME ';', SESSIONSTARTTIME ';', SOURCE ';', STATUS ';', SOURCEFILE_MODTIME) FROM ";
  public static final String LOADER_LOAD_STATEMENT = "LOAD TABLE LOG_SESSION_LOADER (LOADERSET_ID ';', SESSION_ID ';', BATCH_ID ';', TIMELEVEL ';',DATATIME ';',ROWCOUNT ';', SESSIONENDTIME ';', SESSIONSTARTTIME ';', SOURCE ';', STATUS ';', TYPENAME) FROM ";
  public static final String AGGREGATION_LOAD_STATEMENT = "LOAD TABLE LOG_SESSION_AGGREGATION (NOT_DEFINED) FROM ";

  private static Logger statlog = Logger.getLogger("etlengine.SessionLoader");

  private Logger log;
  private Logger fileLog;
  private Logger sqlLog;

  private String sessionStartTime;
  private String filename;


  /**
   * constructor
   */
  public SQLSessionLoader(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName) throws EngineMetaDataException {
    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);
    
    this.log = Logger.getLogger("etl.loader.SQLSessionLoader");
    this.fileLog = Logger.getLogger("file.SessionLoader");
    this.sqlLog = Logger.getLogger("sql.SessionLoader");
    
  }
  
  
  /**
   * Executes session loader.
   */
  
  public void execute() throws EngineException {

    log.finer("SQLSessionLoader - executing");

    Connection connection = null;
    Statement s = null;
    
    try {

      //get this from property file
     //inDir = "/dc/dc5000/platform/logs/";
      
      sessionStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
      log.info("Starting loading session at " + sessionStartTime);
      
      RockFactory rokkiTehras = getConnection();
      connection = rokkiTehras.getConnection(); //Get connection defined on this action
      
      s = connection.createStatement();

      //get filtered filenames
      File[] files = getFilteredFileNames ();
      
      if (files != null) {
        for (int j = 0; j < files.length; j++) {
          
          filename = files[j].toString();
          
          if (filename.indexOf("adapter")>0)
            loadTable("LOG_SESSION_ADAPTER", files[j], ADAPTER_LOAD_STATEMENT, s);
          if (filename.indexOf("loader")>0)
            loadTable("LOG_SESSION_LOADER", files[j], LOADER_LOAD_STATEMENT, s);
          if (filename.indexOf("aggregation")>0)
            loadTable("LOG_SESSION_AGGREGATION", files[j], AGGREGATION_LOAD_STATEMENT, s);
        }
   
        deleteLoadedFiles (files);
      }

    } catch (Exception e) {
      log.log(java.util.logging.Level.WARNING, "Session loader failure (general):", e);
      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
    } finally {

      try {
        s.close();
        connection.close();
      } catch (Exception e) {
        log.log(Level.WARNING, "Error closing JDBC-connection", e);
      }
    }
    
  }
    
    /**
     * Gets a list of files which starts with loader*, adapter* or aggregation*. Filters out filenames which contains text "unfinished" 
     * 
     * @param dir
     *          Directory path for files
     * @throws Exception
     *           is thrown on Error
     */
    
    private File [] getFilteredFileNames () throws NoSuchFieldException {
      
      // create a filter that filters out unwanted files
      FilenameFilter filter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
          if (name.indexOf("unfinished")>=0)
            return false;
          if (name.startsWith("adapter"))
            return true;
          if (name.startsWith("loader"))
            return true;
          if (name.startsWith("aggregation"))
            return true;
          return false;
        }
      };
      
      
      File inputTableDir = new File(StaticProperties.getProperty("SessionHandling.log.adapter.inputTableDir"));
      
      // list all session datafiles
      File[] files = inputTableDir.listFiles(filter);
      
      log.info("Found " + files.length + " datafiles."); 
      return files;
    }
    
    
    /**
     * Deletes a list of files
     * 
     * @param files
     *          List of files to be delete
     * @throws Exception
     *           is thrown on Error
     */
    
    private void deleteLoadedFiles (File[] files) throws EngineException {
      
	    try {
	      log.info("Deleting loaded files.");
	
	      // delete read files
	      if (files != null) {
	        for (int j = 0; j < files.length; j++) {
	          files[j].delete();
	          fileLog.info(files[j].toString() + " deleted.");
	        }
	      }
	    } catch (Exception e) {
	      log.log(java.util.logging.Level.WARNING, "Loader failure (deleting files):", e);
	      throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { this.getTrActions()
	          .getAction_contents() }, e, this, this.getClass().getName(), EngineConstants.ERR_TYPE_EXECUTION);
	    }
    }
  
    
    /**
     * Loads a list of files to the specified table
     * 
     * @param tableName
     *          name of RAW table
     * @param files
     *          List of files
     * @throws Exception
     *           is thrown on Error
     */
    
    private void loadTable(String tableName, File filename, String sqlClause, Statement s)
        throws Exception {
        
        log.info("Executing load table command for table " + tableName);
        sqlLog.finer(sqlClause+filename.toString());
        
        s.execute(sqlClause+filename.toString());
        
        log.finer("Load table command executed");
        
        s.getConnection().commit();
        
        log.finer("Explicit commit performed");

        fileLog.info(filename + " loaded to table " + tableName + ".");
      
    }
  }