package com.distocraft.dc5000.install.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;

import org.apache.tools.ant.BuildException;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;

/**
 * 
 * @author ejarsok
 * 
 */

public class UpdatePropertiesTest {

  private static final String propFile = System.getProperty("user.home") + File.separator + "propertyFile";

  private static UpdateProperties uP = new UpdateProperties();

  private static File prop;

  @BeforeClass
  public static void init() {
    uP.setPropertiesFile(propFile);
    prop = new File(propFile);
    prop.deleteOnExit();
    
    PrintWriter pw;
    try {
      pw = new PrintWriter(new FileWriter(prop));
      pw.write("property=value\n");
      pw.write("property2=value2\n");
      pw.write("deleteThis=notDeleted\n");
      pw.write("property_copy=overwrite\n");
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail("Failed, Can't write in file");
    }
  }

  @Test
  public void testSetAndGetAction() {
    uP.setAction("set_action");
    assertEquals("set_action", uP.getAction());
  }
  
  @Test
  public void testSetAndGetKey() {
    uP.setKey("set_key");
    assertEquals("set_key", uP.getKey());
  }
  
  @Test
  public void testSetAndGetValue() {
    uP.setValue("set_value");
    assertEquals("set_value", uP.getValue());
  }
  
  @Test
  public void testSetAndGetForceCopy() {
    uP.setForceCopy("set_forceCopy");
    assertEquals("set_forceCopy", uP.getForceCopy());
  }
  
  @Test
  public void testSetAndGetTargetKey() {
    uP.setTargetKey("set_targetKey");
    assertEquals("set_targetKey", uP.getTargetKey());
  }
  
  @Test
  public void testSetAndGetPropertieFile() {
    assertEquals(propFile, uP.getPropertiesFile());
  }

  /**
   * Test method add one property value to properties file 
   *
   */
  
  @Test
  public void testExecuteAdd() {
    uP.setAction("add");
    uP.setKey("property1");
    uP.setValue("value1");
    uP.execute();

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(prop));
      assertEquals("value1", props.getProperty("property1"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, testExecuteAdd() Exception");
    }
  }

  /**
   * Test method remove one property value from properties file 
   *
   */
  
  @Test
  public void testExecuteRemove() {
    uP.setAction("remove");
    uP.setKey("deleteThis");
    uP.execute();

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(prop));
      assertEquals(null, props.getProperty("deleteThis"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, testExecuteAdd() Exception");
    }
  }

  /**
   * Test method update one property value in properties file 
   *
   */
  
  @Test
  public void testExecuteUpdate() {
    uP.setAction("update");
    uP.setKey("property");
    uP.setValue("updated_value");
    uP.execute();

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(prop));
      assertEquals("updated_value", props.getProperty("property"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, testExecuteAdd() Exception");
    }
  }

  /**
   * Test method create copy from one property value 
   *
   */
  
  @Test
  public void testExecuteCopy() {
    uP.setAction("copy");
    uP.setKey("property2");
    uP.setTargetKey("property_copy");
    uP.setForceCopy("true");
    uP.execute();
    uP.setKey("property2");
    uP.setTargetKey("property_copy2");
    uP.execute();

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(prop));
      assertEquals("value2", props.getProperty("property_copy"));
      assertEquals("value2", props.getProperty("property_copy2"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, testExecuteAdd() Exception");
    }
  }
  
  @Test
  public void testExeptions1() {
    UpdateProperties uP = new UpdateProperties();
    
    try {
      uP.execute();
      fail("1. Should not execute this line, Expected: Both inputFile and action must not be defined");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExeptions2() {
    UpdateProperties uP = new UpdateProperties();
    
    uP.setPropertiesFile("foobar");
    uP.setAction("action");

    try {
      uP.execute();
      fail("2. Should not execute this line, Expected: InputFile cannot be read or not file");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExeptions3() {
    UpdateProperties uP = new UpdateProperties();
    
    uP.setAction("action");
    uP.setPropertiesFile(propFile);

    try {
      uP.execute();
      fail("3. Should not execute this line, Expected: parameter key must not be defined");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExeptions4() {
    UpdateProperties uP = new UpdateProperties();
    
    uP.setPropertiesFile(propFile);
    uP.setKey("key");
    uP.setAction("add");

    try {
      uP.execute();
      fail("4. Should not execute this line, Expected: parameter value must not be defined");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExeptions5() {
    UpdateProperties uP = new UpdateProperties();
    
    uP.setPropertiesFile(propFile);
    uP.setAction("update");
    uP.setKey("key");

    try {
      uP.execute();
      fail("5. Should not execute this line, Expected: parameter value must not be defined");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExeptions6() {
    UpdateProperties uP = new UpdateProperties();
    
    uP.setPropertiesFile(propFile);
    uP.setAction("copy");
    uP.setKey("key");

    try {
      uP.execute();
      fail("6. Should not execute this line, Expected: parameter targetKey must not be defined");
    } catch (BuildException be) {
      // System.out.println(be.getMessage());
    }
  }
  
  @Test
  public void testExecuteAddNewProperty() {
    uP.setAction("addNewProperty");
    uP.setKey("propertyNew");
    uP.setValue("valueNew");
    uP.execute();

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(prop));
      assertEquals("valueNew", props.getProperty("propertyNew"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, testExecuteAddNewProperty() Exception");
    }
  }

  @AfterClass
  public static void clean() {
    System.gc();
    File prop = new File(propFile);
    prop.delete();
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(UpdatePropertiesTest.class);
  }
}
