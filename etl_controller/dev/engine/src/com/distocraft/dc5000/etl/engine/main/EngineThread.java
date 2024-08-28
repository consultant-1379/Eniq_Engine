package com.distocraft.dc5000.etl.engine.main;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.ExceptionHandler;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionMemoryConsumption;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.structure.TrCollection;
import com.distocraft.dc5000.etl.engine.structure.TrCollectionSet;
import com.distocraft.dc5000.etl.engine.structure.TransferAction;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.AlarminterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.distocraft.dc5000.repository.dwhrep.AlarmreportFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarmreportparameter;
import com.distocraft.dc5000.repository.dwhrep.AlarmreportparameterFactory;

/**
 * The class that executes task in ETLC engine.
 * 
 * 
 * @author Jukka J‰‰heimo
 * @author savinen
 * @author lemminkainen
 */
public class EngineThread extends Thread {

  private String url;

  private String userName;

  private String password;

  private String dbDriverName;

  private String collectionSetName;

  private String collectionName;

  private PluginLoader pluginLoader;

  private RockFactory rockFact = null;
  
  private RockFactory dwhRepRockFact = null;

  private String versionNumber;

  private String setType;

  private List<String> setTables;

  private String setName;

  private Long setID;

  private Long queueID = null;

  private Long setPriority;

  private String schedulingInfo;

  private Long queueTimeLimit;

  private boolean worker = false;

  private Object workerObject = null;

  public SetListener setListener = null;

  /* When engineThread was created */
  private Date creationDate = new Date();

  /* When Engine Thread was last edited */
  /* This is used when timelimit checks and priority increases are done. */
  private Date changeDate;

  /* Is this set active */
  private boolean active = true;

  /* is this set a shutdown set */
  private boolean shutdownSet = false;

  private final Logger log;
  
  private TrCollectionSet trCollectionSet = null;
  
  public String toString() {
    return "EngineThread "+collectionSetName+"/"+collectionName+"/"+setName;
    
  }

  private EngineCom eCom = null;

  /**
   * Constructor for starting the transfer
   * 
   */
  public EngineThread(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, PluginLoader pluginLoader, Logger log, EngineCom eCom) throws Exception {
    super("Engine");

    this.eCom = eCom;
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.dbDriverName = dbDriverName;
    this.collectionSetName = collectionSetName;
    this.collectionName = collectionName;
    this.pluginLoader = pluginLoader;
    this.log = log;

    try {

      this.rockFact = initRock();

      init(this.rockFact, this.collectionSetName, this.collectionName);

    } catch (Exception e) {
      throw new EngineMetaDataException("EngineThread (" + this.collectionSetName + "/" + this.collectionName
          + ") not created", e, this.getClass().getName());

    } finally {

      try {
        log.finest("EngineThread.constructor: closing rockengine connection");
        this.rockFact.getConnection().close();
      } catch (Exception e) {
        log.log(Level.FINER, "Failed to close rock factory connection", e);
      }

      this.rockFact = null;
    }

  }

  /**
   * Constructor for starting the transfer
   * 
   */
  public EngineThread(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, PluginLoader pluginLoader, String schedulingInfo, Logger log, EngineCom eCom)
      throws Exception {
    super("Engine");

    this.eCom = eCom;
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.dbDriverName = dbDriverName;
    this.collectionSetName = collectionSetName;
    this.collectionName = collectionName;
    this.pluginLoader = pluginLoader;
    this.schedulingInfo = schedulingInfo;
    this.log = log;

    try {

      log.finest("EngineThread.constructor: Opening rockengine connection");
      this.rockFact = initRock();
      init(this.rockFact, this.collectionSetName, this.collectionName);

    } catch (Exception e) {
      log.log(Level.WARNING, "EngineThread.constructor: Error while initializing RockEngine ", e);

      throw new EngineMetaDataException("EngineThread (" + this.collectionSetName + "/" + this.collectionName
          + ") not created", e, this.getClass().getName());

    } finally {

      try {
        log.finest("EngineThread.constructor: closing rockengine connection");
        this.rockFact.getConnection().close();
      } catch (Exception e) {
        log.log(Level.FINER, "Failed to close rock factory connection", e);
      }

      this.rockFact = null;
    }

  }

