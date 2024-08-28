package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;

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
public class LogSessionLoader extends Loader {

  final private String name;
  
  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  private static PhysicalTableCache ptc = null;

  public LogSessionLoader(final Meta_versions version, final Long collectionSetId, final Meta_collections collection,
      final Long transferActionId, final Long transferBatchId, final Long connectId, final RockFactory rockFact,
      final ConnectionPool connectionPool, final Meta_transfer_actions trActions, final SetContext sctx,
      final Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions, sctx, clog);

    if (ptc == null) {
      ptc = PhysicalTableCache.getCache();
    }

    final String where = this.getTrActions().getWhere_clause();

    whereProps = stringToProperty(where);

    if (whereProps == null) {
      whereProps = new Properties();
      whereProps.setProperty("where", where);
    }

    this.name = whereProps.getProperty("logname");
    
    notifyTypeName = this.tablename;

  }

  /**
   * Determine prober loggernames for log, sqlLog and fileLog
   */
  protected void initializeLoggers() {

    final String logname = log.getName() + ".LogSessionLoader";

    log = Logger.getLogger(logname);

    final String logname_pfx = logname.substring(logname.indexOf("."));

    fileLog = Logger.getLogger("file." + logname_pfx + ".LogSessionLoader");
    sqlLog = Logger.getLogger("sql." + logname_pfx + ".LogSessionLoader");
    sqlErrLog = Logger.getLogger("sqlerror." + logname_pfx + ".LogSessionLoader");

  }

  /**
   * Should create Map tableName -> List of File-objects
   * 
   * @return
   * @throws Exception
   */
  protected Map getTableToFileMap() throws Exception {

    SessionHandler.rotate(name);

    final String inDir = System.getProperty("ETLDATA_DIR") + "/session/" + name;

    final FilenameFilter filter = new FilenameFilter() {

      public boolean accept(final File dir, final String fname) {
        return fname.startsWith(name) && !fname.endsWith(".unfinished");
      }
    };

    final File[] files = new File(inDir).listFiles(filter);

    final HashMap tableMap = new HashMap();
    
    // No files listed, exit here.
    if (files == null || files.length <= 0) {
      return tableMap;
    }

    final String storageID = "LOG_SESSION_" + name + ":PLAIN";
    
    for (int i = 0; i < files.length; i++) {
      final File file = files[i];
      Date datadate = null;
      String datestamp = null;
      // Catch date format error in order to process files with invalid filename format
      try{
        datestamp = file.getName().substring(file.getName().lastIndexOf(".")+1);
        datadate = sdf.parse(datestamp);
      }
      catch (Exception e){
        log.info("Session load failure: Can't parse date from filename: " + file.getName());
      }

      final String tableName = datadate != null ? ptc.getTableName(storageID,datadate.getTime()) : null;

      if (tableName == null && datestamp != null) 
        log.info("Session load failure: Can't find table for " + storageID + " date " + datestamp);
      
      List filelist = (List)tableMap.get(tableName);
      
      if(filelist == null) {
        filelist = new ArrayList();
        tableMap.put(tableName, filelist);
      }      
      filelist.add(files[i].toString());
    }

    return tableMap;
  }

  /**
   * Add needed values to velocityContext before evaluating
   */
  protected void fillVelocityContext(final VelocityContext context) {

  }

  /**
   * Makes implementations specific modifications to sessionLogEntry.
   */
  protected void updateSessionLog() {
  }

}
