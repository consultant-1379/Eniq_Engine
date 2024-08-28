package com.distocraft.dc5000.etl.engine.executionslots;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.main.EngineThread;

/**
 * @author savinen
 * 
 */
public class ExecutionSlot {

  /* contains list of approved set types */
  private Vector approvedSettypes = null;

  /* contains the running set */
  private EngineThread runningSet = null;

  /* true if slot is on hold */
  private boolean hold = false;

  /* Slots name */
  private String name = "";
  
  private int slotId = -1; 

  private boolean locked = false;

  // executions start time
  private Date startTime;

  // if true slot is removed from profile after execution..
  private boolean removeAfterExecution = false;

  public boolean worker = false;
  
  private final Logger log;

  /**
   * constructor
   * 
   * @param name
   */
  public ExecutionSlot(int id,String name) {

    this.name = name;
    this.slotId = id;
    log = Logger.getLogger("etlengine.Slot." + name);
  }

  /**
   * constructor
   * 
   * 
   * @param name
   * @param setName
   *          comma delimited string containing accepted settypes
   */
  public ExecutionSlot(int id,String name, String settypes) {
    this(id,name);

    setApprovedSettypes(settypes);
  }

  /**
   * constructor
   * 
   * 
   * @param name
   * @param setName
   *          comma delimited string containing accepted settypes
   */
  public ExecutionSlot(int id,String name, Vector settypes) {
    this(id,name);

    setApprovedSettypes(settypes);
  }
  
  public String toString() {
    return "ExecutionSlot: " + name;
  }

  /**
   * 
   * returns true if set is accepted in this slot.
   * 
   * checks if the thread exsists checks the type of the set checks for
   * doublicates in executions slots
   * 
   * @param setName
   * @return
   */
  public boolean isAccepted(final EngineThread exo) {

    /* if null we do not accept */
    if (exo == null) {
      return false;
    }

    String settype = "";

    if (exo.getSetType() != null) {

      settype = exo.getSetType().trim();
    }

    /* check if this set is shutdown set */
    if (exo.isShutdownSet()) {

      // this is shutdown set
      // settype must be equal to the slots name before we accept this set
      if (settype.equalsIgnoreCase(this.name)) {
        return true;
      }

      // is shutdown set and name does not match this is other slots set.
      return false;
    } else {
      /* check for accepted set types */
      /*
       * does the slot accept all types or is the set a message set (type is
       * equal to the slot name)
       */
      if (!approvedSettypes.contains("all")) {
        /* no */
        /* check if settype is in the list */
        boolean approvedSetTypeFound = false;
        final Iterator approvedSetTypesIterator = approvedSettypes.iterator();
        while(approvedSetTypesIterator.hasNext()) {
          final String currentApprovedSet = (String)approvedSetTypesIterator.next();
          if(settype.equalsIgnoreCase(currentApprovedSet)) {
            approvedSetTypeFound = true;
          }
        }
               
        return approvedSetTypeFound;
        
      }

      return true;
    }

  }

  /**
   * 
   * Method checks if slot is free or if slots set has ended then it is set
   * free. return true if this slot is free. False is returned is set is on
   * hold.
   * 
   * 
   * @return
   */
  public boolean isFree() {

    if (!this.hold) {

      if (this.runningSet == null) {
        return true;
      } else {
          if (!this.runningSet.isAlive()) {
            this.runningSet = null;
            return true;
          }
      }
    }

    return false;

  }

  /**
   * 
   * executes a set is this free slot.
   * 
   * 
   * @param set
   */
  public void execute(final EngineThread set) throws Exception {

    if (this.isFree() && !this.locked) {

      if (set.isShutdownSet()) {
        this.locked = true;
        log.fine("Locking execution slot");

      } else {

  
        if (set.isWorker()){

            this.worker = true;
          
        }

        log.finer("Executing set \"" + set.getSetName() + "\"");
        set.start();
        
        this.runningSet = set;
        this.startTime = new Date();

      }
    } else {
      if (this.locked) {
        log.finer("Slot is locked");
      } else {
        log.finer("Slot is busy");
      }
    }

  }

  /**
   * Stop executing a set in this slot.
   * 
   */
  public void stop() {
    // TODO
  }

  /**
   * 
   * 
   */
  public void removeAfterExecution(final boolean flag) {
    this.removeAfterExecution = flag;
  }

  /**
   * 
   * 
   */
  public boolean isRemovedAfterExecution() {
    return this.removeAfterExecution;
  }

  /**
   * Get Running set
   * 
   */
  public EngineThread getRunningSet() {
    return runningSet;
  }

  /**
   * holds a slot
   * 
   */
  public void hold() {
    log.info("Slot holded");
    this.hold = true;
  }

  /**
   * restart a slot
   * 
   */
  public void restart() {
    log.info("Slot restarted");
    this.hold = false;

  }

  /**
   * return true is slot is on hold
   * 
   */
  public boolean isOnHold() {
    return this.hold;

  }

  /**
   * return true is slot is locked
   * 
   */
  public boolean islocked() {
    return this.locked;

  }

  /**
   * 
   * return the name of the execution slot
   * 
   * @return
   */
  public String getName() {
    return this.name;

  }

  /**
   * 
   * inserts the name of the execution slot
   * 
   * @return
   */
  public void setName(final String name) {
    this.name = name;

  }

  /**
   * 
   * 
   * 
   * @return
   */
  public Date getStartTime() {
    return this.startTime;

  }

  /**
   * 
   * 
   * 
   * @return
   */
  public Vector getApprovedSettypes() {
    return approvedSettypes;

  }

  public int getSlotId(){
    return this.slotId;
  }
  
  /**
   * 
   * 
   * 
   * @return
   */
  public void setApprovedSettypes(final Vector vec) {
    approvedSettypes = vec;

  }

  /**
   * 
   * set Approved set types
   * 
   * @return
   */
  public void setApprovedSettypes(final String settypes) {

    if (approvedSettypes == null) {
      
      approvedSettypes = new Vector();
    
    } else {
      
      approvedSettypes.clear();
    }
    
    final StringTokenizer tokens = new StringTokenizer(settypes, ",");

    /* first token */
    if (tokens.countTokens() == 0){
      approvedSettypes.add(settypes);
    }

    while (tokens.hasMoreTokens()) {
      approvedSettypes.add(tokens.nextToken());
    }

  }

}
