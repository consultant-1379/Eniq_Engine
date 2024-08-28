/*
 * Created on 26.1.2005
 *
 */
package com.distocraft.dc5000.etl.engine.executionslots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.main.EngineThread;

/**
 * @author savinen
 * 
 */
public class ExecutionSlotProfile {

  /* profile name */
  private String name;

  /* profile ID */
  private String id;

  /* contains execution slots */
  final private Vector executionSlotList;

  /* is this profile active */
  private boolean active = false;

  private final Logger log;
  
  private HashMap maxConcurrentWorkers = null;
  
  private HashMap regexpsForWorkerLimitations = null;
  
  private int maxMemoryUsageMB = 0;
  
  /**
   * constructor
   * 
   */
  public ExecutionSlotProfile(String name, String ID) {
    executionSlotList = new Vector();
    this.name = name;
    this.id = ID;

    this.log = Logger.getLogger("etlengine.SlotProfile."+name);

    Share share = Share.instance();
    maxConcurrentWorkers = (HashMap) share.get("max_concurrent_workers");
    regexpsForWorkerLimitations = (HashMap) share.get("regexps_for_worker_limitations");
    if (share.contains("execution_profile_max_memory_usage_mb") || share.get("execution_profile_max_memory_usage_mb") != null){
      maxMemoryUsageMB = (Integer) share.get("execution_profile_max_memory_usage_mb");
    }
    else{
      maxMemoryUsageMB = 0;
    }
  }

  public void activate() {
    this.active = true;
  }

  /**
   * adds a execution slot to this profile.
   * 
   * @param exSlot
   */
  public void addExecutionSlot(final ExecutionSlot exSlot) {
    log.fine("Adding slot " + exSlot);
    executionSlotList.add(exSlot);
  }

  /**
   * remove a slot from this profile
   * 
   * @param exSlot
   * 
   */
  public void removeExecutionSlot(final ExecutionSlot exSlot) {
    log.fine("Removing slot " + exSlot);
    executionSlotList.remove(exSlot);
  }

  /**
   * searches a running set with given set name (collection_id) and set id
   * (collection_id) from active profiles slots. if found: set (EngineThread) is
   * returned else null is returned
   * 
   * @param setName
   * @param setID
   * @return EngineThread, or null
   */
  public EngineThread getRunningSet(final String setName, final long setID) {

    final Iterator iter = executionSlotList.iterator();
    while (iter.hasNext()) {

      final ExecutionSlot exSlot = (ExecutionSlot) iter.next();
      final EngineThread rSet = exSlot.getRunningSet();

      if (rSet != null && rSet.getSetID().longValue() == setID && rSet.getSetName().equals(setName)){
        return rSet;
      }
    }

    return null;
  }

  /**
   * remove a slot from this profile
   * 
   * @param name
   * @return
   */
  public ExecutionSlot removeExecutionSlot(final int nro) {
    final ExecutionSlot es = (ExecutionSlot) executionSlotList.remove(nro);
    log.fine("Removed slot " + es);
    return es;

  }

  /**
   * 
   * retrieves execution slot from this profile
   * 
   * @param name
   * @return
   */
  public ExecutionSlot getExecutionSlot(final int nro) {
    return (ExecutionSlot) executionSlotList.get(nro);

  }

  /**
   * 
   * retrieves all execution slots from this profile
   * 
   * @return
   */
  public Iterator getAllExecutionSlots() {
    return executionSlotList.iterator();
  }

