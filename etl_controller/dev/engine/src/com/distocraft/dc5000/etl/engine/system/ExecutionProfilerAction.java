package com.distocraft.dc5000.etl.engine.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_execution_slot;
import com.distocraft.dc5000.etl.rock.Meta_execution_slotFactory;
import com.distocraft.dc5000.etl.rock.Meta_execution_slot_profile;
import com.distocraft.dc5000.etl.rock.Meta_execution_slot_profileFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.dwhrep.Configuration;
import com.distocraft.dc5000.repository.dwhrep.ConfigurationFactory;

/**
 * ExecutionProfiler is for setting execution profiles and slots based on <br>
 * Configuration table's parameters and CPU core amount (Eniq_Proc_Core parameter in niq.ini).<br><br>
 * 
 * Profiles are inserted into META_EXECUTION_SLOT_PROFILE table and slots are inserted -- based on the formula -- into META_EXECUTION_SLOT table. <br><br>
 *
 * Example of profiles' data in Configuration table:<br><br>
 * <table>
 * <td><i>Name</i></td>
 * <td>&nbsp;</td>
 * <td><i>Value</i></td>
 * <tr><td>executionProfile.0.Normal</td><td>&nbsp;</td><td>Y</td></tr>
 * <tr><td>executionProfile.1.NoLoads</td><td>&nbsp;</td><td>N</td></tr>
 * </table>
 *<br>
 * Example of slots' data in Configuration table:<br><br>
 * <table>
 * <td><i>Name</i></td>
 * <td>&nbsp;</td>
 * <td><i>Value</i></td>
 * <tr><td>executionProfile.0.slot1.0.execute</td><td>&nbsp;</td><td>adapter,Adapter,Alarm,Install,Mediation,Topology</td></tr>
 * <tr><td>executionProfile.0.slot1.0.formula</td><td>&nbsp;</td><td>0.9n</td></tr>
 * <tr><td>executionProfile.0.slot2.1.execute</td><td>&nbsp;</td><td>Loader</td></tr>
 * <tr><td>executionProfile.0.slot2.1.formula</td><td>&nbsp;</td><td>0.1n</td></tr>
 * <tr><td>executionProfile.0.slot3.2.execute</td><td>&nbsp;</td><td>Loader,Aggregator</td></tr>
 * <tr><td>executionProfile.0.slot3.2.formula</td><td>&nbsp;</td><td>0.1n</td></tr>
 * <tr><td>executionProfile.0.slot4.3.execute</td><td>&nbsp;</td><td>Aggregator</td></tr>
 * <tr><td>executionProfile.0.slot4.3.formula</td><td>&nbsp;</td><td>0</td></tr>
 * <tr><td>executionProfile.0.slot5.4.execute</td><td>&nbsp;</td><td>Partition,Service,Support</td></tr>
 * <tr><td>executionProfile.0.slot5.4.formula</td><td>&nbsp;</td><td>1</td></tr>
 * <tr><td>executionProfile.1.slot1.5.execute</td><td>&nbsp;</td><td>adapter,Adapter,Support</td></tr>
 * <tr><td>executionProfile.1.slot1.5.formula</td><td>&nbsp;</td><td>1</td></tr>
 * <tr><td>executionProfile.1.slot2.6.execute</td><td>&nbsp;</td><td>adapter,Adapter,Support</td></tr>
 * <tr><td>executionProfile.1.slot2.6.formula</td><td>&nbsp;</td><td>1</td></tr>
 * <tr><td>executionProfile.1.slot3.7.execute</td><td>&nbsp;</td><td>adapter,Adapter,Support</td></tr>
 * <tr><td>executionProfile.1.slot3.7.formula</td><td>&nbsp;</td><td>1</td></tr>
 * </table>
 * 
 * 
 * <br><br>
 * @author eharrka
 */
public class ExecutionProfilerAction extends TransferActionBase {

  private static final int PROFILE_ID = 1;

  private static final int PROFILE_NAME = 2;

  private static final int SLOT_ID = 3;

  private static final int SLOT_NAME = 2;

