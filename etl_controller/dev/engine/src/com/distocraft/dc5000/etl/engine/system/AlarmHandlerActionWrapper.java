package com.distocraft.dc5000.etl.engine.system;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.alarm.AlarmHandlerAction;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class AlarmHandlerActionWrapper extends SQLOperation {

  private Logger log = null;

  private Meta_versions version = null;

  private Long collectionSetId = null;

  private Meta_collections collection = null;

  private Meta_transfer_actions trActions = null;

  private SetContext setcontext = null;

  public AlarmHandlerActionWrapper(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext setcontext, Logger log) throws EngineMetaDataException {
    
    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);    
    
    this.version = version;
    this.collectionSetId = collectionSetId;
    this.collection = collection;
    this.trActions = trActions;
    this.setcontext = setcontext;
    this.log = log;
  }

  public void execute() throws EngineException {
    try {
      AlarmHandlerAction alarmHandlerAction = new AlarmHandlerAction(this.version, this.collectionSetId,
          this.collection, getConnection(), this.trActions,
          this.setcontext, this.log);
      alarmHandlerAction.execute();
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "AlarmHandlerActionWrapper.execute failed.", e);
    }
  }

}
