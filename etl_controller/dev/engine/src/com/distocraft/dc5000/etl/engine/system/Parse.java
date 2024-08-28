package com.distocraft.dc5000.etl.engine.system;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class Parse extends SQLOperation {

  private Logger log;

  private Meta_collections collection;

  private Meta_transfer_actions actions;

  private String collectionSetName;

  private SetContext sctx;

  private RockFactory dwhreprock;
  
  private EngineCom eCom = null;

  /**
   * Empty protected constructor
   */
  protected Parse() {
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
   * @param etlreprock
   *          metadata repository connection object
   * @param connectionPool
   *          a pool for database connections in this collection
   * @param trActions
   *          object that holds transfer action information (db contents)
   */
  public Parse(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory etlreprock, Meta_transfer_actions trActions,
      ConnectionPool connectionPool, SetContext sctx, Logger clog, EngineCom eCom) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, etlreprock,
        connectionPool, trActions);

    this.log = Logger.getLogger(clog.getName() + ".Parse");

    // Get collection set name
    Meta_collection_sets whereCollSet = new Meta_collection_sets(etlreprock);
    whereCollSet.setEnabled_flag("Y");
    whereCollSet.setCollection_set_id(collectionSetId);
    Meta_collection_sets collSet = new Meta_collection_sets(etlreprock, whereCollSet);

    this.eCom = eCom;
    this.collectionSetName = collSet.getCollection_set_name();
    this.collection = collection;
    this.actions = trActions;
    this.sctx = sctx;

    Meta_databases md_cond = new Meta_databases(etlreprock);
    md_cond.setType_name("USER");
    Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);

    Vector dbs = md_fact.get();

    Iterator it = dbs.iterator();
    while (it.hasNext()) {
      Meta_databases db = (Meta_databases) it.next();

      if (db.getConnection_name().equalsIgnoreCase("dwhrep")) {
        dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(),
            db.getDriver_name(), "Parser", true);
      }

    } // for each Meta_databases

    if (dwhreprock == null)
      log.warning("Unable to find dwhrep connection from etlrep.meta_databases. Transformer may not work.");

  }

  public void execute() throws Exception {

    RockFactory rock = getConnection();

    Properties properties = new Properties();

    String act_cont = this.actions.getAction_contents();

    if (act_cont != null && act_cont.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        properties.load(bais);
        bais.close();
      } catch (Exception e) {
        System.out.println("Error loading action contents");
        e.printStackTrace();
      }
    }

    Main parser = new Main(properties, collectionSetName, collection.getSettype(), collection.getCollection_name(),
        rock, dwhreprock, eCom);

    Map map = parser.parse();

    sctx.put("parsedMeastypes", map.get("parsedMeastypes"));

    if (dwhreprock != null) {
      try {
        dwhreprock.getConnection().close();
      } catch (Exception e) {
        log.log(Level.WARNING, "Error closing connection", e);
      }
    }

  }

}
