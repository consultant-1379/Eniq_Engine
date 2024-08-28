package com.distocraft.dc5000.etl.engine.sql;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

/**
 * This action checks that techpack executing this action has correct version in
 * DWH
 * 
 * @author lemminkainen
 * 
 */
public class DWHMVersionCheckAction extends SQLOperation {

  private Logger log;

  private RockFactory etlreprock;

  private RockFactory dwhreprock;

  private Long techPackId;

  private String etlversion = null;

  public DWHMVersionCheckAction(Meta_versions mversion, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(mversion, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.log = Logger.getLogger(clog.getName() + ".VersionCheck");

    this.etlreprock = rockFact;

    this.techPackId = techPackId;

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

      etlversion = mversion.getVersion_number();

    } catch (Exception e) {
      // Failure -> cleanup connections

      try {
        dwhreprock.getConnection().close();
      } catch (Exception se) {
      }

      throw e;
    }

  }

  public void execute() throws Exception {

    log.fine("Checking TechPack version ETL <-> DWH");

    try {

      Meta_collection_sets mcs_cond = new Meta_collection_sets(etlreprock);
      mcs_cond.setCollection_set_id(techPackId);
      Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlreprock, mcs_cond);

      Vector tps = mcs_fact.get();

      Meta_collection_sets tp = (Meta_collection_sets) tps.get(0);

      String tpName = tp.getCollection_set_name();

      if (tpName == null)
        throw new Exception("Unable to resolve TP name");

      Tpactivation tpa_cond = new Tpactivation(dwhreprock);
      tpa_cond.setTechpack_name(tpName);
      TpactivationFactory tpa_fact = new TpactivationFactory(dwhreprock, tpa_cond);

      Vector tpas = tpa_fact.get();

      if (tpas == null || tpas.size() != 1)
        throw new Exception("Techpack " + tpName + " is not active");

      Tpactivation tpa = (Tpactivation) tpas.get(0);

      String dwhVersionID = tpa.getVersionid();
      String etlVersionID = tpName + ":" + etlversion;

      log.fine("Comparing \"" + etlVersionID + "\" <-> \"" + dwhVersionID + "\"");
      
      if(etlVersionID.equals(dwhVersionID))
        throw new Exception("TechPack version in etl ("+etlVersionID+") does not match to dwh version ("+dwhVersionID+")");

    } finally {
      try {
        dwhreprock.getConnection().close();
      } catch (Exception e) {
      }
    }

  }

}
