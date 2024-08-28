package com.distocraft.dc5000.etl.engine.system;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.alarm.AlarmMarkupAction;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.ericsson.eniq.etl.alarm.AlarmConfig;

public class AlarmMarkupActionWrapper extends SQLOperation {

  private final Logger log;

  private final Meta_versions version;

  private final Long collectionSetId;

  private final Meta_collections collection;

  private final Long transferActionId;

  private final Long transferBatchId;

  private final Long connectId;

  private final Meta_transfer_actions trActions;

  private final SetContext setcontext;

  public AlarmMarkupActionWrapper(final Meta_versions version, final Long collectionSetId,
      final Meta_collections collection, final Long transferActionId, final Long transferBatchId, final Long connectId,
      final RockFactory rockFact, final ConnectionPool connectionPool, final Meta_transfer_actions trActions,
      final SetContext setcontext, final Logger log) throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);
    
    this.version = version;
    this.collectionSetId = collectionSetId;
    this.collection = collection;
    this.transferActionId = transferActionId;
    this.transferBatchId = transferBatchId;
    this.connectId = connectId;
    this.trActions = trActions;
    this.setcontext = setcontext;
    this.log = log;
  }

  public void execute() throws EngineException {
    try {
      
      this.setcontext.put(AlarmMarkupAction.ALARM_CONFIG, AlarmConfigCacheWrapper.getInstance());
      
      final AlarmMarkupAction alarmMarkupAction = new AlarmMarkupAction(this.collectionSetId,
          this.collection, getConnection(), this.trActions, this.setcontext, this.log);

      alarmMarkupAction.execute();

    } catch (Exception e) {
      throw new EngineException(e.getMessage(), new String[] { "" }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_SYSTEM);
    }
  }

}