  /**
   * 
   * retrieves all free (not running) execution slots from this profile
   * 
   * @return
   */
  public Iterator getAllFreeExecutionSlots() {

    final Vector tmp = new Vector();

    if (executionSlotList.isEmpty()) {
      log.finer("GetAllFreeSlots: Slot list is empty");
      return null;
    } else {
      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (ex.isFree()) {
          tmp.add(ex);
        }
      }

      return tmp.iterator();
    }
  }

  /**
   * 
   * retrieves first free (not running) execution slots from this profile.
   * Returns null if no free slots available.
   * 
   * @return
   */
  public ExecutionSlot getFirstFreeExecutionSlots() {

    if (executionSlotList.isEmpty()) {
      log.finer("getFirstFreeESlot: Slot list is empty");
    } else {
      
      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (ex.isFree()) {
          return ex;
        }

      }
      log.finer("getFirstFreeESlot: No free slots");

    }

    return null;
  }

  /**
   * 
   * retrieves number of free (not running) execution slots from this profile
   * 
   * @return
   */
  public int getNumberOfExecutionSlots() {

    return executionSlotList.size();

  }

  /**
   * 
   * retrieves number of free (not running) execution slots from this profile
   * 
   * @return
   */
  public int getNumberOfFreeExecutionSlots() {

    final Vector tmp = new Vector();

    if (executionSlotList.isEmpty()) {
      log.finer("getNumOfFreeESlot: Slot list is empty");
      return 0;
    } else {
      
      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (ex.isFree()) {
          tmp.add(ex);
        }

      }

      return tmp.size();
    }

  }

  /**
   * 
   * retrieves all running execution slots from this profile
   * 
   * @return
   */
  public Iterator getAllRunningExecutionSlots() {

    final Vector tmp = new Vector();

    if (executionSlotList.isEmpty()) {
      log.finer("getAllRunningESlot: Slot list is empty");
      return null;
    } else {

      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (!ex.isFree()) {
          tmp.add(ex);
        }

      }

      return tmp.iterator();
    }

  }

  /**
   * 
   * retrieves all running execution slots all set types.
   * 
   * @return
   */
  public Set getAllRunningExecutionSlotSetTypes() {

    final Set tmp = new HashSet();

    if (executionSlotList.isEmpty()) {
      log.finer("getAllRunnintESlotSetTypes: Slot list is empty");
    } else {
      
      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (!ex.isFree()){
          tmp.add(ex.getRunningSet().getSetType());
        }
      }
    }
    
    return tmp;
  }

  /**
   * 
   * 
   * 
   * @return
   */
  public Set getAllRunningExecutionSlotWorkers() {

    final Set tmp = new HashSet();

    if (executionSlotList.isEmpty()) {
      log.finer("getAllRunningESlotWorkers: Slot list is empty");
    } else {

      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        final ExecutionSlot ex = (ExecutionSlot) iter.next();
        if (!ex.isFree() && ex.getRunningSet().getWorkerObject() != null){
          tmp.add(ex.getRunningSet().getWorkerObject());
        }
      }

    }

    return tmp;
  }

  /**
   * return name of this execution profile
   * 
   * @return
   */
  public String name() {

    return this.name;

  }

  /**
   * return ID of this execution profile
   * 
   * @return
   */
  public String ID() {

    return this.id;

  }

  /**
   * return ID of this execution profile
   * 
   * @return
   */
  public void setID(final String id) {

    this.id = id;

  }

  /**
   * return ID of this execution profile
   * 
   * @return
   */
  public void setName(final String name) {

    this.name = name;

  }

  /**
   * 
   * 
   * @return
   */
  public void deactivate() {

    this.active = false;

  }

  /**
   * 
   * 
   * @return
   */
  public boolean IsActivate() {

    return this.active;

  }

  /**
   * return true if given set is not one of the executed sets in the execution
   * slots
   * 
   * @return
   */
  public boolean notInExecution(final EngineThread set) {

    if (!executionSlotList.isEmpty()) {

      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        /* Get the runnig set */
        final EngineThread runningThread = ((ExecutionSlot) iter.next()).getRunningSet();

        if (runningThread != null) {

          if ((runningThread.getSetName().equals(set.getSetName()) && (runningThread.getSetID().equals(set.getSetID())))) {
            return false;
          }

        }
      }
    }

    return true;

  }

  /**
   * return true if one of the given sets tables are allready in execution. if
   * given sets table list is empty, false is allways returned.
   * 
   * @return
   */
  public boolean checkTable(final EngineThread set) {

    if (!executionSlotList.isEmpty()) {

      final Iterator iter = executionSlotList.iterator();

      while (iter.hasNext()) {
        /* Get the runnig set */
        final EngineThread runningThread = ((ExecutionSlot) iter.next()).getRunningSet();

        if (runningThread != null) {

          // if table is empty return false
          if (runningThread.getSetTables() != null && set.getSetTables() != null && !set.getSetTables().isEmpty()) {

            final Iterator iTables = runningThread.getSetTables().iterator();
            // check all tables in executionslot against tables in set.
            while (iTables.hasNext()) {
              if (set.getSetTables().contains(iTables.next())){
                return true;
              }
            }

          }
        }
      }
    }

    return false;

  }

  /**
   * 
   * return true if all slots are locked or free
   * 
   * @return
   */
  public boolean areAllSlotsLockedOrFree() {
    int lock = 0;

    // loop all execution slots
    for (int i = 0; i < executionSlotList.size(); i++) {
      // increase lock if set is locked or free...
      if (((ExecutionSlot) this.executionSlotList.get(i)).islocked()
          || ((ExecutionSlot) this.executionSlotList.get(i)).isFree()){
        lock++;
      }
    }

    // if locked sets is equal to the number of sets.. all are locked or free...
    if (lock == executionSlotList.size()) {
      return true;
    }

    return false;
  }

  /**
   * 
   * Cleans profile from slots marked for removal (isRemovedAfterExecution)
   * after execution has ended (isFree).
   * 
   * 
   */
  public void cleanProfile() {

    // loop all execution slots
    for (int i = 0; i < executionSlotList.size(); i++) {
      final ExecutionSlot exs = (ExecutionSlot) this.executionSlotList.get(i);
      if (exs.isRemovedAfterExecution() && exs.isFree()) {
        log.fine("Removing execution slot from active profile");
        this.executionSlotList.remove(i);
      }

    }

  }

  /**
   * 
   * Checks that the profile does not contain any slots marked for removal
   * (isRemovedAfterExecution).
   * 
   * 
   */
  public boolean isProfileClean() {

    // loop all execution slots
    for (int i = 0; i < executionSlotList.size(); i++) {
      final ExecutionSlot exs = (ExecutionSlot) this.executionSlotList.get(i);
      // return false if we found one slot marked for ready for removal.
      if (exs.isRemovedAfterExecution()){
        return false;
      }
    }

    return true;
  }
  
  /**
   * 
   * Checks that does the maximum amount of concurrent workers exceed.   
   * Returns true if exceeds.
   */
  public boolean hasMaxConcurrentWorkersExceeded(final EngineThread set) {
	  boolean hasExceeded = false;
	  boolean isConfigured = true;
	  WorkerLimits workerLimits = getWorkerLimitsBySetName(set);
	  int amountOfConcurrentWorkersToBeLimited = 0;
	  
	  if(maxConcurrentWorkers.keySet().isEmpty()) {
		  isConfigured = false;
		  log.fine("No configuration for maxConcurrentWorkers.");
	  }
	  
	  if(regexpsForWorkerLimitations.keySet().isEmpty()) {
		  isConfigured = false;
		  log.fine("No configuration for regexpsForWorkerLimitations.");
	  }
	  
	  if (!executionSlotList.isEmpty() && null != workerLimits && isConfigured) {
		  
		  log.fine("Starting to limit workers of set: " + set.getSetName());
	      final Iterator iter = executionSlotList.iterator();

	      /* Get the running sets */
	      while (iter.hasNext()) {
	        final EngineThread runningSet = ((ExecutionSlot) iter.next()).getRunningSet();

	        if (runningSet != null) {
	        	// does the set name match with regexp
	        	if(setNameMatchesWithRegexp(runningSet.getSetName(), workerLimits.regexp)){
	        		amountOfConcurrentWorkersToBeLimited++;
	        	}
	        }
	      }
	      
	      hasExceeded = workerLimits.maximumAmount <= amountOfConcurrentWorkersToBeLimited;
	      log.fine("Set: " + set.getSetName() + " hasExceeded=" + hasExceeded);
	      
	   } else {
		   log.fine("No configuration to limit number of concurrent workers.");
	   }
	  
	  return hasExceeded;
  }
  
  /**
   * 
   * Gets worker limitations by set name (if the set name matches configured regexp).  
   * With found regexp's key the maximum amount of workers are got also. 
   * Returns null if there's no regexp or amount configuration to limit the set.
   */
  private WorkerLimits getWorkerLimitsBySetName(final EngineThread set) {
	  String regexp = null;
	  String key = null;
	  Integer maximumAmountOfWorkers = null;
	  boolean regexpFound = false;
	  final Iterator iter = regexpsForWorkerLimitations.keySet().iterator();
	  
	  while(iter.hasNext() && !regexpFound){
		  key = (String) iter.next();
		  regexp = (String) regexpsForWorkerLimitations.get(key);
		  regexpFound = setNameMatchesWithRegexp(set.getSetName(), regexp);
	  }
	  
	  WorkerLimits workerLimits = null;
	  
	  if(regexpFound){
		  try {
			  maximumAmountOfWorkers = (Integer) maxConcurrentWorkers.get(key);
			  workerLimits = new WorkerLimits(maximumAmountOfWorkers, regexp);
			  log.fine("Using regexp: " + regexp + " to limit max amount of concurrent workers: " + maximumAmountOfWorkers + ". Possible limitation for set:" + set.getSetName());
		  } catch (Exception e) {
			  log.log(Level.WARNING, "No maximum amount of workers calculated for regexp configuration. Key: "+ key + " regexp: " + regexp, e);
		  }
	  } else {
		  log.fine("No limitation configuration found for set: " + set.getSetName());
	  }
	  
	  return workerLimits;
  }
  
  /**
   * 
   * Checks that does the set name match with given regular expression.   
   * 
   */
  private boolean setNameMatchesWithRegexp(final String setName, final String regexp) {
	  boolean returnValue = false;
	  try {
		  final Pattern pat = Pattern.compile(regexp);
		  final Matcher mat = pat.matcher(setName);
		  returnValue = mat.find();
	  } catch (Exception e) {
		  log.log(Level.WARNING,"Set limitation regexp matching failed.", e);
	  }
	  return returnValue;
  }
  
  private class WorkerLimits {
	  public Integer maximumAmount = null;
	  public String regexp = null;
	  
	  public WorkerLimits (final Integer maximumAmount, final String regexp) {
		  this.maximumAmount = maximumAmount;
		  this.regexp = regexp;
	  }
  }
  
  /**
   * 
   * Checks that has the maximum memory usage exceeded.   
   * Returns true if exceeds.
   */
  public boolean hasMaxMemoryUsageExceeded(final EngineThread set) {
	  boolean hasExceeded = false;
	  int totalMemoryUsageMB = 0;
	  int setsMemoryNeed = set.getMemoryConsumptionMB();
	  ExecutionMemoryConsumption emc = ExecutionMemoryConsumption.instance();
	  
	  if (!executionSlotList.isEmpty() && 0 < setsMemoryNeed) {
		  
		  log.finest("Starting to calculate total memory usage in execution slots for set: " + set.getSetName());

	      totalMemoryUsageMB = emc.calculate();
	      
	      hasExceeded = (totalMemoryUsageMB + setsMemoryNeed) >= maxMemoryUsageMB;
	      log.finest("To run set: " + set.getSetName() + " maxMemoryUsageHasExceeded=" + hasExceeded);
	      
	  } 
	  
	  return hasExceeded;
  }
  
	/**
	 * Adds information about execution to given list
	 * 
	 * @param info
	 */
	public void getSlotPrintout(List<String> info) {

		if (executionSlotList.isEmpty()) {
			info.add("Execution slots are empty");
			return;
		}

		ExecutionMemoryConsumption emc = ExecutionMemoryConsumption.instance();
		int totalMemoryUsageMB = emc.calculate();

		info.add("Currently total calculated memory usage in MB: "
				+ totalMemoryUsageMB);

		info.add("Profile name: " + this.name + ".");
		info.add("------------Slots start------------");

		Iterator iter = getAllRunningExecutionSlots();
		while (iter.hasNext()) {
			final ExecutionSlot ex = (ExecutionSlot) iter.next();

			StringBuilder sb = new StringBuilder();
			sb.append("-- Name,ID:");
			sb.append(ex.getName());
			sb.append(",");
			sb.append(ex.getSlotId() + "--------------------");
			info.add(sb.toString());

			sb = new StringBuilder();
			EngineThread et = ex.getRunningSet();
			String setName = et.getSetName();
			int mcMB = et.getMemoryConsumptionMB();
			sb.append("Setname: " + setName);
			sb.append(" Calculated consumed memory: " + mcMB + " MB");
			info.add(sb.toString());

			String currentAct = et.getCurrentAction();
			sb = new StringBuilder();
			sb.append("Current action: " + currentAct);
			sb.append(" Calculated consumed memory: " + mcMB + " MB");
			info.add(sb.toString());

		}
		info.add("------------Slots end------------");
	}

  
  
}
