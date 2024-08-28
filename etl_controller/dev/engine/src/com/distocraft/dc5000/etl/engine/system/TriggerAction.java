package com.distocraft.dc5000.etl.engine.system;


import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;

/** 
 * triggers a set or list of sets in scheduler.
 * sets are defined in action_contents column delimited by comma ','.
 * 
 * ex. set1,set2,set3
 * would trigger sets set1,set2 and set3. if triggered set is not in schedule
 * or it is inactive (on hold) set is not exeuted.
 * 
 */
public class TriggerAction extends TransferActionBase {
    
	private Meta_collections   collection;
	private Meta_transfer_actions actions;
	private RockFactory rockFact;
	private String logString;
	private String shareKey;
	
	/** Empty protected constructor 
    *
    */
    protected TriggerAction() {
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
    public TriggerAction(  Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections   collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            Meta_transfer_actions trActions) throws EngineMetaDataException {
                            
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        this.collection = collection;
        this.actions = trActions;
        this.rockFact = rockFact;
        
        try
        {        
	        Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
	        whereCollSet.setEnabled_flag("Y");
	        whereCollSet.setCollection_set_id(collectionSetId);
	        Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);
	
	        String tech_pack = collSet.getCollection_set_name();
	        String set_type = collection.getSettype();
	        String set_name = collection.getCollection_name();
	        
	        shareKey = tech_pack+"_"+set_name;
	        
	        this.logString = "etl."+tech_pack+"."+set_type+"."+set_name+".action.TriggerSetInSchedulerAction";

        }
        catch (Exception e)
        {
          throw new EngineMetaDataException("ExecuteSetAction unable to initialize loggers", e, "init");
        }

    }
    /** 
    *
    */    
    public void execute() throws EngineException 
	{       	

	  	List list = new ArrayList();
      

		Logger.getLogger(this.logString+".execute").log(Level.FINEST,"Starting TriggerSetListInSchedulerAction");   			    

		
      
 		try
 		{
    	   		  	
 		  	// read possible elements from action_content column
   			StringTokenizer token = new StringTokenizer(this.actions.getAction_contents(),",");		   				
   			while (token.hasMoreElements()){
   			  
   			  String name = token.nextToken(); 	
   			  
   			  if (!name.trim().equalsIgnoreCase("")){
   			  	list.add(name);
     			Logger.getLogger(this.logString+".execute").log(Level.FINE,"Reading sets from action_contents: "+name);   			    
   			  }
   			  
   			}  			
   	  
   			if (list!=null){
     	 	
   			  Iterator iter = list.iterator();	   		
        		
	      		while (iter.hasNext())
	      		{
	      			String name = (String)iter.next();
	      			
	      			SchedulerAdmin admin = new SchedulerAdmin();      			
	                Logger.getLogger(this.logString+".execute").log(Level.FINE,"Triggering set "+name);
	      			admin.trigger(name);
	  			
	    		}  			
    		  			  
   			}
    		  		    		
		}
    	catch (Exception e)
		{
            throw new EngineException(  "Exception in TriggerSetListInSchedulerAction",
                    new String[]{""},
                    e,
                    this,
                    this.getClass().getName(),
                    EngineConstants.ERR_TYPE_SYSTEM);
	
		}

          	
            
    }
    
}
