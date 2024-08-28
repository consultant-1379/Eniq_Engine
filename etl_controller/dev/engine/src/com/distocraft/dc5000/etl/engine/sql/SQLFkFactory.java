package com.distocraft.dc5000.etl.engine.sql;


import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_fk_tables;
import com.distocraft.dc5000.etl.rock.Meta_fk_tablesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that holds common methods for all SQL actions that have a source component 
  *  
  *
  *  @author Jukka J‰‰heimo
  *  @since  JDK1.1
  */
public class SQLFkFactory extends TransferActionBase{
    // List of foreign key tables
    private Meta_fk_tablesFactory   fkTables;
    // List of fk table attributes
    private Vector  vecSqlFkTables;
  
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
    *  @param trActions object that hold transfer action information (db contents)
    *
    *  @author Jukka J‰‰heimo
    *  @since  JDK1.1
    */
    public SQLFkFactory(    Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections   collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory targetConnection,
                            RockFactory rockFact,
                            Meta_transfer_actions trActions,
                            Long        targetTableId,
                            String      targetTableName)
                                throws  EngineMetaDataException {
        
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        try {
            
            Meta_fk_tables whereFkTable = new Meta_fk_tables(rockFact);
            whereFkTable.setVersion_number(version.getVersion_number());
            whereFkTable.setConnection_id(connectId);
            whereFkTable.setTarget_table_id(targetTableId);
            whereFkTable.setCollection_set_id(collectionSetId);
            whereFkTable.setCollection_id(collection.getCollection_id());
            whereFkTable.setTransfer_action_id(transferActionId);

            this.fkTables = new Meta_fk_tablesFactory(
                                            rockFact,
                                            whereFkTable);
                                            
            this.vecSqlFkTables = new Vector();
       
            for (int i=0; i < this.fkTables.size(); i++) {
                Meta_fk_tables dbFkTable = this.fkTables.getElementAt(i);
                
                SQLFkTable sqlFkTable = new SQLFkTable(version,
                                                    collectionSetId,
                                                    collection,
                                                    transferActionId,
                                                    transferBatchId,
                                                    connectId,
                                                    targetConnection,
                                                    rockFact,
                                                    trActions,
                                                    targetTableId,
                                                    targetTableName,
                                                    dbFkTable);
                
                this.vecSqlFkTables.addElement(sqlFkTable);
            }

        
        }
        catch (Exception e) {
            throw new EngineMetaDataException(  EngineConstants.CANNOT_READ_METADATA,
                                                new String[]{"META_FK_TABLES"},
                                                e,
                                                this,
                                                this.getClass().getName());
        }
        
    }
    
    
    
    /** Executes the fk constraint clause
    *
    *   @param  i n:th fk clause
    *   @return int number of defective rows
    */
    public int executeFkCheck() throws EngineException{
        
        int errorCount = 0;
        for (int i=0; i<this.vecSqlFkTables.size();i++) {
            SQLFkTable sqlFkTable = (SQLFkTable)this.vecSqlFkTables.elementAt(i);
            errorCount = sqlFkTable.executeFkCheck();
        }
        return errorCount;
    }

}
