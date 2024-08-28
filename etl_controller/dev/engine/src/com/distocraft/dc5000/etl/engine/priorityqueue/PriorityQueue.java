/*
 * Created on 25.1.2005
 *
 */
package com.distocraft.dc5000.etl.engine.priorityqueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.repository.cache.AggregationStatus;
import com.distocraft.dc5000.repository.cache.AggregationStatusCache;

/**
 * @author savinen
 * 
 */
public class PriorityQueue {

  private final static Logger log = Logger.getLogger("etlengine.priorityqueue");

  /* Contains EngineThreads */
  private Vector priorityQueue;

  /* Contains used IDs */
  private Vector IDPool;

  /* Sleep time between polls in priority queue */
  private long pollInterval = 10000;

  private long id = -1L;

  /* is the queue active or on hold */
  private boolean active = true;

  /* Maximum priority level */
  /* Priority values can be from 0 to maxPriorityLevel */
  private int maxPriorityLevel = 15;

  /* is queue closed, sets can't be added to a closed queue */
  private boolean isClosed = false;

  private int maxAmountOfLoadersForSameType;

  private Vector unremovableSetTypes = null;

  public PriorityQueue() {
    this.priorityQueue = new Vector();
    this.IDPool = new Vector();
    this.maxAmountOfLoadersForSameType = getMaxAmountOfLoadersForSameType();
    this.unremovableSetTypes = getUnremovableSetTypes();
  }

  public PriorityQueue(long priorityQueuePollIntervall, int maxPriorityLevel) {
    this.priorityQueue = new Vector();
    this.IDPool = new Vector();

    if (priorityQueuePollIntervall != 0) {
      this.pollInterval = priorityQueuePollIntervall;
    }

    if (maxPriorityLevel != 0) {
      this.maxPriorityLevel = maxPriorityLevel;
    }

    this.maxAmountOfLoadersForSameType = getMaxAmountOfLoadersForSameType();
    this.unremovableSetTypes = getUnremovableSetTypes();
  }

  /**
   * Reset priority queues poll intervall and max prioritylevel
   * 
   * @param priorityQueuePollIntervall
   * @param maxPriorityLevel
   */
  public void resetPriorityQueue(long priorityQueuePollIntervall, int maxPriorityLevel) {

    if (priorityQueuePollIntervall != 0)
      this.pollInterval = priorityQueuePollIntervall;

    if (maxPriorityLevel != 0)
      this.maxPriorityLevel = maxPriorityLevel;

  }

  /**
   * Adds a set to priority queue. set is added only is isClosed is false.
   */
  public synchronized void addSet(EngineThread set) {
    if (!this.isClosed) {

      Long id = popID();
      log.finer("Set \"" + set.getSetName() + "\" with ID \"" + id + "\" added to the priority queue");

      if (isSetAllowed(set)) {
        priorityQueue.add(set);
        set.setQueueID(id);
      } else {
        log.finer("Set \"" + set.getSetName()
            + "\" was not added to the priority queue because Loader for that type is already in there.");
      }

    } else {
      log.finer("Queue is closed. Set \"" + set.getSetName() + "\" is rejected.");
    }
  }

  /**
   * Removes a set from priority queue
   */
  public synchronized boolean RemoveSet(EngineThread set) {
    pushID(set.getQueueID());
    return priorityQueue.remove(set);
  }

