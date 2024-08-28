package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.AlarminterfaceFactory;

/**
 * This class is a custom made ANT-task that inserts an entry to etlrep database
 * table AlarmInterface. Copyright (c) 1999 - 2007 AB LM Ericsson Oy All rights
 * reserved.
 * 
 * @author ejannbe
 */
public class InsertAlarmInterface extends Task {

  RockFactory etlrepRockFactory = null;

  protected String propertiesFilepath = new String("");

  private String configurationDirectory = new String();

  RockFactory dwhrepRockFactory = null;

  private String interfaceId = new String("");

  private String description = new String("");

  private String status = new String("");

  private String queueNumber = new String("");

  /**
   * This function starts the execution of task.
   */
  public void execute() throws BuildException {
    if (!this.configurationDirectory.endsWith(File.separator)) {
      // Add the missing separator char "/" from the end of the directory
      // string.
      this.configurationDirectory = this.configurationDirectory + File.separator;
    }
    this.propertiesFilepath = this.configurationDirectory + "ETLCServer.properties";
    HashMap databaseConnectionDetails = getDatabaseConnectionDetails();

    // Create the connection to the etlrep.
    this.etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);
    // Create also the connection to dwhrep.
    this.createDwhrepRockFactory();

    boolean insertSuccessful = this.insertInterface();

