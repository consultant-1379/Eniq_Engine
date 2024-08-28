package com.distocraft.dc5000.etl.engine.structure;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.diskmanager.DirectoryDiskManagerAction;
import com.distocraft.dc5000.diskmanager.DiskManagerAction;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.file.SQLInputFromFile;
import com.distocraft.dc5000.etl.engine.file.SQLOutputToFile;
import com.distocraft.dc5000.etl.engine.plugin.Plugin;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.plugin.PluginToSql;
import com.distocraft.dc5000.etl.engine.plugin.SqlToPlugin;
import com.distocraft.dc5000.etl.engine.sql.AggregationRuleCopy;
import com.distocraft.dc5000.etl.engine.sql.DWHMCreateViewsAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMMigrateAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMPartitionAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMStorageTimeAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMTableCheckAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMUpdatePlanAction;
import com.distocraft.dc5000.etl.engine.sql.DWHMVersionUpdateAction;
import com.distocraft.dc5000.etl.engine.sql.DWHSanityCheckerAction;
import com.distocraft.dc5000.etl.engine.sql.DuplicateCheckAction;
import com.distocraft.dc5000.etl.engine.sql.EBSUpdateAction;
import com.distocraft.dc5000.etl.engine.sql.LogSessionLoader;
import com.distocraft.dc5000.etl.engine.sql.PartitionedLoader;
import com.distocraft.dc5000.etl.engine.sql.PartitionedSQLExecute;
import com.distocraft.dc5000.etl.engine.sql.SQLActionExecute;
import com.distocraft.dc5000.etl.engine.sql.SQLCreateAsSelect;
import com.distocraft.dc5000.etl.engine.sql.SQLDelete;
import com.distocraft.dc5000.etl.engine.sql.SQLExecute;
import com.distocraft.dc5000.etl.engine.sql.SQLExtract;
import com.distocraft.dc5000.etl.engine.sql.SQLInsert;
import com.distocraft.dc5000.etl.engine.sql.SQLInsertAndUpdate;
import com.distocraft.dc5000.etl.engine.sql.SQLJoin;
import com.distocraft.dc5000.etl.engine.sql.SQLLogResultSet;
import com.distocraft.dc5000.etl.engine.sql.SQLSummary;
import com.distocraft.dc5000.etl.engine.sql.SQLUpdate;
import com.distocraft.dc5000.etl.engine.sql.TableCleaner;
import com.distocraft.dc5000.etl.engine.sql.UnPartitionedLoader;
import com.distocraft.dc5000.etl.engine.sql.UpdateDIMSession;
import com.distocraft.dc5000.etl.engine.sql.UpdateMonitoredTypes;
import com.distocraft.dc5000.etl.engine.sql.exportAction;
import com.distocraft.dc5000.etl.engine.sql.importAction;
import com.distocraft.dc5000.etl.engine.system.AlarmHandlerActionWrapper;
import com.distocraft.dc5000.etl.engine.system.AlarmMarkupActionWrapper;
import com.distocraft.dc5000.etl.engine.system.ChangeProfileAction;
import com.distocraft.dc5000.etl.engine.system.Config;
import com.distocraft.dc5000.etl.engine.system.CreateDirAction;
import com.distocraft.dc5000.etl.engine.system.Distribute;
import com.distocraft.dc5000.etl.engine.system.ExecutionProfilerAction;
import com.distocraft.dc5000.etl.engine.system.GateKeeperAction;
import com.distocraft.dc5000.etl.engine.system.InvalidateDBLookupCache;
import com.distocraft.dc5000.etl.engine.system.JVMMonitorAction;
import com.distocraft.dc5000.etl.engine.system.Parse;
import com.distocraft.dc5000.etl.engine.system.ReloadDBLookupsAction;
import com.distocraft.dc5000.etl.engine.system.ReloadPropertiesAction;
import com.distocraft.dc5000.etl.engine.system.ReloadTransformationsAction;
import com.distocraft.dc5000.etl.engine.system.SQLLoad;
import com.distocraft.dc5000.etl.engine.system.SetContextTriggerAction;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.engine.system.SetTypeTriggerAction;
import com.distocraft.dc5000.etl.engine.system.SystemCall;
import com.distocraft.dc5000.etl.engine.system.Test;
import com.distocraft.dc5000.etl.engine.system.TestAction;
import com.distocraft.dc5000.etl.engine.system.TriggerAction;
import com.distocraft.dc5000.etl.engine.system.TriggerSetListInSchedulerAction;
import com.distocraft.dc5000.etl.engine.system.addToSetContext;
import com.distocraft.dc5000.etl.mediation.MediationAction;
import com.distocraft.dc5000.etl.mediation.jdbc.JDBCMediationAction;
import com.distocraft.dc5000.etl.mediation.smtp.SMTPMediationAction;
import com.distocraft.dc5000.etl.monitoring.AggregationAction;
import com.distocraft.dc5000.etl.monitoring.AutomaticAggregationAction;
import com.distocraft.dc5000.etl.monitoring.AutomaticReAggregationAction;
import com.distocraft.dc5000.etl.monitoring.BatchUpdateMonitoringAction;
import com.distocraft.dc5000.etl.monitoring.ManualReAggregationAction;
import com.distocraft.dc5000.etl.monitoring.SystemMonitorAction;
import com.distocraft.dc5000.etl.monitoring.UpdateMonitoringAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_statuses;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.uncompress.UncompressorAction;

