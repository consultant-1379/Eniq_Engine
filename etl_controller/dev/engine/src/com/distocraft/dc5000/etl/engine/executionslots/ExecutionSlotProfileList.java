/*
 * Created on 26.1.2005
 *
 */
package com.distocraft.dc5000.etl.engine.executionslots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class ExecutionSlotProfileList {

  /* contains ExecutionSlotProfile Objects */
  private Vector exeSlotsProfileList;

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
  public ExecutionSlotProfileList(String url, String userName, String password, String dbDriverName) throws Exception {

    this.url = url;
    this.userName = userName;
    this.password = password;
    this.dbDriverName = dbDriverName;

    this.log = Logger.getLogger("etlengine.ExecutionSlotProfile");

    try {

      exeSlotsProfileList = new Vector();
      log.fine("init: creating rockEngine");

      this.readProfiles();

      log.finest("init: Execution slot profiles created");

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
  public ExecutionSlotProfileList(int nroOfSlots) throws Exception {
    this.log = Logger.getLogger("etlengine.ExecutionSlotProfile");

    exeSlotsProfileList = new Vector();
    this.createProfile(nroOfSlots);
    log.finest("Default Execution slot profile created TEST");

  }

  /**
   * 
   * 
   */
  public void resetProfiles() throws Exception {

    if (this.locked) {
      log.finest("profiles locked");


    } else {
      exeSlotsProfileList = new Vector();
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
  public void createProfile(final int nro) throws Exception {

    if (this.locked) {
      
      log.finest("profiles locked");

    } else {
      
      final ExecutionSlotProfile esp = new ExecutionSlotProfile("DefaultProfile", "0");
      esp.activate();

      for (int i = 0; i < nro; i++) {

        final ExecutionSlot ex = new ExecutionSlot(i,"Default" + i, "");

        log.config("Default Execution slot (Default" + i + ") created ");

        esp.addExecutionSlot(ex);

      }

      exeSlotsProfileList.add(esp);

    }

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
        
        log.finest("profiles locked");


      } else {
        
        final Meta_execution_slot_profile whereProfile = new Meta_execution_slot_profile(rockFact);
        final Meta_execution_slot_profileFactory espF = new Meta_execution_slot_profileFactory(rockFact, whereProfile);

        for (int ii = 0; ii < espF.size(); ii++) {

          final Meta_execution_slot_profile profile = espF.getElementAt(ii);

          final Meta_execution_slot whereSlot = new Meta_execution_slot(rockFact);
          whereSlot.setProfile_id(profile.getProfile_id());
          final Meta_execution_slotFactory slotF = new Meta_execution_slotFactory(rockFact, whereSlot);

          final ExecutionSlotProfile esp = new ExecutionSlotProfile(profile.getProfile_name(), profile.getProfile_id());

          if (profile.getActive_flag().equalsIgnoreCase("y")){
            esp.activate();
          }
          
          for (int i = 0; i < slotF.size(); i++) {

            final Meta_execution_slot slot = slotF.getElementAt(i);
            int id = Integer.parseInt(slot.getSlot_id());
            final ExecutionSlot ex = new ExecutionSlot(id,slot.getSlot_name(), slot.getAccepted_set_types());

            log.config("Execution slot (" + profile.getProfile_name() + "/" + slot.getSlot_name() + ") read");

            esp.addExecutionSlot(ex);

          }

          exeSlotsProfileList.add(esp);

        }

      }

    } catch (Exception e) {

      log.log(Level.FINEST, "Error while creating rockEngine", e);

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
   * writes profile to DB.
   * 
   */
  public void writeProfile() throws Exception {

    RockFactory rockFact = null;

    try {

      rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLExProfile", true);

      if (exeSlotsProfileList != null && !exeSlotsProfileList.isEmpty()) {

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

          profile.updateDB();

        }

        log.config("Execution slot profile (" + whereProfile.getProfile_name() + ") saved to DB");

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

  /**
   * Set a profile to active
   * 
   * @return true if given profile is activated else false
   */
  public boolean setActiveProfile(final String profileName) throws Exception {

    ExecutionSlotProfile aex = null;
    final List deActivationList = new ArrayList();

    if (this.locked) {

      log.finest("profiles locked");
      
    } else {
      
      for (int i = 0; i < exeSlotsProfileList.size(); i++) {
        final ExecutionSlotProfile ex = (ExecutionSlotProfile) exeSlotsProfileList.get(i);

        if (ex.name().equals(profileName)) {
          log.finest("Execution slot profile(" + ex.name() + ") set to active");

          // activate this profile
          aex = ex;

        } else {

          // deactivate thease profiles
          deActivationList.add(ex);
        }
      }

      // if activated profile is not found, nothing is done.
      if (aex != null) {

        // activate
        aex.activate();

        // deactivate
        final Iterator iter = deActivationList.iterator();
        while (iter.hasNext()) {
          final ExecutionSlotProfile ex = (ExecutionSlotProfile) iter.next();
          ex.deactivate();
        }

        return true;
      }


    }

    return false;

  }

  /**
   * retrieves active execution profile
   * 
   * @return
   */
  public ExecutionSlotProfile getActiveExecutionProfile() throws Exception {

    for (int i = 0; i < exeSlotsProfileList.size(); i++) {
      final ExecutionSlotProfile ex = (ExecutionSlotProfile) exeSlotsProfileList.get(i);
      if (ex.IsActivate()) {
        return ex;
      }
    }

    log.finest("No active execution profile set.");
    throw new Exception("No active execution profile set.");
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

}
