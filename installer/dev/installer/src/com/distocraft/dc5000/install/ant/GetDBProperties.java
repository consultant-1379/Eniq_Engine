package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This is a custom made ANT task that gets the database properties like
 * database url, database username, database password and database driver. This
 * task gets one parameter called <i>name</i> and the ANT properties created by
 * this task are:<br/>
 * <ul>
 * <li><i>name</i>DatabaseUrl</li>
 * <li><i>name</i>DatabaseUsername</li>
 * <li><i>name</i>DatabasePassword</li>
 * <li><i>name</i>DatabaseDriver</li>
 * </ul>
 * 
 * @author berggren
 */
public class GetDBProperties extends Task {

  private String configurationDirectory = new String("");

  private String name = new String("");

  private String type = new String("USER");

  private String propertiesFilepath = new String("");

  /**
   * This function starts the checking of the installation file.
   */
  public void execute() throws BuildException {

    Connection con = null;

    try {

      if (!configurationDirectory.endsWith(File.separator))
        this.configurationDirectory = this.configurationDirectory + File.separator;

      this.propertiesFilepath = this.configurationDirectory + "ETLCServer.properties";
      HashMap etlrepDatabaseConnectionDetails = getEtlrepDatabaseConnectionDetails();

      if (this.name.equalsIgnoreCase("etlrep") && this.type.equalsIgnoreCase("user")) {
        // Set the connection details directly from ETLCServer.properties file.
        getProject().setNewProperty(this.name + "DatabaseUrl",
            etlrepDatabaseConnectionDetails.get("etlrepDatabaseUrl").toString());
        getProject().setNewProperty(this.name + "DatabaseUsername",
            etlrepDatabaseConnectionDetails.get("etlrepDatabaseUsername").toString());
        getProject().setNewProperty(this.name + "DatabasePassword",
            etlrepDatabaseConnectionDetails.get("etlrepDatabasePassword").toString());
        getProject().setNewProperty(this.name + "DatabaseDriver",
            etlrepDatabaseConnectionDetails.get("etlrepDatabaseDriver").toString());

        return;
      }

      // Connect actually to database

      // Create the connection to the etlrep.
      con = createETLRepConnection(etlrepDatabaseConnectionDetails);

      // Set the database connection properties to ANT task properties.
      this.setDBProperties(con);

    } catch (BuildException be) {
      throw be;
    } catch (Exception e) {
      throw new BuildException("Exceptional failure", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (Exception ex) {
        }
      }
    }

  }

  public String getConfigurationDirectory() {
    return configurationDirectory;
  }

  public void setConfigurationDirectory(String configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * This function reads the etlrep database connection details from the file
   * ${CONF_DIR}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database etlrep connection details.
   */
  private HashMap getEtlrepDatabaseConnectionDetails() throws BuildException {

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

  private Connection createETLRepConnection(HashMap etlrepDatabaseConnectionDetails) throws BuildException {
    try {
      Driver driver = (Driver) Class.forName(etlrepDatabaseConnectionDetails.get("etlrepDatabaseDriver").toString())
          .newInstance();

      Properties p = new Properties();
      p.put("user", etlrepDatabaseConnectionDetails.get("etlrepDatabaseUsername").toString());
      p.put("password", etlrepDatabaseConnectionDetails.get("etlrepDatabasePassword").toString());
      p.put("REMOTEPWD", ",,CON=PLAT_INST");

      Connection con = driver.connect(etlrepDatabaseConnectionDetails.get("etlrepDatabaseUrl").toString(), p);

      // This should never happen...
      if (con == null)
        throw new Exception("DB driver initialized null connection object");

      return con;

    } catch (Exception e) {
      throw new BuildException("Database connecting to etlrep failed", e);
    }
  }

  /**
   * This function sets the database connection details to ANT task properties.
   * If etlrep database is requested, database properties are retrieved from
   * ETLCServer.properties.
   */
  private void setDBProperties(Connection con) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      ps = con
          .prepareStatement("SELECT CONNECTION_STRING, USERNAME, PASSWORD, DRIVER_NAME FROM META_DATABASES WHERE CONNECTION_NAME=? AND TYPE_NAME=?");
      ps.setString(1, this.name);
      ps.setString(2, this.type);

      rs = ps.executeQuery();

      if (rs.next()) {

        getProject().setNewProperty(this.name + "DatabaseUrl", rs.getString("CONNECTION_STRING"));
        getProject().setNewProperty(this.name + "DatabaseUsername", rs.getString("USERNAME"));
        getProject().setNewProperty(this.name + "DatabasePassword", rs.getString("PASSWORD"));
        getProject().setNewProperty(this.name + "DatabaseDriver", rs.getString("DRIVER_NAME"));

      } else {
        throw new BuildException("No such database \"" + this.name + "\" of type \"" + type + "\"");
      }

    } catch (BuildException be) {
      throw be;
    } catch (Exception e) {
      throw new BuildException("Failed to load DB properties from etlrep.", e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
