package com.distocraft.dc5000.common;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class PropertiesTest {

  private static Properties p;

  private static Properties p2;

  private static String source;

  private static Map<String, String> env = System.getenv();
  
  @BeforeClass
  public static void init() {
	String homeDir = env.get("WORKSPACE");
    System.setProperty("dc5000.config.directory", homeDir);

    File prop = new File(homeDir, "test.properties");
    prop.deleteOnExit();

    source = "value";
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(prop));
      pw.print(source + ".logProperty=log");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Can´t write in file!");
    }

    Hashtable hT = new Hashtable();
    hT.put("property1", "value1");
    hT.put(source + ".property2", "value2");

    try {
      p = new Properties(source);
      p2 = new Properties(source, hT);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testProperties() {
    assertEquals(source, p.getProperty("source")); // Return String source
  }

  @Test
  public void testProperties2() {
    assertEquals("log", p.getProperty("logProperty")); // Return
    // "logProperty" value
    // from file
  }

  @Test
  public void testProperties3() {
    assertEquals("value1", p2.getProperty("property1")); // Return "property1"
    // value from
    // hashTable
  }

  @Test
  public void testProperties4() {
    assertEquals("value2", p2.getProperty("property2")); // Return source + "."
    // + "property2" value
    // from hashTable
  }

  @Test
  public void testProperties5() {
    assertEquals("default", p2.getProperty("notExists", "default")); // Return
    // default
    // value
    // "default"
  }

  @Test
  public void testProperties6() {
    try {
      assertEquals("Value", p2.getProperty("notExists")); // property value not
      // exists, catch
      // NullPointerException
      fail("Should not execute this");
    } catch (NullPointerException e) {

    } catch (Exception e) {
      e.printStackTrace();
      fail("testProperties failed");
    }
  }

  @Test
  public void testProperties7() {
    try {
      assertEquals("Value", p2.getProperty(null));
      // null, catch
      // NullPointerException
      fail("Should not execute this");
    } catch (NullPointerException e) {

    } catch (Exception e) {
      e.printStackTrace();
      fail("testProperties failed");
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(PropertiesTest.class);
  }
}
