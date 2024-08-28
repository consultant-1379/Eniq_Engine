package com.distocraft.dc5000.etl.engine.structure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.system.AlarmConfigCacheWrapper;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_parameters;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_batches;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.etl.alarm.AlarmConfig;
import com.ericsson.eniq.etl.alarm.CachedAlarmReport;

/**
 * A class for transfer collection. A starting point for a transfer.
 * 
 * 
 * @author Jukka J‰‰heimo
 * @since JDK1.1
 */
public class TrCollection {

  private static final String SCHEDULING_ALARM_INTERFACE_RD = "Scheduling_AlarmInterface_RD";

  private static final Object rockMutex = new Object();

  // version number
  Meta_versions version;

  // collection set id
  Long collectionSetId;

  // collection id
  Meta_collections collection;

  // db connection object
  RockFactory rockFact;

  // Vector of executable transfer actions
  Vector<TransferAction> vecTrActions;

  // The transfer batch created by this transfer
  Meta_transfer_batches trBatch;

  // A pool for database connections
  ConnectionPool connectionPool;

  // Maximum number of total errors (fk+column constraint
  private int maxErrs;

  // Maximum number of foreign key errors
  private int maxFkErrs;

  // Maximum number of column constraint errors
  private int maxColConstErrs;

  // Foreign key errors
  private int fkErrors;

  // Column constraint errors
  private int colConstErrors;

  // Batch column name
  private String batchColumnName;

  // Plugin loader
  private PluginLoader pLoader;

  // Collection set
  private Meta_collection_sets collSet;

  private SetContext sctx = null;

  private Logger log = null;

  private EngineCom eCom = null;

  public static final String SESSIONTYPE = "engine";

  private TransferAction currentAction = null;

  private String setType;

  /**
   * Empty protected constructor
   */
  protected TrCollection() {
  }

  /**
   * Constructor for starting the transfer
   * 
   * @param rockFact
   *          the database connection for the metadata
   * @param versionNumber
   *          version number
   * @param collectionSetId
   *          the id of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   */
  public TrCollection(RockFactory rockFact, Meta_versions version, Long collectionSetId, Meta_collections collection,
      PluginLoader pLoader, EngineCom eCom) throws Exception {

    boolean ok = false;

    try {

      this.eCom = eCom;
      this.rockFact = rockFact;
      this.version = version;
      this.collectionSetId = collectionSetId;
      this.collection = collection;
      this.pLoader = pLoader;

      this.maxErrs = this.collection.getMax_errors().intValue();
      this.maxFkErrs = this.collection.getMax_fk_errors().intValue();
      this.maxColConstErrs = this.collection.getMax_col_limit_errors().intValue();
      setType = collection.getSettype();

      // Get collection set name
      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      collSet = new Meta_collection_sets(rockFact, whereCollSet);

      try {
        Meta_parameters parameters = new Meta_parameters(this.rockFact, this.collection.getVersion_number());
        this.batchColumnName = parameters.getBatch_column_name();
      } catch (SQLException e) {
        this.batchColumnName = null;
      }

      this.connectionPool = new ConnectionPool(this.rockFact);

      this.trBatch = new Meta_transfer_batches(this.rockFact);
      this.trBatch.setVersion_number(this.collection.getVersion_number());
      this.trBatch.setCollection_set_id(this.collectionSetId);
      this.trBatch.setCollection_id(this.collection.getCollection_id());
      this.trBatch.setStart_date(new Timestamp(System.currentTimeMillis()));
      this.trBatch.setMeta_collection_set_name(this.collSet.getCollection_set_name());
      this.trBatch.setMeta_collection_name(this.collection.getCollection_name());
      this.trBatch.setSettype(this.collection.getSettype());
      this.trBatch.setStatus("STARTED");
      this.trBatch.setFail_flag("N");
      if (eCom != null) {
        this.trBatch.setSlot_id(new Integer(eCom.getSlotId()));
      }
      //

      synchronized (rockMutex) {
        this.trBatch.setId(new Long(SessionHandler.getSessionID(SESSIONTYPE)));
        this.trBatch.insertDB();
      }

      log = Logger.getLogger("etl." + this.collSet.getCollection_set_name() + "." + this.collection.getSettype() + "."
          + collection.getCollection_name());

      sctx = new SetContext();

      this.vecTrActions = getTransferActions();

      ok = true;

      log.finest("Set initialized");

    } catch (Exception e) {

      if (log != null) {
  
        log.log(Level.WARNING, "Set initialization failed", e);
      } else {
    	  SessionHandler.setDBMaxSessionID(maxIDCheck());
        throw new Exception("Set initializiation failed and logger was null.", e);
      }

    } finally {

      if (!ok)
        setBatchFailed();

    }
  }

