package com.distocraft.dc5000.etl.engine.sql;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.UpdatePlanAction;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class DWHMUpdatePlanAction extends SQLOperation {

  private Logger log = null;

  private RockFactory etlrepRockFact = null;

  public DWHMUpdatePlanAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext setcontext, Logger log) throws EngineMetaDataException {
    
    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);    
    
    this.etlrepRockFact = getConnection();
    this.log = log;
  }

  public void execute() throws EngineException {
    try {
      
      UpdatePlanAction updatePlanAction = new UpdatePlanAction(this.etlrepRockFact, this.log);
      
      updatePlanAction.execute();
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "DWHMUpdatePlanAction.execute failed.", e);
    }
  }

}
