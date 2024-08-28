package com.distocraft.dc5000.etl.engine.sql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.sybase.jdbc3.jdbc.SybSQLException;


/** 
  * 
  * 
  */
public class SQLActionExecute extends SQLOperation 
{
   
    // Batch column name
	protected String batchColumnName;
    protected String loggerName="etlengine.SQLActionExecute";
    protected Meta_collections collection;
    private Logger log = Logger.getLogger(loggerName);
    
    protected SQLActionExecute() 
    {
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
    public SQLActionExecute(Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections        collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions,
                            String batchColumnName) throws Exception{
        
        super(version, collectionSetId, collection, transferActionId,transferBatchId, connectId, rockFact, connectionPool, trActions);
		       
        loggerName = getLoggerName(collection, collectionSetId, rockFact, trActions.getTransfer_action_name());
        this.batchColumnName = batchColumnName;
            
        this.collection = collection;
        
    }

    

    
    /** Executes a SQL procedure 
    *
    */   
    public void execute() throws Exception 
    {
        try 
          { 
        	log.fine("Beginning the execution of "+loggerName);
            String sqlClause = this.getTrActions().getAction_contents();
			log.finer("Unparsed sql:" + sqlClause);
			executeSQL(sqlClause);
          }
        catch (Exception e) 
          {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{this.getTrActions().getAction_contents()});
            throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                        new String[]{this.getTrActions().getAction_contents()},
                                        e,
                                        this,
                                        this.getClass().getName(),
                                        EngineConstants.ERR_TYPE_EXECUTION);
          }
    }

    
    /** Executes a SQL query 
    *
    */   
    protected ResultSet executeSQLQuery(String sqlClause) throws Exception {
    	

      	RockFactory c = this.getConnection();
      	Connection con = c.getConnection();
      	ResultSet rSet = null;
      	
      	try{
    	    // get max value from DB
    		Statement stmtc = con.createStatement();
    		stmtc.getConnection().commit();
    		rSet = stmtc.executeQuery(sqlClause);
    		stmtc.getConnection().commit();
  		
      	} catch (Exception e) {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{this.getTrActions().getAction_contents()});
            throw new Exception(e);
     		
      	}
		
		return rSet;
    }
 
    
    /** Executes a SQL query 
    *
    */   
    protected int executeSQLUpdate(String sqlClause) throws Exception {
    	

      	RockFactory c = this.getConnection();
      	Connection con = c.getConnection();
      	int count = 0;
      	
      	try{
    	    // get max value from DB
    		Statement stmtc = con.createStatement();
    		stmtc.getConnection().commit();
    		count = stmtc.executeUpdate(sqlClause);
    		stmtc.getConnection().commit();
  		
      	} catch (Exception e) {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{this.getTrActions().getAction_contents()});
            throw new Exception(e);
     		
      	}
		
      	return count;
    }
    
    
    /** Executes a SQL procedure 
    *
    */   
    protected void executeSQL(String sqlClause) throws Exception {
    	

      	RockFactory c = this.getConnection();
      	Connection con = c.getConnection();
      	ResultSet rSet = null;
      	
      	try{

    		Statement stmtc = con.createStatement();
    		stmtc.getConnection().commit();
    		stmtc.execute(sqlClause);
    		stmtc.getConnection().commit();
    	
  		
      	} catch (Exception e) {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{sqlClause});
            throw new Exception(e);
     		
      	}
		
    }
   
    protected void executeSQL(String sqlClause,String tableName) throws Exception {
    	

   	RockFactory c = this.getConnection();
      	Connection con = c.getConnection();
      	ResultSet rSet = null;
      	int error=0;
      	String transferName="";
      	
      	try{
      		Statement stmtc = con.createStatement();
    		stmtc.getConnection().commit();
    		stmtc.execute(sqlClause);
    		stmtc.getConnection().commit();
    	
  		
      	}
      	catch(SybSQLException sybExc)
      	{
      		
      		error=sybExc.getErrorCode();
      		transferName=this.getTransferActionName();
      		
      		log.finest("sybase error code inside executeSQL:"+error);
      		log.finest("this.getTransferActionName() inside executeSQL:"+this.getTransferActionName());
      		
      		// 8405 - Sybase Error Code for row locking issue
      		
      		if ( error==8405 && (transferName.equalsIgnoreCase("UpdateMonitoringOnStartup") || 
      										transferName.equalsIgnoreCase("UpdateMonitoring")))
      			{
      			
      				log.warning("The set "+transferName+" is failed to execute" +
      				" as another set aquires table level lock on the "+tableName+" table");	
      			}
      		
      			else
      			{
      			log.severe(sybExc.getMessage());
      			}
      	}
      	     	
      	catch (Exception e) {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{sqlClause});
            throw new Exception(e);
     		
      	}
		
    }
  
  
  protected int executeSQLUpdate(String sqlClause,String tableName) throws Exception {
  	
  	RockFactory c = this.getConnection();
    	Connection con = c.getConnection();
    	int count = 0;
    	int error = 0;
    	String transferName="";
    	
    	try{
    		
  		Statement stmtc = con.createStatement();
  		stmtc.getConnection().commit();
  		count = stmtc.executeUpdate(sqlClause);
  		stmtc.getConnection().commit();
		
    	} catch(SybSQLException sybExc)
      	{
    		
      		error=sybExc.getErrorCode();
      		transferName=this.getTransferActionName();

      		
      			if ( error==8405 && (transferName.equalsIgnoreCase("UpdateMonitoringOnStartup")||
      								transferName.equalsIgnoreCase("UpdateMonitoring"))) 
      			{
      			
      				log.warning("The set "+this.getTransferActionName()+" is failed to execute" +
      	      				" as another set aquires table level lock on the "+tableName+" table");	
      	      				
      			}
      			else
      			{
      			log.fine("Inside else-->executeSQLUpdate");
      			log.warning(sybExc.getMessage());
      			//log.severe(sybExc.getMessage() + "\r\n" + new String[]{sqlClause});
      			}
      		
      	}catch (Exception e) {
			log.severe(e.getStackTrace() + "\r\n" + new String[]{this.getTrActions().getAction_contents()});
          throw new Exception(e);
   		
    	}
		
    	return count;
  }
  
 
    
   public String getLoggerName(Meta_collections collection, 
                               Long collectionSetId, 
                               RockFactory rockFact, 
                               String actionName)
   {
     String name;
     
     String set = collection.getCollection_name();
	 
	 Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact); 
	 whereCollSet.setEnabled_flag("Y");
	 whereCollSet.setCollection_set_id(collectionSetId);
	 String techpack = "";
	 try 
	   {
    	 Meta_collection_sets collSet;
		 collSet = new Meta_collection_sets(rockFact, whereCollSet);
		 techpack = collSet.getCollection_set_name();
	   }
	 	catch(Exception e)
	   {

	   }
 
	 
	 String setType = collection.getSettype();
	 
     name = "sql." + techpack + "." + setType + "." + actionName;
   	 
   	 return name;    
   }
   
 
}
