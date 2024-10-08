package com.distocraft.dc5000.etl.engine.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.engine.sql.SQLSource;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_joints;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that implements file construction from a table
  *  
  *
  *  @author Jukka J��heimo
  *  @since  JDK1.1
  */
public class SQLOutputToFile extends SQLOperation {
    private String selectClause;
    // Batch column name
    private String batchColumnName;
    // Source table
    private SQLSource source;
    // The File object
    private SQLFile sqlFile;
    
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
    *  @author Jukka J��heimo
    *  @since  JDK1.1
    */
    public SQLOutputToFile( Meta_versions      version,
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
                
        this.sqlFile = new SQLFile(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions,batchColumnName );
                
        this.source = new SQLSource(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        
        
        this.batchColumnName = batchColumnName;
        String pathName = this.sqlFile.getFile().getPath();
        
        File pathFile =  new File(pathName);
        
        this.selectClause = this.source.getSelectClause(false);
        if (this.source.getWhereClause().length()>0) {
            this.selectClause += " WHERE ";
        }
        this.selectClause += this.source.getWhereClause();   
        
    }

    /** Executes the file output 
    *
    */
    public void execute() throws EngineException {
            try {
                writeDebug(this.selectClause);
                RockResultSet results = this.source.getConnection().setSelectSQL(this.selectClause);
                InsertToFile(results);
                results.close();
            }
            catch (Exception e) {
                throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                            new String[]{this.selectClause},
                                            e,
                                            this,
                                            this.getClass().getName(),
                                            EngineConstants.ERR_TYPE_EXECUTION);
            }
    }
    
    /** The actual insertion to file
    *
    *
    */
    private void InsertToFile(RockResultSet results) throws EngineException{
        try {
            
            
            FileOutputStream fileOutStream = new FileOutputStream(this.sqlFile.getFile());
            PrintWriter fileWriter = new PrintWriter(fileOutStream);
            
          
            while (results.getResultSet().next()) {
                
                for (int i=0; i<this.source.getJoinedColumns().size(); i++) {
                    Object obj = results.getResultSet().getObject(i+1);
                    String objStr = "";
                    

                    if (obj != null) {
                        objStr = obj.toString();
                    }
                    
                    if (this.sqlFile.getFillWithBlanks() == false) {
                        if (i<this.source.getJoinedColumns().size()-1) {
                            objStr += this.sqlFile.getColumnDelim();
                        }
                    }
                    else {
                    
                        Meta_joints joint = (Meta_joints)this.source.getJoinedColumns().getElementAt(i);
                        
                        for (int j = objStr.length(); j < joint.getColumn_space_at_file().intValue();j++) {
                            objStr += " ";
                        }
                        
                        objStr = objStr.substring(0,joint.getColumn_space_at_file().intValue());
                    }
                    
                    fileWriter.print(objStr);
                }
                
                if ((this.sqlFile.getRowDelim()==null) || (this.sqlFile.getRowDelim().equals(""))) {
                    fileWriter.println();
                }
                else {
                    fileWriter.print(this.sqlFile.getRowDelim());
                }
                
            }
            
            fileWriter.close();
            fileOutStream.close();
            
        }
            catch (Exception e) {
                throw new EngineException(  EngineConstants.CANNOT_WRITE_FILE,
                                            new String[]{this.sqlFile.getFileName()},
                                            e,
                                            this,
                                            this.getClass().getName(),
                                            EngineConstants.ERR_TYPE_EXECUTION);
            }

        
    }
    
}
