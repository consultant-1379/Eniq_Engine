package com.distocraft.dc5000.etl.engine.system;


import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
 * test
  */
public class Test extends TransferActionBase {
    
	private Meta_collections   collection;
	private Meta_transfer_actions actions;
	private Logger log = Logger.getLogger("Test");

	
	/** Empty protected constructor 
    *
    */
    protected Test() {
    }
    /** 
    *  Constructor
    *  
    *  @param versionNumber metadata version
    *  @param collectionSetId primary key for collection set
    *  @param collectionId primary key for collection
    *  @param transferActionId primary key for transfer action
    *  @param transferBatchId primary key for transfer batch
    *  @param connectId primary key for database connections
    *  @param rockFact metadata repository connection object
    *  @param connectionPool a pool for database connections in this collection
    *  @param trActions object that holds transfer action information (db contents)
    *
    */
    public Test(      Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections   collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            Meta_transfer_actions trActions) {
                            
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        this.collection = collection;
        this.actions = trActions;

    }
    /** Executes a SQL procedure 
    *
    */
    
    public void execute() throws EngineException 
	{
          	
    	try
		{
    		
    		Share sh = Share.instance();
    		ExecutionSlotProfileHandler executionSlotHandler = (ExecutionSlotProfileHandler)sh.get("executionSlotProfileObject");
    		EngineThread set = executionSlotHandler.getActiveExecutionProfile().getRunningSet(collection.getCollection_name(), collection.getCollection_id().longValue());
    		List settables = set.getSetTables();
    		settables.add("TEST1,TEST2,TEST3");
    		
    		/*Read config data from share*/
    		Share share = Share.instance();
    		String key = "ConfigData"+Long.toString(this.getTransferActionId().longValue());
    		String str="";
    		
    		Vector vec = (Vector)share.get(key);
    		
    		if (vec!=null)
    		{
    			/* Share Vector exists */
    			/* loop all sub Vectors in it*/
    			for (int i = 0; i < vec.size(); i++)
    			{
    				/* We are interested only with the data element (second element) */
    				str +=((Vector)vec.get(i)).get(1);
				}
    			
    			/* Remove vector from share*/
    			share.remove(key);
    		}

        	
        	
          	Random rnd = new Random(System.currentTimeMillis());
          	int i = rnd.nextInt(10);
            log.finest(this.collection.getScheduling_info()+"  "+i+"  "+str);  	
              	if ( i == 1)
              	{

              		throw new Exception("RANDOM ERROR");	

              	}
    		
		}
    	catch (Exception e)
		{
            throw new EngineException(  "Exception in Test",
                    new String[]{""},
                    e,
                    this,
                    this.getClass().getName(),
                    EngineConstants.ERR_TYPE_SYSTEM);
	
		}

          	
            
    }
    
}
