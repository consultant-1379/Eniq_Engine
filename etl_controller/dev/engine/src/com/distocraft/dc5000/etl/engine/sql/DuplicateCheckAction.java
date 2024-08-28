package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.dwhrep.Dwhcolumn;
import com.distocraft.dc5000.repository.dwhrep.DwhcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.ericsson.eniq.common.VelocityPool;

/**
 * This action checks if duplicate entries exist in database tables. This action
 * is intended to be used after Loader action. <br />
 * Copyright Distocraft 2006
 * 
 * @author Berggren
 * 
 */
public class DuplicateCheckAction extends SQLOperation {
  
  private Connection dwhRockFactoryConnection = null;

  protected SetContext setContext;

  protected RockFactory etlrepRockFactory;

  protected RockFactory dwhRockFactory;

  protected RockFactory dwhrepRockFactory;

  private Logger log;

  protected Meta_transfer_actions duplicateCheckMetaTransferAction = null;

  /**
   * Class constructor. Initializes class variables.
   * 
   * @param metaVersions
   * @param collectionSetId
   * @param collection
   * @param transferActionId
   * @param transferBatchId
   * @param connectId
   * @param rockFactory
   *          Database connection rockfactory-object.
   * @param connectionPool
   * @param metaTransferActions
   * @param setContext
   *          Velocitycontext with variables set by loader.
   * @throws EngineMetaDataException
   */
  public DuplicateCheckAction(Meta_versions metaVersions, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFactory,
      ConnectionPool connectionPool, Meta_transfer_actions metaTransferActions, SetContext setContext)
      throws EngineMetaDataException {
    super(metaVersions, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFactory,
        connectionPool, metaTransferActions);

    this.duplicateCheckMetaTransferAction = metaTransferActions;
    this.setContext = setContext;

    if (rockFactory == null) {
      this.log.log(Level.SEVERE, "DuplicateCheckAction received a null database connection.");
    }

    this.etlrepRockFactory = rockFactory;

    try {

      Meta_collection_sets whereCollSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(this.etlrepRockFactory, whereCollSet);

      String techPack = collSet.getCollection_set_name();
      String setType = collection.getSettype();
      String setName = collection.getCollection_name();
      String logName = techPack + "." + setType + "." + setName;
      String where = metaTransferActions.getWhere_clause();
      Properties properties = new Properties();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(where.getBytes());
      properties.load(inputStream);
      inputStream.close();
      this.log = Logger.getLogger("etl." + logName + ".loader.DuplicateCheckAction");
      this.createDwhRockFactories();

    } catch (Exception e) {
      throw new EngineMetaDataException("DuplicateCheckAction initialization error", e, "init");
    }
  }