  /**
   * Constructor for starting the transfer
   * 
   */
  public EngineThread(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, PluginLoader pluginLoader, String schedulingInfo, SetListener list, Logger log,
      EngineCom eCom) throws Exception {
    super("Engine");

    this.eCom = eCom;
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.dbDriverName = dbDriverName;
    this.collectionSetName = collectionSetName;
    this.collectionName = collectionName;
    this.pluginLoader = pluginLoader;
    this.schedulingInfo = schedulingInfo;
    this.setListener = list;
    this.log = log;

    try {

      log.finest("EngineThread.constructor: Opening rockengine connection");

      this.rockFact = initRock();
      init(this.rockFact, this.collectionSetName, this.collectionName);

    } catch (Exception e) {
      throw new EngineMetaDataException("EngineThread (" + this.collectionSetName + "/" + this.collectionName
          + ") not created", e, this.getClass().getName());

    } finally {

      try {
        log.finest("EngineThread.constructor: Closing rockengine connection");
        this.rockFact.getConnection().close();
      } catch (Exception e) {
        log.log(Level.FINER, "Failed to close rock factory connection", e);
      }

      this.rockFact = null;
    }

  }

  /**
   * Constructor for starting the transfer
   * 
   */
  public EngineThread(RockFactory rockFact, String collectionSetName, String collectionName, PluginLoader pluginLoader,
      Logger log, EngineCom eCom) throws Exception {
    super("Engine");

    this.eCom = eCom;
    this.collectionSetName = collectionSetName;
    this.collectionName = collectionName;
    this.pluginLoader = pluginLoader;
    this.rockFact = rockFact;
    this.log = log;

    try {

      init(this.rockFact, this.collectionSetName, this.collectionName);

    } catch (Exception e) {
      throw new EngineMetaDataException("EngineThread (" + this.collectionSetName + "/" + this.collectionName
          + ") not created", e, this.getClass().getName());
    }

  }

  /**
   * 
   * constructor for creating a shutdown set
   * 
   * 
   * @param message
   */
  public EngineThread(String name, Long priority, Logger log, EngineCom eCom) {
    super("Engine");

    this.eCom = eCom;
    this.log = log;

    this.shutdownSet = true;

    this.setType = name;
    this.setName = name;
    this.setID = new Long(-1);
    this.setPriority = priority;
    this.queueTimeLimit = new Long(10000);

    this.creationDate = new Date();
    this.changeDate = new Date();
  }

  /**
   * 
   * constructor for creating a worker set
   * 
   * 
   * @param message
   */
  public EngineThread(String name, String type, Long priority, Object wobj, Logger log) {
    super("Engine");

    this.log = log;

    this.worker = true;

    this.setType = type;
    this.setName = name;
    this.setID = new Long(-1);
    this.setPriority = priority;
    this.queueTimeLimit = new Long(10000);
    this.workerObject = wobj;
    this.creationDate = new Date();
    this.changeDate = new Date();
  }

  public void run() {

    if (this.worker) {

      log.info("Starting a worker: " + setName);
      updateMemoryConsumption(true);
      ((Runnable) workerObject).run();
      updateMemoryConsumption(false);
    } else

    if (!this.shutdownSet) {

      try {

        if (this.rockFact == null) {
          this.rockFact = initRock();
        }

        trCollectionSet = new TrCollectionSet(this.rockFact, this.collectionSetName, this.collectionName,
            this.pluginLoader, eCom);

        // executes collection if enabled
        if (trCollectionSet.isEnabled()) {

          trCollectionSet.getCollection(this.collectionName).setSchedulingInfo(schedulingInfo);
          
          // If there is a setListener defined for this EngineThread, use it to 
          // observe set execution.
          if (setListener != null) { 
        	trCollectionSet.executeSet(setListener);
          }
          else { 
            trCollectionSet.executeSet(); 
          }

          if (setListener != null) {
            setListener.succeeded();
          }
        } else {
          log.fine("Execution cancelled: Package " + this.collectionSetName + " is disabled");
        }

      } catch (Exception e) {
        ExceptionHandler.handleException(e, "Execution failed exceptionally");
        if (setListener != null) {
          setListener.failed();
        }
      } finally {

        try {
          if (trCollectionSet != null) {
            final int count = trCollectionSet.cleanSet();

            log.finest("Closing rockengine, " + count + " connections closed from connectionpool");
          }
        } catch (Exception e) {
          ExceptionHandler.handleException(e, "Cleanup failed");

        } finally {
          try {
            if (this.dwhRepRockFact != null && this.dwhRepRockFact.getConnection() != null) {
              this.dwhRepRockFact.getConnection().close();
            }

            this.dwhRepRockFact = null;
          } catch (Exception e) {
            ExceptionHandler.handleException(e);
          }
          try {
            if (this.rockFact != null && this.rockFact.getConnection() != null) {
              this.rockFact.getConnection().close();
            }

            this.rockFact = null;
          } catch (Exception e) {
            ExceptionHandler.handleException(e);
          }
        }
      }

    }
  }

