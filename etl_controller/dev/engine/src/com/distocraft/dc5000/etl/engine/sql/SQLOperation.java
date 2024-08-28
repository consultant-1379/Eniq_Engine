package com.distocraft.dc5000.etl.engine.sql;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that holds common methods for all SQL actions 
  *  
  *
  *  @author Jukka J‰‰heimo
  *  @since  JDK1.1
  */
public class SQLOperation extends TransferActionBase {
    // a pool for database connections in this collection
    private ConnectionPool connectionPool;
    private RockFactory connect;
    
    /** Empty protected constructor 
    *
    */
    protected SQLOperation() {
        super();
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
    *  @author Jukka J‰‰heimo
    *  @since  JDK1.1
    */
    public SQLOperation(    Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections   collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectionId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions)throws EngineMetaDataException {

        super(  version,collectionSetId,collection,transferActionId,
                    transferBatchId,connectionId,rockFact,trActions);
        
        this.connectionPool = connectionPool;
        if (connectionId!=null) {
            this.connect = connectionPool.getConnect(
                                        this,
                                        version.getVersion_number(),
                                        connectionId);
        }
                                
    }
    /** Returns a connect object from the connectionPool 
    *
    */
    public RockFactory getConnection() {
        return this.connect;
    }

    /** Get methods for member variables 
    *
    */
    public ConnectionPool getConnectionPool(){
        return this.connectionPool;
    }
    
    
}