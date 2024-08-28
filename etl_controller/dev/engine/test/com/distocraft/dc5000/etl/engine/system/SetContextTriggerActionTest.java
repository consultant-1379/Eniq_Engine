package com.distocraft.dc5000.etl.engine.system;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.distocraft.dc5000.etl.engine.main.TestISchedulerRMI;

/**
 * 
 * @author ejarsok
 *
 */

public class SetContextTriggerActionTest {

  private SetContextTriggerAction sc = new SetContextTriggerAction();
  
  private static Map<String, String> env = System.getenv();
  
  @BeforeClass
  public static void init() {
    System.setProperty("dc5000.config.directory", env.get("WORKSPACE"));
    File prop = new File(env.get("WORKSPACE"), "ETLCServer.properties");
    prop.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(prop));
      pw.write("name=value");
      pw.close();
    } catch (IOException e3) {
      e3.printStackTrace();
      fail("Can't write in file");
    }
  }
  
  @Test
  public void testIsEqual() {
    //  TODO assertion fix
    try {
      assertEquals(false, sc.isEqual(null, "foobar"));
      assertEquals(true, sc.isEqual(true, "true"));
      assertEquals(false, sc.isEqual(true, "false"));
      assertEquals(false, sc.isEqual("notBoolean", "foobar"));
      assertEquals(true, sc.isEqual(1, "1"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testIsMore() {
    //  TODO assertion fix
    try {
      assertEquals(true, sc.isMore(1, "2"));
      assertEquals(false, sc.isMore(2, "1"));
      assertEquals(false, sc.isMore(null, "2"));  //false if null
      assertEquals(false, sc.isMore(1, null));    //false if null
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsMore() failed");
    }
  }

  @Test
  public void testIsLess() {
    //  TODO assertion fix
    try {
      assertEquals(true, sc.isLess(2, "1"));
      assertEquals(false, sc.isLess(1, "2"));
      assertEquals(false, sc.isLess(null, "2"));  //false if null
      assertEquals(false, sc.isLess(1, null));    //false if null
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsMore() failed");
    }
  }

  @Test
  public void testTrigger() {
    //  TODO assertion fix
    try {
      TestISchedulerRMI tsRMI = new TestISchedulerRMI(false);
      ArrayList l = new ArrayList();
      l.add("tName1");
      l.add("tName2");
      
      sc.trigger(l, ":");
      ArrayList al = tsRMI.getTriggerArrayList();
      
      assertEquals(":tName1", al.get(0));
      assertEquals(":tName2", al.get(1));
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTriggerSchedule() failed");
    }
  }
  
  @Test
  public void testTriggerSchedule() {
    try {
      TestISchedulerRMI tsRMI = new TestISchedulerRMI(false);
      sc.triggerSchedule("tName");
      ArrayList al = tsRMI.getTriggerArrayList();
      
      assertEquals("tName", al.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTriggerSchedule() failed");
    }
  }

  @Test
  public void testContains() {
    //  TODO assertion fix
    HashSet s = new HashSet();
    s.add("foo");
    s.add("bar");
    
    try {
      assertEquals(true, sc.contains(s, "foo,bar"));
      assertEquals(false, sc.contains(s, "not,in,set"));
      assertEquals(false, sc.contains(null, "foo,bar"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  @Test
  public void testStrToList() {
    ArrayList al= new ArrayList();
    ArrayList al2= new ArrayList();
    al2.add("foo");
    al2.add("bar");
    
    al = (ArrayList) sc.strToList("foo,bar");
    assertEquals(true, al.containsAll(al2));
  }

  @Test
  public void testSetToList() {
    HashSet s = new HashSet();
    ArrayList al= new ArrayList();
    s.add("foo");
    s.add("bar");
    
    al = (ArrayList) sc.setToList(s);
    assertEquals(true, s.containsAll(al));
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(SetContextTriggerActionTest.class);
  }*/
}
