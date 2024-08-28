package com.distocraft.dc5000.etl.engine.sql;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.CreateViewsAction;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;

public class DWHMCreateViewsAction extends SQLOperation {

  private Logger log = null;

  private RockFactory etlreprock = null;

  private RockFactory dwhreprock = null;

  private RockFactory dwhrock = null;

  private RockFactory dbadwhrock = null;
  
  private String tpName = null;

  private Vector types = null;

  public DWHMCreateViewsAction(Meta_versions version, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.log = clog;
    
    this.etlreprock = rockFact;

    try {

      Meta_databases md_cond = new Meta_databases(etlreprock);
      //md_cond.setType_name("USER");
      Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);

      Vector dbs = md_fact.get();

      Iterator it = dbs.iterator();
      while (it.hasNext()) {
        Meta_databases db = (Meta_databases) it.next();

        if (db.getConnection_name().equalsIgnoreCase("dwhrep") && db.getType_name().equals("USER")) {
          dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
              .getDriver_name(), "DWHMgr", true);
        } else if (db.getConnection_name().equalsIgnoreCase("dwh") && db.getType_name().equals("USER")) {
          dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
              "DWHMgr", true);
        } else if (db.getConnection_name().equalsIgnoreCase("dwh") && db.getType_name().equals("DBA")) {
            dbadwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
                    "DWHMgr", true);
              }

      } // for each Meta_databases

      if (dwhrock == null)
        throw new Exception("Database dwh is not defined in Meta_databases?!");

      if (dwhreprock == null)
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");

      if (dbadwhrock == null)
          throw new Exception("Database dwh (DBA) is not defined in Meta_databases?!");

      
      Meta_collection_sets mcs_cond = new Meta_collection_sets(etlreprock);
      mcs_cond.setCollection_set_id(techPackId);
      Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlreprock, mcs_cond);

      Vector tps = mcs_fact.get();

      Meta_collection_sets tp = (Meta_collection_sets) tps.get(0);

      tpName = tp.getCollection_set_name();

      if (tpName == null)
        throw new Exception("Unable to resolve TP name");

      Dwhtype typeCond = new Dwhtype(dwhreprock);
      typeCond.setTechpack_name(tpName);
      DwhtypeFactory typeFact = new DwhtypeFactory(dwhreprock, typeCond, " ORDER BY typename, tablelevel DESC ");

      types = typeFact.get();

      if (types == null || types.size() <= 0)
        throw new Exception("Unable to resolve Types");

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

      try {
        dbadwhrock.getConnection().close();
      } catch (Exception de) {
      }
     
      throw e;
    }

  }

  public void execute() throws Exception {

    log.fine("CreateViews executing for techpack " + tpName);

    try {

      Iterator i = types.iterator();
      while (i.hasNext()) {

        Dwhtype type = (Dwhtype) i.next();

        try {

          CreateViewsAction pa = new CreateViewsAction(dbadwhrock, dwhrock, dwhreprock, type, log);

        } catch (Exception e) {
          log.log(Level.WARNING, "Unable to create view for " + type.getTypename() + " (" + type.getTablelevel() + ")",e);
        }

      } // foreach type

    } finally {
      try {
        dwhreprock.getConnection().close();
      } catch (Exception e) {
      }

      try {
        dwhrock.getConnection().close();
      } catch (Exception e) {
      }
      
      try {
        dbadwhrock.getConnection().close();
      } catch (Exception e) {
      }
    }

  }

}