  /**
   * Executes the duplicate checking functionality.
   */
  public void execute() throws EngineException {

    this.log.log(Level.FINE, "Starting DuplicateCheckAction.");

    if (!this.setContext.containsKey("tableList")) {
      this.log
          .log(
              Level.SEVERE,
              "Velocitycontext parameter \"tableList\" set by loader was not found in DuplicateCheckAction. No tables to check for, exiting duplicate checking...");
      final Exception e = new Exception();
      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
    }

    String template = (String) this.duplicateCheckMetaTransferAction.getAction_contents();

    if (template == null || template.length() <= 0) {
      template = StaticProperties.getProperty("DuplicateCheck.default", null);
    }

    if (template == null) {
      throw new EngineException(EngineConstants.CANNOT_EXECUTE + " DuplicateCheck.default is missing", null, this, this
          .getClass().getName(), EngineConstants.ERR_TYPE_EXECUTION);
    }

    Connection dwhrepRockFactoryConnection = null;
    
    try {
      final List tableList = (List) this.setContext.get("tableList");
      final Iterator tableListIterator = tableList.iterator();

      if (tableList.size() == 0) {
        this.log.log(Level.INFO, "List tableList in setContext was empty.");
      }

      this.dwhRockFactoryConnection = this.dwhRockFactory.getConnection();
      dwhrepRockFactoryConnection = this.dwhrepRockFactory.getConnection();
      //WCC WI 1.13: We put the start time before Duplicate check action start
      long startTime = System.currentTimeMillis();
      this.log.finest("Duplicate Start Action start time:" + startTime);
      while (tableListIterator.hasNext()) {
        // The tablename in tablelist has number postfix on it. For example
        // MY_EXAMPLE_TABLE_RAW_01.
        final String rawTableName = (String) tableListIterator.next();
        // Drop the postfix "_01" from the raw table name.
        final String tableName = rawTableName.substring(0, rawTableName.lastIndexOf("_"));
        final Dwhtype whereDwhType = new Dwhtype(this.dwhrepRockFactory);
        whereDwhType.setBasetablename(tableName);
        final DwhtypeFactory dwhTypeFactory = new DwhtypeFactory(this.dwhrepRockFactory, whereDwhType);
        final Vector dwhTypeVector = dwhTypeFactory.get();

        if (dwhTypeVector.size() == 1) {
          
          final Dwhtype targetDwhType = (Dwhtype) dwhTypeVector.get(0);
          final String storageId = targetDwhType.getStorageid();
          
          // Iterate through all the DWHColumns and get the table columns.
          final Dwhcolumn whereDwhColumn = new Dwhcolumn(this.dwhrepRockFactory);
          whereDwhColumn.setStorageid(storageId);
          whereDwhColumn.setUniquekey(1);
          
          
          final DwhcolumnFactory dwhColumnFactory = new DwhcolumnFactory(this.dwhrepRockFactory, whereDwhColumn);
          final Vector dwhColumns = dwhColumnFactory.get();
          final Iterator dwhColumnsIterator = dwhColumns.iterator();
          final Vector columns = new Vector();
          
          while (dwhColumnsIterator.hasNext()) {
            final Dwhcolumn currentDwhColumn = (Dwhcolumn) dwhColumnsIterator.next();
            //WCC WI 1.13: Removed the DWH Column from markDuplicates and displayed here only 
            // which reduced one iteration
            this.log.log(Level.FINE, currentDwhColumn.getDataname()); 
            columns.add(currentDwhColumn);
          }

          // Mark the duplicates to the table.

          markDuplicates(rawTableName, columns, template);
          
  

        } else if (dwhTypeVector.size() == 0) {
          this.log.log(Level.WARNING,
              "DuplicateCheckAction did not found any entries from DWHType table for tablename " + tableName);
        } else {
          this.log.log(Level.WARNING, "DuplicateCheckAction found too many entries from DWHType table for tablename "
              + tableName);
        }

      }
    //WCC WI 1.13: We put the start time before Duplicate check action start
      long totalTime = System.currentTimeMillis() - startTime;
      this.log.finest("Duplicate Action Total time:" + totalTime);

    } catch (Exception e) {
      this.log.log(Level.SEVERE, "DuplicateCheckAction failed.", e);
      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
    } finally {
      if (this.dwhRockFactoryConnection != null) {
        try {
          this.dwhRockFactoryConnection.close();
        } catch (Exception e) {

        }
      }
      
      if (dwhrepRockFactoryConnection != null){
        try {
          dwhrepRockFactoryConnection.close();
        } catch (Exception e) {

        }
      }
    }
  }

