package com.distocraft.dc5000.etl.scheduler;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.scheduler.trigger.FileExists;
import com.distocraft.dc5000.etl.scheduler.trigger.Interval;
import com.distocraft.dc5000.etl.scheduler.trigger.Monthly;
import com.distocraft.dc5000.etl.scheduler.trigger.Once;
import com.distocraft.dc5000.etl.scheduler.trigger.WaitToBeTriggered;
import com.distocraft.dc5000.etl.scheduler.trigger.Weekly;
import com.distocraft.dc5000.etl.scheduler.trigger.WeeklyInterval;

public class SchedulerThread extends Thread {

  private Logger log = Logger.getLogger("scheduler.thread");

  private static HashMap tpcache = new HashMap();

  // Time to connect again to the database if it fails
  private long reConnectTime;

  // The first connect must success
  private static boolean firstConnect = true;

  // Trigger list
  private Vector triggerThreadList = null;

  // The database connect object
  private RockFactory rockFact;

  // The database connect information:
  private String url;

  private String userName;

  private String password;

  private String dbDriverName;

  // RMI connection name to engine.
  private String engineURL;

  // Boolean value for stopping the execution
  private boolean stopped = false;

  private long pollInterval = 5000;

  private HashMap triggerMap = null;

  private static Map penaltyBox = new HashMap();

  private int penaltyWait = 30;

  /**
   * Constructor for starting the transfer
   */
  public SchedulerThread(long pollInterval, int penaltyWait, String engineURL, long reConnectTime, String url,
      String dbDriverName, String userName, String password) throws RemoteException {

    this.url = url;
    this.dbDriverName = dbDriverName;
    this.userName = userName;
    this.password = password;
    this.stopped = false;
    this.reConnectTime = reConnectTime;
    this.pollInterval = pollInterval;
    this.penaltyWait = penaltyWait;
    this.triggerMap = new HashMap();
    this.engineURL = engineURL;
  }

  /**
   * Stops the thread execution.
   * 
   * @exception RemoteException
   */
  public void cancel() {
    try {

      // Stop execution
      this.stopped = true;

      // Wait to actually stop the thread
      sleep((this.pollInterval * 2));

      // Clear data
      this.clearThreads();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Cancel failed exceptionally, exiting...: ", e);
      System.exit(0);
    }

  }

  /**
   * Creates threads
   * 
   * @exception RemoteException
   */
  public void run() {

    try {

      // Cretaes threads from metadata
      makeThreads();

      // loop until we drop...
      while (!this.stopped) {
        try {

          sleep(this.pollInterval);
          startThreads();

        } catch (Exception e) {
          Logger.getLogger("scheduler").log(Level.SEVERE, "General Error in Scheduler", e);

        }

      }

    } catch (Exception e) {
      log.log(Level.SEVERE, "Unrecoverable error occured", e);
    }

  }

  /**
   * Destroys the theads in the threadlist and removes all elements from the
   * vector.
   */
  private void clearThreads() throws SQLException {

    if (this.triggerThreadList != null) {
      for (int i = 0; i < this.triggerThreadList.size(); i++) {
        IScheduleTrigger sTrigger = (IScheduleTrigger) this.triggerThreadList.elementAt(i);
        sTrigger.cancel();
      }
      this.triggerThreadList.removeAllElements();
    }

    // clear triggers
    this.triggerMap.clear();

    // clear techpack name cache.
    tpcache.clear();
  }

  /**
   * Initialises rock engine
   */
  private void initRock() throws Exception {

    this.rockFact = null;

    while (this.rockFact == null) {

      try {
        this.rockFact = new RockFactory(this.url, this.userName, this.password, this.dbDriverName, "ETLSch", true);
        firstConnect = false;
      } catch (Exception e) {

        if (firstConnect) {
          log.log(Level.SEVERE, "First database connection failed", e);
          throw e;
        }

        try {
          Thread.sleep(this.reConnectTime);
        } catch (InterruptedException f) {
        }

      }
    }

  }

