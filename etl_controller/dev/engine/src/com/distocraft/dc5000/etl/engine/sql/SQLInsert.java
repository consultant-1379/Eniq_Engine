package com.distocraft.dc5000.etl.engine.sql;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.RemoveDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that executes SQL insert clauses 
  *  
  *
  *  @author Jukka Jääheimo
  *  @since  JDK1.1
  */
public class SQLInsert extends SQLOperation {
    // The source table object
    private SQLSource source;
    // The target table object
    private SQLTarget target;
    // The insert clause
    private String insertClause;
    // The select part of insert clause
    private String insertSelectClause;
    // Batch column name
    private String batchColumnName;
    
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
    public SQLInsert(       Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections        collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions,
                            String batchColumnName)throws EngineMetaDataException {
                            
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions);
             
        this.source = new SQLSource(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        
        
        this.target = new SQLTarget(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        

        
        this.batchColumnName = batchColumnName;
                
        this.insertSelectClause = this.source.getSelectClause(this.target.tableContainsBatchColumn());
        if (this.source.getWhereClause().length()>0) {
            this.insertSelectClause += " WHERE ";
        }
        this.insertSelectClause += this.source.getWhereClause();                        

        this.insertClause = "INSERT INTO "+this.target.getTableName()+ " (";
        this.insertClause += this.target.getCommaSeparatedColumns(false,false) + ")";
        this.insertClause += this.insertSelectClause;
    }
    /** Executes an insert clause 
    *
    */
    
    public void execute() throws EngineException{
            // Updates last transferred value to META_SOURCES
            this.source.setLastTransferDate();
              
            if (this.source.getConnection() == this.target.getConnection()) {
                executeInsideDB();
            }
            else {
                executeThroughJava();
            }
        
    }
    
    /** Executes an update clause inside a database
    *
    */
    private void executeInsideDB() throws EngineException{
            try {
                this.writeDebug(this.insertClause);
                this.source.getConnection().executeSql(this.insertClause);
            }
        catch (Exception e) {
            throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                        new String[]{this.insertClause},
                                        e,
                                        this,
                                        this.getClass().getName(),
                                        EngineConstants.ERR_TYPE_EXECUTION);
        }
    }
    
    /** Executes an insert clause via Java: First select into a vector, then insert into DB
    *
    */
    private void executeThroughJava() throws EngineException {
            String sqlClause= "";
            try {
                sqlClause= this.insertSelectClause;// was:sqlClause= "this.insertSelectClause"; 
                this.writeDebug(this.insertSelectClause);
                RockResultSet results = this.source.getConnection().setSelectSQL(this.insertSelectClause);
                Vector objVec = this.source.getSelectObjVec(results,false);
                String preparedSqlStr = this.target.getPreparedInsertClause();
                sqlClause= "preparedSqlStr";
                this.writeDebug(preparedSqlStr);
                this.target.getConnection().executePreparedSql(preparedSqlStr,objVec);
                results.close();
            }
        catch (Exception e) {
            throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                        new String[]{sqlClause},
                                        e,
                                        this,
                                        this.getClass().getName(),
                                        EngineConstants.ERR_TYPE_EXECUTION);
        }
    }
    
        /** Executes the foreign key constraint checking
    *   
    *   @return int number of fk errors
    */
    public int executeFkCheck()throws EngineException{
        return this.target.sqlFkFactory.executeFkCheck();
    }

    /** If transfer fails, removes the data transferred before fail
    *
    */
    public void removeDataFromTarget()throws EngineMetaDataException,RemoveDataException{
        this.target.removeDataFromTarget();
    }
}