  /**
   * Creates executed transfer action objects
   * 
   */
  private Vector<TransferAction> getTransferActions() throws Exception {
    log.finest("Starting to create actions...");

    Vector<TransferAction> vec = new Vector<TransferAction>();
    String collectionName = collection.getCollection_name();

    try {
      Meta_transfer_actions whereActions = new Meta_transfer_actions(this.rockFact);
      // whereActions.setVersion_number(this.version.getVersion_number());
      whereActions.setVersion_number(this.collection.getVersion_number());
      whereActions.setCollection_set_id(this.collectionSetId);
      whereActions.setCollection_id(this.collection.getCollection_id());
      Meta_transfer_actionsFactory dbTrActions = new Meta_transfer_actionsFactory(this.rockFact, whereActions,
          "ORDER BY ORDER_BY_NO");

      @SuppressWarnings("unchecked")
      Vector<Meta_transfer_actions> dbVec = dbTrActions.get();
      String baseTable = "";
      String collecionSetName = this.collSet.getCollection_set_name();

      for (int i = 0; i < dbVec.size(); i++) {
        Meta_transfer_actions dbTrAction = (Meta_transfer_actions) dbVec.elementAt(i);
        String actionType = dbTrAction.getAction_type();
        String actionName = dbTrAction.getTransfer_action_name();

        if (actionType.equals("Aggregation")&& actionName.startsWith("Aggregator")) {
          baseTable = getAggregationBaseTable(actionName);
          log.finest("Base table for: " + actionType + " is " + baseTable);
        }

        if (actionType.equals("Loader")) {
          baseTable = actionName.substring("Loader_".length()) + "_RAW";
          log.finest("Base table for: " + actionType + " is " + baseTable);
        }

        if (actionType.equals("UnPartitioned Loader")) {
          baseTable = actionName.substring("UnPartitioned_Loader_".length()) + "_RAW";
          log.finest("Base table for: " + actionType + " is " + baseTable);
        }

        Logger alog = Logger.getLogger("etl." + collecionSetName + "." + setType + "." + collectionName + "."
            + dbTrAction.getOrder_by_no().intValue());

        try {
          TransferAction trAction = new TransferAction(this.rockFact, this.version, this.collectionSetId, this.collSet,
              this.collection, dbTrAction.getTransfer_action_id(), dbTrAction, this.trBatch.getId(),
              this.connectionPool, this.batchColumnName, this.pLoader, this.sctx, alog, eCom);

          vec.addElement(trAction);
        } catch (Exception e) {
          log.warning("Could not create TransferAction " + actionName);
          throw new Exception("Transfer action " + actionName + " with id " + dbTrAction.getTransfer_action_id()
              + " initialization returned null." + e.getMessage());
        }
      }

      // this will add the TriggerAlarmAction to the Loader(Unpartitioned Loaders also) and Aggregator Sets
      if ((setType.equals("Loader") || setType.equals("Aggregator")) && !baseTable.equals("")) {
        if (hasSimultaneousReport(baseTable)) {
          log.info(collectionName + " has alarm reports defined for the " + baseTable + " table");
          Meta_transfer_actions tempAction = (Meta_transfer_actions) dbVec.elementAt(dbVec.size() - 1);
          Meta_transfer_actions triggerAlarmAction = createTriggerAlarmAction(tempAction);

          Logger actionLog = Logger.getLogger("etl." + collecionSetName + "." + setType + "." + collectionName + "."
              + tempAction.getOrder_by_no().intValue() + 1);

          try {
            log.info("Adding Dynamic Alarm Action " + triggerAlarmAction.getTransfer_action_name() + " to "
                + collecionSetName + ".");
            TransferAction trAction = new TransferAction(this.rockFact, this.version, this.collectionSetId,
                this.collSet, this.collection, triggerAlarmAction.getTransfer_action_id(), triggerAlarmAction,
                this.trBatch.getId(), this.connectionPool, this.batchColumnName, this.pLoader, this.sctx, actionLog,
                eCom);

            vec.addElement(trAction);
            log.info("Added Dynamic Alarm Action to " + collecionSetName + ".");
          } catch (Exception e) {
            log.warning("Could not create TransferAction " + triggerAlarmAction.getTransfer_action_name());
            log.warning(e.getMessage());
            throw new Exception("Transfer action " + this.getName() + " with id "
                + triggerAlarmAction.getTransfer_action_id() + " initialization returned exception: " + e.getMessage());
          }
        } else {
          log.info(collecionSetName + " has no alarms defined for " + baseTable);
        }

      }
      log.finer("Successfully created " + vec.size() + " actions");

    } catch (Exception e) {
      setBatchFailed();
      throw e;
    }

    return vec;
  }

