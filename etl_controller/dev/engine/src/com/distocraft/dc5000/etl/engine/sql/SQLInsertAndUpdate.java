package com.distocraft.dc5000.etl.engine.sql;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that executes SQL insert and update clauses 
  *  
  *
  *  @author Jukka Jääheimo
  *  @since  JDK1.1
  */
public class SQLInsertAndUpdate extends SQLUpdate {
    
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
    public SQLInsertAndUpdate(       Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections            collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions,
                            String batchColumnName)throws EngineMetaDataException {
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);
                
        this.batchColumnName = batchColumnName;
                
        this.insertSelectClause = this.getSource().getSelectClause(this.getTarget().tableContainsBatchColumn());
        
        this.insertSelectClause += " WHERE ";
        this.insertSelectClause += this.getSource().getWhereClause();                        
        if (this.getSource().getWhereClause().length()>0) {
            this.insertSelectClause += " AND ";
        }

        this.insertClause = "INSERT INTO "+this.getTarget().getTableName()+ " (";
        this.insertClause += this.getTarget().getCommaSeparatedColumns(false,false) + ")";
        this.insertClause += this.insertSelectClause;
        
        String additionalWhere = "NOT EXISTS (select * from ";
        additionalWhere += this.getTarget().getTableName()+ " " + EngineConstants.TARGET_TABLE_ALIAS;
        additionalWhere += " WHERE " + this.getTarget().getPkWhereClause(this.getSource()) + ")";
    
        this.insertClause += additionalWhere;
    }
    
    

    /** Executes an update clause 
    *
    */
    public void execute() throws EngineException {
            if (this.getSource().getConnection() == this.getTarget().getConnection()) {
                executeInsideDB();
            }
            else {
                executeThroughJava();
            }
    }
    
    /** Executes an update clause inside a database
    *
    */
    protected void executeInsideDB() throws EngineException{
        try {
            super.executeInsideDB();
            
            this.writeDebug(this.insertClause);
            this.getSource().getConnection().executeSql(this.insertClause);
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
    
    /** Executes an update clause via Java: First select into a vector, then update into DB
    *
    */
    protected  void executeThroughJava() throws EngineException {
            
            String sqlClause = "";
            try {
                
                // Updates last updated value to META_SOURCES
                this.getSource().setLastTransferDate();
                
                sqlClause =this.getUpdateSelectClause();
                writeDebug(this.getUpdateSelectClause());
                RockResultSet results = this.getSource().getConnection().setSelectSQL(this.getUpdateSelectClause());
                Vector objVec = this.getSource().getSelectObjVec(results,true);
                String preparedUpdStr = this.getPreparedUpdateClause(false);
                String preparedWhereStr = this.getTarget().getPkPreparedWhereClause(false);
                if (preparedWhereStr.length() > 0) {
                    preparedUpdStr += " WHERE " + preparedWhereStr;
                }
                String preparedInsStr = this.getTarget().getPreparedInsertClause();
                sqlClause =preparedInsStr;
                this.writeDebug(preparedUpdStr);
                this.writeDebug(preparedInsStr);
                this.getTarget().getConnection().executePreparedInsAndUpdSql(preparedUpdStr,objVec,preparedInsStr);
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
   
}