  /**
   * Returns priority of a set in the queue
   * 
   * @return true if priority was changed false otherwise
   */
  public synchronized boolean ChangeSetsPriority(EngineThread set, long priority) {
    log.info("Set \"" + set.getName() + "\" changing priority to " + priority);

    if (priority <= maxPriorityLevel && priority > 0) {
      set.setSetPriority(new Long(priority));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the list of sets from the queue. Held up sets are not returned.
   */
  public synchronized Enumeration getAvailable() {
    if (!priorityQueue.isEmpty()) {

      Vector tmp = new Vector();

      for (int i = 0; i < priorityQueue.size(); i++) {
        EngineThread et = (EngineThread) priorityQueue.get(i);
        if (et.isActive())
          tmp.add(et);
      }
      return tmp.elements();
    } else {
      return null;
    }

  }

  /**
   * Returns the number of sets in queue. Held up sets are not returned.
   */
  public synchronized int getNumberOfAvailable() {

    if (!priorityQueue.isEmpty()) {

      Vector tmp = new Vector();

      for (int i = 0; i < priorityQueue.size(); i++) {
        EngineThread et = (EngineThread) priorityQueue.get(i);
        if (et.isActive())
          tmp.add(et);
      }
      return tmp.size();
    } else {
      return 0;
    }

  }

  /**
   * Returns the number of sets in queue.
   */
  public synchronized int getNumberOfSetsInQueue() {

    if (!priorityQueue.isEmpty()) {
      return priorityQueue.size();
    } else {
      return 0;
    }

  }

  /**
   * Returns sets in queue.
   * 
   * @return Iterator of sets in queue
   */
  public synchronized Enumeration getSetsInQueue() {

    if (!priorityQueue.isEmpty()) {
      return priorityQueue.elements();
    } else {
      return null;
    }

  }

  class priorityComparator implements java.util.Comparator {

    public int compare(Object o1, Object o2) {
      Long i1 = ((EngineThread) o1).getSetPriority();
      Long i2 = ((EngineThread) o2).getSetPriority();

      return i2.compareTo(i1);
    }
  }

  /**
   * Sorts the sets in priority queue by the priority. Accending order.
   */
  public synchronized void sort() {
    Collections.sort(priorityQueue, new priorityComparator());
  }

  /**
   * Checks if a set has been in the priority queue too long. First it increases
   * the priority level of the set. If the priority level is at maximum, warning
   * is thrown and set is removed from queue.
   * 
   * Held up set are not checked.
   */
  public synchronized void checkTimeLimit() {

    if (priorityQueue == null) {
      log.warning("CheckTimeLimit: No Queue");
      return;
    }

    if (!priorityQueue.isEmpty()) {

      for (int i = 0; i < priorityQueue.size(); i++) {
        EngineThread et = (EngineThread) priorityQueue.get(i);

        if (et == null) {
          log.warning("Null thread removed from PriorityQueue");
          priorityQueue.remove(i);
          i--;
          continue;
        }
        
        try {

          if (et.isActive()) {

            Date date = new Date();
            long time = System.currentTimeMillis();

            // if change date + time limit is less or equal than current time
            // time
            // to upgrade priority

            if (et.getQueueTimeLimit() == null) {
              log.info("Set dropped because of null queueTimeLimit");
              priorityQueue.remove(i);
              i--;
              continue;
            }

            if ((et.getChangeDate().getTime() + et.getQueueTimeLimit().longValue() * 60000) <= time) {
              et.setChangeDate(date);

              if (et.getSetPriority() == null) {
                log.info("Set dropped because of null setPriority");
                priorityQueue.remove(i);
                i--;
                continue;
              }

              // is priority allready at maximum allowed level
              if (et.getSetPriority().longValue() < this.maxPriorityLevel) {
                // No, so increase priority
                ChangeSetsPriority(et, et.getSetPriority().longValue() + 1);
                
              } else if (doWeRemove(et)) { // Check if removable.
                // Priority at maximum - removing set
                this.RemoveSet(et);
                if (et.setListener != null) {
                  et.setListener.dropped();
                }
                
                log.warning("Set \"" + et.getName() + "\" reached priority " + et.getSetPriority()
                    + " and dropped from queue");

                log.finer("Removed set was of type: "+et.getSetType());
                
                if (et.getSetType().equalsIgnoreCase("Aggregator")){
                	updateAggregatorStatus(et, "FAILEDDEPENDENCY");
                }
                
              }

            }

          }

        } catch (Exception e) {
          log.log(Level.WARNING,"CheckTimeLimit failed exceptionally for "+et,e);
        }

      }

    }

  }

  /**
   * Updates the status of an aggregator (of specified engine thread) in table Log_AggregationStatus.
   * @param et	The EngineThread for the aggregator.
   * @param newStatus	The status to which it will be set, e.g. "FAILEDDEPENDENCY".
   */
  private void updateAggregatorStatus(EngineThread et, String newStatus) {
	  
	  //Get the Set's scheduling info in the form of a Properties object
	  final String schedulingInfo = et.getSchedulingInfo();
	  log.finer("Sheduling info of "+et.getSetName()+"is: \n"+schedulingInfo);
	  Properties schedInfoProps = new Properties();
	  if (schedulingInfo != null && schedulingInfo.length() > 0) {
		  final ByteArrayInputStream bais = new ByteArrayInputStream(schedulingInfo.getBytes());
		  try {
			  schedInfoProps.load(bais);
			  bais.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
		  
	  }
	  
	  //Get the aggregation name and date from scheduling info
	  final String aggregation = schedInfoProps.getProperty("aggregation");	    
	  final String dataDate = schedInfoProps.getProperty("aggDate");
	  if (null==aggregation || null==dataDate){
		  log.finer("Could not get scheduling info of "+et.getSetName()+". It will not have status updated.");
		  return;
	  }
	  final SimpleDateFormat sdfshort = new SimpleDateFormat("yyyy-MM-dd");
	  String shortDate = null;
	  shortDate = sdfshort.format(new Date(Long.valueOf(dataDate)));
	  
	  //Look up aggregation in cache and get its AggregationStatus object
	  AggregationStatus aggSta = null;
	  try {
		  aggSta = AggregationStatusCache.getStatus(aggregation, Long.valueOf(dataDate));
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	  if (null == aggSta) {
		  log.finer("Aggregation "+aggregation+" for "+shortDate.toString()+" not found in AggregationStatusCache. No status update will be done.");
	  } else {
		  //Update status of set
		  log.finer("Putting Aggregation "+aggregation+" for "+shortDate.toString()+" to "+newStatus);
		  aggSta.STATUS = newStatus;
		  try {
			  AggregationStatusCache.setStatus(aggSta);
			  log.finer("Status of Aggregator set "+aggregation+" is changed to "+newStatus+". Set will re-run upon ReAggregation");
		  } catch (Exception e) {
			  log.finer("Exception when setting status of Aggregation "+aggregation+" for "+shortDate.toString());
		  }
	  }
	  
  }

  public synchronized long getPollIntervall() {
    return this.pollInterval;
  }

  public synchronized void setPollIntervall(long poll) {
    this.pollInterval = poll;
  }

  public synchronized boolean isActive() {
    return this.active;
  }

  /**
   * Holds the queue
   */
  public synchronized void hold() {
    this.active = false;
  }

  /**
   * Restarts a held up queue
   */
  public synchronized void restart() {
    this.active = true;
  }

  /**
   * Closes queue
   */
  public synchronized void closeQueue() {
    this.isClosed = true;
  }

  /**
   * Opens queue
   */
  public synchronized void openQueue() {
    this.isClosed = false;
  }

  public synchronized boolean isQueueClosed() {
    return this.isClosed;
  }

  /**
   * Inserts a shutdown marker set at the queue.
   * 
   * @param priority
   *          priority of the added set.
   */
  public synchronized void addShutdownMarker(String name, Long priority) {
    EngineThread set = new EngineThread(name, priority, Logger.getLogger("etlengine.Engine"), null);
    priorityQueue.add(set);
  }

  /**
   * Return id to the pool
   * 
   * @param id
   */
  private void pushID(Long id) {

    if (!this.IDPool.contains(id)) {
      this.IDPool.add(id);
    } else {
      // error error error
    }

  }

  /**
   * Returns id from pool
   */
  private Long popID() {

    // is there id in pool
    if (!IDPool.isEmpty()) {

      // fetch id from pool
      Long id = (Long) this.IDPool.firstElement();
      this.IDPool.remove(id);
      return id;
    } else {

      // create new id
      id++;

      return new Long(id);
    }

  }

  private synchronized boolean isSetAllowed(final EngineThread set) {
    boolean returnValue = true;
    if ("Loader".equalsIgnoreCase(set.getSetType())) {
      if (-1 < maxAmountOfLoadersForSameType) {
        returnValue = !doesMaxAmountExceed(set);
      }
    }
    return returnValue;
  }

  private synchronized boolean doesMaxAmountExceed(final EngineThread set) {
    int amount = 0;
    boolean isAtMaximum = false;
    final Enumeration enu = priorityQueue.elements();
    while (enu.hasMoreElements() && !isAtMaximum) {
      final EngineThread et = (EngineThread) enu.nextElement();
      if (et.getName().equals(set.getName())) {
        amount++;
      }
      isAtMaximum = maxAmountOfLoadersForSameType == amount;
    }
    return isAtMaximum;
  }

  private int getMaxAmountOfLoadersForSameType() {
    int amount = Integer.parseInt(StaticProperties.getProperty("PriorityQueue.maxAmountOfLoadersForSameTypeInQueue",
        "1"));
    if (-1 > amount || 0 == amount) {
      amount = -1;
    }
    return amount;
  }

  private Vector getUnremovableSetTypes() {
    final String setTypes = StaticProperties.getProperty("PriorityQueue.unremovableSetTypes", "");
    final String[] setTypesArray = setTypes.split(",");
    final Vector vec = new Vector();
    for (int i = 0; i < setTypesArray.length; i++) {
      vec.add(setTypesArray[i]);
    }
    return vec;
  }

  private synchronized boolean doWeRemove(final EngineThread set) {
    return !isUnremovableSetType(set.getSetType());
  }

  private synchronized boolean isUnremovableSetType(final String setType) {
    boolean returnValue = false;
    final Enumeration enu = this.unremovableSetTypes.elements();
    while (enu.hasMoreElements() && !returnValue) {
      final String unRemovableSetType = (String) enu.nextElement();
      returnValue = unRemovableSetType.equalsIgnoreCase(setType);
    }
    return returnValue;
  }
  
}
