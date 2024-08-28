package com.distocraft.dc5000.etl.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Scheduler extends UnicastRemoteObject implements ISchedulerRMI {

  private static final long serialVersionUID = 3493067702136629615L;

  public static final String STATUS_EXECUTED = "Executed";

  public static final String STATUS_FAILED = "Exec failed!";

  private static Logger log = Logger.getLogger("scheduler");

  private String serverHostName;

  private int serverPort;

  private String serverRefName;

  private String url = "";

  private String userName = "";

  private String password = "";

  private String dbDriverName = "";

  // Time to connect again to the database if it fails
  private long reConnectTime;

  private transient SchedulerThread schedulerThread;

  private long pollIntervall;

  private int penaltyWait;

  private String engineURL;

  /**
   * Constructor for starting the transfer
   */
  public Scheduler() throws RemoteException {
    super();

    log.info("---------- ETLC scheduler is initializing ----------");

  }

  /**
   * (re)start scheduler thread
   */
  public void reload() throws RemoteException {

    try {

      boolean reload = false;

      if (this.schedulerThread != null) {
        this.schedulerThread.cancel();
        reload = true;
      }

      this.schedulerThread = new SchedulerThread(this.pollIntervall, this.penaltyWait, this.engineURL,
          this.reConnectTime, url, dbDriverName, userName, password);

      this.schedulerThread.start();

      if (reload) {
        log.info("Reloaded");
      } else {
        log.info("Loaded");
      }

    } catch (Exception e) {
      log.log(Level.WARNING, "Reload failed exceptionally", e);
      throw new RemoteException("Reload failed exceptionally", e);
    }

  }

  /**
   * Places scheduler on hold.
   * 
   * @exception RemoteException
   */
  public void hold() throws RemoteException {

    try {

      if (this.schedulerThread != null) {
        this.schedulerThread.cancel();

        log.info("On hold");
      } else {
        log.info("Allready on hold");
      }

      this.schedulerThread = null;

    } catch (Exception e) {
      log.log(Level.WARNING, "Hold failed exceptionally", e);
      throw new RemoteException("Hold failed exceptionally", e);
    }

  }

  /**
   * Method to shutdown Schedule.
   */
  public void shutdown() throws RemoteException {

    try {

      // if threads are running, close em, and shut down
      if (this.schedulerThread != null) {
        this.schedulerThread.cancel();
      }

      log.info("Shuting Down...");

      System.exit(0);

    } catch (Exception e) {
      log.log(Level.WARNING, "Shutdown failed exceptionally", e);
      throw new RemoteException("Shutdown failed exceptionally", e);
    }
  }

  /**
   * Return status information
   */
  public List<String> status() {
    final List<String> al = new ArrayList<String>();

    al.add("--- ETLC Scheduler ---");
    if (this.schedulerThread != null) {
      al.add("  Status: active");
    } else {
      al.add("  Status: on hold");
    }
    al.add("  Poll interval: " + this.pollIntervall);
    al.add("  Penalty Wait: " + this.penaltyWait);

    return al;
  }

  /**
   * Triggers a set if set does not exists or set is in hold, nothing is done.
   * 
   * @param name
   *          name of the triggered set.
   */
  public void trigger(final String name) {
    trigger(name, "");
  }

  /**
   * Triggers a list of sets, if set does not exists or set is in hold, nothing
   * is done.
   * 
   * @param name
   *          name of the triggered set.
   */
  @Deprecated
  public void trigger(final List<String> list) {

    if (list != null) {
      final Iterator<String> iter = list.iterator();
      while (iter.hasNext()) {
        final Object temp = iter.next();
        final String name = (String) temp;
        log.fine("Triggering set " + name);
        trigger(name, "");
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.scheduler.ISchedulerRMI#trigger(java.util.Map)
   */
  public void trigger(final List<String> list, final Map<String, String> map) {
    
    final String setName = map.get("setName");
    final String setType = map.get("setType");
    final String baseTable = map.get("setBaseTable");
    
    final Properties schedulingInfo = new Properties();
    for (Entry<String, String> entry : map.entrySet()) {
      schedulingInfo.put(entry.getKey(), entry.getValue());
    }
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      schedulingInfo.store(baos, "");
    } catch (IOException e) {
      log.severe("Failed to serialize schedulingInfo map as string: " + e.getMessage());
    }

    for (String item : list) {
      log.fine("Triggering set " + item +  " with parameters " + setName + " for " + setType + "_" + baseTable);
      trigger(item, baos.toString());
    }
    
    
  }


  /**
   * Triggers a set if set does not exists or set is in hold, nothing is done.
   * 
   * 
   * @param name
   *          name of the triggered set.
   * @param command
   *          context
   */
  public void trigger(final String name, final String command) {

    if (this.schedulerThread != null) {
      if (this.schedulerThread.startThread(name, command)) {
        log.info("Trigger \"" + name + "\" triggered");
      } else {
        log.info("Trigger \"" + name + "\" NOT triggered");
      }
    } else {
      log.log(Level.INFO, "Did not trigger set \"" + name + "\" because scheduler is on hold or initializing");
    }
  }

  /**
   * Reload logging property files
   */
  public void reloadLoggingProperties() throws RemoteException {

    try {
      LogManager.getLogManager().reset();
      LogManager.getLogManager().readConfiguration();
      log.info("Logging properties reloaded");

    } catch (Exception e) {
      log.log(Level.WARNING, "Error while reloading logging properties", e);
      throw new RemoteException("Error while reloading logging properties", e);
    }

  }

  /**
   * Initializes the dagger Scheduler server - binds the server object to RMI
   * registry - initializes the omi connection - instantiates the access
   * controller
   * 
   * @param name
   *          String Name of server in registry
   * @return boolean true if initialisation succeeds otherwise false
   * @exception
   */
  private boolean init() {

    log.finer("Initializing...");

    final String rmiRef = "//" + this.serverHostName + ":" + this.serverPort + "/" + this.serverRefName;

    log.config("RMI Reference is: " + rmiRef);

    try {
      Naming.rebind(rmiRef, this);
      log.fine("Server registered");
    } catch (Throwable e) {
      log.info("LocateRegistry is not running.");

      try {

        log.info("Starting RMI-Registry on port " + this.serverPort);
        LocateRegistry.createRegistry(this.serverPort);
        log.info("RMI-Registry started");

        Naming.bind(rmiRef, this);

      } catch (Exception exception) {

        log.log(Level.SEVERE, "Unable to initialize LocateRegistry", exception);

        return false;
      }
    }

    log.fine("initialized.");

    return true;

  }

  /**
   * Load configuration
   */
  private void loadProperties() throws Exception {

    String sysPropDC5000 = System.getProperty("dc5000.config.directory");
    if (sysPropDC5000 == null) {
      log.severe("System property dc5000.config.directory not defined");
      throw new Exception("System property dc5000.config.directory not defined");
    }

    if (!sysPropDC5000.endsWith(File.separator)) {
      sysPropDC5000 += File.separator;
    }

    FileInputStream streamProperties = null;
    final Properties appProps = new Properties();

    try {

      streamProperties = new FileInputStream(sysPropDC5000 + "ETLCServer.properties");
      appProps.load(streamProperties);

    } finally {
      if (streamProperties != null) {
        streamProperties.close();
      }
    }

    // Reading DB connection properties

    this.url = appProps.getProperty("ENGINE_DB_URL");
    log.config("Using DB @ " + this.url);

    this.userName = appProps.getProperty("ENGINE_DB_USERNAME");
    this.password = appProps.getProperty("ENGINE_DB_PASSWORD");
    this.dbDriverName = appProps.getProperty("ENGINE_DB_DRIVERNAME");

    this.serverHostName = appProps.getProperty("SCHEDULER_HOSTNAME", null);
    if (this.serverHostName == null) { // trying to determine hostname
      this.serverHostName = "localhost";

      try {
        this.serverHostName = InetAddress.getLocalHost().getHostName();
      } catch (java.net.UnknownHostException ex) {
        log.log(Level.FINE, "getHostName failed", ex);
      }
    }

    // Reading engine connection properties

    String engineServerHostName = appProps.getProperty("ENGINE_HOSTNAME", null);
    if (engineServerHostName == null) { // trying to determine hostname
      engineServerHostName = "localhost";

      try {
        engineServerHostName = InetAddress.getLocalHost().getHostName();
      } catch (java.net.UnknownHostException ex) {
        log.log(Level.FINE, "getHostName failed", ex);
      }
    }

    int engineServerPort = 1200;
    final String engineSporttmp = appProps.getProperty("ENGINE_PORT", null);
    try {
      engineServerPort = Integer.parseInt(engineSporttmp);
    } catch (NumberFormatException nfe) {
      log.config("Using default ENGINE_PORT 1200.");
    }

    String engineServerRefName = appProps.getProperty("ENGINE_REFNAME", null);
    if (engineServerRefName == null) {
      log.config("Using default ENGINE_REFNAME \"TransferEngine\"");
      engineServerRefName = "TransferEngine";
    }

    this.engineURL = "rmi://" + engineServerHostName + ":" + engineServerPort + "/" + engineServerRefName;

    log.config("Engine RMI Reference is: " + engineURL);

    this.serverPort = 1200;
    final String sporttmp = appProps.getProperty("SCHEDULER_PORT", "1200");
    try {
      this.serverPort = Integer.parseInt(sporttmp);
    } catch (NumberFormatException nfe) {
      log.config("Using default SCHEDULER_PORT 1200.");
    }

    this.serverRefName = appProps.getProperty("SCHEDULER_REFNAME", null);
    if (this.serverRefName == null) {
      log.config("Using default SCHEDULER_REFNAME \"Scheduler\"");
      this.serverRefName = "Scheduler";
    }

    final String pollIntervall = appProps.getProperty("SCHEDULER_POLL_INTERVALL");
    if (pollIntervall != null) {
      this.pollIntervall = Long.parseLong(pollIntervall);
    }
    log.config("Using pollInterval " + pollIntervall);

    final String penaltyWait = appProps.getProperty("SCHEDULER_PENALTY_WAIT", "30");
    if (penaltyWait != null) {
      this.penaltyWait = Integer.parseInt(penaltyWait);
    }
    log.config("Using penaltyWait " + penaltyWait);

    try {
      this.reConnectTime = new Long(appProps.getProperty("SERVER_RECONNECT_TIME")).longValue();
    } catch (Exception e) {
      this.reConnectTime = 60000;
      log.config("Using default reconnect time " + this.reConnectTime);
    }

    log.log(Level.CONFIG, "Properties loaded");

  }

  public static void main(final String args[]) {

    System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());

    try {
      final Scheduler dc = new Scheduler();
      dc.loadProperties();

      if (!dc.init()) {
        log.severe("Initialisation failed... exiting");
        System.exit(0);
      } else {
        log.info("Scheduler Ready");
        // activate scheduler
        dc.reload();
      }

    } catch (Exception e) {
      log.log(Level.SEVERE, "Initialization failed exceptionally", e);
      e.printStackTrace();
    }

  }

}