    if (insertSuccessful == true) {
      System.out.println("Insertion of AlarmInterface was successful.");
    }
    
  }

  /**
   * This function reads the database connection details from the file
   * ${configurationDirectory}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database connection details.
   */
  private HashMap getDatabaseConnectionDetails() {
    HashMap databaseConnectionDetails = new HashMap();

    try {
      File targetFile = new File(propertiesFilepath);
      if (targetFile.isFile() == false || targetFile.canRead() == false) {
        throw new BuildException("Could not read database properties. Please check that the file " + propertiesFilepath
            + " exists and it can be read.");
      }

      BufferedReader reader = new BufferedReader(new FileReader(targetFile));

      String line = new String();
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
      reader.close();
      // Set the database connection properties as ANT properties.
      Iterator databaseConnectionDetailsIterator = databaseConnectionDetails.keySet().iterator();
      while (databaseConnectionDetailsIterator.hasNext()) {
        String property = (String) databaseConnectionDetailsIterator.next();
        String value = (String) databaseConnectionDetails.get(property);
        // System.out.println(property + " = " + value);
        getProject().setNewProperty(property, value);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Connection to database failed.", e);
    }

    return databaseConnectionDetails;
  }

  /**
   * This function creates the rockfactory object to etlrep from the database
   * connection details read from ETLCServer.properties file.
   * 
   * @param databaseConnectionDetails
   * @return Returns the created RockFactory.
   */
  private RockFactory createEtlrepRockFactory(HashMap databaseConnectionDetails) throws BuildException {
    RockFactory rockFactory = null;
    String databaseUsername = databaseConnectionDetails.get("etlrepDatabaseUsername").toString();
    String databasePassword = databaseConnectionDetails.get("etlrepDatabasePassword").toString();
    String databaseUrl = databaseConnectionDetails.get("etlrepDatabaseUrl").toString();
    String databaseDriver = databaseConnectionDetails.get("etlrepDatabaseDriver").toString();

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, "PreinstallCheck",
          true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Unable to initialize database connection.", e);
    }

    if (rockFactory == null)
      throw new BuildException(
          "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");

    return rockFactory;
  }

  /**
   * This function creates the RockFactory to dwhrep. The created RockFactory is
   * inserted in class variable dwhrepRockFactory.
   */
  private void createDwhrepRockFactory() {
    try {
      Meta_databases whereMetaDatabases = new Meta_databases(this.etlrepRockFactory);
      whereMetaDatabases.setConnection_name("dwhrep");
      whereMetaDatabases.setType_name("USER");
      Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.etlrepRockFactory, whereMetaDatabases);
      Vector metaDatabases = metaDatabasesFactory.get();

      if (metaDatabases != null || metaDatabases.size() == 1) {
        Meta_databases targetMetaDatabase = (Meta_databases) metaDatabases.get(0);

        this.dwhrepRockFactory = new RockFactory(targetMetaDatabase.getConnection_string(), targetMetaDatabase
            .getUsername(), targetMetaDatabase.getPassword(), etlrepRockFactory.getDriverName(), "PreinstallCheck",
            true);

      } else {
        throw new BuildException("Unable to connect metadata (No dwhrep or multiple dwhreps defined in Meta_databases)");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Creating database connection to dwhrep failed.", e);
    }
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInterfaceId() {
    return interfaceId;
  }

  public void setInterfaceId(String interfaceId) {
    this.interfaceId = interfaceId;
  }

  public String getQueueNumber() {
    return queueNumber;
  }

  public void setQueueNumber(String queueNumber) {
    this.queueNumber = queueNumber;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * This function tries to insert an entry to table AlarmInterface.
   * 
   * @return Returns true if the insertion was succesful, otherwise returns
   *         false.
   */
  private boolean insertInterface() {
    try {
      Alarminterface whereAlarmInterface = new Alarminterface(this.dwhrepRockFactory);
      whereAlarmInterface.setInterfaceid(this.interfaceId);
      AlarminterfaceFactory alarmInterfaceFactory = new AlarminterfaceFactory(this.dwhrepRockFactory,
          whereAlarmInterface);
      Vector<Alarminterface> alarmInterfaceVector = alarmInterfaceFactory.get();
      Long metaCollectionSetId = new Long(0);
      Long metaCollectionId = new Long(0);
      if (alarmInterfaceVector.size() > 0) {
        System.out.println("AlarmInterface with interfaceId " + this.interfaceId
            + " already exists. Skipping insertion of AlarmInterface.");
        return false;
      } else {
        String collection_set_name;
        if ("AlarmInterface_RD".equals(this.interfaceId)) {
          collection_set_name = "DC_Z_ALARM";
        } else {
          collection_set_name = "AlarmInterfaces";
        }
        System.out.println("Inserting new entry to table AlarmInterface with interfaceId " + this.interfaceId + " for TP " + collection_set_name);
        Meta_collection_sets whereMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
        whereMetaCollectionSet.setCollection_set_name(collection_set_name);
        whereMetaCollectionSet.setEnabled_flag("Y");
        Meta_collection_setsFactory metaCollectionSetFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
            whereMetaCollectionSet);
        Vector<Meta_collection_sets> metaCollectionSetVector = metaCollectionSetFactory.get();

        if (metaCollectionSetVector.size() > 0) {
          Meta_collection_sets targetMetaCollectionSet = (Meta_collection_sets) metaCollectionSetVector.get(0);
          metaCollectionSetId = targetMetaCollectionSet.getCollection_set_id();
        } else {
          System.out.println("Could not found Meta_collection_set_id. Insertion of AlarmInterface failed.");
          return false;
        }

        Meta_collections whereMetaCollection = new Meta_collections(this.dwhrepRockFactory);
        whereMetaCollection.setCollection_set_id(metaCollectionSetId);
        whereMetaCollection.setCollection_name("Adapter_" + this.interfaceId);
        whereMetaCollection.setEnabled_flag("Y");

        Meta_collectionsFactory metaCollectionFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
            whereMetaCollection);
        
        @SuppressWarnings("unchecked")
        Vector<Meta_collections> metaCollectionVector = metaCollectionFactory.get();

        if (metaCollectionVector.size() > 0) {
          Meta_collections targetMetaCollection = (Meta_collections) metaCollectionVector.get(0);
          metaCollectionId = targetMetaCollection.getCollection_id();
        } else {
          System.out.println("Could not find Meta_collection where metaCollectionSetId=" + metaCollectionSetId
              + ", collectionName=" + "Adapter_" + this.interfaceId + ", enabled=Y. Insertion of AlarmInterface failed.");
          return false;
        }

        // At this point, metaCollectionSetId and metaCollectionId are retrieved
        // from database.
        // Insert the entry to AlarmInterface table.
        Alarminterface newAlarmInterface = new Alarminterface(this.dwhrepRockFactory);
        newAlarmInterface.setInterfaceid(this.interfaceId);
        newAlarmInterface.setDescription(this.description);
        newAlarmInterface.setStatus(this.status);
        newAlarmInterface.setCollection_set_id(metaCollectionSetId);
        newAlarmInterface.setCollection_id(metaCollectionId);
        newAlarmInterface.setQueue_number(new Long(this.queueNumber));
        newAlarmInterface.insertDB();

        System.out
            .println("Inserted new entry to database table AlarmInterface with interfaceId = " + this.interfaceId);
        return true;

      }

    } catch (Exception e) {
      System.out.println("Inserting AlarmInterface failed.");
      throw new BuildException("Inserting AlarmInterface failed.", e);
    }
  }

  
  public String getConfigurationDirectory() {
    return configurationDirectory;
  }

  
  public void setConfigurationDirectory(String configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }
}