  /**
   * This function creates a RockFactories to dwh and dwhrep database schemas.
   * Class variable etlrepRockFactory must be set before calling this function.
   */
  private void createDwhRockFactories() {
    try {
      this.log.log(Level.FINE, "Starting to create database connections.");
      final Meta_databases whereMetaDatabases = new Meta_databases(this.etlrepRockFactory);
      whereMetaDatabases.setType_name("USER");
      final Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.etlrepRockFactory, whereMetaDatabases);

      final Vector metaDatabases = metaDatabasesFactory.get();

      final Iterator metaDatabasesIterator = metaDatabases.iterator();
      while (metaDatabasesIterator.hasNext()) {
        final Meta_databases currentMetaDatabase = (Meta_databases) metaDatabasesIterator.next();

        if (currentMetaDatabase.getConnection_name().equalsIgnoreCase("dwh")) {
          this.log.log(Level.FINE, "Trying to create dwh database connection.");
          this.dwhRockFactory = new RockFactory(currentMetaDatabase.getConnection_string(), currentMetaDatabase
              .getUsername(), currentMetaDatabase.getPassword(), currentMetaDatabase.getDriver_name(), "DuplicateCheckAction", true);
          this.log.log(Level.FINE, "Databaseconnection to dwh created in DuplicateCheckAction.");
        } else if (currentMetaDatabase.getConnection_name().equalsIgnoreCase("dwhrep")) {
          this.log.log(Level.FINE, "Trying to create dwhrep database connection.");
          this.dwhrepRockFactory = new RockFactory(currentMetaDatabase.getConnection_string(), currentMetaDatabase
              .getUsername(), currentMetaDatabase.getPassword(), currentMetaDatabase.getDriver_name(), "DuplicateCheckAction", true);
          this.log.log(Level.FINE, "Databaseconnection to dwhrep created in DuplicateCheckAction.");
        }

      }
      this.log.log(Level.FINE, "Database connections created succesfully.");
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "Creation of RockFactories in DuplicateCheckAction failed.", e);
    }
  }

  /**
   * This function marks the duplicates to the dwh table.
   * 
   * @param rawTableName
   *          is the name of the table to be updated.
   * @param columns
   *          is a Vector containing DWHColumn RockObjects. These are the
   *          columns of the table to be updated.
   */
  private void markDuplicates(final String rawTableName, final Vector columns, final String template) throws EngineException {

    VelocityEngine velocityEngine = null;

    Statement statement = null;
    
    try {
      this.log.log(Level.FINE, "Marking duplicates for " + rawTableName);
      
      dwhRockFactoryConnection = this.dwhRockFactory.getConnection();
      statement = dwhRockFactoryConnection.createStatement();

      if (dwhRockFactoryConnection == null) {
        this.log.log(Level.SEVERE, "Variable connection was null in DuplicateCheckAction.markDuplicates.");
      }
      if (statement == null) {
        this.log.log(Level.SEVERE, "Variable statement was null in DuplicateCheckAction.markDuplicates.");
      }

      // Create the Velocity context and set it's references.
      final VelocityContext context = new VelocityContext();

      if (context == null) {
        this.log.log(Level.SEVERE, "Variable context was null in DuplicateCheckAction.markDuplicates.");
      }

      this.log.log(Level.FINE, "rawTableName = " + rawTableName);
      this.log.log(Level.FINE, "columns content");
      //WCC WI 1.13: Following commented piece of code is removed
      //which is used just to display Column names. Coulumn names are displayed in execute() method
/*      final Iterator columnsIterator = columns.iterator();
      while (columnsIterator.hasNext()) {
        final Dwhcolumn currentColumn = (Dwhcolumn) columnsIterator.next();
        this.log.log(Level.FINE, currentColumn.getDataname());
      }*/

      context.put("rawTableName", rawTableName);
      context.put("columns", columns);

      final StringWriter output = new StringWriter();

      if (this.duplicateCheckMetaTransferAction == null) {
        this.log.log(Level.SEVERE, "duplicateCheckMetaTransferAction was null in DuplicateCheckAction.markDuplicates.");
      }

      if (this.duplicateCheckMetaTransferAction.getAction_contents() == null) {
        this.log.log(Level.SEVERE,
            "duplicateCheckMetaTransferAction.getAction_contents() was null in DuplicateCheckAction.markDuplicates.");
      }

      log.log(Level.FINEST, "duplicateCheckMetaTransferAction.getAction_contents() = "
          + duplicateCheckMetaTransferAction.getAction_contents());

      velocityEngine = VelocityPool.reserveEngine();

      final boolean velocityEvaluateSuccesfull = velocityEngine.evaluate(context, output, "DuplicateCheckAction", template);

      if (!velocityEvaluateSuccesfull) {
        this.log.log(Level.WARNING, "DuplicateCheckAction failed to evaluate action contents.");
      }

      final String sqlQuery = output.toString();

      this.log.log(Level.FINE, "Executing query: " + sqlQuery);
      statement.executeUpdate(sqlQuery);

    } catch (Exception e) {
      this.log.log(Level.SEVERE, "Marking duplicates in DuplicateCheckAction failed.", e);

      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
    } finally {
      VelocityPool.releaseEngine(velocityEngine);
      if(statement != null) {
        try {
          statement.close();
        } catch (Exception e) {
          
        }
      }
    }
  }

}