  private static final int SLOT_FORM_OR_EXEC = 4;

  private static final int CONF_PROFILE_NAMESPACE_AMOUNT = 3;

  private static final String CPU_CORE_VARIABLE_STR = "n";

  private static final String MULTIPLY_STR = "multiply";

  private static final String CONF_NAME_FIELD_DELIM_STR = ".";

  private static final String CONF_NAME_FORMULA_FIELD = "formula";

  private static final String CONF_NAME_EXECUTE_FIELD = "execute";

  private static final String CONF_EXEC_PROFILE_NAMESPACE = "executionProfile.";

  private static final String INI_NAME_VALUE_CONNECT_STR = "=";

  private static final String INI_SECTION_NAME_STARTS_WITH = "[";

  private static final String HW_INFO_SECTION_NAME_IN_INI = "[ENIQ_HW_INFO]";

  private static final String CPU_CORE_NAME_IN_INI = "Eniq_Proc_Core";

  private static final String ENIQ_INI_FILENAME = "niq.ini";

  private static final String DEFAULT_CONF_PATH = "/eniq/sw/conf/";

  private final String eniqINIFile;

  private final RockFactory etlRepRock;

  private RockFactory dwhRepRock;

  private final int cpuCoreAmount;

  private final Logger log;

  private int slotIDCounter;

  private final HashMap profiles = new HashMap();

  private final HashMap slots = new HashMap();

