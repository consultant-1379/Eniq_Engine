package com.distocraft.dc5000.etl.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;

/**
 * Program to control Scheduler
 */
public class SchedulerAdmin {

  private final Logger log = Logger.getLogger("SchedulerAdmin");

  private String serverHostName;

  private int serverPort;

  private String serverRefName = "Scheduler";

  public SchedulerAdmin() throws Exception {
    getProperties();
  }

  public SchedulerAdmin(final String serverHostName, final int serverPort) {
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
  }

  private static void showUsage() {
    System.out.println("Usage: scheduler COMMAND");
    System.out.println("  commands:");
    System.out.println("    start");
    System.out.println("    stop");
    System.out.println("    status");
    System.out.println("    activate");
    System.out.println("    hold");

    System.exit(1);
  }

  private static void showScheduleStatusChangeUsage() {
    System.out.println("Usage: scheduler -e ACTION TECHPACK SCHEDULE_NAME");
    System.out.println("  ACTION:");
    System.out.println("    disable_schedule");
    System.out.println("    enable_schedule");

    System.exit(1);
  }

  public static void main(final String args[]) {
    try {

      System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());

      if (args.length < 1) {
        showUsage();
      }

      if (args[0].equalsIgnoreCase("stop")) {
        final SchedulerAdmin admin = new SchedulerAdmin();
        admin.shutdown();
      } else if (args[0].equalsIgnoreCase("activate")) {
        final SchedulerAdmin admin = new SchedulerAdmin();
        admin.activate();
      } else if (args[0].equalsIgnoreCase("hold")) {
        final SchedulerAdmin admin = new SchedulerAdmin();
        admin.hold();
      } else if (args[0].equalsIgnoreCase("status")) {
        final SchedulerAdmin admin = new SchedulerAdmin();
        admin.status();
      } else if (args[0].equalsIgnoreCase("reloadLoggingProperties")) {
        final SchedulerAdmin admin = new SchedulerAdmin();
        admin.reloadLoggingProperties();
      } else if (args[0].equalsIgnoreCase("-e")) {

        // Extended command
        if (args.length < 2) {
          System.out.println("Unknown command \"" + args[0] + "\"");
          showUsage();
        } else {

          if (args[1].equalsIgnoreCase("enable_schedule") || args[1].equalsIgnoreCase("disable_schedule")) {
            if (args.length < 4) {
              System.out.println("Not enough parameters for command \"" + args[1] + "\"");
              showScheduleStatusChangeUsage();
            } else {
              final SchedulerAdmin admin = new SchedulerAdmin();
              admin.changeScheduleStatus(args[1], args[2], args[3]);
            }
          } else {
            System.out.println("Unknown command \"" + args[1] + "\"");
            showUsage();
          }
        }

      } else {
        System.out.println("Unknown command \"" + args[0] + "\"");
        showUsage();
      }

    } catch (java.rmi.UnmarshalException e) {
      // Exception, cos connection breaks, when engine is shutdown
      System.exit(3);
    } catch (java.rmi.ConnectException e) {
      System.err.println("Connection to scheduler failed. (Connection)");
      System.exit(2);
    } catch (java.rmi.NotBoundException e) {
      System.err.println("Connection to scheduler failed. (Not bound)");
      System.exit(4);
    } catch (Exception e) {
      System.err.println("\n");
      e.printStackTrace(System.err);
      System.exit(1);
    }

