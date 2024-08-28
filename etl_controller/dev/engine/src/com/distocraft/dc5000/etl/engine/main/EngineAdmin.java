package com.distocraft.dc5000.etl.engine.main;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ActivateSetInPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeAggregationStatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeProfileAndWaitCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeSetPriorityInPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.DisableSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.EnableSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.GetProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.GiveEngineCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.HoldPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.HoldSetInPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.InvalidArgumentsException;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.LockExecutionProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.LoggingStatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.PrintSlotInfoCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RefreshDBLookupsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RefreshTransformationsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadAggregationCacheCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadAlarmCacheCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadConfigCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadLoggingCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadProfilesCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RemoveSetFromPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RestartPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowActiveInterfaces;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowDisabledSetsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowSetsInExecutionSlotsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowSetsInQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShutdownForcefulCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShutdownSlowCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartAndWaitSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetInEngineCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StopOrShutdownFastCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UnlockExecutionProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UpdateThresholdLimit;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UpdateTransformationCommand;
import com.distocraft.dc5000.etl.engine.main.exceptions.InvalidSetParametersRemoteException;
import com.distocraft.dc5000.etl.engine.main.exceptions.NoSuchCommandException;
import com.distocraft.dc5000.etl.engine.system.SetListener;

/**
 * Program to control TransferEngine.
 */
public class EngineAdmin {

  private static Logger log = Logger.getLogger("etlengine.engine.EngineAdmin");

  private static Map<String, Class<? extends Command>> commandToClassMap = new HashMap<String, Class<? extends Command>>();

  private int serverPort;

  private String serverHostName;

  private String serverRefName = "TransferEngine";

  private StaticProperties sp;

  public EngineAdmin() {
    getProperties();
    sp = new StaticProperties();
  }

  public EngineAdmin(final String serverHostName, final int serverPort) {
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
    sp = new StaticProperties();
  }

  private static void showUsage() {
    System.out.println("Usage: engine -e command");
    System.out.println("  commands:");
    System.out.println("    start");
    System.out.println("    stop");
    System.out.println("    status");
    System.out.println("    shutdown_fast (=stop)");
    System.out.println("    shutdown_slow");
    System.out.println("    shutdown_forceful");
    System.out.println("    reloadConfig");
    System.out.println("    reloadAggregationCache");
    System.out.println("    reloadProfiles");
    System.out.println("    reloadAlarmCache");
    System.out.println("    loggingStatus");
    System.out.println("    startSet 'Techpack name' 'Set name' 'Schedule'");
    System.out.println("    changeProfile 'Profile name'");
    System.out.println("    holdPriorityQueue");
    System.out.println("    restartPriorityQueue");
    System.out.println("    showSetsInQueue");
    System.out.println("    showSetsInExecutionSlots");
    System.out.println("    removeSetFromPriorityQueue 'ID' ");
    System.out.println("    changeSetPriorityInPriorityQueue 'ID' 'New Priority' ");
    System.out.println("    activateSetInPriorityQueue 'ID' ");
    System.out.println("    holdSetInPriorityQueue 'ID' ");
    System.out.println("    currentProfile");
    System.out.println("    updatethresholdLimit 'value'");
    System.out.println();
    System.out.println("	The following commands are not supported and shall not be used unless directed by Ericsson.");
    System.out
        .println("    engine -e disableSet [<Techpack name> <set name>] | [<Interface name> <set name>] | <action order number>");
    System.out.println("    engine -e enableSet [<Techpack name> <set name>] | [<Interface name> <set name>]");
    System.out.println("    engine -e showDisabledSets");
    System.out.println("    engine -e showActiveInterfaces");

    System.exit(1);
  }