  private String getTechpackName(Meta_schedulings schedule) throws Exception {

    if (tpcache.containsKey(schedule)) {

      return (String) tpcache.get(schedule);

    } else {

      // setup collection set
      Meta_collection_sets whereCollection_sets = new Meta_collection_sets(this.rockFact);
      whereCollection_sets.setCollection_set_id(schedule.getCollection_set_id());
      Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(this.rockFact, whereCollection_sets);
      Meta_collection_sets mcs = mcsF.getElementAt(0);

      // create collection set
      Meta_collection_sets collection_set = new Meta_collection_sets(schedule.getRockFactory(), schedule
          .getCollection_set_id(), mcs.getVersion_number());

      String tp = collection_set.getCollection_set_name();

      tpcache.put(schedule, tp);

      return tp;

    }

  }

  /**
   * Creates triggers from etlrep.Meta_schedulings
   * 
   * @param currentTime
   * @exception RemoteException
   */
  private void makeThreads() throws Exception {

    try {

      log.info("Creating triggers");

      this.initRock();

      this.triggerThreadList = new Vector();

      ITransferEngineRMI engine = null;

      tpcache.clear();

      log.fine("Trying to connect ETLC engine in " + engine);

      engine = (ITransferEngineRMI) Naming.lookup(engineURL);

      log.info("Connected to ETLC engine");

      // Wait until engine has been initialized.
      while (!engine.isInitialized()) {
        engine = (ITransferEngineRMI) Naming.lookup(engineURL);
        this.log.fine("Waiting for the engine to initialize before starting scheduling.");
        Thread.sleep(1000);
      }

      // get list of collection sets
      Meta_collection_sets whereCollection_sets = new Meta_collection_sets(this.rockFact);
      Meta_collection_setsFactory activeCollectionSets = new Meta_collection_setsFactory(this.rockFact,
          whereCollection_sets);

      // list IDs of the active TechPacks

      List activeCollectionIDs = new ArrayList();

      for (int i = 0; i < activeCollectionSets.size(); i++) {

        Meta_collection_sets cSet = activeCollectionSets.getElementAt(i);

        // if collection set is active add it to the list
        if (cSet.getEnabled_flag().equalsIgnoreCase("y")) {

          activeCollectionIDs.add(cSet.getCollection_set_id());
        }
      }

      log.info(activeCollectionIDs.size() + " active techpacks found");

      // retrievs rows from schedule
      Meta_schedulings whereSchedule = new Meta_schedulings(rockFact);
      Meta_schedulingsFactory schedules = new Meta_schedulingsFactory(rockFact, whereSchedule);

      log.fine(schedules.size() + " schedules found");

      for (int i = 0; i < schedules.size(); i++) {

        Meta_schedulings schedule = schedules.getElementAt(i);

        log.finer("Schedule \"" + schedule.getName() + "\"");

        // Create only scheduling that reference active techpack

        if (activeCollectionIDs.contains(schedule.getCollection_set_id())) {
          log.fine("Creating trigger for schedule " + schedule.getName());

          // Create scheduler trigger type
          IScheduleTrigger sTrigger = createScheduleTrigger(schedule, engine);

          if (sTrigger != null) {
            triggerThreadList.addElement(sTrigger);
          }

        } else {
          log.fine("Schedule " + schedule.getName() + " references to not active TP");
        }

      } // foreach schedule

      log.info("Trigger creation complete. " + triggerThreadList.size() + " triggers created.");

    } catch (NotBoundException nbe) {
      throw new Exception("ETLC Engine is not running", nbe);
    } catch (RemoteException re) {
      throw new Exception("Registry cannot be contacted", re);
    } catch (Exception e) {
      throw new Exception("Unexpected error while creating triggers", e);
    }
  }

