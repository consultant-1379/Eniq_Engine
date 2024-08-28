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

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

/**
 * This class is a custom made ANT-task that prints out information about
 * installed tech packs. Copyright (c) 1999 - 2007 AB LM Ericsson Oy All rights
 * reserved.
 * 
 * @author ejannbe
 */
public class GetInstalledTechPacks extends Task {

  RockFactory etlrepRockFactory = null;

  protected String propertiesFilepath = new String("");

  private String configurationDirectory = new String();

  RockFactory dwhrepRockFactory = null;

  private String showNames = new String("");

  private String showProductNumbers = new String("");

  private String showVersionNumbers = new String("");

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

    // Print out the information about installed techpacks.
    this.printInstalledTechPack();
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

  public String getConfigurationDirectory() {
    return configurationDirectory;
  }

  public void setConfigurationDirectory(String configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }

  public String getShowNames() {
    return showNames;
  }

  public void setShowNames(String showNames) {
    this.showNames = showNames;
  }

  public String getShowProductNumbers() {
    return showProductNumbers;
  }

  public void setShowProductNumbers(String showProductNumbers) {
    this.showProductNumbers = showProductNumbers;
  }

  public String getShowVersionNumbers() {
    return showVersionNumbers;
  }

  public void setShowVersionNumbers(String showVersionNumbers) {
    this.showVersionNumbers = showVersionNumbers;
  }

  /**
   * This function prints out the details about installed tech packs.
   * 
   * @throws BuildException
   */
  private void printInstalledTechPack() throws BuildException {

    try {
      if (this.showNames.equalsIgnoreCase("true") == false && this.showProductNumbers.equalsIgnoreCase("true") == false
          && this.showVersionNumbers.equalsIgnoreCase("true") == false) {
        this.showNames = "true";
        this.showProductNumbers = "true";
        this.showVersionNumbers = "true";
      }

      // First get the active tech packs from table TPActivation.
      Tpactivation whereTPActivation = new Tpactivation(this.dwhrepRockFactory);
      whereTPActivation.setStatus("ACTIVE");

      TpactivationFactory tpActivationFact = new TpactivationFactory(this.dwhrepRockFactory, whereTPActivation, " ORDER BY techpack_name;");
      Iterator tpActivationsIter = tpActivationFact.get().iterator();

      while (tpActivationsIter.hasNext()) {
        Tpactivation currTPActivation = (Tpactivation) tpActivationsIter.next();

        // Get the data related to this active tech pack from the table
        // Versioning.
        Versioning whereVersioning = new Versioning(this.dwhrepRockFactory);
        whereVersioning.setVersionid(currTPActivation.getVersionid());

        VersioningFactory versioningFactory = new VersioningFactory(this.dwhrepRockFactory, whereVersioning);
        Vector versions = versioningFactory.get();

        if (versions.size() == 1) {
          Versioning currentVersioning = (Versioning) versions.get(0);

          if (showNames.equalsIgnoreCase("true")) {
            System.out.print(currentVersioning.getTechpack_name());

            if (showVersionNumbers.equalsIgnoreCase("true") || showProductNumbers.equalsIgnoreCase("true")) {
              System.out.print(";");
            }
          }

          if (showProductNumbers.equalsIgnoreCase("true")) {

            if (currentVersioning.getProduct_number() == null
                || currentVersioning.getProduct_number().equalsIgnoreCase("null")) {
              System.out.print("n/a");
            } else {
              System.out.print(currentVersioning.getProduct_number());
            }

            if (showVersionNumbers.equalsIgnoreCase("true")) {
              System.out.print(";");
            }
          }

          if (showVersionNumbers.equalsIgnoreCase("true")) {
            System.out.print(currentVersioning.getTechpack_version());
          }

          System.out.print("\n");

        }
      }
    } catch (Exception e) {
      throw new BuildException("Getting installed tech packs failed.");
    }
  }
}
