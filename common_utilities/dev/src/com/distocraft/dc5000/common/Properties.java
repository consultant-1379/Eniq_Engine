package com.distocraft.dc5000.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on Feb 16, 2005
 * 
 * Convenience class for using Properties. Properties are loaded from a
 * directory.
 * 
 * @author lemminkainen
 */
public class Properties extends java.util.Properties {

  private static Logger log = Logger.getLogger("etlengine.common.Properties");

  private static java.util.Properties props = null;

  private Hashtable localProperties;
  private String source = "";

  /**
   * Constructor. Reads properties from configuration directory specified by
   * dc5000.config.directory system property.
   * 
   * @param source
   *          VendorID used as prefix for each propertyName
   * @param localProperties
   *          Local definition for overwriting values discovered from config
   *          directory
   * @throws Exception
   *           is thrown on initialization failure
   */
  public Properties(String source, Hashtable localProperties) throws Exception {
    super();
    this.source = source;
    this.localProperties = localProperties;

    if (props == null)
      reload();

  }

  public Properties(String source, Hashtable localProperties,java.util.Properties props) throws Exception {
    super();
    this.source = source;
    this.localProperties = localProperties;
    this.props = props;
  }
  
  /**
   * Convinience constructor without overwrite
   * 
   * @param source
   *          VendorID used as prefix for each propertyName
   * @throws Exception
   *           is thrown on initialization failure
   */
  public Properties(String source) throws Exception {
    this(source, new Hashtable());
  }

  /**
   * Returns specified property.
   * 
   * @param name
   *          Name of property
   * @throws NullPointerException
   *           if property was not found
   */
  public String getProperty(String name) {
    String val = getProperty(name, null);

    if (val != null)
      return val;
    else
      throw new NullPointerException("Property " + source + "." + name + " is undefined");

  }

  /**
   * Returns specified property with default.
   * 
   * @param name
   *          Name of property
   * @param defaultValue
   *          Default value used if property value is not found
   */
  public String getProperty(String name, String defaultValue) {

    if (name.equalsIgnoreCase("source"))
      return this.source;

    if (localProperties.containsKey(name))
      return (String) localProperties.get(name);

    if (localProperties.containsKey(source + "." + name))
      return (String) localProperties.get(source + "." + name);

    String val = props.getProperty(source + "." + name, null);

    if (val != null)
      return val;
    else
      return defaultValue;

  }

  /**
   * Reads configuration files from defined configuration directory into
   * cache object.
   * 
   * @throws Exception
   *           is thrown in case of failure
   */
  public static void reload() throws Exception {

    log.fine("Reloading configuration...");

    java.util.Properties xprops = new java.util.Properties();

    File dir = new File(System.getProperty("dc5000.config.directory"));

    File[] list = dir.listFiles(new FileFilter() {
      public boolean accept(File f) {
        if (f.isFile() && f.canRead() && f.getName().endsWith(".properties")
            && !f.getName().equalsIgnoreCase("logging.properties")
            && !f.getName().equalsIgnoreCase("static.properties") && !f.getName().equalsIgnoreCase("torque.properties"))
          return true;
        else
          return false;
      }
    });

    for (int i = 0; i < list.length; i++) {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(list[i]);
        xprops.load(new FileInputStream(list[i]));
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (Exception e) {
            log.log(Level.WARNING, "Error closing file", e);
          }
        }
      }
    } // for each config-file

    log.info("Configuration (re)loaded. " + list.length + " propertyFiles read.");

    props = xprops;
  }

}
