package com.distocraft.dc5000.etl.engine.sql;


import java.sql.ResultSet;
import java.util.Vector;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_column_constraints;
import com.distocraft.dc5000.etl.rock.Meta_column_constraintsFactory;
import com.distocraft.dc5000.etl.rock.Meta_columns;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that holds common methods for all SQL actions that have a source component 
  *  
  *
  *  @author Jukka J‰‰heimo
  *  @since  JDK1.1
  */
public class SQLColConstraint extends TransferActionBase{
    // The joint corresponding columns
    private Vector vecTargetColumns;
    // The joint corresponding column constraints
    private Vector vecFactoryConstraints;
    // The target table name
    private String targetTableName;
    // A vector of sqlclauses for checking the constraints
    private Vector vecSqlClauses;
    // Target tables db connection for executing sql
    private RockFactory targetConnection;
    
    
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
    public SQLColConstraint(
                            Meta_versions           version,
                            Long        collectionSetId,
                            Meta_collections        collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory targetConnection,
                            RockFactory rockFact,
                            Meta_transfer_actions trActions,
                            Long        targetTableId,
                            String      targetTableName,
                            Vector      vecTargetColumns)throws EngineMetaDataException {
        
        
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions);
        
        this.targetTableName = targetTableName;
        this.targetConnection = targetConnection;
        this.vecTargetColumns = vecTargetColumns;
        this.vecFactoryConstraints = new Vector();

        try {
            
            for (int i = 0; i < this.vecTargetColumns.size(); i++) {
                Meta_columns column = (Meta_columns)this.vecTargetColumns.elementAt(i);
                
                Meta_column_constraints whereConstraint = 
                    new Meta_column_constraints(rockFact);
                whereConstraint.setVersion_number(version.getVersion_number());
                whereConstraint.setConnection_id(connectId);
                whereConstraint.setTable_id(targetTableId);
                whereConstraint.setColumn_id(column.getColumn_id());
                    
                Meta_column_constraintsFactory factConstraint = 
                        new Meta_column_constraintsFactory(rockFact,whereConstraint);
                        
                this.vecFactoryConstraints.addElement(factConstraint);
                
            }
         
        this.vecSqlClauses = createConstraintClauses();
        
        }
        catch (Exception e) {
            throw new EngineMetaDataException(  EngineConstants.CANNOT_READ_METADATA,
                                                new String[]{"META_COLUMN_CONSTRAINTS"},
                                                e,
                                                this,
                                                this.getClass().getName());
        }
        
    }
    
    /** Function to compose the column constraint clauses
    *
    *
    */
    private Vector createConstraintClauses(){
        
        Vector vecSqlClauses = new Vector();
        
        for (int i=0; i<this.vecTargetColumns.size(); i++) {
            Meta_columns column = (Meta_columns)this.vecTargetColumns.elementAt(i);
            Meta_column_constraintsFactory factConstraint =
                    (Meta_column_constraintsFactory)this.vecFactoryConstraints.elementAt(i);
            
            String sqlStr = "";
            
            for (int j=0; j<factConstraint.size(); j++) {
                
                if (j==0) {
                    sqlStr = "SELECT COUNT(*) FROM " + this.targetTableName +" WHERE ";
                }
                
                Meta_column_constraints colConst = factConstraint.getElementAt(i);
                
                String lowValue = colConst.getLow_value();
                String highValue = colConst.getHigh_value();
                
                if (j>0) {
                    sqlStr += " AND ";
                }

                if ((highValue != null) && (highValue.length()>0)) {
                    sqlStr += column.getColumn_name() + " > '" + lowValue + "'";
                    sqlStr += " AND "+column.getColumn_name() + " < '" + highValue + "'";
                }
                else {
                    sqlStr += column.getColumn_name() + " = '" + lowValue + "'";
                }
            }
            vecSqlClauses.addElement(sqlStr);
            
        }
        return vecSqlClauses;
    }
    
    
    /** Executes the fk check clause
    *
    *   @return int number of defective rows
    */
    public int executeColConstCheck() throws EngineException{
        return executeInsideDB();
    }
    /** Executes the fk clause inside a database
    *
    *   @return number of errors
    */
    private int executeInsideDB() throws EngineException {
            String sqlClause = "";

            
            try {
                
                int errCount = 0;
                for (int i=0; i<this.vecSqlClauses.size(); i++){
                    sqlClause = (String)this.vecSqlClauses.elementAt(i);
                    Meta_columns column = (Meta_columns)this.vecTargetColumns.elementAt(i);
                    this.writeDebug(sqlClause);
                    RockResultSet rockResults = this.targetConnection.setSelectSQL(sqlClause);
                    ResultSet results = rockResults.getResultSet();
                
                    String errString = "";
                
                    while (results.next()) {
                        int errors = results.getInt(1);
                        errCount += errors;
                        errString += "#";
                        errString += "Table:"+this.targetTableName+", Column:"+column.getColumn_name()+", Number of errors:"+errors;
                        errString += "#";
      
                        errString = "Column Constraint error: "+errString;

                        this.writeError(errString,"SQLColConstraint.execute()",EngineConstants.ERR_TYPE_WARNING);
                    }
                }
                return errCount;
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
