package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

/**
 * This class is a custom made ANT-task that changes the permissions of dcbo and
 * dcpublic users. They can be either locked or unlocked. Copyright (c) 1999 -
 * 2008 AB LM Ericsson Oy All rights reserved.
 * 
 * @author ejannbe
 */
public class ChangeDBUsersPermissions extends Task {

  RockFactory etlrepRockFactory = null;

  protected String propertiesFilepath = new String("");

  private String configurationDirectory = new String("");

  RockFactory dwhRockFactory = null;

  private String action = new String("");

  private String dbUser = new String("");

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
    final HashMap<String,String> databaseConnectionDetails = getDatabaseConnectionDetails();

    // Create the connection to the etlrep.
    this.etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);
    // Create also the connection to dwhrep.
    this.createDwhRockFactory();

    // Do the actual change of the db users permissions.
    int result = this.changeDBPermissions();
    
    if(result == 0) {
      System.out.println("Database user permission changed successfully.");
    } else {
      System.out.println("Changing database user permission failed. Please see log for more details.");
      System.exit(result);
    }
    
  }

  /**
   * This function reads the database connection details from the file
   * ${configurationDirectory}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database connection details.
   */
  private HashMap<String,String> getDatabaseConnectionDetails() {
    final HashMap<String,String> databaseConnectionDetails = new HashMap<String,String>();

    try {
      final File targetFile = new File(propertiesFilepath);
      if (targetFile.isFile() == false || targetFile.canRead() == false) {
        throw new BuildException("Could not read database properties. Please check that the file " + propertiesFilepath
            + " exists and it can be read.");
      }

      final BufferedReader reader = new BufferedReader(new FileReader(targetFile));

      final Properties props = new Properties();
      props.load(reader);
      
      reader.close();

      databaseConnectionDetails.put("etlrepDatabaseUrl", props.getProperty("ENGINE_DB_URL"));
      databaseConnectionDetails.put("etlrepDatabaseUsername", props.getProperty("ENGINE_DB_USERNAME"));
      databaseConnectionDetails.put("etlrepDatabasePassword", props.getProperty("ENGINE_DB_PASSWORD"));
      databaseConnectionDetails.put("etlrepDatabaseDriver", props.getProperty("ENGINE_DB_DRIVERNAME"));
      
      // Set the database connection properties as ANT properties.
      // WHY? o WHY? GetDBProperties does it later...
      final Iterator<String> databaseConnectionDetailsIterator = databaseConnectionDetails.keySet().iterator();
      while (databaseConnectionDetailsIterator.hasNext()) {
        final String property = (String) databaseConnectionDetailsIterator.next();
        final String value = (String) databaseConnectionDetails.get(property);
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
  private RockFactory createEtlrepRockFactory(final HashMap<String,String> databaseConnectionDetails) throws BuildException {
    
    RockFactory rockFactory = null;

    final String databaseUsername = databaseConnectionDetails.get("etlrepDatabaseUsername").toString();
    final String databasePassword = databaseConnectionDetails.get("etlrepDatabasePassword").toString();
    final String databaseUrl = databaseConnectionDetails.get("etlrepDatabaseUrl").toString();
    final String databaseDriver = databaseConnectionDetails.get("etlrepDatabaseDriver").toString();

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver,
          "ChangeDBUsersPermissions", true);

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
   * This function creates the RockFactory to dwh. The created RockFactory is
   * inserted in class variable dwhRockFactory.
   */
  private void createDwhRockFactory() {
    try {
      final Meta_databases whereMetaDatabases = new Meta_databases(this.etlrepRockFactory);
      whereMetaDatabases.setConnection_name("dwh");
      whereMetaDatabases.setType_name("DBA");
      final Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.etlrepRockFactory, whereMetaDatabases);
      final Vector metaDatabases = metaDatabasesFactory.get();

      if (metaDatabases != null && metaDatabases.size() == 1) {
        final Meta_databases targetMetaDatabase = (Meta_databases) metaDatabases.get(0);

        this.dwhRockFactory = new RockFactory(targetMetaDatabase.getConnection_string(), targetMetaDatabase
            .getUsername(), targetMetaDatabase.getPassword(), targetMetaDatabase.getDriver_name(),
            "ChangeDBUsersPermissions", true);

      } else {
        throw new BuildException("Unable to connect metadata (No dwh or multiple dwhs defined in Meta_databases)");
      }
    } catch(final RockException re) {
      re.printStackTrace();
      final Throwable t = re.getNestedException();
      if(t != null) {
        System.err.println("Caused by");
        t.printStackTrace();
      }
      throw new BuildException("Creating database connection to dwh failed as dba.", re);
    } catch (final Exception e) {
      e.printStackTrace();
      throw new BuildException("Creating database connection to dwh failed as dba.", e);
    }
  }

  public String getConfigurationDirectory() {
    return configurationDirectory;
  }

  public void setConfigurationDirectory(final String configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }

  /**
   * This function changes the permissions of users dcbo and dcpublic to the dwh
   * database. These users can be either locked or unlocked.
   */
  private int changeDBPermissions() {

    Statement stmnt = null;

    try {
      if (this.action == null || this.action.equalsIgnoreCase("")) {
        System.out
            .println("Parameter action was empty or null. Please specify the action to be performed to the database users.");
        return 1;
      }
      if (this.dbUser == null || this.dbUser.equalsIgnoreCase("")) {
        System.out.println("Parameter databaseuser was empty or null. Please specify the database user.");
        return 2;
      }

      stmnt = this.dwhRockFactory.getConnection().createStatement();
      ResultSet rs = null;

      if (this.dbUser.equalsIgnoreCase("dcpublic") || this.dbUser.equalsIgnoreCase("dcbo")
          || this.dbUser.equalsIgnoreCase("all")) {
        if (this.action.equalsIgnoreCase("lock")) {
          // Lock users

          if (this.dbUser.equalsIgnoreCase("all")) {
            // Lock all
            rs = stmnt.executeQuery("CALL lock_user('dcbo');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL lock_user('dcpublic');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            // Drop the existing connections
            rs = stmnt.executeQuery("CALL drop_user_connections('dcbo');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL drop_user_connections('dcpublic');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

          } else {
            // Lock specific user
            rs = stmnt.executeQuery("CALL lock_user('" + this.dbUser + "');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            // Drop the existing connections
            rs = stmnt.executeQuery("CALL drop_user_connections('" + this.dbUser + "');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();
          }

        } else if (this.action.equalsIgnoreCase("unlock")) {
          // Unlock users

          if (this.dbUser.equalsIgnoreCase("all")) {
            // Unlock all
            rs = stmnt.executeQuery("CALL unlock_user('dcbo');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL unlock_user('dcpublic');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

          } else {
            // Unlock specific user
            rs = stmnt.executeQuery("CALL unlock_user('" + this.dbUser + "');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();
          }

        } else {
          System.out.println("Parameter action was unknown with value " + action);
          return 3;
        }

      } else {
        System.out
            .println("Incorrect database user parameter. Please use either dcpublic, dcbo or all as dbuser parameter.");
        return 4;
      }

      // All is fine, return 0.
      return 0;
      
    } catch (Exception e) {
      System.out.println("Changing database users permissions failed.");
      e.printStackTrace();
      return 5;
    } finally {

      if (stmnt != null) {
        try {
          stmnt.close();
        } catch (Exception e) {
          System.out.println("Error while closing SQL Statement object.");
          e.printStackTrace();
        }
      }

      // Close the database connections if they are still opened.
      if (this.dwhRockFactory != null) {
        if (this.dwhRockFactory.getConnection() != null) {
          try {
            this.dwhRockFactory.getConnection().close();
          } catch (Exception e) {
            // Don't mind if the connection is already closed.
          }

        }
      }

      if (this.etlrepRockFactory != null) {
        if (this.etlrepRockFactory.getConnection() != null) {
          try {
            this.etlrepRockFactory.getConnection().close();
          } catch (Exception e) {
            // Don't mind if the connection is already closed.
          }
        }
      }
    }

  }

  public String getAction() {
    return action;
  }

  public void setAction(final String action) {
    this.action = action;
  }

  public String getDbUser() {
    return dbUser;
  }

  public void setDbUser(final String dbUser) {
    this.dbUser = dbUser;
  }

}
