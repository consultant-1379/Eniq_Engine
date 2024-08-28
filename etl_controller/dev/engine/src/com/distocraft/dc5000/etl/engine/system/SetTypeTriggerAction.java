package com.distocraft.dc5000.etl.engine.system;


import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;
import com.distocraft.dc5000.repository.cache.ActivationCache;

/** 
 * triggers a set or list of sets in scheduler.
 * sets are defined in action_contents column delimited by comma ','.
 * 
 * ex. set1,set2,set3
 * would trigger sets set1,set2 and set3. if triggered set is not in schedule
 * or it is inactive (on hold) set is not exeuted.
 * 
 */
public class SetTypeTriggerAction extends TransferActionBase {
    
	private Meta_collections   collection;
	private Meta_transfer_actions actions;
	private RockFactory rockFact;
	private String logString;
  private Logger log;

	/** Empty protected constructor 
    *
    */
    protected SetTypeTriggerAction() {
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
    public SetTypeTriggerAction(  Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections   collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            Meta_transfer_actions trActions, Logger clog) throws EngineMetaDataException {
                            
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        this.log = Logger.getLogger(clog.getName() + ".SetTypeTrigger");
        this.rockFact = rockFact;
        this.actions = trActions;
    }
    
    
    public void execute() throws Exception {
     
      try {

        Properties properties = new Properties();

        String act_cont = this.actions.getAction_contents();

        if (act_cont != null && act_cont.length() > 0) {

          try {
            ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
            properties.load(bais);
            bais.close();
          } catch (Exception e) {
            log.severe("Error while reading properties: " + act_cont);
            e.printStackTrace();
          }
        }

        int triggered = 0;
        
        String setType = properties.getProperty("setType","");

        log.info("Triggering "+setType+" sets.");
        
        Meta_collection_sets sets = new Meta_collection_sets(rockFact);
        sets.setEnabled_flag("Y");
        Meta_collection_setsFactory setsf = new Meta_collection_setsFactory(rockFact,sets);
        
        Iterator iter0 = setsf.get().iterator();
        while (iter0.hasNext()){

          Meta_collection_sets tmp0 = (Meta_collection_sets) iter0.next();
          
          Meta_collections cols = new Meta_collections(rockFact);
          cols.setCollection_set_id(tmp0.getCollection_set_id());
          cols.setEnabled_flag("Y");
          cols.setSettype(setType);
          Meta_collectionsFactory colsf = new Meta_collectionsFactory(rockFact,cols);
          
          Iterator iter1 = colsf.get().iterator();          
          while (iter1.hasNext()){
            
            Meta_collections tmp1 = (Meta_collections) iter1.next();
  
            Meta_schedulings msche = new Meta_schedulings(rockFact);
            msche.setCollection_id(tmp1.getCollection_id());
            msche.setExecution_type("wait");
            Meta_schedulingsFactory mschef = new Meta_schedulingsFactory(rockFact,msche);
            Iterator iter2 = mschef.get().iterator();
            
            while (iter2.hasNext()){
              
              Meta_schedulings tmp2 = (Meta_schedulings) iter2.next();             
              String name = tmp2.getName();              
              SchedulerAdmin admin = new SchedulerAdmin();            
              log.info("Triggering set "+name);
              admin.trigger(name);
              triggered++;
              
            }           
          }      
        }
          
        log.info(triggered+" Sets Triggered.");
        
      } catch (Exception e) {

        log.severe(e.getStackTrace() + "\r\n" + new String[] { this.getTrActions().getAction_contents() });
        throw new EngineException(EngineConstants.CANNOT_EXECUTE,
            new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
            EngineConstants.ERR_TYPE_EXECUTION);
      }
    }
    
    
    /** 
    *
    */    
    public void aexecute() throws EngineException 
	{       	

	  	List list = new ArrayList();
      

		Logger.getLogger(this.logString+".execute").log(Level.FINEST,"Starting SetTypeTriggerAction");   			    

		
      
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
            throw new EngineException(  "Exception in SetTypeTriggerAction",
                    new String[]{""},
                    e,
                    this,
                    this.getClass().getName(),
                    EngineConstants.ERR_TYPE_SYSTEM);
	
		}

          	
            
    }
    
}
