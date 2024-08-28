package com.distocraft.dc5000.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import com.distocraft.dc5000.common.StaticProperties;

/**
 *
 * @author ejarsok
 *
 */
public class StaticPropertiesTest {

  protected Mockery context = new JUnit4Mockery();
  {
    // we need to mock classes, not just interfaces.
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }
  
  private static Properties prop;

  private static String homeDir;

  
  private FileOutputStream fosmock;

  private File fileMock;
  
  @BeforeClass
  public static void setup() throws Exception {

    Map<String, String> env = System.getenv();

   homeDir = env.get("WORKSPACE");
   if (homeDir == null) {
     homeDir = "";     
   }

    // File sp = File.createTempFile("static.properties", "");
    // File sp = File.createTempFile("static", "properties", new File(homeDir));
    File sp = new File(homeDir, "static.properties");
    sp.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(sp));
      pw.print("foo=bar");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Can´t write in file!");
    }

    prop = new Properties();
    prop.setProperty("property1", "value1");
    StaticProperties.giveProperties(null);
    System.clearProperty("dc5000.config.directory");
  }


  @Test
  public void testGetProperty() {

    try {
      StaticProperties.getProperty("property1"); // Should catch
      // NullPointerException
      fail("Failed, getProperty should throw NullPointerException");
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {

    }
  }

  @Test
  public void testGetProperty2() {

    try {
      StaticProperties.getProperty("property", "value"); // Should catch
      // NullPointerException
      fail("Failed, getProperty should throw NullPointerException");
    } catch (NullPointerException e) {

    }
  }

  @Test
  public void testGetProperty3() {

    try {
      StaticProperties.giveProperties(prop);
      assertEquals("value1", StaticProperties.getProperty("property1")); // Should
      // catch
      // NoSuchFieldException
      StaticProperties.getProperty("notExist");
      fail("Failed, should not execute this");
    } catch (NoSuchFieldException e) {

    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, Exception");
    }
  }

  @Test
  public void testGetProperty4() {
    assertEquals("value", StaticProperties.getProperty("notExist", "value")); // return
    // default
    // value
  }

  @Test
  public void testReload() {

    try {
      new StaticProperties().reload(); // Should catch NullPointerException,
      // System.getProperty("dc5000.config.directory");
      // not set yet
      fail("Failed, should not execute this");
    } catch (NullPointerException e) {

    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, Exception");
    }
  } 
  
  @Test
  public void testReload2() {
    System.setProperty("dc5000.config.directory", homeDir);
    try {
      new StaticProperties().reload();
      assertEquals("bar", StaticProperties.getProperty("foo")); // return
      // property foo
      // value from
      // file
    } catch (Exception e) {
      e.printStackTrace();
      fail("Failed, Exception");
    }
  }
  
    
  @Test
  public void testSetProperty() throws Exception {
    try {
      System.setProperty("dc5000.config.directory", homeDir);

      StaticProperties.giveProperties(prop);

      String propName = "testName";
      String propValue = "testValue";

      new StaticProperties().setProperty(propName, propValue);

      assertEquals(propValue, StaticProperties.getProperty(propName));
    } catch (Exception setexc) {
      setexc.printStackTrace();
      fail("Failed, Exception");
    }

  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(StaticPropertiesTest.class);
  }
  
  @Test
  public void testSave() throws IOException {
    
    fosmock = context.mock(FileOutputStream.class);
    fileMock = context.mock(File.class);

    // Expect the following calls:
    context.checking(new Expectations() {
      {
        allowing(fileMock).exists();
        will(returnValue(true));
        
        allowing(fileMock).isFile();
        will(returnValue(true));
        
        allowing(fileMock).canRead();
        will(returnValue(true));
        
        allowing(fosmock);
      }
    });
    
  StaticProperties testInstance = new StaticProperties(){
 
    protected FileOutputStream createFileOutputStream(final String fileName) throws FileNotFoundException {      
      return fosmock;
    }
    
    protected File createNewFile(String confDir) {
      return fileMock;
    }
    
    protected String getSystemProperty(final String propertyName) {
      return "";
    }
    
  };

  boolean result = testInstance.save();
  assertTrue("Result should be true for successful properties save.", result);
     
  }
  
  @Test
  public void testSaveFileNotThere() throws IOException {
    
    fosmock = context.mock(FileOutputStream.class);
    fileMock = context.mock(File.class);

    // Expect the following calls:
    context.checking(new Expectations() {
      {
        allowing(fileMock).exists();
        will(returnValue(false));
        
        allowing(fileMock).isFile();
        will(returnValue(false));
        
        allowing(fileMock).canRead();
        will(returnValue(false));
        
        oneOf(fileMock).getCanonicalPath();
        will(returnValue(""));
        
        allowing(fosmock);
      }
    });
    
  StaticProperties testInstance = new StaticProperties(){
 
    protected FileOutputStream createFileOutputStream(final String fileName) throws FileNotFoundException {      
      return fosmock;
    }
    
    protected File createNewFile(String confDir) {
      return fileMock;
    }
    
    protected String getSystemProperty(final String propertyName) {
      return "";
    }
    
  };
  boolean result = testInstance.save();
  assertFalse("Result should be false if there is an error saving properties.", result);
     
  }
  
  
}