  public static void main(final String args[]) {
    try {
      System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());

      if (args.length < 1) {
        showUsage();
      } else {
        final String commandName = args[0];
        final Command command = createCommand(commandName, args);
        command.validateArguments();
        command.performCommand();
      }

    } catch (final java.rmi.UnmarshalException ume) {
      // Exception, cos connection breaks, when engine is shutdown
      System.exit(3);
    } catch (final java.rmi.ConnectException rme) {
      System.err.println("Connection to engine refused.");
      System.exit(2);
    } catch (final InvalidSetParametersRemoteException invalidSetParamsEx) {
      System.err.println(invalidSetParamsEx.getMessage());
      showUsage();
      System.exit(1);
    } catch (final NoSuchCommandException noSuchCommandEx) {
      System.err.println(noSuchCommandEx.getMessage());
      showUsage();
      System.exit(1);
    } catch (final InvalidArgumentsException invalidArgsEx) {
      System.err.println(invalidArgsEx.getMessage());
      System.exit(1);
    } catch (final RemoteException remoteEx) {
      System.err.println(remoteEx.getMessage());
      System.exit(1);
    } catch (final Exception e) {
      System.err.println(e.getMessage());
      log.log(java.util.logging.Level.FINE, "General Exception", e);
      if(e.getMessage().equals("Engine initialization has not been completed yet")){
    	  System.exit(4);
      }
      System.exit(1);
    }

