package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Hashtable;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.Project;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 * 
 * @author ejarsok
 *
 */

public class LsToPropertyTest {

  private static String homeDir;
  
  @BeforeClass
  public static void init() {
    homeDir = System.getProperty("user.home");
    
    File f1 = new File(homeDir, "fFile1");
    File f2 = new File(homeDir, "File2");
    File f3 = new File(homeDir, "foobar");
    
    f1.deleteOnExit();
    f2.deleteOnExit();
    f3.deleteOnExit();

    try {
      PrintWriter pw = new PrintWriter(new FileWriter(f1));
      pw.print("foobar1");
      pw.close();
      pw = new PrintWriter(new FileWriter(f2));
      pw.print("foobar2");
      pw.close();
      pw = new PrintWriter(new FileWriter(f3));
      pw.print("foobar3");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Can´t write in file!");
    }
  }
  
  /**
   * Test method create property value from file name which ends with 'File1'
   *
   */
  
  @Test
  public void testExecute() {
    LsToProperty ltp = new LsToProperty();
    ltp.setDir(homeDir);
    ltp.setPattern("*File1");
    ltp.setProperty("PROPERTY");
    
    Project proj = new Project();
    ltp.setProject(proj);
    
    ltp.execute();
    
    proj = ltp.getProject();
    Hashtable p = proj.getProperties();
    assertEquals("fFile1", p.get("PROPERTY"));
  }
  
  /**
   * Test method create property value from file name which starts with 'File'
   *
   */
  
  @Test
  public void testExecute2() {
    LsToProperty ltp = new LsToProperty();
    ltp.setDir(homeDir);
    ltp.setPattern("File*");
    ltp.setProperty("PROPERTY");
    
    Project proj = new Project();
    ltp.setProject(proj);
    
    ltp.execute();
    
    proj = ltp.getProject();
    Hashtable p = proj.getProperties();
    assertEquals("File2", p.get("PROPERTY"));
  }
  
  /**
   * Test method create property value from file name which name equals pattern 'foobar'
   *
   */
  
  @Test
  public void testExecute3() {
    LsToProperty ltp = new LsToProperty();
    ltp.setDir(homeDir);
    ltp.setPattern("foobar");
    ltp.setProperty("PROPERTY");
    
    Project proj = new Project();
    ltp.setProject(proj);
    
    ltp.execute();
    
    proj = ltp.getProject();
    Hashtable p = proj.getProperties();
    assertEquals("foobar", p.get("PROPERTY"));
  }

  @Test
  public void testSetDir() {
    LsToProperty ltp = new LsToProperty();
    Class secretClass = ltp.getClass();
    
    try {
      Field dir = secretClass.getDeclaredField("dir");
      
      dir.setAccessible(true);
      
      ltp.setDir("DIR");
      
      assertEquals("DIR", dir.get(ltp));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetters() failed, Exception");
    } 
  }
  
  @Test
  public void testSetProperty() {
    LsToProperty ltp = new LsToProperty();
    Class secretClass = ltp.getClass();
    
    try {
      Field property = secretClass.getDeclaredField("property");
      
      property.setAccessible(true);
      
      ltp.setProperty("PROPERTY");
      
      assertEquals("PROPERTY", property.get(ltp));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetters() failed, Exception");
    } 
  }
  
  @Test
  public void testSetPattern() {
    LsToProperty ltp = new LsToProperty();
    Class secretClass = ltp.getClass();
    
    try {
      Field pattern = secretClass.getDeclaredField("pattern");
      
      pattern.setAccessible(true);

      ltp.setPattern("PATTERN");

      assertEquals("PATTERN", pattern.get(ltp));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetters() failed, Exception");
    } 
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(LsToPropertyTest.class);
  }
}
