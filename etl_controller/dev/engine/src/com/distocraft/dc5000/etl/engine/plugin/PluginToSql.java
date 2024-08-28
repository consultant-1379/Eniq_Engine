package com.distocraft.dc5000.etl.engine.plugin;

import java.lang.reflect.Method;
import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.RemoveDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.sql.SQLOperation;
import com.distocraft.dc5000.etl.engine.sql.SQLTarget;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_columns;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/** 
  *  A Class that implements plugin construction from a table
  *  
  *
  *  @author Jukka J‰‰heimo
  *  @since  JDK1.1
  */
public class PluginToSql extends SQLOperation {
    // Plugin
    private TransferPlugin plugin ;
    // Target table
    private SQLTarget target;
    // The insert clause
    private String preparedInsertClause;
    
    
    
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
    public PluginToSql( 
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
                
        this.target = new SQLTarget(version,collectionSetId,collection,transferActionId,
                transferBatchId,connectId,rockFact,connectionPool,trActions,batchColumnName);        
        
        this.preparedInsertClause = target.getPreparedInsertClause();
        

    }

    /** Executes the file output 
    *
    */
    public void execute() throws EngineException {
            try {
                Object pluginObject = plugin.getPluginObject();
                Method pluginIterateMethod = this.getObjectMethod(pluginObject,EngineConstants.PLUGIN_ITERATOR_METHOD_NAME,null);
                Method pluginHasNextMethod = this.getObjectMethod(pluginObject,EngineConstants.PLUGIN_HASNEXT_METHOD_NAME,null);
                Vector pluginGetMethodNames = plugin.getJoinedPluginMethodNames();
                Vector pluginGetMethodParams = plugin.getJoinedPluginMethodParams();
                
                Vector rowVec = new Vector();
                int rowsToCommit = 0;
                boolean isFirstInsert = true;
                
                while (((Boolean)pluginIterateMethod.invoke(pluginObject,null)).booleanValue()) {
                    
                    rowsToCommit ++;
                    
                    Object[] vecObjs = new Object[2];
                    Vector objVec = new Vector();
                    Vector pkVec = new Vector();
                    vecObjs[0] = objVec;
                    vecObjs[1] = pkVec;
                    for (int i=0; i<pluginGetMethodNames.size(); i++) {
                        
                        String methodName = (String)pluginGetMethodNames.elementAt(i);
                        Object[] objs = (Object[])pluginGetMethodParams.elementAt(i);
                        Method pluginMethod = this.getObjectMethod(pluginObject,methodName,objs);
                        String strCell = (String)pluginMethod.invoke(pluginObject,objs);
                        
                        Meta_columns column = (Meta_columns)this.target.getColumns().elementAt(i);
                                                
                        Object convertedStr = createConvertedString(column,strCell);
                        objVec.addElement(new Object[]{convertedStr,null});
                    
                    }
                    rowVec.addElement(vecObjs);
                    
                    if (((plugin.getCommitAfterNRows() > 0) && (plugin.getCommitAfterNRows()==rowsToCommit)) ||
                         ((((Boolean)pluginHasNextMethod.invoke(pluginObject,null)).booleanValue()==false))) {
                            
                        if (isFirstInsert) {
                            this.writeDebug(this.preparedInsertClause);
                        }
                        
                        isFirstInsert = false;
                        this.target.getConnection().executePreparedSql(this.preparedInsertClause,rowVec);
                        
                        this.target.getConnection().commit();
                        rowsToCommit = 0;
                        
                        for (int i=0; i< rowVec.size(); i++) {
                            Object objs[] = (Object[])rowVec.elementAt(i);
                            ((Vector)objs[0]).removeAllElements();
                        }
                        rowVec.removeAllElements();
                    }
                    
                }
            }
            catch (Exception e) {
                throw new EngineException(  EngineConstants.CANNOT_EXECUTE,
                                            new String[]{this.preparedInsertClause},
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
    private Object createConvertedString(Meta_columns column,String strCell)
    {

    	if ((column.getColumn_type().toUpperCase().equals("TIMESTAMP")))
    	{
            return strCell;   		
    	}

    	if ((column.getColumn_type().toUpperCase().equals("DATE")))
    	{
            return strCell;   		
    	}

    	
    	
    	if ((column.getColumn_type().toUpperCase().equals("INT")) ||
       	  (column.getColumn_type().toUpperCase().equals("INTEGER")) ||
		  (column.getColumn_type().toUpperCase().equals("NUMERIC")))
       {
       		return Integer.decode(strCell);
       }

		if ((column.getColumn_type().toUpperCase().equals("TINYINT")) ||
			(column.getColumn_type().toUpperCase().equals("SMALLINT")) ||
		    (column.getColumn_type().toUpperCase().equals("UNSIGNED INT")))
		    
       {
       		return (Object)Integer.decode(strCell);
       }

            return strCell;

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
   /** Executes the foreign key constraint checking
    *   
    *   @return int number of fk errors
    */
    public int executeFkCheck()throws EngineException{
        return this.target.getSqlFkFactory().executeFkCheck();
    }
    /** If transfer fails, removes the data transferred before fail
    *
    */
    public void removeDataFromTarget()throws EngineMetaDataException,RemoveDataException{
        this.target.removeDataFromTarget();
    }
}
