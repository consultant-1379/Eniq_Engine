
/*
 * Created on 26.1.2005
 *
 */
package com.distocraft.dc5000.etl.engine.executionslots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_execution_slot;
import com.distocraft.dc5000.etl.rock.Meta_execution_slotFactory;
import com.distocraft.dc5000.etl.rock.Meta_execution_slot_profile;
import com.distocraft.dc5000.etl.rock.Meta_execution_slot_profileFactory;

/**
 * @author savinen
 * 
 */
public class ExecutionSlotProfileHandler {

	private int numberOfAdapterSlots    = 0;
	private int numberOfAggregatorSlots = 0;
	
  /* contains ExecutionSlotProfile Objects */
  private Map exeSlotsProfileMap;

  // active "extra" profile
  private ExecutionSlotProfile activeSlotProfile;

  // private RockFactory rockFact;
  private boolean locked = false;

  private String url;

  private String userName;

  private String password;

  private String dbDriverName;

  private final Logger log;


  /**
   * constructor
   * 
   */
  public ExecutionSlotProfileHandler(String url, String userName, String password, String dbDriverName)
      throws Exception {

    this.url = url;
    this.userName = userName;
    this.password = password;
    this.dbDriverName = dbDriverName;

    log = Logger.getLogger("etlengine.SlotProfileHandler");

    try {

      exeSlotsProfileMap = new HashMap();
      log.finer("Init: creating rockEngine");

      activeSlotProfile = createActiveProfile(1);
      this.readProfiles();

      log.finest("Init: execution slot profiles created");

    } catch (Exception e) {
      log.log(Level.WARNING, "Error while initializing ExecutionSlotProfileList", e);
    }

  }

  /**
   * constructor
   * 
   * creates number of default slots..
   * 
   */
  public ExecutionSlotProfileHandler(int nroOfSlots) throws Exception {

    log = Logger.getLogger("etlengine.SlotProfileHandler");

    exeSlotsProfileMap = new HashMap();

    activeSlotProfile = createActiveProfile(nroOfSlots);

    log.warning("Default Execution slot profile created");

  }

  /**
   * 
   * 
   */
  public void resetProfiles() throws Exception {

    if (this.locked) 
    {
      log.finest("profiles locked");
    } 
    
    else 
    {
      exeSlotsProfileMap = new HashMap();
      this.readProfiles();
      log.finest("Execution slot profiles reseted");
      
    }

  }

  /**
   * 
   * creates default execution profile that contains number of execution slots.
   * All of created slots accept all possible set types
   * 
   * 
   * @param nro
   * @throws Exception
   */
  public ExecutionSlotProfile createActiveProfile(final int nro) throws Exception 
  {
    if (this.locked)
    {
      log.finest("Profile is locked");
    } 
    
    else 
    {     
      final ExecutionSlotProfile esp = new ExecutionSlotProfile("DefaultProfile", "0");
      log.info("Profile used: " + esp.name());
      
      for (int i = 0; i < nro; i++) 
      {
           final ExecutionSlot ex = new ExecutionSlot(i,"Default" + i, "Nothing"); // this is so that nothing will get executed 
           																		   // if the default profile is used
           log.info("Default Execution slot (Default " + i + ") created ");
           log.info("Execution slot: " + ex.getName() + " is being used");
           log.info("Executing Slot: Running Set: " + ex.getRunningSet());
           
           esp.addExecutionSlot(ex);
      }

      esp.activate();
      return esp;
    }

    return null;
  }

  
  
