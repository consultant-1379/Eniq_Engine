package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.SanityChecker;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.DwhtechpacksFactory;

public class DWHSanityCheckerAction extends SQLOperation {

  private Logger log;

  private RockFactory etlreprock = null;

  private RockFactory dwhreprock = null;

  private RockFactory dwhrock = null;

  private Dwhtechpacks dtp = null;

  private boolean all = false;

  public DWHSanityCheckerAction(Meta_versions version, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.etlreprock = rockFact;
    this.log = clog;

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

      Meta_collection_sets mcs_cond = new Meta_collection_sets(etlreprock);
      mcs_cond.setCollection_set_id(techPackId);
      Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlreprock, mcs_cond);

      Vector tps = mcs_fact.get();

      Meta_collection_sets tp = (Meta_collection_sets) tps.get(0);

      Properties conf = new Properties();

      String act_cont = trActions.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          conf.load(bais);
          bais.close();
        } catch (Exception e) {
          log.log(Level.INFO, "Error loading action contents", e);
          e.printStackTrace();
        }
      }

      String mode = conf.getProperty("mode", null);

      if (mode != null && mode.equalsIgnoreCase("ALL")) {

        all = true;

      } else {

        String tpName = tp.getCollection_set_name();

        if (tpName == null)
          throw new Exception("Unable to resolve TP name");

        Dwhtechpacks dtp_cond = new Dwhtechpacks(dwhreprock);
        dtp_cond.setTechpack_name(tpName);
        DwhtechpacksFactory dtp_fact = new DwhtechpacksFactory(dwhreprock, dtp_cond);

        Vector dtps = dtp_fact.get();

        if (dtps == null || dtps.size() != 1)
          throw new Exception("Unable to resolve DWHTechPacks for " + tpName);

        dtp = (Dwhtechpacks) dtps.get(0);

      }

    } catch (Exception e) {
      // Failure cleanup connections

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

    try {

      SanityChecker pa = new SanityChecker(dwhreprock, dwhrock, log);
      if (all) {
        log.fine("SanityCheck executing for all techpacks");
        pa.sanityCheck();
      } else {
        log.fine("SanityCheck executing for techpack " + dtp.getTechpack_name());
        pa.sanityCheck(dtp);
      }
      
      log.fine("SanityCheck successfully finished");

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
