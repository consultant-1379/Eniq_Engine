package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.AggregationStatusCache;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.ericsson.eniq.common.VelocityPool;

/**
 * 
 * 
 * 
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 * <tr>
 * <td>Basetablename</td>
 * <td>typename</td>
 * <td>Defines the basetablename for the partitioned table where the sql clause is executed.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>SQL Template</td>
 * <td>Action_contents</td>
 * <td>Defines the SQL clause (velocity template) that is executed in every active partition in basetablename pointed
 * partitioned table. The actual tablename is given to the SQL template in $tableName container.</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * @author lemminkainen
 * @author savinen
 * 
 */
public class PartitionedSQLExecute extends SQLOperation {

  private Logger log;

  private String storageid = null;

  private String basetablename = null;

  private boolean  aggStatusCacheUpdate = false;

  private String clause = null;

  private RockFactory etlreprock = null;

  private RockFactory dwhreprock = null;

  private RockFactory dwhrock = null;
  
  private SetContext setContext = null;
  
  private boolean useOnlyLoadedPartitions = false;

  protected PartitionedSQLExecute() {
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
  public PartitionedSQLExecute(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);

    try {

      this.log = Logger.getLogger(clog.getName() + ".PartitionedSQLExecute");

      String where = trActions.getWhere_clause();
      
      setContext = sctx;

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
          } else if (db.getConnection_name().equalsIgnoreCase("dwh")) {
            dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
                .getDriver_name(), "DWHMgr", true);
          }

        } // for each Meta_databases

        if (dwhrock == null)
          throw new Exception("Database dwh is not defined in Meta_databases?!");

        if (dwhreprock == null)
          throw new Exception("Database dwhrep is not defined in Meta_databases?!");

        if (where != null && where.length() > 0) {
          ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
          prop.load(bais);
          bais.close();
        }
      } catch (Exception e) {
        log.log(Level.SEVERE, "Error reading where parameter", e);
      }

      basetablename = prop.getProperty("typeName",prop.getProperty("tablename"));
      
      aggStatusCacheUpdate = "true".equalsIgnoreCase(prop.getProperty("aggStatusCacheUpdate", "false"));
      if (aggStatusCacheUpdate){
    	  log.fine("Updating log_aggregationstatus partitions via cache");
      }
      
      useOnlyLoadedPartitions = "1".equalsIgnoreCase(prop.getProperty("useOnlyLoadedPartitions", "0"));

      log.finer("basetablename: " + basetablename);

      if (basetablename == null)
        throw new Exception("Parameter basetablename must be defined");

      if(!useOnlyLoadedPartitions) {
    	  Dwhtype dt = new Dwhtype(dwhreprock);
    	  dt.setBasetablename(basetablename);
    	  DwhtypeFactory dtf = new DwhtypeFactory(dwhreprock, dt);
    	  Dwhtype dtr = dtf.getElementAt(0);

    	  if (dtr == null)
    		  throw new Exception("Basetablename " + basetablename + " Not found from DWHType");

    	  storageid = dtr.getStorageid();

    	  log.finer("storageid: " + storageid);
      } else {
    	  log.fine("Using only loaded partitions.");
      }

      clause = trActions.getAction_contents();

      log.finer("SQLclause: " + clause);

      if (clause == null)
        throw new Exception("Parameter clause must be defined");

    } finally {

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

  public void execute() throws Exception {

    VelocityEngine ve = null;

    try {

      VelocityContext ctx = new VelocityContext();

      ve = VelocityPool.reserveEngine();
      
      List tables = null;
      
      if(useOnlyLoadedPartitions) {
    	  
    	  tables = (List) this.setContext.get("tableList");

      } else {
    	  
          PhysicalTableCache ptc = PhysicalTableCache.getCache();
          tables = ptc.getActiveTables(storageid);
          
      }

      Iterator i = tables.iterator();

      Exception exp = null;

      while (i.hasNext()) {

        try {

          String table = (String) i.next();

          ctx.put("tableName", table);

          StringWriter writer = new StringWriter();

          ve.evaluate(ctx, writer, "", clause);

          String sqlClause = writer.toString();

          log.finer("Trying to execute: " + sqlClause);
          if (aggStatusCacheUpdate){
        	  AggregationStatusCache.update(sqlClause);
          }
          else{
        	  this.getConnection().executeSql(sqlClause);
          }
        } catch (Exception e) {
          log.log(Level.WARNING, "SQL execution failed to exception", e);
          exp = e;
        }

      } // foreach table

      if (exp != null) {
        log.severe("Exception occured during execution. Failing set.");
        throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { this.getTrActions()
            .getAction_contents() }, exp, this, this.getClass().getName(), EngineConstants.ERR_TYPE_EXECUTION);

      }

    } finally {

      VelocityPool.releaseEngine(ve);

    }

  }

}
