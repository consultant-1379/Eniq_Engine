package com.distocraft.dc5000.etl.engine.sql;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that executes SQL create as select clauses 
  *  
  *
  *  @author Jukka Jääheimo
  *  @since  JDK1.1
  */
public class SQLCreateAsSelect extends SQLOperation {
    
    // The source table object
    private SQLSource source;
    // The select part of the sql clause
    private String selectClause;
    // The executable sql clause
    private String createAsSelectClause;
    // Table name to create
    private String createAsSelectTableName;
    
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
    *  @author Jukka Jääheimo
    *  @since  JDK1.1
    */
    public SQLCreateAsSelect(  
                            Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections        collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions,
                            String batchColumnName) throws EngineMetaDataException {
                                
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions);
             
        this.source = new SQLSource(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        
                
        this.createAsSelectTableName = trActions.getAction_contents();
        
        this.selectClause = this.source.getSelectClause(false);
        if (this.source.getWhereClause().length()>0) {
            this.selectClause += " WHERE ";
        }
        this.selectClause += this.source.getWhereClause(); 

        this.createAsSelectClause = "CREATE TABLE "+this.createAsSelectTableName+ " " + this.source.getTable().getAs_select_options();
        
        if ((this.source.getTable().getAs_select_tablespace()!=null) && 
            (this.source.getTable().getAs_select_tablespace().length()>0)) {
            this.createAsSelectClause += " TABLESPACE ";
        }
        this.createAsSelectClause += this.source.getTable().getAs_select_tablespace();
        this.createAsSelectClause += " AS SELECT " + this.selectClause;
    }
    /** Executes an insert clause 
    *
    */
    
    public void execute() throws EngineException {
                
            // Updates last transferred value to META_SOURCES
            this.source.setLastTransferDate();
                
            executeInsideDB();
        
    }
    
    /** Executes an update clause inside a database
    *
    */
    private void executeInsideDB()throws EngineException {
            try {
                this.writeDebug(this.createAsSelectClause);
                this.source.getConnection().executeSql(this.createAsSelectClause);
            }
            catch (Exception e) {
                throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                            new String[]{this.createAsSelectClause},
                                            e,
                                            this,
                                            this.getClass().getName(),
                                            EngineConstants.ERR_TYPE_EXECUTION);
            }
    }
    
}
