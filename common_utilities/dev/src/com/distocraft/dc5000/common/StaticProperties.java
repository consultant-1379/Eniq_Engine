package com.distocraft.dc5000.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on Feb 18, 2005
 * 
 * A property implementation that lies as static entity on ETLC.
 * 
 * @author lemminkainen
 */
public class StaticProperties {

  private static final String PROPERTYFILENAME = "static.properties";

  private static Logger log = Logger.getLogger("etlengine.common.StaticProperties");

  private static java.util.Properties props = null;

  public StaticProperties() {
  }

  /**
   * Returns the value of defined property
   * 
   * @param name
   *          name of property
   * @return value of property
   * @throws NoSuchFieldException
   *           is thrown if property is not defined
   */
  public static String getProperty(String name) throws NoSuchFieldException {
    if (props == null)
      throw new NullPointerException("StaticProperties class is not initialized");

    String value = props.getProperty(name);
    if (value != null)
      return value;
    else
      throw new NoSuchFieldException("Property " + name + " not defined in " + PROPERTYFILENAME);
  }

  /**
   * Returns the value of defined property with default value.
   * 
   * @param name
   *          name of property
   * @param defaultValue
   *          default value of property that is used if property is not defined
   *          on config-file.
   * @return value of property
   */
  public static String getProperty(String name, String defaultValue) {
    if (props == null)
      throw new NullPointerException("StaticProperties class is not initialized");

    return props.getProperty(name, defaultValue);
  }

  /**
   * Reloads the configuration file
   * 
   * @throws Exception
   *           thrown in case of failure
   */
  public void reload() throws Exception {
    try {
      java.util.Properties nprops = new java.util.Properties();

      String confDir = getSystemProperty("dc5000.config.directory");
      
      if(confDir == null)
        throw new NullPointerException("System property dc5000.config.directory must be defined");
      
      if (!confDir.endsWith(File.separator))
        confDir += File.separator;

      File confFile = createNewFile(confDir + PROPERTYFILENAME);

      if (!confFile.exists() && !confFile.isFile() && !confFile.canRead())
        throw new IOException("Unable to read configFile from "+confFile.getCanonicalPath());

      FileInputStream fis = null;
      
      try {
        fis = new FileInputStream(confDir + PROPERTYFILENAME);
        nprops.load(fis);
      } finally {
        if(fis != null) {
          try {
            fis.close();
          } catch(Exception e) {
            log.log(Level.WARNING,"Error closing file",e);
          }
        }
      }
      
      props = nprops;
      
      log.info("Configuration successfully (re)loaded.");

    } catch (Exception e) {
      log.log(Level.WARNING,"Reading config file failed",e);
      throw e;
    }

  }

  /**
   * Reloads the configuration file
   * 
   * @throws Exception
   *           thrown in case of failure
   */
  public static void giveProperties(java.util.Properties nprops) throws Exception {
    props = nprops;
  }

  /**
   * Updates the property name and value
   * 
   * @param updatedName - Property name
   * @param updatedValue - Property value
   * @throws Exception 
   */
  public void setProperty(String updatedName, String updatedValue) {
    if (props == null) {
      log.log(Level.WARNING, "Error setting properties. Static properties not initialized.");
    }

    // run the new save method here
    try {
      props.setProperty(updatedName, updatedValue);
      final boolean savedOk = save();
      if (!savedOk) {
        log.log(Level.WARNING, "Error setting properties");
      }
      reload();
    } catch (Exception e) {
      log.log(Level.WARNING, "Error setting properties", e);
    }
  }
  
  /**
   * Saves the updated threshold value to the file
   * @return 
   * @throws IOException 
   * 
   */
  public boolean save() {
    boolean savedOK = false;

    String confDir = getSystemProperty("dc5000.config.directory");

    if (confDir == null) {
      log.log(Level.WARNING, "System property dc5000.config.directory must be defined");
      savedOK = false;
      return savedOK;      
    }

    if (!confDir.endsWith(File.separator))
      confDir += File.separator;

    final File confFile = createNewFile(confDir + PROPERTYFILENAME);
    if (!confFile.exists() && !confFile.isFile() && !confFile.canRead()) {
      log.log(Level.WARNING, "Error reading configFile");
      savedOK = false;
      return savedOK;
    }

    try {
      FileOutputStream fos = createFileOutputStream(confDir + PROPERTYFILENAME);
      props.store(fos, "Saving static.properties");
      savedOK = true;
    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing file", e);
      savedOK = false;
    }
    return savedOK;
  }

  /**
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  protected FileOutputStream createFileOutputStream(final String fileName) throws FileNotFoundException {
    FileOutputStream fos = null;
    fos = new FileOutputStream(fileName);
    return fos;
  }

  /**
   * @param confDir
   * @return
   */
  protected File createNewFile(String confDir) {
    File confFile = new File(confDir);
    return confFile;
  }

  /**
   * @param propertyName
   * @return
   */
  protected String getSystemProperty(final String propertyName) {
    String propertyValue = System.getProperty(propertyName);
    return propertyValue;
  }
}