  /**
   * Triggers all the triggers that have correct trigger term
   */
  private void startThreads() {

    if (triggerThreadList != null) {

      log.finer("Checking triggers. List size " + triggerThreadList.size());

      for (int i = 0; i < triggerThreadList.size(); i++) {

        IScheduleTrigger sTrigger = (IScheduleTrigger) this.triggerThreadList.elementAt(i);

        log.finest("Trigger " + sTrigger.getName());

        Meta_schedulings schedule = sTrigger.getSchedule();
        ITransferEngineRMI engine = sTrigger.getEngine();

        try {

          String tpname = getTechpackName(schedule);

          // Is scheduled trigger on hold
          if (schedule.getHold_flag().equalsIgnoreCase("n")) {

            // is schedule in penaltyBox
            if (!penaltyBox.containsKey(sTrigger.getName())) {

              try {

                // Release the trigger, if execution terms are correct
                if (sTrigger.execute()) {
                  log.finest("Trigger " + sTrigger.getName() + " released. Trying to execute set...");

                  if (engine != null) {
                    try {
                      triggerSet(sTrigger, schedule, engine, "", tpname);
                    } catch (Exception e) {
                      Logger.getLogger("tp." + tpname)
                          .warning("Schedule " + sTrigger.getName() + " set trigger failed");
                      log.log(Level.WARNING, "Schedule " + sTrigger.getName() + " set execution failed", e);
                      executionFailed(sTrigger, schedule);
                    }
                  } else {
                    log.warning("Could not trigger " + sTrigger.getName() + " no engine found");
                  }
                }

              } catch (Exception e) {

                log.log(Level.WARNING, "Trigger " + sTrigger.getName() + " error while evaluating", e);

                penaltyBox.put(sTrigger.getName(), new Integer(penaltyWait));

                log.fine("Trigger " + sTrigger.getName() + " sent to penalty box");

              }

            } else {

              int pValue = ((Integer) penaltyBox.get(sTrigger.getName())).intValue();

              if (pValue > 0) {
                // schedulings penalty reduced

                penaltyBox.put(sTrigger.getName(), new Integer(--pValue));
                log.finest("Trigger " + sTrigger.getName() + " in penalty, " + pValue + " cycles left");

              } else {
                // schdulings penalty removed

                penaltyBox.remove(sTrigger.getName());
                log.finest("Trigger " + sTrigger.getName() + " released from penalty");
              }

            }

          } else {
            log.finest("Trigger " + sTrigger.getName() + " is on HOLD");
          }

        } catch (Exception e) {
          executionFailed(sTrigger, schedule);
          log.log(Level.WARNING, "Error while starting a thread", e);
        }
      }
    }
  }

  /**
   * Triggers a thread with given name.
   * 
   * @param triggerName
   * @return boolean true if set was triggered
   */
  boolean startThread(String triggerName, String command) {

    log.fine("Triggering: " + triggerName);

    if (this.triggerThreadList != null) {

      boolean found = false;

      for (int i = 0; i < this.triggerThreadList.size(); i++) {

        IScheduleTrigger sTrigger = (IScheduleTrigger) this.triggerThreadList.elementAt(i);

        if (!sTrigger.getName().equalsIgnoreCase(triggerName)) {
          continue;
        }

        found = true;

        log.finest("Trigger " + triggerName + " found: \"" + sTrigger.getName() + "\"");

        Meta_schedulings schedule = sTrigger.getSchedule();
        ITransferEngineRMI engine = sTrigger.getEngine();

        String tpname = "N/A";

        try {

          tpname = getTechpackName(schedule);

          // Is scheduled trigger on hold
          if (schedule.getHold_flag().equalsIgnoreCase("n")) {

            if (engine != null) {

              try {
                triggerSet(sTrigger, schedule, engine, command, tpname);
                return true;

              } catch (Exception e) {
                Logger.getLogger("tp." + tpname).log(Level.FINE, "Schedule " + triggerName + " set trigger failed");
                log.log(Level.WARNING, "Trigger " + sTrigger.getName() + " set execution failed", e);
                executionFailed(sTrigger, schedule);
              }
            } else {
              log.warning("Could not trigger set " + sTrigger.getName() + " no engine found.");
            }

          } else {
            log.finest("Trigger " + sTrigger.getName() + " is on HOLD");
            Logger.getLogger("tp." + tpname).info("Schedule " + sTrigger.getName() + " is on HOLD");
          }

        } catch (Exception e) {
          executionFailed(sTrigger, schedule);
          log.log(Level.WARNING, "Error while starting a thread", e);
        }

      } // foreach trigger

      if (!found)
        log.info("Trigger " + triggerName + " not found");

    }

    return false;
  }