  public ExecutionProfilerAction(final Meta_versions version, final Long collectionSetId,
      final Meta_collections collection, final Long transferActionId, final Long transferBatchId, final Long connectId,
      final RockFactory etlRepRock, final Meta_transfer_actions trActions, final Logger log) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, etlRepRock, trActions);

    this.log = log;
    this.eniqINIFile = getEniqINIFilePath();
    this.cpuCoreAmount = getCPUCoreAmountFromINI();
    this.etlRepRock = etlRepRock;
    initDwhRepRock();
    this.slotIDCounter = getNextSlotID();
    log.fine("The next slot id is " + this.slotIDCounter);
  }

  private String getEniqINIFilePath() {
    String etlcServerPropertiesPath = System.getProperty("CONF_DIR");
    if (null == etlcServerPropertiesPath) {
      log.config("System property CONF_DIR not defined. Using default");
      etlcServerPropertiesPath = DEFAULT_CONF_PATH;
    }
    if (!etlcServerPropertiesPath.endsWith(File.separator)) {
      etlcServerPropertiesPath += File.separator;
    }
    return etlcServerPropertiesPath + ENIQ_INI_FILENAME;
  }

  private void initDwhRepRock() throws Exception {
    try {
      final Meta_databases mdCondition = new Meta_databases(this.etlRepRock);
      final Meta_databasesFactory mdFactory = new Meta_databasesFactory(this.etlRepRock, mdCondition);

      final List databases = mdFactory.get();
      final Iterator iterator = databases.iterator();
      while (iterator.hasNext()) {
        final Meta_databases database = (Meta_databases) iterator.next();

        if ("dwhrep".equalsIgnoreCase(database.getConnection_name()) && "USER".equals(database.getType_name())) {
          this.dwhRepRock = new RockFactory(database.getConnection_string(), database.getUsername(), database
              .getPassword(), database.getDriver_name(), "ExecutionProfiler", true);
        }
      }

      if (null == this.dwhRepRock) {
        throw new Exception("Database dwhrep is not defined in Meta_databases?!");
      }

    } catch (Exception e) {
      // if something went wrong try to close dwhrep connection
      try {
        this.dwhRepRock.getConnection().close();
      } catch (Exception exception) {
      }

      throw e;
    }
  }
  
  private int getNextSlotID() throws Exception {
    int returnValue = 0;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    
    try { 
      statement = etlRepRock.getConnection().prepareStatement("SELECT COALESCE(MAX(CAST(SLOT_ID AS integer))+1,0) AS nextID FROM META_EXECUTION_SLOT;");
      resultSet = statement.executeQuery();
      
      if(resultSet.next()){
        returnValue = resultSet.getInt("nextID");
      }
      
    } catch (Exception e) {
      log.warning("An error occured on getting the latest+1 id from database table meta_execution_slot");
      throw e;
    } finally {
      if(resultSet != null) {
        try {
          resultSet.close();
        } catch (Exception e) {
        }
      }
      
      if(statement != null) {
        try {
          statement.close();
        } catch (Exception e) {
        }
      }
    }
    
    return returnValue;
  }

  public void execute() throws Exception {
    try {
      log.info("Starting configuration reading from database...");
      getConfiguration();
    } catch (Exception e) {
      log.warning("Exception occured in configuration quering. " + e.toString());
      throw e;
    }

    log.info("Starting to update execution profiles and slots...");
    try {
      updateExecutionProfiles();
    } catch (Exception e) {
      log.warning("Exception occured in updating execution profiles. " + e.toString());
      throw e;
    }

    try {
      updateExecutionSlots();
    } catch (Exception e) {
      log.warning("Exception occured in updating execution slots. " + e.toString());
      throw e;
    }
    
    closeDBConnections();
  }

  private void getConfiguration() throws Exception {
    
	log.finest("Getting slot configuration from database.");
	
    String sqlStr = "SELECT PARAMNAME, PARAMVALUE FROM CONFIGURATION "
    	+ "WHERE PARAMNAME LIKE '" +CONF_EXEC_PROFILE_NAMESPACE + "%'"
    	+ " ORDER BY PARAMNAME";
    
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    List <Configuration> execProfileConfigurations = new ArrayList<Configuration>();
    try { 
      statement = dwhRepRock.getConnection().prepareStatement(sqlStr);
      resultSet = statement.executeQuery();
      
      while(resultSet.next()){
    	  Configuration conf = new Configuration(dwhRepRock);
    	  conf.setParamname(resultSet.getString("PARAMNAME"));
    	  conf.setParamvalue(resultSet.getString("PARAMVALUE"));
    	  execProfileConfigurations.add(conf);
      }
    	
    } catch (Exception e) {
      log.warning("An error occured on getting slot configurations.");
      throw e;
    } finally {
      if(resultSet != null) {
        try {
          resultSet.close();
        } catch (Exception e) {
        }
      }
      
      if(statement != null) {
        try {
          statement.close();
        } catch (Exception e) {
        }
      }
    }
    
    final Iterator iterator = execProfileConfigurations.iterator();
    while (iterator.hasNext()) {
      final Configuration conf = (Configuration) iterator.next();
      final String paramName = conf.getParamname();

      log.fine("Reading parameter name: " + paramName);
      
      if (CONF_EXEC_PROFILE_NAMESPACE.equalsIgnoreCase(paramName.substring(0, CONF_EXEC_PROFILE_NAMESPACE.length()))) {
        final String paramValue = conf.getParamvalue();
        final String[] paramNameSplitted = paramName.split("\\" + CONF_NAME_FIELD_DELIM_STR);

        // check that case is about execution profiles
        if (CONF_PROFILE_NAMESPACE_AMOUNT == paramNameSplitted.length) {
          log.fine("Reading execution profile parameter.");
          
          final Profile tempProfile = new Profile();
          tempProfile.profileID = paramNameSplitted[PROFILE_ID];
          tempProfile.profileName = paramNameSplitted[PROFILE_NAME];
          tempProfile.active = paramValue;
          profiles.put(tempProfile.profileID, tempProfile);
          
          log.fine("Read a profile with id: " + tempProfile.profileID);
        } else {
          // else case is about execution slots
          log.fine("Reading execution slot parameter.");
          
          final Slot tempSlot = new Slot();
          tempSlot.slotID = paramNameSplitted[SLOT_ID];
          tempSlot.profileID = paramNameSplitted[PROFILE_ID];
          tempSlot.slotName = paramNameSplitted[SLOT_NAME];

          final Configuration secondRowConf = (Configuration) iterator.next();
          final String secondRowValue = secondRowConf.getParamvalue();

          log.finest("Checking whether the first read parameter is " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_FORMULA_FIELD + " or " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_EXECUTE_FIELD + "...");
          
          if (CONF_NAME_FORMULA_FIELD.equalsIgnoreCase(paramNameSplitted[SLOT_FORM_OR_EXEC])) {
            log.finest("Reading " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_FORMULA_FIELD + " value.");
            tempSlot.formula = paramValue;
            log.finest("Reading " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_EXECUTE_FIELD + " value.");
            tempSlot.setTypes = secondRowValue;
          } else if (CONF_NAME_EXECUTE_FIELD.equalsIgnoreCase(paramNameSplitted[SLOT_FORM_OR_EXEC])) {
            log.finest("Reading " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_EXECUTE_FIELD + " value.");
            tempSlot.setTypes = paramValue;
            log.finest("Reading " + CONF_NAME_FIELD_DELIM_STR + CONF_NAME_FORMULA_FIELD + " value.");
            tempSlot.formula = secondRowValue;
          }
          tempSlot.amountOfSlots = getAmountOfSlots(cpuCoreAmount, tempSlot.formula);
          slots.put(tempSlot.slotID, tempSlot);
          
          log.fine("Read a slot with id: " + tempSlot.slotID);
        }
      }
    }
  }

  private void updateExecutionProfiles() throws Exception {
    final Set profilesSet = profiles.keySet();
    final Iterator profilesIter = profilesSet.iterator();
    while (profilesIter.hasNext()) {
      final Profile profile = (Profile) profiles.get(profilesIter.next().toString());
      // delete slots from db by profileID
      log.fine("Deleting slots by profileID: " + profile.profileID);
      deleteSlotsByProfileID(profile.profileID);

      // delete profile from db by by profileID
      log.fine("Deleting profile by profileID: " + profile.profileID);
      deleteProfileByID(profile.profileID);

      // add new profile into db
      log.fine("Adding profile by profileID: " + profile.profileID);
      addProfile(profile);
    }
  }

  private void updateExecutionSlots() throws Exception {
    final Set slotsSet = slots.keySet();
    final Iterator slotsIter = slotsSet.iterator();
    while (slotsIter.hasNext()) {
      final Slot slot = (Slot) slots.get(slotsIter.next().toString());
      addSlots(slot);
    }
  }

  private void addProfile(final Profile profile) throws Exception {
    final Meta_execution_slot_profile mesp = new Meta_execution_slot_profile(etlRepRock);
    mesp.setActive_flag(profile.active);
    mesp.setProfile_id(profile.profileID);
    mesp.setProfile_name(profile.profileName);
    mesp.saveDB();
  }

  private void addSlots(final Slot slot) throws Exception {
    for (int i = 0; i < slot.amountOfSlots; i++) {
      final Meta_execution_slot mes = new Meta_execution_slot(etlRepRock);
      mes.setSlot_id(Integer.toString(slotIDCounter));
      mes.setProfile_id(slot.profileID);
      mes.setSlot_name("Slot" + Integer.toString(slotIDCounter));
      mes.setAccepted_set_types(slot.setTypes);
      
      log.fine("Adding slot " + mes.getSlot_name() + " with id: " + mes.getSlot_id());
      mes.saveDB();
      
      slotIDCounter++;
    }
  }

  private void deleteSlotsByProfileID(final String profileID) throws Exception {
    final Meta_execution_slot profileIDCondForSlot = new Meta_execution_slot(etlRepRock);
    profileIDCondForSlot.setProfile_id(profileID);

    final Meta_execution_slotFactory esFactory = new Meta_execution_slotFactory(etlRepRock, profileIDCondForSlot);
    final List slotsToBeDeleted = esFactory.get();

    final Iterator iterator = slotsToBeDeleted.iterator();
    while (iterator.hasNext()) {
      final Meta_execution_slot mesToDelete = (Meta_execution_slot) iterator.next();
      mesToDelete.deleteDB();
    }
  }

  private void deleteProfileByID(final String profileID) throws Exception {
    final Meta_execution_slot_profile profileIDCondForProfile = new Meta_execution_slot_profile(etlRepRock);
    profileIDCondForProfile.setProfile_id(profileID);

    final Meta_execution_slot_profileFactory espFactory = new Meta_execution_slot_profileFactory(etlRepRock,
        profileIDCondForProfile);
    final List profilesToBeDeleted = espFactory.get();

    final Iterator iterator = profilesToBeDeleted.iterator();
    while (iterator.hasNext()) {
      final Meta_execution_slot_profile mespToDelete = (Meta_execution_slot_profile) iterator.next();
      mespToDelete.deleteDB();
    }
  }

  private int getCPUCoreAmountFromINI() {
    int coreAmount = 1;
    final String targetFilePath = eniqINIFile;
    final File targetFile = new File(targetFilePath);

    if (targetFile.isFile() && targetFile.canRead()) {
      FileReader fileReader = null;
      BufferedReader bufferedReader = null;

      try {
        fileReader = new FileReader(targetFile);
        bufferedReader = new BufferedReader(fileReader);

        boolean readingCorrectSectionParameters = false;
        boolean coreAmountFound = false;

        String line = bufferedReader.readLine();
        while (line != null && !coreAmountFound) {
          if (line.startsWith(INI_SECTION_NAME_STARTS_WITH)) {
            readingCorrectSectionParameters = line.startsWith(HW_INFO_SECTION_NAME_IN_INI);
          }

          if (readingCorrectSectionParameters) {
            if (line.startsWith(CPU_CORE_NAME_IN_INI + INI_NAME_VALUE_CONNECT_STR)) {
              coreAmount = Integer.parseInt(line.substring(
                  (CPU_CORE_NAME_IN_INI + INI_NAME_VALUE_CONNECT_STR).length(), line.length()));
              coreAmountFound = true;
              log.fine("CPU core amount found from ini-file: " + coreAmount);
            }
          }
          line = bufferedReader.readLine();
        }

      } catch (Exception e) {
        log.warning("Reading of file " + targetFilePath + " failed." + e.toString());
      } finally {
        if (null != bufferedReader) {
          try {
            bufferedReader.close();
          } catch (Exception e) {
          }
        }

        if (null != fileReader) {
          try {
            fileReader.close();
          } catch (Exception e) {
          }
        }
      }

    } else {
      log.warning("Could not read file " + targetFilePath + ". Please check that the file " + targetFilePath
          + " exists and it can be read.");
    }

    log.info("CPU core amount: " + coreAmount);
    return coreAmount;
  }

  /*
   * This method multiplies all n-variables (cpuCoreAmount) within formula and
   * rounds the result up to ceiling, because for example 0.2n formula could
   * result less than one
   */
  private int getAmountOfSlots(final int cpuCoreAmount, final String formula) {
    double result = 1.0;
    final String formulaToUse = formula.replaceAll(CPU_CORE_VARIABLE_STR, MULTIPLY_STR + Integer.toString(cpuCoreAmount) + MULTIPLY_STR);
    final String[] formulaNumericParts = formulaToUse.split(MULTIPLY_STR);

    for (int i = 0; i < formulaNumericParts.length; i++) {
      if (formulaNumericParts[i].length() > 0) {
        result *= Double.parseDouble(formulaNumericParts[i]);
      }
    }

    final int finalResult = (int) Math.ceil(result);
    
    log.fine("With formula: '" + formula + "' where '" + CPU_CORE_VARIABLE_STR + "=" + cpuCoreAmount + "' the slot amount is " + finalResult);
    return finalResult;
  }
  
  private void closeDBConnections() {
    Connection connection = null;
    try {
      connection = dwhRepRock.getConnection();
    } catch (Exception e) {
    } finally {
      if(connection != null ){
        try {
          connection.close();
        } catch (Exception e) {
        }
      }
    }
  }

  private class Profile {

    public String profileID;

    public String profileName;

    public String active;
  }

  private class Slot {

    public String slotID;

    public String slotName;

    public String profileID;

    public String setTypes;

    public String formula;

    public int amountOfSlots;
  }
}