/**
 * A class for transfer action.
 * 
 * @author Jukka Jääheimo, Tuomas Lemminkainen, Jarno Savinen
 */
public class TransferAction {

  // version number
  Meta_versions version;

  // collection set id
  Long collectionSetId;

  // collection
  Meta_collections collection;

  // transfer action id
  Long transferActionId;

  // db connection object
  RockFactory rockFact;

  // the corresponding database object
  Meta_transfer_actions dbTrAction;

  // The action to be executed
  TransferActionBase trBaseAction;

  // Type of the transfer action
  String transferActionType;
  
  String transferActionName;

  // All connections used in this transfer
  ConnectionPool connectionPool;

  // Transfer batch id
  Long transferBatchId;

  // Number fk errors
  private int fkErrors;

  // Number of con const errors
  private int colConstErrors;

  // Batch column name
  private String batchColumnName;

  // collection set
  Meta_collection_sets collSet;

  private Logger log;

  /**
   * Empty protected constructor
   */
  protected TransferAction() {
  }

  /**
   * Constructor
   * 
   * @param rockFact
   *          metadata repository connection object
   * @param version
   *          metadata version
   * @param collectionSetId
   *          primary key for collection set
   * @param collection
   *          collection
   * @param transferActionId
   *          primary key for transfer action
   * @param transferBatchId
   *          primary key for transfer batch
   * @param dbTrAction
   *          object that holds transfer action information (db contents)
   * @param connectionPool
   *          a pool for database connections in this collection
   * @author Jukka Jääheimo
   * @since JDK1.1
   */
  public TransferAction(RockFactory rockFact, Meta_versions version, Long collectionSetId,
      Meta_collection_sets collSet, Meta_collections collection, Long transferActionId,
      Meta_transfer_actions dbTrAction, Long transferBatchId, ConnectionPool connectionPool, String batchColumnName,
      PluginLoader pLoader, SetContext sctx, Logger log, EngineCom eCom) throws Exception {

    this.rockFact = rockFact;
    this.version = version;
    this.collectionSetId = collectionSetId;
    this.collection = collection;
    this.dbTrAction = dbTrAction;
    this.transferActionId = transferActionId;
    this.transferBatchId = transferBatchId;
    this.connectionPool = connectionPool;
    this.batchColumnName = batchColumnName;
    this.collSet = collSet;
    this.log = log;

    this.transferActionType = this.dbTrAction.getAction_type();
    this.transferActionName = this.dbTrAction.getTransfer_action_name();

    if (this.dbTrAction.getEnabled_flag().equalsIgnoreCase("Y")) {

      try {

        // --- OFFICIAL ACTIONS NAMES STARTS HERE ---

        if (this.transferActionType.equals("UnPartitioned Loader")) {
          this.trBaseAction = new UnPartitionedLoader(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, sctx, log);
        } else if (this.transferActionType.equals("Loader") || this.transferActionType.equals("Partitioned Loader")) {
          this.trBaseAction = new PartitionedLoader(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, sctx, log);
        } else if (this.transferActionType.equals("UpdateDimSession")) {
          this.trBaseAction = new UpdateDIMSession(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("Parse")) {
          this.trBaseAction = new Parse(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.dbTrAction,
              this.connectionPool, sctx, log, eCom);
          // } else if (this.transferActionType.equals("Join")) {
          // this.trBaseAction = new JoinerAction(this.version,
          // this.collectionSetId, this.collection,
          // this.transferActionId, this.transferBatchId,
          // this.dbTrAction.getConnection_id(), this.rockFact,
          // this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("Uncompress")) {
          this.trBaseAction = new UncompressorAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("System Call")) {
          this.trBaseAction = new SystemCall(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("SessionLog Loader")) {
          this.trBaseAction = new LogSessionLoader(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("Aggregator") || this.transferActionType.equals("Aggregation")) {
          this.trBaseAction = new AggregationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("UpdateMonitoring")) {
          this.trBaseAction = new UpdateMonitoringAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("AutomaticAggregation")) {
          this.trBaseAction = new AutomaticAggregationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("AutomaticREAggregati")) {
          this.trBaseAction = new AutomaticReAggregationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("ManualReAggregation")) {
          this.trBaseAction = new ManualReAggregationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Execute")) {
          this.trBaseAction = new SQLExecute(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, sctx, log);
        } else if (this.transferActionType.equals("CreateDir")) {
          this.trBaseAction = new CreateDirAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("System call")) {
          this.trBaseAction = new SystemCall(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
          // } else if (this.transferActionType.equals("Joiner")) {
          // this.trBaseAction = new JoinerAction(this.version,
          // this.collectionSetId, this.collection,
          // this.transferActionId, this.transferBatchId,
          // this.dbTrAction.getConnection_id(), this.rockFact,
          // this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("Distribute")) {
          this.trBaseAction = new Distribute(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("JVMMonitor")) {
          this.trBaseAction = new JVMMonitorAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("Diskmanager")) {
          this.trBaseAction = new DiskManagerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("DirectoryDiskmanager")) {
          this.trBaseAction = new DirectoryDiskManagerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("Trigger")) {
          this.trBaseAction = new TriggerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("SetContextTrigger")) {
          this.trBaseAction = new SetContextTriggerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("TriggerScheduledSet")) {
          this.trBaseAction = new TriggerSetListInSchedulerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("GateKeeper")) {
          this.trBaseAction = new GateKeeperAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("BatchUpdateMonitorin")) {
          this.trBaseAction = new BatchUpdateMonitoringAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("import")) {
          this.trBaseAction = new importAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, sctx);
        } else if (this.transferActionType.equals("export")) {
          this.trBaseAction = new exportAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, sctx);
        } else if (this.transferActionType.equals("Mediation")) {
          this.trBaseAction = new MediationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("JDBC Mediation")) {
          this.trBaseAction = new JDBCMediationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("AlarmHandler")) {
          this.trBaseAction = new AlarmHandlerActionWrapper(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("AlarmMarkup")) {
          this.trBaseAction = new AlarmMarkupActionWrapper(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("UpdatePlan")) {
          this.trBaseAction = new DWHMUpdatePlanAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("SMTP Mediation")) {
          this.trBaseAction = new SMTPMediationAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("System Monitor")) {
          this.trBaseAction = new SystemMonitorAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("DuplicateCheck")) {
          this.trBaseAction = new DuplicateCheckAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx);
        } else if (this.transferActionType.equals("SQL Extract")) {
          this.trBaseAction = new SQLExtract(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("ChangeProfile")) {
          this.trBaseAction = new ChangeProfileAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("reloadProperties")) {
          this.trBaseAction = new ReloadPropertiesAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("PartitionAction")) {
          this.trBaseAction = new DWHMPartitionAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("StorageTimeAction")) {
          this.trBaseAction = new DWHMStorageTimeAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("SanityCheck")) {
          this.trBaseAction = new DWHSanityCheckerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("CreateViews")) {
          this.trBaseAction = new DWHMCreateViewsAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("DWHMigrate")) {
          this.trBaseAction = new DWHMMigrateAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("VersionUpdate")) {
          this.trBaseAction = new DWHMVersionUpdateAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("UpdateMonitoredTypes")) {
          this.trBaseAction = new UpdateMonitoredTypes(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("TableCleaner")) {
          this.trBaseAction = new TableCleaner(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("SetTypeTrigger")) {
          this.trBaseAction = new SetTypeTriggerAction(version, collectionSetId, collection, transferActionId,
              transferBatchId, this.dbTrAction.getConnection_id(), rockFact, dbTrAction, log);
        } else if (this.transferActionType.equals("TableCheck")) {
          this.trBaseAction = new DWHMTableCheckAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("AggregationRuleCopy")) {
          this.trBaseAction = new AggregationRuleCopy(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("PartitionedSQLExec")) {
          this.trBaseAction = new PartitionedSQLExecute(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("SQLLogResultSet")) {
          this.trBaseAction = new SQLLogResultSet(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, log);
        } else if (this.transferActionType.equals("ReloadDBLookups")) {
          this.trBaseAction = new ReloadDBLookupsAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("ReloadTransformation")) {
          this.trBaseAction = new ReloadTransformationsAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("ExecutionProfiler")) {
          this.trBaseAction = new ExecutionProfilerAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, log);
        } else if (this.transferActionType.equals("ReloadProperties")) {
          this.trBaseAction = new ReloadPropertiesAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("RefreshDBLookup")) {
          this.trBaseAction = new InvalidateDBLookupCache(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, log);
        } else if (this.transferActionType.equals("SQLJoiner")) {
          this.trBaseAction = new SQLJoin(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, sctx, log);
        } else if (this.transferActionType.equals("EBSUpdate")) {
            this.trBaseAction = new EBSUpdateAction(this.version, this.collectionSetId, this.collection,
                this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
                 this.dbTrAction, log);         
                   
          
          // --- WILD WEST OF ACTION NAMES STARTS HERE ---

        } else if (this.transferActionType.equals("SQLActionExecute")) {
          this.trBaseAction = new SQLActionExecute(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Insert")) {
          this.trBaseAction = new SQLInsert(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.connectionPool,
              this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Update")) {
          this.trBaseAction = new SQLUpdate(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.connectionPool,
              this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Update&Ins")) {
          this.trBaseAction = new SQLInsertAndUpdate(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Summary")) {
          this.trBaseAction = new SQLSummary(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Delete")) {
          this.trBaseAction = new SQLDelete(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.connectionPool,
              this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("SQL Create as Select")) {
          this.trBaseAction = new SQLCreateAsSelect(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("DB -> File")) {
          this.trBaseAction = new SQLOutputToFile(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("File -> DB")) {
          this.trBaseAction = new SQLInputFromFile(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName);
        } else if (this.transferActionType.equals("Plugin -> DB")) {
          this.trBaseAction = new PluginToSql(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, pLoader);
        } else if (this.transferActionType.equals("DB -> Plugin")) {
          this.trBaseAction = new SqlToPlugin(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.connectionPool, this.dbTrAction, this.batchColumnName, pLoader);
        } else if (this.transferActionType.equals("SQL Load")) {
          this.trBaseAction = new SQLLoad(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.dbTrAction,
              this.connectionPool);
        } else if (this.transferActionType.equals("Plugin")) {
          this.trBaseAction = new Plugin(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.dbTrAction, pLoader);
        } else if (this.transferActionType.equals("Config")) {
          this.trBaseAction = new Config(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.dbTrAction);
        } else if (this.transferActionType.equals("Test") || this.transferActionType.equals("A")
            || this.transferActionType.equals("B") || this.transferActionType.equals("C")
            || this.transferActionType.equals("D")) {
          this.trBaseAction = new Test(this.version, this.collectionSetId, this.collection, this.transferActionId,
              this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact, this.dbTrAction);
        } else if (this.transferActionType.equals("TestAction")) {
          this.trBaseAction = new TestAction(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction);
        } else if (this.transferActionType.equals("addToSetContext")) {
          this.trBaseAction = new addToSetContext(this.version, this.collectionSetId, this.collection,
              this.transferActionId, this.transferBatchId, this.dbTrAction.getConnection_id(), this.rockFact,
              this.dbTrAction, sctx);
        } else {
          throw new Exception("Unknown transfer action type");
        }

      } catch (Exception e) {
        throw new Exception("Error initializing action: " + transferActionType, e);
      }

    } else {
      log.fine("Skipping... Action is not enabled");

      this.trBaseAction = null;
    }

  }

  /**
   * Method for executing the action.
   * 
   */
  public void execute(final int maxErrs, final int maxFkErrs, final int maxColConstErrs, int fkErrs, int colConstErrs) throws Exception {
    execute(maxErrs, maxFkErrs, maxColConstErrs, fkErrs, colConstErrs, SetListener.NULL);
  }
  
  /**
   * Method for executing the action.
   * 
   */
  public void execute(final int maxErrs, final int maxFkErrs, final int maxColConstErrs, int fkErrs, int colConstErrs, SetListener setListener) throws Exception {

    if (this.trBaseAction != null) {

      this.trBaseAction.execute(setListener); // EXECUTE throws exception

      this.fkErrors += this.trBaseAction.executeFkCheck();
      this.colConstErrors += 0;

      fkErrs += this.fkErrors;
      colConstErrs += this.colConstErrors;

      if (maxColConstErrs < colConstErrs) {
        // writeStatus(EngineConstants.STATUS_EXEC_ENDED_WITH_ERR);
        throw new EngineException(EngineConstants.COL_CONST_ERROR_STOP_TEXT, new String[] { "" + colConstErrs + "",
            "" + maxColConstErrs + "" }, null, this.trBaseAction, this.getClass().getName(),
            EngineConstants.ERR_TYPE_VALIDATION);
      }
      if (maxFkErrs < fkErrs) {
        // writeStatus(EngineConstants.STATUS_EXEC_ENDED_WITH_ERR);
        throw new EngineException(EngineConstants.COL_FK_ERROR_STOP_TEXT, new String[] { "" + fkErrs + "",
            "" + maxFkErrs + "" }, null, this.trBaseAction, this.getClass().getName(),
            EngineConstants.ERR_TYPE_VALIDATION);

      }
      if (maxErrs < (fkErrs + colConstErrs)) {
        // writeStatus(EngineConstants.STATUS_EXEC_ENDED_WITH_ERR);
        throw new EngineException(EngineConstants.MAX_ERROR_STOP_TEXT, new String[] {
            "" + (fkErrs + colConstErrs) + "", "" + maxErrs + "" }, null, this.trBaseAction, this.getClass().getName(),
            EngineConstants.ERR_TYPE_VALIDATION);

      }

      // writeStatus(EngineConstants.STATUS_EXEC_STOP);
    }
  }

  /**
   * If transfer fails, removes the data transferred before fail
   */
  public void removeDataFromTarget() throws Exception {
    if (this.batchColumnName != null) {
      if (this.dbTrAction.getEnabled_flag().equals("Y")) {
        this.trBaseAction.removeDataFromTarget();
      }
    }
  }

  /**
   * Writes status information into the database
   * 
   * @param statusText
   *          text to write into db.
   */
  public void writeStatus(String statusText) throws Exception {

    if (statusText == null) {
      statusText = EngineConstants.NO_STATUS_TEXT;
    }

    final Meta_statuses metaStatus = new Meta_statuses(this.rockFact);
    metaStatus.setStatus_description(statusText);
    metaStatus.setVersion_number(this.collection.getVersion_number());
    metaStatus.setCollection_set_id(this.collectionSetId);
    metaStatus.setCollection_id(this.collection.getCollection_id());
    metaStatus.setTransfer_batch_id(this.transferBatchId);
    metaStatus.setTransfer_action_id(this.transferActionId);
    metaStatus.insertDB();

    log.finer("Wrote into Meta_statuses: " + statusText);

  }

  /**
   * GET MEthods for member variables
   */
  public int getFkErrors() {
    return this.fkErrors;
  }

  public int getColConstErrors() {
    return this.colConstErrors;
  }

  public boolean isGateClosed() {

    if (this.trBaseAction != null) {
      return this.trBaseAction.isGateClosed();
    }

    return false;

  }
  
  public String getActionType() {
    return transferActionType;
  }
  
  public String getActionName() {
    return transferActionName;
  }
  
}