    System.exit(0);
  }

  /**
   * 
   * @param commandName
   *          commandName that user has entered to CLI engine -e
   * @param args
   *          all arguments that user has entered to cLI engine -e must be at least of length 1
   * @return newly created Command to perform task
   * 
   * @throws IllegalArgumentException
   *           none of these should every really be thrown from here - they arise from using reflection to find and
   *           instantiate the class See the Command constructor - this performs no logic, so there should never be any
   *           exceptions coming from it
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * 
   * @throws NoSuchCommandException
   *           if the user has entered an invalid command type
   */
  static Command createCommand(final String commandName, final String[] args) throws IllegalArgumentException,
      InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException,
      NoSuchMethodException, NoSuchCommandException {
    final Class<? extends Command> classToUse = commandToClassMap.get(commandName);
    if (classToUse == null) {
      throw new NoSuchCommandException("Invalid command entered: " + commandName);
    }
    final Class<? extends String[]> class1 = args.getClass();
    final Constructor<? extends Command> constructor = classToUse.getConstructor(class1);
    final Object constructorArguments = args;
    return constructor.newInstance(constructorArguments);
  }

  /**
   * Prints the information about executing slots. Including Calculated memory consumption.
   */
  public void printSlotInfo() throws Exception {
    System.out.println("Connecting to engine process ...");
    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Getting the slot info ...");
    final List al = termi.slotInfo();
    final Iterator i = al.iterator();
    while (i.hasNext()) {
      final String t = (String) i.next();
      System.out.println(t);
    }

    System.out.println("Finished successfully");
  }

  public void disableTechpack(final String techpackName) throws Exception {
    log.info("Disabling Techpack: " + techpackName);

    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Disabling Techpack...");
    termi.disableTechpack(techpackName);
    System.out.println("Techpack Disabled");
    showDisabledSets();
  }

  public void disableSet(final String techpackName, final String setName) throws Exception {
    log.info("Disabling Set: " + techpackName + "." + setName);
    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Disabling Set...");
    termi.disableSet(techpackName, setName);
    System.out.println("Set Disabled");

    showDisabledSets();
  }

  public void disableAction(final String techpackName, final String setName, final Integer actionOrder)
      throws Exception {
    log.info("Disabling Action: " + techpackName + "." + setName + "." + actionOrder);
    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Disabling Action...");
    termi.disableAction(techpackName, setName, actionOrder);
    System.out.println("Action Disabled");
    showDisabledSets();
  }

  public void enableTechpack(final String techpackName) throws Exception {
    log.info("Enabling Techpack: " + techpackName);

    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Enabling Techpack...");
    termi.enableTechpack(techpackName);
    System.out.println("Techpack Enabled...");
    showDisabledSets();
  }

  public void enableSet(final String techpackName, final String setName) throws Exception {
    log.info("Enabling Techpack: " + techpackName);

    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Enabling Set...");
    termi.enableSet(techpackName, setName);
    System.out.println("Set Enabled...");
    showDisabledSets();
  }

  public void enableAction(final String techpackName, final String setName, final Integer actionNumber)
      throws Exception {
    log.info("Enabling Techpack: " + techpackName);

    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Enabling Action...");
    termi.enableAction(techpackName, setName, actionNumber);
    System.out.println("Action Enabled...");
    showDisabledSets();
  }

  public void showDisabledSets() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Getting Disabled Sets...");

    final ArrayList disabledSets = termi.showDisabledSets();

    if (disabledSets != null) {
      for (int i = 0; i < disabledSets.size(); i++) {
        System.out.println((String) disabledSets.get(i));
      }
    } else {
      System.out.println("There are no Disabled Sets");
    }
  }

  public void showActiveInterfaces() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();

    System.out.println("Getting active interfaces...");

    final ArrayList activeInterfaces = termi.showActiveInterfaces();

    if (activeInterfaces != null) {
      for (int i = 0; i < activeInterfaces.size(); i++) {
        System.out.println((String) activeInterfaces.get(i));
      }
    } else {
      System.out.println("There are no active interfaces.");
    }
  }

  public void status() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Getting status...");
    final List al = termi.status();
    final Iterator i = al.iterator();
    while (i.hasNext()) {
      final String t = (String) i.next();
      System.out.println(t);
    }

    System.out.println("Finished successfully");
  }

  /**
   * shutdown engine forcefully.
   */
  public void forceShutdown() throws Exception {
    log.info("Shutting down...");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Shutting down...");
    termi.forceShutdown();
    System.out.println("Shutdown requested successfully");
  }

  /**
   * shutdownd engine gently.
   */
  public void fastGracefulShutdown() throws Exception {
    log.info("Shutting down...");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Shutting down...");
    termi.fastGracefulShutdown();
    System.out.println("Shutdown requested successfully");
  }

  /**
   * shutdownd engine gently.
   */
  public void slowGracefulShutdown() throws Exception {
    log.info("Shutting down...");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Shutting down...");
    termi.slowGracefulShutdown();
    System.out.println("Shutdown requested successfully");
  }

  public boolean changeProfileWtext(final String profileName) throws Exception {
    System.out.println("Changing profile to: " + profileName);
    final boolean result = changeProfile(profileName);
    if (result) {
      System.out.println("Change profile requested successfully");
    } else {
      System.out.println("Could not activate profile (" + profileName + ") ");
    }

    return result;
  }

  public boolean changeProfileWtext(final String profileName, final String messageText) throws Exception {
    System.out.println("Changing profile to: " + profileName);
    final boolean result = changeProfile(profileName, messageText);
    if (result) {
      System.out.println("Change profile (" + profileName + ") requested successfully. Reason for change: "
          + messageText);
    } else {
      System.out.println("Could not activate profile (" + profileName + ") ");
    }

    return result;
  }

  public boolean changeProfileAndWaitWtext(final String profileName) throws Exception {
    System.out.println("Changing profile to: " + profileName);
    final boolean result = changeProfileAndWait(profileName);
    if (result) {
      System.out.println("Change profile requested successfully");
    } else {
      System.out.println("Could not activate profile (" + profileName + ") ");
    }

    return result;
  }

  public void addWorkerToQueue(final String name, final String type, final Object wobj) throws Exception {

    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.addWorkerToQueue(name, type, wobj);

  }

  public Set getAllRunningExecutionSlotWorkers() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.getAllRunningExecutionSlotWorkers();

  }

  public boolean changeProfile(final String profileName) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.setActiveExecutionProfile(profileName);
  }

  public String currentProfile() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.currentProfile();
  }

  public boolean getProfile(final String profileName) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.setActiveExecutionProfile(profileName);

  }

  public boolean changeProfile(final String profileName, final String messageText) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.setActiveExecutionProfile(profileName, messageText);

  }

  public boolean changeProfileAndWait(final String profileName) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.setAndWaitActiveExecutionProfile(profileName);

  }

  public void unLockExecutionprofile() throws Exception {

    log.info("Unlocking Execution Profile... ");
    getProperties();
    System.out.println("Unlocking Execution Profile");
    final ITransferEngineRMI termi = connect();
    termi.unLockExecutionprofile();
    System.out.println("Profile unlocked successfully");

  }

  public void refreshTransformations() throws Exception {
    log.info("Refreshing transformations");
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.reloadTransformations();
    System.out.println("Transformations refreshed succesfully.");

  }

  public void reloadLogging() throws Exception {
    log.info("Reloading logging levels");
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.reloadLogging();
    System.out.println("Reloaded logging levels succesfully.");

  }

  public void loggingStatus() throws Exception {
    log.info("Printing logging status");
    getProperties();
    final ITransferEngineRMI termi = connect();
    final List al = termi.loggingStatus();
    final Iterator i = al.iterator();
    while (i.hasNext()) {
      final String t = (String) i.next();
      System.out.println(t);
    }
    System.out.println("Logging status printed succesfully.");

  }

  public void updateTransformation(final String tpName) throws Exception {
    log.info("Updating transformation " + tpName);
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.updateTransformation(tpName);
    System.out.println("Transformation " + tpName + " updated succesfully.");

  }

  public void refreshDBLookups(final String tableName) throws Exception {
    log.info("refreshing database lookups");
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.reloadDBLookups(tableName);
    System.out.println("Lookups refreshed succesfully.");

  }

  public void lockExecutionprofile() throws Exception {
    log.info("Locking Execution Profile... ");
    getProperties();
    System.out.println("Locking Execution Profile");
    final ITransferEngineRMI termi = connect();
    termi.lockExecutionprofile();
    System.out.println("Profile locked successfully");

  }

  public Set getAllActiveSetTypesInExecutionProfiles() throws Exception {

    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.getAllActiveSetTypesInExecutionProfiles();

  }

  public void reloadProfiles() throws Exception {
    log.info("Reloading Execution Profiles... ");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Reload profiles");
    termi.reloadExecutionProfiles();
    System.out.println("Reload profiles requested successfully");
  }

  public void holdPriorityQueue() throws Exception {
    log.info("Holding a Priority Queue... ");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Hold priority queue");
    termi.holdPriorityQueue();
    System.out.println("Hold priority queue requested successfully");
  }

  public void restartPriorityQueue() throws Exception {
    log.info("Restarting a Priority Queue... ");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Restart priority queue");
    termi.restartPriorityQueue();
    System.out.println("Restart priority queue requested successfully");
  }

  public void startSet(final String ip, final String username, final String passwd, final String driver,
      final String coll, final String set) throws Exception {
    log.info("Starting a set: " + set);

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Start set");
    termi.execute(ip, username, passwd, driver, coll, set);
    System.out.println("Start set requested successfully");
  }

  public void startSet(final String coll, final String set, final String scheduleInfo) throws Exception {
    log.info("Starting a set: " + set);

    getProperties();
    final ITransferEngineRMI termi = connect();
    // System.out.println("Start set");
    termi.execute(coll, set, scheduleInfo);
    // System.out.println("Start set requested successfully");
  }

  public void startAndWaitSet(final String coll, final String set, final String scheduleInfo) throws Exception {
    log.info("Starting a set: " + set);

    getProperties();
    final ITransferEngineRMI termi = connect();
    // System.out.println("Start set");
    final String status = termi.executeAndWait(coll, set, scheduleInfo);

    log.info("Set execution finished with status: " + status);

    if (status.equals(SetListener.SUCCEEDED)) {
      System.exit(0);
    } else if (status.equals(SetListener.NOSET)) {
      System.exit(1);
    } else if (status.equals(SetListener.DROPPED)) {
      System.exit(2);
    } else {
      System.exit(69);
    }

  }

  public void startSets(final String techpacks, final String sets, final int times) throws Exception {

    for (int i = 0; i < times; i++) {
      final StringTokenizer collTokens = new StringTokenizer(techpacks, ",");
      final StringTokenizer setTokens = new StringTokenizer(sets, ",");

      if (collTokens.countTokens() == setTokens.countTokens()) {

        while (collTokens.hasMoreTokens()) {
          final String tp = collTokens.nextToken();
          final String set = setTokens.nextToken();

          try {

            log.info("Starting: " + tp + "/" + set);
            getProperties();
            final ITransferEngineRMI termi = connect();
            System.out.println("Starting (" + i + "): " + tp + "/" + set);
            termi.execute(tp, set, "");
            System.out.println("Start set requested successfully");

          } catch (final Exception e) {
            System.out.println("Error in: " + tp + "/" + set + "\n" + e);
            Thread.sleep(1000);
          }

        }
      }
    }

  }

  public void reloadProperties() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.reloadProperties();
  }

  public void reloadAggregationCache() throws Exception {
    log.info("Reload aggregation cache");
    getProperties();
    System.out.println("Reload aggregation cache");

    final ITransferEngineRMI termi = connect();
    termi.reloadAggregationCache();
    System.out.println("Reload aggregation cache requested successfully");
  }

  public void reloadPropertiesFromConfigTool() throws Exception {
    // Do not run getProperties() here.
    final ITransferEngineRMI termi = connect();
    termi.reloadProperties();
  }

  public void reloadAlarmConfigCache() throws Exception {
    log.info("Reload alarm cache");
    getProperties();
    System.out.println("Reload alarm cache");

    final ITransferEngineRMI termi = connect();
    termi.reloadAlarmConfigCache();
    System.out.println("Reload alarm cache requested successfully");
  }

  public boolean testConnection() throws Exception {

    try {

      final ITransferEngineRMI termi = connect();

    } catch (final Exception e) {

      return false;

    }

    return true;

  }

  public void giveEngineCommand(final String com) throws Exception {
    log.info("Engine command: " + com);
    System.out.println("Engine command: " + com);
    final ITransferEngineRMI termi = connect();
    termi.giveEngineCommand(com);
    System.out.println("Engine command given successfully");

  }

  public void reloadPropertiesWText() throws Exception {
    log.info("Reload properties");
    System.out.println("Reload properties");
    reloadProperties();
    System.out.println("Reload properties requested successfully");
  }

  public void removeSetFromPriorityQueue(final long ID) throws Exception {
    log.info("Reload properties");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Removes set (" + ID + ") from priority queue");
    if (termi.removeSetFromPriorityQueue(new Long(ID))) {
      System.out.println("Set (" + ID + ") Removed from priority queue successfully");
    } else {
      System.out.println("Set (" + ID + ") not removed from priority queue ");
    }
  }

  public void changeSetPriorityInPriorityQueue(final long ID, final long priority) throws Exception {
    log.info("Reload properties");

    getProperties();
    final ITransferEngineRMI termi = connect();
    if (termi.changeSetPriorityInPriorityQueue((new Long(ID)), priority)) {
      System.out.println("Set (" + ID + ")  priority changed to " + priority + " successfully");
    } else {
      System.out.println("Set (" + ID + ") priority not changed to " + priority);
    }
  }

  public void activateSetInPriorityQueue(final long ID) throws Exception {
    log.info("Reload properties");
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.activateSetInPriorityQueue((new Long(ID)));
    System.out.println("Set (" + ID + ") is activated ");

  }

  public void holdSetInPriorityQueue(final long ID) throws Exception {
    log.info("Reload properties");
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.holdSetInPriorityQueue((new Long(ID)));
    System.out.println("Set (" + ID + ") is set on hold ");
  }

  public boolean isSetRunning(final Long techpackID, final Long setID) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    return termi.isSetRunning(techpackID, setID);
  }

  public boolean isSetInQueue(final String setname) throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    final List l = termi.getQueuedSets();

    final Iterator i = l.iterator();
    while (i.hasNext()) {
      final Map setMap = (Map) i.next();
      if (setMap.get("techpackName").equals(setname)) {
        return true;
      }
    }
    return false;
  }

  public void showSetsInQueue() throws Exception {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Querying sets in queue");
    final List l = termi.getQueuedSets();

    System.out.println("QueueID TechPack SetName SetType Priority Version Created Active");
    System.out.println("-----");

    final Iterator i = l.iterator();
    while (i.hasNext()) {
      final Map setMap = (Map) i.next();

      final String id = setMap.get("ID").toString();
      final String tpa = (String) setMap.get("techpackName");
      final String sna = (String) setMap.get("setName");
      final String sty = (String) setMap.get("setType");
      final String pri = (String) setMap.get("priority");
      final String ver = (String) setMap.get("version");
      final String cDate = (String) setMap.get("creationDate");
      final String act = (String) setMap.get("active");

      System.out.println(id + " " + tpa + " " + sna + " " + sty + " " + pri + " " + ver + " " + cDate + " " + act);

    }
    System.out.println("-----");
    System.out.println("Finished successfully");
  }

  public void showSetsInExecutionSlots() throws Exception {

    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Querying sets in execution");
    final List l = termi.getRunningSets();

    System.out.println("TechPack SetName SetType StartTime Priority Version");
    System.out.println("-----");

    final Iterator i = l.iterator();
    while (i.hasNext()) {
      final Map setMap = (Map) i.next();

      final String tpa = (String) setMap.get("techpackName");
      final String sna = (String) setMap.get("setName");
      final String sty = (String) setMap.get("setType");
      final String sti = (String) setMap.get("startTime");
      final String pri = (String) setMap.get("priority");
      final String ver = (String) setMap.get("version");

      System.out.println(tpa + " " + sna + " " + sty + " " + sti + " " + pri + " " + ver);

    }
    System.out.println("-----");
    System.out.println("Finished successfully");
  }

  public void changeAggregationStatus(final String status, final String aggregation, final long datadate)
      throws Exception {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    getProperties();
    final ITransferEngineRMI termi = connect();
    System.out.println("Changing aggregation " + aggregation + " at " + sdf.format(new Date(datadate)) + " status to "
        + status);
    termi.changeAggregationStatus(status, aggregation, datadate);
    System.out.println("Finished successfully");
  }

  public void acticateScheduler() throws Exception {
    getProperties();
    final ITransferEngineRMI termi = connect();
    termi.activateScheduler();

  }

  public void setRMI(final String serverHostName, final int serverPort) {
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
  }
  
  
  
  
  
  

  /**
   * Reads configuration
   */
  protected void getProperties() {
    try {

      String sysPropDC5000 = System.getProperty("dc5000.config.directory");
      if (sysPropDC5000 == null) {
        sysPropDC5000 = "/eniq/sw/conf";
      }

      if (!sysPropDC5000.endsWith(File.separator)) {
        sysPropDC5000 += File.separator;
      }

      final FileInputStream streamProperties = new FileInputStream(sysPropDC5000 + "ETLCServer.properties");
      final Properties appProps = new Properties();
      appProps.load(streamProperties);

      if (this.serverHostName == null || this.serverHostName.equalsIgnoreCase("")) {

        this.serverHostName = appProps.getProperty("ENGINE_HOSTNAME", null);
        if (this.serverHostName == null) { // trying to determine hostname
          this.serverHostName = "localhost";

          try {
            this.serverHostName = InetAddress.getLocalHost().getHostName();
          } catch (final java.net.UnknownHostException ex) {
          }
        }
      }

      this.serverPort = 1200;
      final String sporttmp = appProps.getProperty("ENGINE_PORT", "1200");
      try {
        this.serverPort = Integer.parseInt(sporttmp);
      } catch (final NumberFormatException nfe) {
      }

      this.serverRefName = appProps.getProperty("ENGINE_REFNAME", "TransferEngine");

      streamProperties.close();

    } catch (final Exception e) {
      System.err.println("Cannot read configuration: " + e.getMessage());
    }
  }

  /**
   * Looks up the transfer engine
   */
  private ITransferEngineRMI connect() throws Exception {

    final String rmiURL = "//" + serverHostName + ":" + serverPort + "/" + serverRefName;

    // System.out.println("Connecting engine @ " + rmiURL);

    final ITransferEngineRMI termi = (ITransferEngineRMI) Naming.lookup(rmiURL);

    return termi;
  }

  static {
    commandToClassMap.put("activateSetInPriorityQueue", ActivateSetInPriorityQueueCommand.class);
    commandToClassMap.put("changeAggregationStatus", ChangeAggregationStatusCommand.class);
    commandToClassMap.put("changeProfile", ChangeProfileCommand.class);
    commandToClassMap.put("changeProfileAndWait", ChangeProfileAndWaitCommand.class);
    commandToClassMap.put("changeSetPriorityInPriorityQueue", ChangeSetPriorityInPriorityQueueCommand.class);
    commandToClassMap.put("disableSet", DisableSetCommand.class);
    commandToClassMap.put("enableSet", EnableSetCommand.class);
    commandToClassMap.put("giveEngineCommand", GiveEngineCommand.class);
    commandToClassMap.put("holdPriorityQueue", HoldPriorityQueueCommand.class);
    commandToClassMap.put("holdSetInPriorityQueue", HoldSetInPriorityQueueCommand.class);
    commandToClassMap.put("lockExecutionprofile", LockExecutionProfileCommand.class);
    commandToClassMap.put("loggingStatus", LoggingStatusCommand.class);
    commandToClassMap.put("printSlotInfo", PrintSlotInfoCommand.class);
    commandToClassMap.put("queue", ShowSetsInQueueCommand.class);
    commandToClassMap.put("refreshDBLookups", RefreshDBLookupsCommand.class);
    commandToClassMap.put("refreshTransformations", RefreshTransformationsCommand.class);
    commandToClassMap.put("reloadAggregationCache", ReloadAggregationCacheCommand.class);
    commandToClassMap.put("reloadConfig", ReloadConfigCommand.class);
    commandToClassMap.put("reloadLogging", ReloadLoggingCommand.class);
    commandToClassMap.put("reloadProfiles", ReloadProfilesCommand.class);
    commandToClassMap.put("reloadAlarmCache", ReloadAlarmCacheCommand.class);
    commandToClassMap.put("removeSetFromPriorityQueue", RemoveSetFromPriorityQueueCommand.class);
    commandToClassMap.put("restartPriorityQueue", RestartPriorityQueueCommand.class);
    commandToClassMap.put("showDisabledSets", ShowDisabledSetsCommand.class);
    commandToClassMap.put("showSetsInExecutionSlots", ShowSetsInExecutionSlotsCommand.class);
    commandToClassMap.put("showSetsInQueue", ShowSetsInQueueCommand.class);
    commandToClassMap.put("shutdown_slow", ShutdownSlowCommand.class);
    commandToClassMap.put("slots", ShowSetsInExecutionSlotsCommand.class);
    commandToClassMap.put("startAndWaitSet", StartAndWaitSetCommand.class);
    commandToClassMap.put("startSetInEngine", StartSetInEngineCommand.class);
    commandToClassMap.put("startSet", StartSetCommand.class);
    commandToClassMap.put("startSets", StartSetsCommand.class);
    commandToClassMap.put("status", StatusCommand.class);
    commandToClassMap.put("shutdown_fast", StopOrShutdownFastCommand.class);
    commandToClassMap.put("shutdown_forceful", ShutdownForcefulCommand.class);
    commandToClassMap.put("stop", StopOrShutdownFastCommand.class);
    commandToClassMap.put("unLockExecutionprofile", UnlockExecutionProfileCommand.class);
    commandToClassMap.put("updateTransformation", UpdateTransformationCommand.class);
    commandToClassMap.put("showActiveInterfaces", ShowActiveInterfaces.class);
    commandToClassMap.put("currentProfile", GetProfileCommand.class);
    commandToClassMap.put("updatethresholdLimit", UpdateThresholdLimit.class);
  }

  /**
   * Protected method to call static method in static properties
   * 
   * @throws Exception
   */
  protected void refreshStaticProperties() throws Exception {
    sp.reload();
  }

  /**
   * Protected method to call static method to set the threshold limit in static properties
   * 
   * @param numberOfMinutes - The threshold value 
   * @param name - The name of the property
   */
  protected void setStaticProperty(final int numberOfMinutes, String name) {
    sp.setProperty(name, Integer.toString(numberOfMinutes));
  }

  /**
   * This method updates the threshold property in staticProperties
   * @param numberOfMinutes - The time the user specifies.
   * @throws Exception
   */
  public void updateThresholdProperty(final int numberOfMinutes) throws Exception {

    String name = EngineConstants.THRESHOLD_NAME;
    refreshStaticProperties();
    setStaticProperty(numberOfMinutes, name);
    refreshStaticProperties();
  }


}
