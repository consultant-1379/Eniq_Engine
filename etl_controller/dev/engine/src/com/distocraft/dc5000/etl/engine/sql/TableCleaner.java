package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
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
 * 
 * 
 */
public class TableCleaner extends SQLActionExecute {

  private Logger log;
  private Logger sqlLog;

  private Meta_collections collection;

  private Meta_transfer_actions actions;

  private String collectionSetName;

  private SetContext sctx;

  protected TableCleaner() {
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
  public TableCleaner(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory etlreprock, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName, Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, etlreprock,
        connectionPool, trActions, batchColumnName);

    this.log = Logger.getLogger(clog.getName() + ".TableCleaner");
    this.sqlLog = Logger.getLogger(clog.getName() + ".TableCleaner");
    
    // Get collection set name
    Meta_collection_sets whereCollSet = new Meta_collection_sets(etlreprock);
    whereCollSet.setEnabled_flag("Y");
    whereCollSet.setCollection_set_id(collectionSetId);
    Meta_collection_sets collSet = new Meta_collection_sets(etlreprock, whereCollSet);

    this.collectionSetName = collSet.getCollection_set_name();
    this.collection = collection;
    this.actions = trActions;

    Meta_databases md_cond = new Meta_databases(etlreprock);
    md_cond.setType_name("USER");
    Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);
  }

  /**
   * Executes a SQL procedure
   * 
   */
  public void execute() throws Exception {

    String sql = "";
    
    try {

      Properties properties = new Properties();

      String act_cont = this.actions.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          properties.load(bais);
          bais.close();
        } catch (Exception e) {
          log.severe("Error while reading properties: " + act_cont);
          e.printStackTrace();
        }
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      String tablename = properties.getProperty("tablename");
      String datecolumn = properties.getProperty("datecolumn");
      int threshold = Integer.parseInt(properties.getProperty("threshold"));

      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(new Date(System.currentTimeMillis()));
      cal.add(GregorianCalendar.DATE, -threshold);
      
      sql = "delete from " + tablename + " where " + datecolumn + " <= '" + sdf.format(cal.getTime()) + "'"; 
      sqlLog.finest("SQL: "+sql);
      int count = executeSQLUpdate(sql);

      log.info(count + " Rows removed from "+tablename+" with "+datecolumn+" <= " + sdf.format(cal.getTime()));
      
    } catch (Exception e) {

      log.severe(e.getStackTrace() + "\r\n" + new String[] { this.getTrActions().getAction_contents() });
      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
      
    }
  }
}
