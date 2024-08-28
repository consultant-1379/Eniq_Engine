package com.distocraft.dc5000.etl.engine.system;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlot;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * TODO intro TODO usage TODO used databases/tables TODO used properties
 * 
 * @author melantie Copyright Distocraft 2005 $id$
 */
public class TestAction extends TransferActionBase {

  private Logger log = Logger.getLogger("TestAction");

  /**
   * Empty protected constructor
   */
  protected TestAction() {}
  
	private Meta_collections   collection;

  public TestAction(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, Meta_transfer_actions trActions) {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);
    this.collection = collection;
  }

  /**
   * Executes a SQL procedure
   */

  public void execute() throws Exception {

    String where = this.getTrActions().getWhere_clause();
    long executionTime = 60;
    
    String message = "Test: ";
    String errorMessage = "RANDOM ERROR";
    boolean createError = false;
    boolean showTime = true;
    int errorFrequency = 100;
    int errorWaitTime = 30;
    
    Properties properties = new Properties();
    
      try {
        if (where != null && where.length() > 0) {

          ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
          properties.load(bais);
          message = properties.getProperty("message", "");

          showTime = "TRUE".equalsIgnoreCase(properties.getProperty("showTime", "true"));
          errorMessage = properties.getProperty("errorMessage", "");

          createError = "TRUE".equalsIgnoreCase(properties.getProperty("createError", "false"));
          errorMessage = properties.getProperty("errorMessage", "");
          errorFrequency = Integer.parseInt(properties.getProperty("errorFrequency", "100"));
          errorWaitTime = Integer.parseInt(properties.getProperty("errorWaitTime","0"));
          executionTime = Integer.parseInt(properties.getProperty("executionTime","60"));
          
          bais.close();
        }

      } catch (Exception e) {
        throw new Exception("Failed to read configuration from WHERE", e);
      }
    
      
 		
        // Get all the basetablenames from tableName.
        Share sh = Share.instance();
        this.log.log(Level.FINE, "Trying to get first executionSlotProfile." );
        
        if(sh.get("executionSlotProfileObject") == null) {
          this.log.log(Level.WARNING, "share.get returned null.");
        } else {
          this.log.log(Level.FINE, "share.get returned non null.");
        }
        
        ExecutionSlotProfileHandler executionSlotProfile = (ExecutionSlotProfileHandler) sh
            .get("executionSlotProfileObject");
        
        Iterator runningExecutionSlotsIterator = executionSlotProfile.getActiveExecutionProfile()
            .getAllRunningExecutionSlots();

        List currentRunningSetBasetables = new Vector();

        while (runningExecutionSlotsIterator.hasNext()) {
        	
        	Object o =runningExecutionSlotsIterator.next();
        	if (o != null){
        		ExecutionSlot currentRunningExecutionSlot = (ExecutionSlot)o;
        		this.log.log(Level.INFO, " running set "+currentRunningExecutionSlot.getName() );
        	}
        }



 		
 		
		ExecutionSlotProfileHandler executionSlotHandler = (ExecutionSlotProfileHandler)sh.get("executionSlotProfileObject");
		EngineThread set = executionSlotHandler.getActiveExecutionProfile().getRunningSet(this.collection.getCollection_name(), collection.getCollection_id().longValue());
		List settables = set.getSetTables();
		String sTables = properties.getProperty("addedtablename","");
		
		StringTokenizer st = new StringTokenizer(sTables,",");
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			settables.add(token);
		}
			
		/*Read config data from share*/
		Share share = Share.instance();
		String key = "ConfigData"+Long.toString(this.getTransferActionId().longValue());
		String str="";

      
      log.info("Execution time: " + executionTime + " sec.");
      
      if (createError){
        log.info("Error message: " + errorMessage);
        log.info("Error frequency: 1/" + errorFrequency);
        log.info("Error Wait time: " + errorWaitTime);

      }
      else
        log.info("No errors created");

      Random rnd = new Random(System.currentTimeMillis());
     
      
      for (int i=0; i< executionTime; i++) {
        Thread.sleep (1000);  
        
        if (createError && i > errorWaitTime && rnd.nextInt(errorFrequency-1)==0)
          throw new Exception(errorMessage);
        
        if (showTime) log.info(message + (executionTime-i));
      }
                
  }
}