  /**
   * This will create the TriggerAlarmAction on the end of the loader Set.
   * 
   * @param action_contents
   * @param maxOrderNo
   * @param connectionID
   * @return
   */
  private Meta_transfer_actions createTriggerAlarmAction(final Meta_transfer_actions tempAction) {
    Long transferActionID = 0L;
    if (setType.equals("Loader")) {
      transferActionID = 1234567890L;
    } else if (setType.equals("Aggregator")) {
      transferActionID = 1234567899L;
    }

    Meta_transfer_actions triggerAlarmAction = new Meta_transfer_actions(rockFact);

    triggerAlarmAction.setVersion_number(this.collection.getVersion_number());
    triggerAlarmAction.setTransfer_action_id(transferActionID);
    triggerAlarmAction.setCollection_id(this.collection.getCollection_id());
    triggerAlarmAction.setCollection_set_id(this.collectionSetId);
    triggerAlarmAction.setAction_type("TriggerScheduledSet");
    triggerAlarmAction.setTransfer_action_name("TriggerAlarmsAction_" + this.collection.getCollection_name());
    triggerAlarmAction.setOrder_by_no(tempAction.getOrder_by_no() + 1);
    triggerAlarmAction.setEnabled_flag("Y");
    triggerAlarmAction.setConnection_id(tempAction.getConnection_id());
    triggerAlarmAction.setAction_contents_01(SCHEDULING_ALARM_INTERFACE_RD);
    return triggerAlarmAction;
  }

