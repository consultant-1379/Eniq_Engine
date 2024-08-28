package com.distocraft.dc5000.etl.engine.system;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * 
 */
public class ReloadDBLookupsAction extends TransferActionBase {

  private String logString;

  private String tableName;

  /**
   * Empty protected constructor
   * 
   */
  protected ReloadDBLookupsAction() {
  }

  private SetContext sctx = null;
  private Logger log;
  
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
  public ReloadDBLookupsAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, Meta_transfer_actions trActions, SetContext sctx, Logger clog)
      throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);
    this.log = Logger.getLogger(clog.getName() + ".ReloadDBLookupsAction");
    this.sctx = sctx;
    
  }

  /**
   * 
   */
  public void execute() throws EngineException {

    Properties properties = new Properties();

    try {

      Integer rowsAffected = (Integer) sctx.get("RowsAffected");
      if (rowsAffected != null && rowsAffected.intValue() < 1){
        log.finer("No rows affected -> nothing reloaded");
        return;
      }
      
      String where = this.getTrActions().getAction_contents();

      if (where != null && where.length() > 0) {

        ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
        properties.load(bais);
        tableName = properties.getProperty("tableName", null);
        if (tableName != null && tableName.equalsIgnoreCase(""))
          tableName = null;
        bais.close();
      }

    } catch (Exception e) {
      throw new EngineException("Failed to read configuration from WHERE", new String[] { "" }, e, this, this
          .getClass().getName(), EngineConstants.ERR_TYPE_SYSTEM);

    }

    try {

      EngineAdmin admin = new EngineAdmin();
      Logger.getLogger(this.logString + ".execute").log(Level.INFO, "Reloading Database lookups.");
      admin.refreshDBLookups(tableName);

    } catch (Exception e) {
      throw new EngineException("Exception in reloadDBLookupsAction", new String[] { "" }, e, this, this.getClass()
          .getName(), EngineConstants.ERR_TYPE_SYSTEM);

    }

  }

}