  public void addSlotId(int id) {
    if (eCom != null) {
      eCom.setSlotId(id);
    }
  }

  public Long getSetPriority() {
    return this.setPriority;
  }

  public String getSetVersion() {
    return this.versionNumber;
  }

  public Long getQueueID() {
    return this.queueID;
  }

  public void setQueueID(final Long id) {
    this.queueID = id;
  }

  public void setSetPriority(final Long l) {
    this.setPriority = l;
  }

  public String getSetType() {
    return this.setType;
  }

  /**
   * Sets the Set Type for the Set
   * @param setType
   */
  public void setSetType(final String setType){
	  this.setType = setType;
  }
  
  public String getSetName() {
    return this.setName;
  }

  public List<String> getSetTables() {
    return this.setTables;
  }
  
  /**
   * Adds the set Table
   * @param tableName
   */
  public void addSetTable(final String tableName) {
	  	this.setTables.add(tableName);  
  }

  /**
   * Removes the set table from the list
   * @param tableName
   */
  public void removeSetTable(final String tableName) {
  	this.setTables.remove(tableName);  
  }
  
  public String getTechpackName() {
    return this.collectionSetName;
  }

  public Long getTechpackID() {

    Long id = null;

    try {

      final Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_name(collectionSetName);
      final Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);
      id = collSet.getCollection_set_id();

    } catch (Exception e) {

    }