  /**
   * Checks if the basetable has any reports
   * 
   * @return
   */
  private boolean hasSimultaneousReport(final String baseTable) {

    if (baseTable == null || baseTable.equals("")) {
      log.info(collection.getCollection_name() + " has no Alarm Report(s)");
      return false;
    }

    AlarmConfig alarms = AlarmConfigCacheWrapper.getInstance();

    List<CachedAlarmReport> cachedAlarmReports = alarms.getAlarmReportsByBasetable(baseTable);
    if (cachedAlarmReports != null && cachedAlarmReports.size() > 0) {
      for (CachedAlarmReport cachedAlarmReport : cachedAlarmReports) {
        if (cachedAlarmReport.isSimultaneous()) {
          log.info(collection.getCollection_name() + " has Alarm Report(s)");
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Gets the name of the base table the alarm will be based on
   * 
   * @param aggregationName
   * @return baseTable name
   */
  private String getAggregationBaseTable(String aggregationName) {

    String baseTable = "";
    StringBuilder sql = new StringBuilder();
    RockFactory dwhdb = DatabaseConnections.getDwhDBConnection();
    try {
      try {
        aggregationName = aggregationName.substring("Aggregator_".length());
        log.finest("Aggregator: " + aggregationName + " will be checked for an existing Target Table.");

        sql.append(" Select Target_Table From Log_AggregationRules");
        sql.append(" Where Aggregation = '" + aggregationName + "'");
        Statement stmt = dwhdb.getConnection().createStatement();
        try {
          ResultSet rs = stmt.executeQuery(sql.toString());
          log.info("ResultSet is: " + rs);
          try {
            if (rs.next()) {
              baseTable = rs.getString("Target_Table");
              log.info("Base Table is: " + baseTable);
            } else {
              log.warning("No Aggregation exists in Log_AggregationRules table for: " + aggregationName);
            }
          } finally {
            rs.close();
          }
        } finally {
          stmt.close();
        }
      } finally {
        dwhdb.getConnection().close();
      }
    } catch (Exception e) {
      log.warning("Could not retrieve Aggregation Base Table: " + sql.toString());
    }
    return baseTable;
  }

  /**
   * Executes all transfer actions of this collection
   * 
   */
  public void execute() throws Exception {
    execute(SetListener.NULL);
  }

  /**
   * Executes all transfer actions of this collection
   * 
   * @param SetListener
   */
  public void execute(SetListener setListener) throws Exception {

    int lastExecutedCounter = 0;
    int returnCode = EngineConstants.EXECUTION_NORMAL_CODE;

    try {
      if (this.vecTrActions == null)
        throw new Exception("Trying to execute set without actions");

      log.fine("Set execution started. " + this.vecTrActions.size() + " actions");

      for (int i = 0; i < this.vecTrActions.size(); i++) {

        TransferAction trAction = (TransferAction) this.vecTrActions.elementAt(i);
        if (returnCode < EngineConstants.EXECUTION_STOP_LIMIT_CODE) {
          lastExecutedCounter = i;

          currentAction = trAction;

          trAction.execute(this.maxErrs, this.maxFkErrs, this.maxColConstErrs, this.fkErrors, this.colConstErrors,
              setListener);

          currentAction = null;

          this.fkErrors += trAction.getFkErrors();
          this.colConstErrors += trAction.getColConstErrors();

          if (trAction != null)
            if (trAction.isGateClosed()) {
              log.fine("Set execution interreupted by " + trAction.transferActionType);
              break;
            }

        }
      }

      setBatchOk();

    } catch (EngineException ee) {
      log.log(Level.SEVERE, "Set execution failed to EngineException: " + ee.getMessage());
      if (ee.getCause() != null)
        log.log(Level.SEVERE, "Original cause", ee.getCause());

      removeDataFromTarget(lastExecutedCounter);
      setBatchFailed();

      throw ee;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Set execution failed exceptionally", e);

      removeDataFromTarget(lastExecutedCounter);
      setBatchFailed();

      throw e;
    }
  }

  private void setBatchFailed() throws Exception {
    try {

      if (this.trBatch != null) {
        this.trBatch.setEnd_date(new Timestamp(System.currentTimeMillis()));
        this.trBatch.setFail_flag("Y");
        this.trBatch.setStatus("FAILED");
        synchronized (rockMutex) {
          this.trBatch.updateDB();
        }

        if (log != null) {
          log.info("Logged failed set execution");
        }

      } else {
        if (log != null) {
          log.warning("trBatch object was null. No need to set it with FAILED because initialization failed");
        } else {
          throw new Exception(
              "trBatch object was null. No need to set it with FAILED because initialization failed and log was null");
        }
      }

    } catch (Exception e) {
      if (log != null) {
        log.log(Level.WARNING, "Update failed status to etlrep.Meta_transfer_batches failed", e);
      } else {
        throw new Exception("Update failed status to etlrep.Meta_transfer_batches failed and log was null", e);
      }

    }
  }

  private void setBatchOk() throws Exception {
    try {

      this.trBatch.setEnd_date(new Timestamp(System.currentTimeMillis()));
      this.trBatch.setStatus("FINISHED");
      synchronized (rockMutex) {
        this.trBatch.updateDB();
      }

      log.info("Logged successful set execution");

    } catch (Exception e) {
      log.warning("Log successful set execution failed");
      throw new Exception("Update finished status to etlrep.Meta_transfer_batches failed", e);
    }
  }

  private void removeDataFromTarget(int lastElement) throws Exception {
    if (this.vecTrActions == null)
      return;

    for (int i = 0; i < lastElement + 1; i++) {
      TransferAction trAction = (TransferAction) this.vecTrActions.elementAt(i);
      trAction.removeDataFromTarget();
    }

  }

  /**
   * Checking the maximum ID from META_TRANSFER_BATCHES table.
   * 
   * @return long type maximum ID value.
   * @throws Exception
   */
  private long maxIDCheck() throws Exception {

    long maxID = 0L;

    try {

      Connection c = rockFact.getConnection();
      Statement s = c.createStatement();

      // Hard coded query to database for efficiency reasons
      ResultSet resultSet = s.executeQuery("SELECT max(ID) FROM META_TRANSFER_BATCHES");

      if (resultSet.next()) {
        maxID = resultSet.getLong(1);
      }

    } catch (Exception e) {
      throw new Exception("Could not retrieve maximum ID from META_TRANSFER_BATCHES.", e);
    }

    return maxID;
  }

  public int cleanCollection() {

    return this.connectionPool.cleanPool();
  }

  /**
   * 
   * @param id
   * @return
   */
  public TransferAction getTransferAction(int id) {
    return null;
  }

  public String getName() {
    return this.collection.getCollection_name();
  }

  public Long getPriority() {
    return this.collection.getPriority();
  }

  public Long getID() {
    return this.collection.getCollection_id();
  }

  public Long getQueuTimeLimit() {
    return this.collection.getQueue_time_limit();
  }

  public String getSettype() {
    return this.collection.getSettype();
  }

  public void setSchedulingInfo(String info) {
    this.collection.setScheduling_info(info);
  }

  public String getEnabledFlag() {
    return this.collection.getEnabled_flag();
  }

  public String getHoldFlag() {
    return this.collection.getHold_flag();
  }

  public TransferAction getCurrentAction() {
    return currentAction;
  }

}
