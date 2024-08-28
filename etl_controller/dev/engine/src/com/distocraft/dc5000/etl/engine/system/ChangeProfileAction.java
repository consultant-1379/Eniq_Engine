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

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;


/** 
 * 
 */
public class ChangeProfileAction extends TransferActionBase {
    
	private Meta_collections   collection;
	private Meta_transfer_actions actions;
	private RockFactory rockFact;
	private String logString;
	private String shareKey;

	/** Empty protected constructor 
    *
    */
    protected ChangeProfileAction() {
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
    public ChangeProfileAction(  Meta_versions      version,
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
	        
	        this.logString = "etl."+tech_pack+"."+set_type+"."+set_name+".action.ChangeProfileAction";

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

    	
        Properties properties = new Properties();

        String act_cont = this.actions.getAction_contents();

        if (act_cont != null && act_cont.length() > 0) {

          try {
            ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
            properties.load(bais);
            bais.close();
          } catch (Exception e) {
            System.out.println("Error loading action contents");
            e.printStackTrace();
          }
        }
    	
        try {
        	 
        		String profileName = properties.getProperty("profileName","");
        		String forbidenTypes = properties.getProperty("forbidenTypes","");
        		
        		List forbidenTypesList = new ArrayList();
        		StringTokenizer token = new StringTokenizer(forbidenTypes,",");
        		
        		while (token.hasMoreTokens()){
        			forbidenTypesList.add(token.nextToken());
        		}
        		
        		// not empty
        		if (!profileName.equalsIgnoreCase("")){
        			        			
    		  		EngineAdmin admin = new EngineAdmin();
	
    		        Logger.getLogger(this.logString+".execute").log(Level.INFO,"changing to profile" + profileName);  		        
    		          		        
    		        boolean result = admin.changeProfile(profileName);
   		        
    				// if profile change is succesful
    				if  (result){
    					
        		        // if no forbiden types defined no need to wait anything.
            			if (!forbidenTypesList.isEmpty()){
            				
    						boolean waiting = true;      			
            				
    						Logger.getLogger(this.logString+".execute").log(Level.INFO," Waiting for forbiden set types to clear from execution slots.. ");
    						while (waiting){
    
        						// no forbiden setTypes found so free to end..
        						waiting = false;
        						
    							// sleep half second...
    							Thread.sleep(500);
    							
    							// Get execution slots
    							Set tmpVec = admin.getAllActiveSetTypesInExecutionProfiles();
    							
    							// if no sets in execution slots we can exit...
    							if (tmpVec.isEmpty()) break;
    							
    							Iterator slotIter = tmpVec.iterator();
            					
            					            					
            					// loop all execution slots or untill forbiden type is found
            					while (slotIter.hasNext() && !waiting){
            					
            						String setType = (String)slotIter.next();
         
                					// get forbiden types
                					Iterator forbidenTypesIter = forbidenTypesList.iterator();

            						// check for forbiden types in slot
            						while (forbidenTypesIter.hasNext()){
                						
                   						String forbidenType = (String)forbidenTypesIter.next();
                						
                						if (forbidenType.equalsIgnoreCase(setType)){
                						
                							// we found one forbiden setType so we wait somem more
                							waiting = true;
                							break;	
                						}
                						

                					}     						
            					}       					
            				}
    						
            			}Logger.getLogger(this.logString+".execute").log(Level.INFO," No forbiden set types in execution slots..");						
            			
    				} else
    					
   						Logger.getLogger(this.logString+".execute").log(Level.WARNING," Could not change to profile: "+profileName);
        		}   		  		    		
		}
    	catch (Exception e)
		{
            throw new EngineException(  "Exception in ChangeProfileAction",
                    new String[]{""},
                    e,
                    this,
                    this.getClass().getName(),
                    EngineConstants.ERR_TYPE_SYSTEM);
	
		}
          
    }
   
}