    System.exit(0);
  }



  private void getProperties() throws Exception {

    String sysPropDC5000 = System.getProperty("dc5000.config.directory");
    if (sysPropDC5000 == null) {
      sysPropDC5000 = "/dc/dc5000/conf/";
    }
    if (!sysPropDC5000.endsWith(File.separator)) {
      sysPropDC5000 += File.separator;
    }

    final FileInputStream streamProperties = new FileInputStream(sysPropDC5000 + "ETLCServer.properties");
    final Properties appProps = new Properties();
    appProps.load(streamProperties);

    this.serverHostName = appProps.getProperty("SCHEDULER_HOSTNAME", null);
    if (this.serverHostName == null) { // trying to determine hostname
      this.serverHostName = "localhost";

      try {
        this.serverHostName = InetAddress.getLocalHost().getHostName();
      } catch (java.net.UnknownHostException ex) {
      }

    }

    this.serverPort = 1200;
    final String sporttmp = appProps.getProperty("SCHEDULER_PORT", "1200");
    try {
      this.serverPort = Integer.parseInt(sporttmp);
    } catch (NumberFormatException nfe) {
    }

    this.serverRefName = appProps.getProperty("SCHEDULER_REFNAME", "Scheduler");

    streamProperties.close();

  }

  private void reloadLoggingProperties() throws Exception {

    final ISchedulerRMI scheduler = connect();

    System.out.println("Reloading Scheduler logging properties...");

    log.log(Level.INFO, "Reloading Scheduler logging properties...");

    scheduler.reloadLoggingProperties();

    System.out.println("Reload logging properties successfully requested");
  }

  private ISchedulerRMI connect() throws Exception {

    final String rmiURL = "//" + serverHostName + ":" + serverPort + "/" + serverRefName;

    // System.out.println("Connecting scheduler @ " + rmiURL);

    final ISchedulerRMI scheduler = (ISchedulerRMI) Naming.lookup(rmiURL);

    return scheduler;

  }
  
  private void shutdown() throws Exception {

    final ISchedulerRMI scheduler = connect();

    System.out.println("Shutting down...");

    log.log(Level.INFO, "Shutting down Scheduler...");

    scheduler.shutdown();

    System.out.println("Shutdown successfully requested");

  }

  public void setRMI(final String serverHostName, final int serverPort) {
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
  }

  public void activate_silent() throws Exception {

    final ISchedulerRMI scheduler = connect();
    scheduler.reload();

  }

  public boolean testConnection() throws Exception {

    try {

      //final ISchedulerRMI scheduler = 
      connect();

    } catch (Exception e) {

      return false;

    }

    return true;

  }

  private void activate() throws Exception {

    final ISchedulerRMI scheduler = connect();

    System.out.println("Activating Scheduler...");

    log.log(Level.INFO, "Starting Scheduler...");

    scheduler.reload();

    System.out.println("Start successfully requested");

  }

  private void hold() throws Exception {

    final ISchedulerRMI scheduler = connect();

    System.out.println("Holding Scheduler...");

    log.log(Level.INFO, "Holding Scheduler...");

    scheduler.hold();

    System.out.println("Hold successfully requested");

  }

  private void status() throws Exception {

    final ISchedulerRMI scheduler = connect();

    System.out.println("Getting status...");

    final List<String> al = scheduler.status();

    final Iterator<String> i = al.iterator();
    while (i.hasNext()) {
      final String t = i.next();
      System.out.println(t);
    }

    System.out.println("Finished successfully");
  }

  public void trigger(final String name) throws Exception {

    getProperties();

    final ISchedulerRMI scheduler = connect();

    log.log(Level.INFO, "Triggering a set (" + name + ") in scheduler...");

    scheduler.trigger(name);

  }

  @Deprecated
  public void trigger(final List<String> list) throws Exception {

    getProperties();
    final ISchedulerRMI scheduler = connect();
    scheduler.trigger(list);

  }

  public void trigger(final List<String> list, final Map<String, String> map) throws Exception {

    getProperties();
    final ISchedulerRMI scheduler = connect();
    scheduler.trigger(list, map);

  }

  public void trigger(final String name, final String context) throws Exception {

    getProperties();

    final ISchedulerRMI scheduler = connect();

    log.log(Level.INFO, "Triggering a set (" + name + ") with context in scheduler...");

    scheduler.trigger(name, context);

  }

  /**
   * This function disables/enables the specified schedule.
   * 
   * @param command
   *          Either "enable_schedule" or "disable_schedule".
   * @param techpack
   *          Name of the techpack that the schedule is related to.
   * @param schedule
   *          Name of the schedule to disable or enable.
   * @return Returns true if the status change was successfull.
   */
  private boolean changeScheduleStatus(final String command, final String techpack, final String schedule) {

    RockFactory etlrepRockFactory = null;

    try {
      final Map<String, String> databaseConnectionDetails = getDatabaseConnectionDetails();
      etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);

      final Meta_collection_sets whereMetaCollSet = new Meta_collection_sets(etlrepRockFactory);
      whereMetaCollSet.setCollection_set_name(techpack);
      whereMetaCollSet.setEnabled_flag("Y");

      final Meta_collection_setsFactory metaCollSetFact = new Meta_collection_setsFactory(etlrepRockFactory, whereMetaCollSet);

      final Vector<?> metaCollSets = metaCollSetFact.get();

      if (metaCollSets.size() < 1) {
        System.out.println("Could not find active techpack named " + techpack + ". Exiting...");
        return false;
      } else {
        final Meta_collection_sets targetMetaCollSet = (Meta_collection_sets) metaCollSets.get(0);

        final Meta_schedulings whereMetaSchedulings = new Meta_schedulings(etlrepRockFactory);
        whereMetaSchedulings.setCollection_set_id(targetMetaCollSet.getCollection_set_id());
        whereMetaSchedulings.setName(schedule);

        final Meta_schedulingsFactory metaSchedulingsFact = new Meta_schedulingsFactory(etlrepRockFactory,
            whereMetaSchedulings);

        final Vector<?> metaSchedulings = metaSchedulingsFact.get();

        if (metaSchedulings.size() < 1) {
          System.out.println("Could not find any schedule named " + schedule + " for techpack " + techpack
              + ". Exiting...");
          return false;
        } else if (metaSchedulings.size() > 1) {
          System.out.println("Found more than one schedule named " + schedule + " for techpack " + techpack
              + ". Exiting...");
          return false;
        } else {

          final Meta_schedulings targetMetaScheduling = (Meta_schedulings) metaSchedulings.get(0);

          if (command.equalsIgnoreCase("enable_schedule")) {
            if (targetMetaScheduling.getHold_flag().equalsIgnoreCase("N")) {
              System.out.println("Schedule " + schedule + " is already enabled.");
            } else {
              targetMetaScheduling.setHold_flag("N");
              targetMetaScheduling.updateDB();
              System.out.println("Schedule " + schedule + " enabled successfully. Please reload scheduler for the changes to take effect.");
            }
          } else if (command.equalsIgnoreCase("disable_schedule")) {
            if (targetMetaScheduling.getHold_flag().equalsIgnoreCase("Y")) {
              System.out.println("Schedule " + schedule + " is already disabled.");
            } else {
              targetMetaScheduling.setHold_flag("Y");
              targetMetaScheduling.updateDB();
              System.out.println("Schedule " + schedule + " disabled successfully. Please reload scheduler for the changes to take effect.");
            }

          } else {
            System.out.println("Unknown command " + command + ". Exiting...");
            return false;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Changing schedule status failed.");
    } finally {
      if (etlrepRockFactory != null) {
        try {
          etlrepRockFactory.getConnection().close();
        } catch (Exception e) {

        }
      }
    }

    return true;

  }

  /**
   * This function creates the rockfactory object to etlrep from the database
   * connection details read from ETLCServer.properties file.
   * 
   * @param databaseConnectionDetails
   * @return Returns the created RockFactory.
   */
  private RockFactory createEtlrepRockFactory(final Map<String, String> databaseConnectionDetails) throws Exception {
    RockFactory rockFactory = null;
    final String databaseUsername = databaseConnectionDetails.get("etlrepDatabaseUsername");
    final String databasePassword = databaseConnectionDetails.get("etlrepDatabasePassword");
    final String databaseUrl = databaseConnectionDetails.get("etlrepDatabaseUrl");
    final String databaseDriver = databaseConnectionDetails.get("etlrepDatabaseDriver");

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, "PreinstallCheck",
          true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Unable to initialize database connection.", e);
    }

    return rockFactory;
  }

  /**
   * This function reads the database connection details from the file
   * ${CONF_DIR}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database connection details.
   */
  private Map<String, String> getDatabaseConnectionDetails() throws Exception {
    final Map<String, String> databaseConnectionDetails = new HashMap<String, String>();

    String confDirPath = System.getProperty("CONF_DIR");

    if (confDirPath == null) {
      log.config("System property CONF_DIR not defined. Using default /eniq/sw/conf");
      confDirPath = "/eniq/sw/conf";
    }

    if (!confDirPath.endsWith(File.separator)) {
      confDirPath = confDirPath + File.separator;
    }
    final String propertiesFilepath = confDirPath + "ETLCServer.properties";

    try {
      final File targetFile = new File(propertiesFilepath);
      if (!targetFile.isFile() || !targetFile.canRead()) {
        throw new Exception("Could not read database properties. Please check that the file " + propertiesFilepath
            + " exists and it can be read.");
      }

      final FileReader fileReader = new FileReader(targetFile);
      try {
        final BufferedReader reader = new BufferedReader(fileReader);
        try {
          String line;
          while ((line = reader.readLine()) != null) {
            if (line.indexOf("ENGINE_DB_URL") != -1) {
              String databaseUrl = line.substring(line.indexOf("=") + 1, line.length());
              if (databaseUrl.charAt(0) == ' ') {
                // Drop the empty string after the equals (=) character.
                databaseUrl = databaseUrl.substring(1, databaseUrl.length());
              }
              databaseConnectionDetails.put("etlrepDatabaseUrl", databaseUrl);
            } else if (line.indexOf("ENGINE_DB_USERNAME") != -1) {
              String databaseUsername = line.substring(line.indexOf("=") + 1, line.length());
              if (databaseUsername.charAt(0) == ' ') {
                // Drop the empty string after the equals (=) character.
                databaseUsername = databaseUsername.substring(1, databaseUsername.length());
              }
              databaseConnectionDetails.put("etlrepDatabaseUsername", databaseUsername);
            } else if (line.indexOf("ENGINE_DB_PASSWORD") != -1) {
              String databasePassword = line.substring(line.indexOf("=") + 1, line.length());
              if (databasePassword.charAt(0) == ' ') {
                // Drop the empty string after the equals (=) character.
                databasePassword = databasePassword.substring(1, databasePassword.length());
              }
              databaseConnectionDetails.put("etlrepDatabasePassword", databasePassword);
            } else if (line.indexOf("ENGINE_DB_DRIVERNAME") != -1) {
              String databaseDriver = line.substring(line.indexOf("=") + 1, line.length());
              if (databaseDriver.charAt(0) == ' ') {
                // Drop the empty string after the equals (=) character.
                databaseDriver = databaseDriver.substring(1, databaseDriver.length());
              }
              databaseConnectionDetails.put("etlrepDatabaseDriver", databaseDriver);
            }
          }
        } finally {
          reader.close();
        }
      } finally {
        fileReader.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Connection to database failed.", e);
    }

    return databaseConnectionDetails;
  }

}
