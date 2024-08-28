package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;


public class IntervalDSTModifier extends Task {
  private RockFactory etlrepRockFactory = null;

  /**
   * This function starts the daylight savings
   */
  public void execute() throws BuildException {
    try {
      fixDSTScheduling(); 
    } catch (Exception e) {
      throw new BuildException("Interval schedule fixing failed.", e);
    }
  }
  
  /**
   * This method fixes over 1 hour schedulings when DST has changed in
   * schedulings.
   * 
   * @throws Exception
   */
  private void fixDSTScheduling() throws Exception {
    
    try {
      HashMap databaseConnectionDetails = getDatabaseConnectionDetails();
      etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);
      final GregorianCalendar currentCalendar = new GregorianCalendar();
      Meta_schedulings whereMetaSchedulings = new Meta_schedulings(etlrepRockFactory);
      whereMetaSchedulings.setExecution_type("interval");
      Meta_schedulingsFactory metaSchedulingsFact = new Meta_schedulingsFactory(etlrepRockFactory,
          whereMetaSchedulings);

      Vector metaSchedulings = metaSchedulingsFact.get();
      if (metaSchedulings != null) {
        for(int i = 0; i < metaSchedulings.size();i++) {
          try {
            Meta_schedulings targetMetaScheduling = (Meta_schedulings) metaSchedulings.get(i);
            if (targetMetaScheduling.getInterval_hour() != null 
                && targetMetaScheduling.getInterval_hour().intValue() >= 1) {
              int year = 0;
              int month = 0;
              int day = 0;
              int hour = 0;
              int minute = 0;
              if (targetMetaScheduling.getScheduling_year() != null) {
                year = targetMetaScheduling.getScheduling_year().intValue();
              }
              if (targetMetaScheduling.getScheduling_month() != null) {
                month = targetMetaScheduling.getScheduling_month().intValue();
              }
              if (targetMetaScheduling.getScheduling_day() != null) {
                day = targetMetaScheduling.getScheduling_day().intValue();
              }
              if (targetMetaScheduling.getScheduling_hour() != null) {
                hour = targetMetaScheduling.getScheduling_hour().intValue();
              }
              if (targetMetaScheduling.getScheduling_min() != null) {
                minute = targetMetaScheduling.getScheduling_min().intValue();
              }
              final GregorianCalendar initialCalendar = new GregorianCalendar();
              initialCalendar.set(year, month, day, hour, minute);
              int offSetHour = (currentCalendar.get(Calendar.ZONE_OFFSET) + currentCalendar.get(Calendar.DST_OFFSET) - initialCalendar.get(Calendar.ZONE_OFFSET) - initialCalendar.get(Calendar.DST_OFFSET)) / (1000 * 60 * 60);
              if (offSetHour != 0) {
                final GregorianCalendar lastExecuted = new GregorianCalendar();
                if (targetMetaScheduling.getLast_execution_time() != null) {
                  lastExecuted.setTimeInMillis(targetMetaScheduling.getLast_execution_time().getTime());
                  lastExecuted.add(Calendar.HOUR,-offSetHour);
                  targetMetaScheduling.setLast_execution_time(new Timestamp(lastExecuted.getTimeInMillis()));
                  targetMetaScheduling.updateDB();
                }
              }
            }
          } catch (Exception e) {
            System.out.println("Scheduling updation failed.");
          }
        }
      }
    } finally {
      if (etlrepRockFactory != null) {
        try {
          etlrepRockFactory.getConnection().close();
        } catch (Exception e) {
        }
      }
    }
  }
  
  /**
   * This function reads the database connection details from the file
   * ${CONF_DIR}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database connection details.
   */
  private HashMap getDatabaseConnectionDetails() throws Exception {
    HashMap databaseConnectionDetails = new HashMap();

    String confDirPath = System.getProperty("CONF_DIR");

    if (confDirPath == null) {
      System.out.println("System property CONF_DIR not defined. Using default /eniq/sw/conf");
      confDirPath = "/eniq/sw/conf";
    }

    if (confDirPath.endsWith(File.separator) == false) {
      confDirPath = confDirPath + File.separator;
    }
    String propertiesFilepath = confDirPath + "ETLCServer.properties";

    try {
      File targetFile = new File(propertiesFilepath);
      if (targetFile.isFile() == false || targetFile.canRead() == false) {
        throw new Exception("Could not read database properties. Please check that the file " + propertiesFilepath
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
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Connection to database failed.", e);
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
  private RockFactory createEtlrepRockFactory(HashMap databaseConnectionDetails) throws Exception {
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
      throw new Exception("Unable to initialize database connection.", e);
    }

    if (rockFactory == null)
      throw new Exception(
      "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");

    return rockFactory;
  }
}