  /**
   * Triggers set
   * 
   * 
   * @param sTrigger
   * @param schedule
   * @param engine
   * @throws Exception
   */
  private void triggerSet(IScheduleTrigger sTrigger, Meta_schedulings schedule, ITransferEngineRMI engine,
      String command, String tpname) throws Exception {

    // Some sanity checking...
    if (log == null) {
      log = Logger.getLogger("tp." + tpname);
      log.severe("SchedulerThread.triggerSet log was null. Created a new one.");
    }

    if (sTrigger == null) {
      this.log.severe("SchedulerThread.triggerSet variable sTrigger was null.");
    }

    if (schedule == null) {
      this.log.severe("SchedulerThread.triggerSet variable schedule was null.");
    }

    if (engine == null) {
      this.log.severe("SchedulerThread.triggerSet variable engine was null.");
    }

    if (command == null) {
      this.log.severe("SchedulerThread.triggerSet variable command was null.");
    }

    if (tpname == null) {
      this.log.severe("SchedulerThread.triggerSet variable tpname was null.");
    }

    // setup collection set
    Meta_collection_sets whereCollection_sets = new Meta_collection_sets(this.rockFact);
    whereCollection_sets.setCollection_set_id(schedule.getCollection_set_id());
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(this.rockFact, whereCollection_sets);

    if (mcsF.size() <= 0) {
      Logger.getLogger("tp." + tpname).warning("Schedule " + sTrigger.getName() + ": No such techpack");
      log.info("Techpack not found");
    }

    Meta_collection_sets mcs = mcsF.getElementAt(0);

    if (mcs == null) {
      this.log.severe("SchedulerThread.triggerSet variable mcs was null.");
    }

    // setup collection
    Meta_collections whereCollection = new Meta_collections(this.rockFact);
    whereCollection.setCollection_id(schedule.getCollection_id());
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(this.rockFact, whereCollection);

    if (mcsF.size() <= 0) {
      Logger.getLogger("tp." + tpname).warning("Schedule " + sTrigger.getName() + ": No such set");
      log.info("Set not found");
    }

    if (mcsF == null) {
      this.log.severe("SchedulerThread.triggerSet variable mcsF was null.");
    }

    Meta_collections mc = mcF.getElementAt(0);

    // create collection set
    Meta_collection_sets collection_set = new Meta_collection_sets(schedule.getRockFactory(), schedule
        .getCollection_set_id(), mcs.getVersion_number());

    // create collection
    Meta_collections collection = new Meta_collections(schedule.getRockFactory(), schedule.getCollection_id(), mc
        .getVersion_number(), schedule.getCollection_set_id());

    // execute set in engine
    engine.execute(schedule.getRockFactory().getDbURL(), schedule.getRockFactory().getUserName(), schedule
        .getRockFactory().getPassword(), schedule.getRockFactory().getDriverName(), collection_set
        .getCollection_set_name(), collection.getCollection_name(), command);

    Logger.getLogger("tp." + tpname).info(
        "Schedule " + sTrigger.getName() + " triggered set " + collection.getCollection_name());
    log.info("Trigger " + sTrigger.getName() + " triggered set " + collection.getCollection_name());

    // Set execution time
    schedule.setLast_execution_time(new Timestamp(sTrigger.getLastExecutionTime()));
    schedule.setLast_exec_time_ms(new Long(sTrigger.getLastExecutionTime()));

    // Set Status
    schedule.setStatus(Scheduler.STATUS_EXECUTED);

    // Save to DB
    schedule.updateDB();

  }

