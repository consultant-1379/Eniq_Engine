package com.distocraft.dc5000.etl.engine.sql;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.MigrateAction;
import com.distocraft.dc5000.dwhm.PartitionAction;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;

public class DWHMMigrateAction extends SQLOperation {

  private Logger log;

  private RockFactory etlreprock;

  private RockFactory dwhreprock;

  private RockFactory dwhrock;

  private String where;
  
  public DWHMMigrateAction(Meta_versions version, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName, Logger clog) throws Exception {

    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.etlreprock = rockFact;
    this.log = clog;

    this.where = trActions.getWhere_clause();
    
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
          dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
              "DWHMgr", true);
        }

      } // for each Meta_databases

      if (dwhrock == null)
        throw new Exception("Database dwh is not defined in Meta_databases?!");

      if (dwhreprock == null)
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");

    } catch (Exception e) {
      // Failure -> cleanup connections

      try {
        dwhreprock.getConnection().close();
      } catch (Exception se) {
      }

      try {
        dwhrock.getConnection().close();
      } catch (Exception ze) {
      }

      throw e;
    }

  }

  public void execute() throws Exception {
    log.fine("Migrate executing...");

    try {

      MigrateAction ma = new MigrateAction(where, dwhreprock, dwhrock, log);

      ma.execute();
      
      PhysicalTableCache.getCache().revalidate();

    } finally {
      try {
        dwhreprock.getConnection().close();
      } catch (Exception e) {
      }

      try {
        dwhrock.getConnection().close();
      } catch (Exception e) {
      }
    }

  }

}