  /**
   * 
   * reads execution profiles from DB
   * 
   * @throws Exception
   */
  private void readProfiles() throws Exception {
	
    
	RockFactory rockFact = null;
    try {

      rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLExProfile", true);

      if (this.locked) {

        log.finest("Profile is locked");
        
      } else {
    		resetNumberOfSlots();

        final Meta_execution_slot_profile whereProfile = new Meta_execution_slot_profile(rockFact);
        final Meta_execution_slot_profileFactory espF = new Meta_execution_slot_profileFactory(rockFact, whereProfile);

        for (int ii = 0; ii < espF.size(); ii++) {

          final Meta_execution_slot_profile profile = espF.getElementAt(ii);

          final Meta_execution_slot whereSlot = new Meta_execution_slot(rockFact);
          whereSlot.setProfile_id(profile.getProfile_id());
          final Meta_execution_slotFactory slotF = new Meta_execution_slotFactory(rockFact, whereSlot);

          final ExecutionSlotProfile esp = new ExecutionSlotProfile(profile.getProfile_name(), profile.getProfile_id());

          if (profile.getActive_flag().equalsIgnoreCase("y")) {
            esp.activate();
          }

          for (int i = 0; i < slotF.size(); i++) {

            final Meta_execution_slot slot = slotF.getElementAt(i);

            int id = Integer.parseInt(slot.getSlot_id());
            
            final ExecutionSlot ex = new ExecutionSlot(id,slot.getSlot_name(), slot.getAccepted_set_types());

            updateNumberOfAdapterSlots(slot.getAccepted_set_types());
            
            log.config("Execution slot (" + profile.getProfile_name() + "/" + slot.getSlot_name() + ") read");

            esp.addExecutionSlot(ex);

          }

          exeSlotsProfileMap.put(esp.name(), esp);

          if (esp.IsActivate()) {
            setActiveProfile(esp.name());
          }

        }

        
      }

    } catch (Exception e) {

      log.log(Level.WARNING, "Error while creating rockEngine", e);

    } finally {

      try {

        if (rockFact != null && rockFact.getConnection() != null){
          rockFact.getConnection().close();
        }

        rockFact = null;

      } catch (Exception e) {

        log.log(Level.WARNING, "Error while closing rockEngine", e);

      }

    }

  }

  /*
   * Reset the number of Adapter and Aggregator Slots.
   */
  private void resetNumberOfSlots() {
	  numberOfAdapterSlots     = 0;
	  numberOfAggregatorSlots  = 0;
  }

  /*
   * Update the number of Adapter|Aggregator Slots. This method passes in the 
   * setTypes from the Execution Slot. If the set type contains an 
   * adapter, then the count is updated.
   */
  private void updateNumberOfAdapterSlots(String acceptedSetTypes) {
	  if (acceptedSetTypes.contains("adapter") || acceptedSetTypes.contains("Adapter")){
		  this.numberOfAdapterSlots = numberOfAdapterSlots + 1;
	  }
	  else if (acceptedSetTypes.contains("aggregator") || acceptedSetTypes.contains("Aggregator")){
		  this.numberOfAggregatorSlots = numberOfAggregatorSlots + 1;
	  }
  }

  /*
   * Return the number of Adapter Slots.
   */
  public int getNumberOfAdapterSlots() {
	  return this.numberOfAdapterSlots;
  }

  /*
   * Return the number of Aggregator Slots.
   */
  public int getNumberOfAggregatorSlots() {
	  return this.numberOfAggregatorSlots;
  }

/**
   * writes profile to DB.
   * 
   */
  public void writeProfile() throws Exception {

    RockFactory rockFact = null;

    try {

      rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLExProfile", true);

      if (exeSlotsProfileMap != null && !exeSlotsProfileMap.isEmpty()) {

        final ExecutionSlotProfile aesp = getActiveExecutionProfile();

        final Meta_execution_slot_profile whereProfile = new Meta_execution_slot_profile(rockFact);
        final Meta_execution_slot_profileFactory espF = new Meta_execution_slot_profileFactory(rockFact, whereProfile);

        for (int ii = 0; ii < espF.size(); ii++) {

          final Meta_execution_slot_profile profile = espF.getElementAt(ii);

          if (profile.getProfile_id().equalsIgnoreCase(aesp.ID())) {        	  
        	  profile.setActive_flag("Y");
          } else {
            profile.setActive_flag("N");
          }
          
          log.config("Execution slot profile (" + profile.getProfile_name() + ") saved to DB with active flag: " + profile.getActive_flag());
          profile.updateDB();

        }
      }

    } catch (Exception e) {

      log.log(Level.WARNING, "Error while creating rockEngine", e);

    } finally {

      try {

        if (rockFact != null && rockFact.getConnection() != null){
          rockFact.getConnection().close();
        }

        rockFact = null;

      } catch (Exception e) {

        log.log(Level.WARNING, "Error while closing rockEngine", e);

      }
    }

  }

