package com.distocraft.dc5000.etl.engine.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * A Class that implements a system call execution
 * 
 * 
 * @author Jukka J‰‰heimo
 * @since JDK1.1
 */
public class SystemCall extends TransferActionBase {

  private static Logger log = Logger.getLogger("etlengine.CreateDirAction");

  protected SystemCall() {

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
   * @param rockFact
   *          metadata repository connection object
   * @param connectionPool
   *          a pool for database connections in this collection
   * @param trActions
   *          object that holds transfer action information (db contents)
   * 
   * @author Jukka J‰‰heimo
   * @since JDK1.1
   */
  public SystemCall(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, Meta_transfer_actions trActions, SetContext sctx) {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);

  }

  /**
   * Executes a SQL procedure
   * 
   */

  public void execute() throws EngineException {
    String systemClause = this.getTrActions().getAction_contents();
    try {

      log.finer("Executing systemCall \"" + systemClause + "\"");

      if (systemClause != null) {

        Runtime runTime = Runtime.getRuntime();
        Process proc = runTime.exec(systemClause);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = null;
        while ( (line = br.readLine()) != null)
            log.info("STDOUT: "+line);

        BufferedReader bre = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        String eline = null;
        while ( (eline = bre.readLine()) != null)
            log.warning("STDERR: "+eline);
        
        proc.waitFor();
        int exValue = proc.exitValue();

        if (exValue != 0)
          log.warning("Returned with abnormal exit code " + exValue);

      }
    } catch (Exception e) {
      throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { systemClause }, e, this, this.getClass()
          .getName(), EngineConstants.ERR_TYPE_SYSTEM);
    }

  }

}
