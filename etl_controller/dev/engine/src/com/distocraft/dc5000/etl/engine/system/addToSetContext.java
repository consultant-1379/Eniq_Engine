package com.distocraft.dc5000.etl.engine.system;


import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
 * test
  */
public class addToSetContext extends TransferActionBase {
    
	private Meta_transfer_actions actions;
	private Logger log = Logger.getLogger("Test");
	private Meta_collections   collection;
	private RockFactory rockFact;
	private String logString;
	private String shareKey;
	private SetContext sctx;
	private String where;
	private String action;
	
	
	
	/** Empty protected constructor 
    *
    */
    protected addToSetContext() {
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
    public addToSetContext(  Meta_versions      version,
            Long        collectionSetId,
            Meta_collections   collection,
            Long        transferActionId,
            Long        transferBatchId,
            Long        connectId,
            RockFactory rockFact,
            Meta_transfer_actions trActions, SetContext sctx) {
                            
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        this.collection = collection;
        this.rockFact = rockFact;
        this.sctx = sctx;
        this.where = trActions.getWhere_clause();
        this.action = trActions.getAction_contents();       


    }
    /** Executes a SQL procedure 
    *
    */
    
    public void execute() throws EngineException 
	{
          	
    	try
		{
    	 
    		HashSet hashSet = new HashSet();
    		hashSet.add("c1");
    		hashSet.add("c2");
    		hashSet.add("c3");
    		hashSet.add("c4");
    		hashSet.add("c5");
    		hashSet.add("c6");
    		
    		
    		sctx.put("anyObjectInSetContext","");   
    		sctx.put("s1",hashSet);   
    		sctx.put("s2",new Integer(10));   
    		
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
