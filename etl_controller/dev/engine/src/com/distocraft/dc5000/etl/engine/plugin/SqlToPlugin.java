package com.distocraft.dc5000.etl.engine.plugin;

import java.lang.reflect.Method;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.engine.sql.SQLSource;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_columns;
import com.distocraft.dc5000.etl.rock.Meta_joints;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that implements plugin construction from a table
  *  
  *
  *  @author Jukka Jääheimo
  *  @since  JDK1.1
  */
public class SqlToPlugin extends SQLOperation {
    // Plugin
    private TransferPlugin plugin ;
    // Source table
    private SQLSource source;
    // The source select clause
    private String selectClause;
    
    
    
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
    public SqlToPlugin( 
                            Meta_versions      version,
                            Long        collectionSetId,
                            Meta_collections        collection,
                            Long        transferActionId,
                            Long        transferBatchId,
                            Long        connectId,
                            RockFactory rockFact,
                            ConnectionPool connectionPool,
                            Meta_transfer_actions trActions,
                            String batchColumnName,
                            PluginLoader pLoader) throws EngineMetaDataException {
                                
        super(  version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions);
        
        this.plugin = new TransferPlugin(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,trActions,batchColumnName, pLoader);        
                
        this.source = new SQLSource(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        
        
        this.selectClause = this.source.getSelectClause(false);
        if (this.source.getWhereClause().length()>0) {
            this.selectClause += " WHERE ";
        }
        this.selectClause += this.source.getWhereClause();   
        
        

    }

    /** Executes the plugin output 
    *
    */
    public void execute() throws EngineException {
            try {
                writeDebug(this.selectClause);
                RockResultSet results = this.source.getConnection().setSelectSQL(this.selectClause);
                
                Object pluginObject = plugin.getPluginObject();
                
                
                while (results.getResultSet().next()) {
                    
                    for (int i=0; i<this.plugin.getJoinedColumns().size(); i++) {
                        Object obj = results.getResultSet().getObject(i+1);
                        String objStr = "";
                        

                        if (obj != null) {
                            objStr = obj.toString();
                        }
                        
                        Meta_joints joint = (Meta_joints)this.source.getJoinedColumns().getElementAt(i);
                        String methodName = joint.getPlugin_method_name();
                        
                        Object[] objs = (Object[])plugin.getJoinedPluginMethodParams().elementAt(i);
                        
                        Object[] paramObjs = new Object[objs.length+1];
                        
                        for (int j=0; j<objs.length; j++) {
                            paramObjs[j] = objs[j];
                        }
                        
                        paramObjs[objs.length] = objStr;
                        Method pluginMethod = this.getObjectMethod(pluginObject,methodName,paramObjs);
                        pluginMethod.invoke(pluginObject,paramObjs);
                            
                        
                    }
                    ((PluginWriteClass)pluginObject).addRow();
                }
                ((PluginWriteClass)pluginObject).commit();
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
    
    /** Create a string to insert to the database accoding to column type
    *
    *
    */
    private String createConvertedString(Meta_columns column,String strCell){
        
        if (column.getColumn_type().toUpperCase()=="NUMBER") {
            return "TO_NUMBER("+strCell+")";
        }
        else if ((column.getColumn_type().toUpperCase()=="VARCHAR2") ||
                 (column.getColumn_type().toUpperCase()=="VARCHAR")||
                 (column.getColumn_type().toUpperCase()=="CHAR")){
            return strCell;
        } 
        else if (column.getColumn_type().toUpperCase()=="DATE") {
            return  "TO_DATE('"+strCell+"')";
        }
        else {
            return strCell;
        }
    }
    
    
    /** Returns the objects corresponding method
    *
    *   @param Object obj The object holding the method
    *   @param String name  The method name to look for.
    *   @return Method The method found.
    */
    private Method getObjectMethod(Object obj, String name,Object[] objs)throws NoSuchMethodException {
        if (objs!=null) {
            Class[] paramClasses = new Class[objs.length];
            for (int i=0; i<objs.length;i++) {
               paramClasses[i] = objs[i].getClass();
            }
            Class objClass = obj.getClass();
            return objClass.getMethod(name, paramClasses);
        }
        else {
            Class objClass = obj.getClass();
            return objClass.getMethod(name, null);
        }
        
    }
    
}
