package com.distocraft.dc5000.etl.engine.main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.distocraft.dc5000.etl.engine.system.SetStatusTO;

import ssc.rockfactory.RockFactory;

public interface ITransferEngineRMI extends Remote {

  /**
   * Executes the initialized collection set / coleection
   * 
   * @param rockFact
   *          The database connection
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   * @exception RemoteException
   */
  public void execute(RockFactory rockFact, String collectionSetName, String collectionName) throws RemoteException;

  /**
   * Executes the initialized collection set / coleection
   * 
   * @param url
   *          Database url
   * @param userName
   *          Database user
   * @param password
   *          Database users password
   * @param dbDriverName
   *          Database driver
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   * @exception RemoteException
   */
  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName) throws RemoteException;

  /**
   * Executes the initialized collection set / coleection
   * 
   * @param url
   *          Database url
   * @param userName
   *          Database user
   * @param password
   *          Database users password
   * @param dbDriverName
   *          Database driver
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   * @param SchedulerInfo
   *          Information from the scheduler
   * @exception RemoteException
   */
  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, String ScheduleInfo) throws RemoteException;

  /**
   * Executes the initialized collection set / collection
   * 
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   * @param SchedulerInfo
   *          Information from the scheduler
   * @exception RemoteException
   */
  public void execute(String collectionSetName, String collectionName, String ScheduleInfo) throws RemoteException;

  /**
   * 
   * @param collectionSetName
   * @param collectionName
   * @param ScheduleInfo
   * @return
   * @throws RemoteException
   */
  public String executeAndWait(String collectionSetName, String collectionName, String ScheduleInfo)
      throws RemoteException;
  
  /**
   * Executes the initialized collection set / collection, and creates a 
   * listener object to observe the execution. The listener id, returned by the
   * method, can be used to examine the execution's status and events with the
   * getStatusEventsWithId method.
   * 
   * @param collectionSetName
   * @param collectionName
   * @param ScheduleInfo
   * @return Set listener id
   * @throws RemoteException
   */
  public String executeWithSetListener(String collectionSetName, String collectionName, String ScheduleInfo) 
    throws RemoteException;
  
  
  /**
   * Executes the initialized collection set / collection, and creates a 
   * listener object to observe the execution. 
   * 
   * @param collectionSetName
   * @param collectionName
   * @param ScheduleInfo
   * @param properties
   * @return SetStatusTO
   * @throws RemoteException
   */
  public SetStatusTO executeSetViaSetManager(String collectionSetName,
      String collectionName, String ScheduleInfo, java.util.Properties props)
      throws RemoteException;
  
  public SetStatusTO getSetStatusViaSetManager(String collectionSetName, String collectionName, int beginIndex, int count)
    throws RemoteException;
  
  
  /**
   * Retrieves the status information and events from a set listener object. The
   * listener is identified by the statusListenerId parameter. 
   * 
   * @param statusListenerId Status listener's id, returned by the 
   * 						 executeWithSetListener method.
   * @param beginIndex		 The index of the first retrieved status event
   * @param count			 The number of status events to be retrieved
   * @return				 A set status transfer object, containing the 
   * 						 observed state and the status events. 
   * @throws RemoteException
   */
  public SetStatusTO getStatusEventsWithId(String statusListenerId, int beginIndex, int count) 
  throws RemoteException;
  
  /**
   * Writes the SQL Loader ctl file contents
   * 
   * @param String
   *          fileContents The ctl -file description.
   * @param String
   *          fileName The ctl file name.
   * @exception RemoteException
   */
  public void writeSQLLoadFile(String fileContents, String fileName) throws RemoteException;

  /**
   * Returns all available plugin names
   * 
   * @return String[] plugin names
   * @throws RemoteException
   */
  public String[] getPluginNames() throws RemoteException;

  /**
   * Returns the specified plugins methods
   * 
   * @param String
   *          pluginName the plugin that the methods are fetched from
   * @param boolean
   *          isGetSetMethods if true, only set method names are returned
   * @param boolean
   *          isGetGetMethods if true, only get method names are returned
   * @return String[] method names
   * @throws RemoteException
   */
  public String[] getPluginMethods(String pluginName, boolean isGetSetMethods, boolean isGetGetMethods)
      throws RemoteException;

  /**
   * Returns the constructor parameters separated with ,
   * 
   * @param String
   *          pluginName The plugin to load
   * @return String
   */
  public String getPluginConstructorParameters(String pluginName) throws RemoteException;

  // SS
  /**
   * Returns the constructor parameter info
   * 
   * @param String
   *          pluginName The plugin to load
   * @return String
   */
  public String getPluginConstructorParameterInfo(String pluginName) throws RemoteException;

  /**
   * Returns the method parameters separated with ,
   * 
   * @param String
   *          pluginName The plugin to load
   * @param String
   *          methodName The method that hold the parameters
   * @return String
   */
  public String getPluginMethodParameters(String pluginName, String methodName) throws RemoteException;

  /**
   * Method to query engine status
   */
  public List<String> status() throws RemoteException, Exception;

  /**
   * Method to query currentProfile
   */
  public String currentProfile() throws RemoteException;
    
  /**
   * Method to shutdown engine
   */
  public void fastGracefulShutdown() throws RemoteException;

  /**
   * Method to shutdown engine
   */
  public void slowGracefulShutdown() throws RemoteException;

  /**
   * Method to shutdown engine
   */
  public void forceShutdown() throws RemoteException;

  /**
   * Method to change active profile
   */
  public boolean setActiveExecutionProfile(String profileName) throws RemoteException;
  
  /**
   * Method to change active profile with message text
   */
  public boolean setActiveExecutionProfile(String profileName, String messageText) throws RemoteException;

  /**
   * Method to change active profile
   */
  public boolean setAndWaitActiveExecutionProfile(String profileName) throws RemoteException;

  /**
   * @throws RemoteException
   */
  public void reloadExecutionProfiles() throws RemoteException;

  /**
   * Hold a Execution slot.
   */

  public void holdExecutionSlot(int ExecutionSlotNumber) throws RemoteException;

  /**
   * Restart a Execution slot.
   */
  public void restartExecutionSlot(int ExecutionSlotNumber) throws RemoteException;

  public boolean removeSetFromPriorityQueue(Long ID) throws RemoteException;

  public boolean changeSetPriorityInPriorityQueue(Long ID, long priority) throws RemoteException;

  public void holdPriorityQueue() throws RemoteException;

  public void restartPriorityQueue() throws RemoteException;

  public void reloadProperties() throws RemoteException;
  
  public void reloadAggregationCache() throws RemoteException;
  
  public void reloadAlarmConfigCache() throws RemoteException;
  
  public List loggingStatus() throws RemoteException;

  public boolean isSetRunning(Long techpackID, Long setID) throws RemoteException;

  public void activateSetInPriorityQueue(Long ID) throws RemoteException;

  public void holdSetInPriorityQueue(Long ID) throws RemoteException;

  /**
   * Returns the failed sets in the ETL engine.
   * 
   * @return
   */
  public List getFailedSets() throws java.rmi.RemoteException;

  /**
   * Returns the queued sets in the ETL engine.
   * 
   * @return
   */
  public List getQueuedSets() throws java.rmi.RemoteException;

  /**
   * Returns the executed sets in the ETL engine.
   * 
   * @return
   */
  public List getExecutedSets() throws java.rmi.RemoteException;

  /**
   * Returns the running sets in the ETL engine.
   * 
   * @return
   */
  public List getRunningSets() throws java.rmi.RemoteException;

  public void reaggregate(String aggregation, long datadate) throws RemoteException;

  public void changeAggregationStatus(String status, String aggregation, long datadate) throws RemoteException;

  public Set getAllActiveSetTypesInExecutionProfiles() throws RemoteException;

  public void lockExecutionprofile() throws RemoteException;

  public void unLockExecutionprofile() throws RemoteException;

  public void activateScheduler() throws RemoteException;

  public Set getAllRunningExecutionSlotWorkers() throws RemoteException;

  public void addWorkerToQueue(String name, String type, Object wobj) throws RemoteException;

  public void reloadDBLookups(String tableName) throws RemoteException;

  public void reloadTransformations() throws RemoteException;
  
  public void updateTransformation(String tpName) throws RemoteException;
  
  public boolean isInitialized()throws RemoteException;
  
  public void reloadLogging() throws RemoteException;

  public void giveEngineCommand(String com)throws RemoteException;

  public List<String> slotInfo() throws RemoteException;
  
  public void disableTechpack (String techpackName)throws RemoteException;
  
  public void disableSet(String techpackName, String setName) throws RemoteException;
  
  public void disableAction (String techpackName, String setName, Integer actionOrder) throws RemoteException;
  
  public void enableTechpack (String techpackName) throws RemoteException;
  
  public void enableSet (String techpackName, String setName) throws RemoteException;
 
  public void enableAction(String techpackName, String setName, Integer actionNumber) throws RemoteException;
  
  public ArrayList<String> showDisabledSets() throws RemoteException;
  
  public ArrayList<String> showActiveInterfaces() throws RemoteException;
  
}