  /**
   * Creates objects from thre trigger classes
   * 
   * @param schedule
   * @param engine
   * @return
   * @throws Exception
   */
  private IScheduleTrigger createScheduleTrigger(Meta_schedulings schedule, ITransferEngineRMI engine) throws Exception {

    IScheduleTrigger ist = null;

    if (schedule.getExecution_type().equalsIgnoreCase("fileExists")) {

      ist = (IScheduleTrigger) new FileExists();

    } else if (schedule.getExecution_type().equalsIgnoreCase("interval")
        || schedule.getExecution_type().equalsIgnoreCase("intervall")) {

      ist = (IScheduleTrigger) new Interval();

    } else if (schedule.getExecution_type().equalsIgnoreCase("once")) {

      ist = (IScheduleTrigger) new Once();

    } else if (schedule.getExecution_type().equalsIgnoreCase("weekly")) {

      ist = (IScheduleTrigger) new Weekly();

    } else if (schedule.getExecution_type().equalsIgnoreCase("weeklyinterval")) {

      ist = (IScheduleTrigger) new WeeklyInterval();

    } else if (schedule.getExecution_type().equalsIgnoreCase("monthly")) {

      ist = (IScheduleTrigger) new Monthly();

    } else if (schedule.getExecution_type().equalsIgnoreCase("wait")) {

      ist = (IScheduleTrigger) new WaitToBeTriggered();

    } else if (schedule.getExecution_type().equalsIgnoreCase("onStartup")) {

      log.fine("Trigger type \"" + schedule.getName() + " is of type onStartup and handled by ETLC engine");
      return null;

    } else {

      log.warning("Unknown Trigger type: " + schedule.getExecution_type());
      return null;

    }

    ist.init(schedule, engine);

    log.fine("Trigger " + schedule.getName() + " created: " + getDesc(schedule));

    return ist;
  }

  /**
   * Reports a failed execution into th db.
   */
  private void executionFailed(IScheduleTrigger sTrigger, Meta_schedulings schedule) {
    try {
      schedule.setStatus(Scheduler.STATUS_FAILED);
      schedule.setLast_execution_time(new Timestamp(sTrigger.getLastExecutionTime()));
      schedule.setLast_exec_time_ms(new Long(sTrigger.getLastExecutionTime()));
      schedule.updateDB();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Updating execution status failed", e);
    }

  }

  /**
   * Returns description of specified Schedule
   */
  private String getDesc(Meta_schedulings sch) {

    String typ = sch.getExecution_type();
    if (typ.equals("interval") || typ.equals("intervall")) {
      return "Occurs once every " + sch.getInterval_hour() + " hours " + sch.getInterval_min() + " minutes";
    } else if (typ.equals("wait")) {
      return "Waiting trigger";
    } else if (typ.equals("fileExists")) {
      return "Waiting file " + sch.getTrigger_command();
    } else if (typ.equals("weekly")) {
      StringBuffer sb = new StringBuffer("");
      if ("Y".equals(sch.getMon_flag()))
        sb.append("Mon");
      if ("Y".equals(sch.getTue_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Tue");
      }
      if ("Y".equals(sch.getWed_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Wed");
      }
      if ("Y".equals(sch.getThu_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Thu");
      }
      if ("Y".equals(sch.getFri_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Fri");
      }
      if ("Y".equals(sch.getSat_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Sat");
      }
      if ("Y".equals(sch.getSun_flag())) {
        if (sb.length() > 0)
          sb.append(",");
        sb.append("Sun");
      }
      sb.insert(0, "Every ");
      sb.append(" at ");
      sb.append(sch.getScheduling_hour()).append(":").append(sch.getScheduling_min());

      return sb.toString();
    } else if (typ.equals("monthly")) {
      int day = sch.getScheduling_day().intValue();
      if (day <= 0) {
        return "Occures last day of month at " + sch.getScheduling_hour() + ":" + sch.getScheduling_min();
      } else {
        return "Occures " + sch.getScheduling_day() + ". day of month at " + sch.getScheduling_hour() + ":"
            + sch.getScheduling_min();
      }

    }

    return "";
  }

}