    return id;

  }

  public Long getQueueTimeLimit() {
    return this.queueTimeLimit;
  }
  
  /**
   * Sets the Queue Time Limit For the Set
   * @param queueTimeLimit
   */
  public void setQueueTimeLimit(Long queueTimeLimit){
	  this.queueTimeLimit = queueTimeLimit;
  }

  public Date getCreationDate() {
    return this.creationDate;
  }

  public Date getChangeDate() {
    return this.changeDate;
  }

  public void setChangeDate(final Date d) {
    this.changeDate = d;
  }

  public Long getSetID() {
    return this.setID;
  }

  public boolean isActive() {
    return this.active;
  }

  public void hold() {
    this.active = false;
  }

  public void restart() {
    this.active = true;
  }

  public boolean isWorker() {
    return this.worker;
  }

  public void setWorker(final boolean b) {
    this.worker = b;
  }

  public Object getWorkerObject() {
    return workerObject;
  }

  /**
   * return true is this set is a shutdown set
   * 
   * 
   * @return
   */
  public boolean isShutdownSet() {
    return this.shutdownSet;
  }

  /**
   * Fetch variables from set.
   * 
   * @param trCollectionSet
   * @throws Exception
   */
  private void init(final RockFactory rockFact, final String collectionSetName, final String collectionName)
      throws Exception {

    log.finest("EngineThread initializing...");

    Long collectionId = null;
    Long collectionSetId = null;
    Meta_collections coll = null;
    Meta_collection_sets collSet = null;
    String versionNumber = "";

    getProperties();

    final Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
    whereCollSet.setEnabled_flag("Y");
    whereCollSet.setCollection_set_name(collectionSetName);
    collSet = new Meta_collection_sets(rockFact, whereCollSet);
    collectionSetId = collSet.getCollection_set_id();
    versionNumber = collSet.getVersion_number();

    log.finest("EngineThread found Techpack " + collectionSetName + ": \"" + versionNumber + "\","
        + collectionSetId.toString() + ")");

    if (collectionName != null) {

      final Meta_collections whereColl = new Meta_collections(rockFact);
      whereColl.setVersion_number(versionNumber);
      whereColl.setCollection_set_id(collectionSetId);
      whereColl.setCollection_name(collectionName);
      coll = new Meta_collections(rockFact, whereColl);
      collectionId = coll.getCollection_id();
    }

    this.setType = coll.getSettype();
    this.setName = coll.getCollection_name();
    if (setType != null && setType.equalsIgnoreCase("Alarm") && this.setName.startsWith("Adapter_")) {
      if (this.setName.endsWith("_RD")) {
        this.setTables = getReducedDelayAlarmActionTables(collectionSetId, collectionId);
      } else {
        this.setTables = getAlarmActionTables(collectionSetId, collectionId);
      }
    } else {
      this.setTables = getActionTables(collectionSetId, collectionId);
    }
    this.setID = coll.getCollection_id();
    this.setPriority = coll.getPriority();
    this.queueTimeLimit = coll.getQueue_time_limit();

    this.creationDate = new Date();
    this.changeDate = new Date();

    log.finest("EngineThread initialized (" + collectionSetId.toString() + "," + collectionId.toString() + ")");

  }

  /**
   * @param collectionSetId
   * @param collectionId
   * @return
   */
  private List<String> getReducedDelayAlarmActionTables(Long collectionSetId, Long collectionId) {
    final List<String> list = getAlarmActionTables(collectionSetId, collectionId);
    list.add("DC_Z_ALARM_INFO");
    return list;
  }

  private List<String> getAlarmActionTables(final Long collectionSetId, final Long collectionId) {
    final List<String> list = new ArrayList<String>();
   
    try {
      if (this.dwhRepRockFact == null) {
        this.dwhRepRockFact = initDwhRep();
      }
      log.finest("EngineThread getting alarm interfaces (" + collectionSetId.toString() + "," + collectionId.toString()
          + ")");

      final Alarminterface whereInterface = new Alarminterface(this.dwhRepRockFact);
      whereInterface.setCollection_set_id(collectionSetId);
      whereInterface.setCollection_id(collectionId);
      final AlarminterfaceFactory dbAlarmInterfaces = new AlarminterfaceFactory(this.dwhRepRockFact, whereInterface);
      final Vector<Alarminterface> dbVec = dbAlarmInterfaces.get();

      if (dbVec == null) {
        log.finest("EngineThread did not find any alarm interfaces for set");
        return list;
      }
      log.finest("EngineThread found " + dbVec.size() + " interfaces for set");

      for (int i = 0; i < dbVec.size(); i++) {

        final Alarminterface dbInterface = (Alarminterface) dbVec.elementAt(i);
        final String interface_id = dbInterface.getInterfaceid();
        log.finest("EngineThread getting alarm interface reports for " + interface_id);
        final Alarmreport whereReport = new Alarmreport(this.dwhRepRockFact);
        whereReport.setInterfaceid(interface_id);
        final AlarmreportFactory dbAlarmReports = new AlarmreportFactory(this.dwhRepRockFact, whereReport);
        final Vector<Alarmreport> dbReports = dbAlarmReports.get();

        if (dbReports == null) {
          log.finest("EngineThread did not find any alarm reports for interface " + interface_id);
        } else {
          log.finest("EngineThread found " + dbReports.size() + " reports for interface");
          for (int j = 0; j < dbReports.size(); j++) {
            final Alarmreport dbReport = (Alarmreport) dbReports.elementAt(j);
            final String report_id = dbReport.getReportid();
            log.finest("EngineThread getting basetable names for report " + report_id);
            final Alarmreportparameter whereReportParameter = new Alarmreportparameter(this.dwhRepRockFact);
            whereReportParameter.setReportid(report_id);
            whereReportParameter.setName("eniqBasetableName");
            final AlarmreportparameterFactory dbAlarmParams = new AlarmreportparameterFactory(this.dwhRepRockFact,
                whereReportParameter);
            final Vector<Alarmreportparameter> dbParams = dbAlarmParams.get();
            if (dbParams == null) {
              log.finest("EngineThread did not find any alarm base tables for report " + report_id);
            } else {
              log.finest("EngineThread found " + dbParams.size() + " basetables for report");
              for (int k = 0; k < dbParams.size(); k++) {
                final Alarmreportparameter dbParam = (Alarmreportparameter) dbParams.elementAt(k);
                String value = dbParam.getValue();
                if (value != null && value.indexOf("_RAW") > 0) {
                  value = value.substring(0,value.lastIndexOf("_RAW"));
                }
                list.add(value != null ? value.toUpperCase() : value);
                log.finest("EngineThread found " + value + " basetable for report");
              }
            }
          }
        }
      }

    } catch (Exception e) {

    }

    return list;
  }

  private List<String> getActionTables(final Long collectionSetId, final Long collectionId) {

    final List<String> list = new ArrayList<String>();

    try {

      log.finest("EngineThread getting actions (" + collectionSetId.toString() + "," + collectionId.toString() + ")");

      final Meta_transfer_actions whereActions = new Meta_transfer_actions(this.rockFact);
      whereActions.setVersion_number(versionNumber);
      whereActions.setCollection_set_id(collectionSetId);
      whereActions.setCollection_id(collectionId);
      final Meta_transfer_actionsFactory dbTrActions = new Meta_transfer_actionsFactory(this.rockFact, whereActions,
          "ORDER BY ORDER_BY_NO");
      final Vector<?> dbVec = dbTrActions.get();

      log.finest("EngineThread found " + dbVec.size() + " actions for set");

      for (int i = 0; i < dbVec.size(); i++) {

        final Meta_transfer_actions dbTrAction = (Meta_transfer_actions) dbVec.elementAt(i);

        final String act_cont = dbTrAction.getWhere_clause();
        final Properties properties = new Properties();

        if (act_cont != null && act_cont.length() > 0) {

          try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
            properties.load(bais);
            bais.close();

            final String tables = properties.getProperty("tablename");
            if (tables != null) {
              final StringTokenizer token = new StringTokenizer(tables, ",");
              while (token.hasMoreTokens()) {
                String value = token.nextToken();
                if (value != null && value.indexOf("_PREV") > 0) {
                  value = value.substring(0,value.lastIndexOf("_PREV"));
                }
                list.add(value != null ? value.toUpperCase() : value);
              }
            }

            String typeName = properties.getProperty("typeName");
            if (typeName != null) {
              StringTokenizer token = new StringTokenizer(typeName, ",");
              while (token.hasMoreTokens()) {
                String value = token.nextToken();
                if (value != null && value.indexOf("_PREV") > 0) {
                  value = value.substring(0,value.lastIndexOf("_PREV"));
                }
                list.add(value.toUpperCase());
              }
            }

          } catch (Exception e) {
            // coud not create property from action content
          }
        }
      }

    } catch (Exception e) {

    }

    return list;
  }

  private RockFactory initRock() throws Exception {

    RockFactory rockFactTmp = null;

    try {
      log.finest("Initializing DB connection");
      rockFactTmp = new RockFactory(url, userName, password, dbDriverName, "ETLEngThr", true);
      log.finest("DB connection initialized");
      return rockFactTmp;
    } catch (Exception e) {

      log.log(Level.SEVERE, "Failed to connect to DB ", e);

      if (rockFactTmp != null && rockFactTmp.getConnection() != null) {
        try {
          rockFactTmp.getConnection().close();
        } catch (Exception ex) {
          log.log(Level.WARNING, "Cleanup failed", ex);
        }
      }

      throw e;
    }

  }
  
  private RockFactory initDwhRep() throws Exception {
    
    RockFactory rockFactTmp = null;
    
    try {

      Meta_databases md_cond = new Meta_databases(rockFact);
      md_cond.setType_name("USER");
      md_cond.setConnection_name("dwhrep");
      Meta_databasesFactory md_fact = new Meta_databasesFactory(rockFact, md_cond);

      Vector<?> dbs = md_fact.get();
      if (dbs == null) {
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");
      }
      Iterator<?> it = dbs.iterator();
      while (it.hasNext()) {
        Meta_databases db = (Meta_databases) it.next();

        if (db.getConnection_name().equalsIgnoreCase("dwhrep")) {
          log.finest("Initializing DB connection");
          rockFactTmp = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
              .getDriver_name(), "DWHMgr", true);
          log.finest("DB connection initialized");
        }
      } // for each Meta_databases
    } catch (Exception e) {

      log.log(Level.SEVERE, "Failed to connect to DB ", e);

      if (rockFactTmp != null && rockFactTmp.getConnection() != null) {
        try {
          rockFactTmp.getConnection().close();
        } catch (Exception ex) {
          log.log(Level.WARNING, "Cleanup failed", ex);
        }
      }

      throw e;
    }
    return rockFactTmp;
  }
  
  public String getCurrentAction() {
    if(trCollectionSet != null) {
      final TrCollection set = trCollectionSet.getCurrentCollection();
      if(set != null) {
        final TransferAction action = set.getCurrentAction();
        if(action != null) {
          return action.getActionName();
        }
      }
    }
    
    return "";
  }

  private void getProperties() throws Exception {

  }
  
  public int getMemoryConsumptionMB() {
	  if (worker) {
		  if (workerObject instanceof MemoryRestrictedParser) {
			  return ((MemoryRestrictedParser) workerObject).memoryConsumptionMB();
		  } else {
			  return 0;
		  }
	  } else {
		  return 0;
	  }
  }

  private void updateMemoryConsumption(boolean add) {
	  if (worker && (workerObject instanceof MemoryRestrictedParser))  {
		  ExecutionMemoryConsumption emc = ExecutionMemoryConsumption.instance();
	      if (add){
	    	  emc.add(workerObject);
	      }
	      else{
	    	  emc.remove(workerObject);
	      }
	  }
  }
  
  
  public String getSchedulingInfo() {
	  return this.schedulingInfo;
  }
}
