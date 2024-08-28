package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.DWHTableCheckAction;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class DWHMTableCheckAction extends SQLOperation {

  private Logger log;

  private RockFactory dwhreprock = null;

  private RockFactory dwhrock = null;

  private String mode;

  private Properties conf;

  public DWHMTableCheckAction(Meta_versions version, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {

    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.log = clog;

    try {

      Meta_databases md_cond = new Meta_databases(rockFact);
      md_cond.setType_name("USER");
      md_cond.setConnection_name("dwhrep");
      Meta_databasesFactory md_fact = new Meta_databasesFactory(rockFact, md_cond);

      Vector dbs = md_fact.get();

      if (dbs != null && dbs.size() == 1) {
        Meta_databases db = (Meta_databases) dbs.get(0);
        dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(),
            db.getDriver_name(), "DWHMgr", true);
        log.fine("Connection to DWHRep established");
        
      } else {
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");
      }

      md_cond = new Meta_databases(rockFact);
      md_cond.setType_name("DBA");
      md_cond.setConnection_name("dwh");
      md_fact = new Meta_databasesFactory(rockFact, md_cond);

      dbs = md_fact.get();

      if (dbs != null && dbs.size() == 1) {
        Meta_databases db = (Meta_databases) dbs.get(0);
        dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
            "DWHMgr", true);
        log.fine("Connection to DWH established");
      } else {
        throw new Exception("Database dwh (dba) is not defined in Meta_databases?!");
      }

      mode = trActions.getWhere_clause();
      if (mode == null || mode.length() <= 0)
        mode = "READONLY";

      conf = new Properties();

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

    log.fine("Executing TableCheck in mode " + mode);

    try {

      DWHTableCheckAction dwtca = new DWHTableCheckAction(dwhreprock, dwhrock, log, conf, mode);
      dwtca.execute();

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