  /**
   * 
   * returns list of execution profile names
   * 
   * @return
   */
  public Vector getExecutionProfileNames() {

    return null;
  }

  /**
   * retrieves a execution profile by name
   * 
   * @param name
   * @return
   */
  public ExecutionSlotProfile getExecutionProfile(final String name) {

    return null;
  }

  /**
   * retrieves all execution profiles
   * 
   * @return
   */
  public Vector getAllExecutionProfiles() {

    return null;
  }
  
  public boolean setActiveProfile(final String profileName, final String messageText) throws Exception {
    log.info("Starting to change profile to " + profileName + ". Reason why: " + messageText);
    return setActiveProfile(profileName);
  }

  public boolean setActiveProfile(final String profileName) throws Exception {

    if (this.locked) {

      log.info("Execution Profile is locked");
      
    } else {

      if (exeSlotsProfileMap.containsKey(profileName)) {

        final ExecutionSlotProfile newProfile = (ExecutionSlotProfile) exeSlotsProfileMap.get(profileName);
        newProfile.activate();
        activeSlotProfile.setID(newProfile.ID());
        activeSlotProfile.setName(newProfile.name());

        final Iterator newIter = newProfile.getAllExecutionSlots();

        final Iterator all = activeSlotProfile.getAllExecutionSlots();

        final List newSlotList = new ArrayList();

        ExecutionSlot oldExs = null;
        ExecutionSlot newExs = null;

        // update existing slots to new.
        while (newIter.hasNext() || oldExs != null) {

          // is there rejected slot waiting
          if (oldExs == null) {
            // no slot waiting, get a new one.
            newExs = (ExecutionSlot) newIter.next();
          } else {
            // there is a old one, take it.
            newExs = oldExs;
            oldExs = null;
          }

          if (all.hasNext()) {

            // update sets to
            final ExecutionSlot exs = (ExecutionSlot) all.next();

            // update only free slots
            if (exs.isFree()) {
              // if set was marked for removal, cancel that
              exs.removeAfterExecution(false);
              exs.setApprovedSettypes(newExs.getApprovedSettypes());
              exs.setName(newExs.getName());
            } else {

              // slot is not free, mark it as to be removed
              exs.setApprovedSettypes("");
              exs.setName("TO_BE_REMOVED");
              exs.removeAfterExecution(true);

              // and try a new slot.
              oldExs = newExs;
            }

          } else {

            // just add new slots to active profile
            int id = newExs.getSlotId();
            newSlotList.add(new ExecutionSlot(id,newExs.getName(), newExs.getApprovedSettypes()));
          }
        }

        // remove extra free sets from active profile, running set stay onboard
        while (all.hasNext()) {
          final ExecutionSlot exs = (ExecutionSlot) all.next();
          if (exs.isFree()) {
            all.remove();
          } else {
            exs.setApprovedSettypes("");
            exs.setName("TO_BE_REMOVED");
            exs.removeAfterExecution(true);
          }
        }

        final Iterator nIter = newSlotList.iterator();
        // add new sets to active profile
        while (nIter.hasNext()) {
          final ExecutionSlot exs = (ExecutionSlot) nIter.next();
          activeSlotProfile.addExecutionSlot(exs);

        }

        return true;
      } else {
        log.warning("Profile name not found " + profileName);
      }

    }

    return false;
  }

  /**
   * retrieves all execution profiles
   * 
   * @return
   */
  public boolean addSlot(final ExecutionSlot slot) {

    return true;
  }

  /**
   * retrieves all execution profiles
   * 
   * @return
   */
  public ExecutionSlot removeSlot(final String name) {

    return null;
  }

  /**
   * retrieves active execution profile
   * 
   * @return
   */
  public ExecutionSlotProfile getActiveExecutionProfile() throws Exception {

    return this.activeSlotProfile;

  }

  public void lockProfile() {
    this.locked = true;
  }

  public void unLockProfile() {
    this.locked = false;
  }

  public boolean isProfileLocked() {
    return this.locked;
  }

  /**
   * 
   * 
   * 
   * @return true if active profile is clean, does not contain any ready for
   *         removal slots.
   */
  public boolean isProfileClean() {
    return activeSlotProfile.isProfileClean();
  }

  public void cleanActiveProfile() {

    activeSlotProfile.cleanProfile();

  }



}
