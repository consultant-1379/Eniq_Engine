package com.distocraft.dc5000.etl.engine.executionslots;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.etl.engine.BaseMock;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.main.EngineThread;


public class ExecutionSlotProfileTest extends BaseMock{

  // ExecutionSlotProfile
  private static Field name;
  
  private static Field id;
  
  private static Field executionSlotList;
  
  private static Field active;
  private static Field activeThread;
  private static Field engineThreadWorkerField;
  
  
  // ExecutionSlot
  private static Field runningSet;
  
  EngineThread mockedEngineThread;
  
  @BeforeClass
  public static void init() {
	   Share share = Share.instance();
	   share.add("execution_profile_max_memory_usage_mb", 512);
	   
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    Class secretClass = esp.getClass();
    
    ExecutionSlot es = new ExecutionSlot(0, null);
    Class secretClass2 = es.getClass();
    
    try {
      //    ExecutionSlotProfile
      name = secretClass.getDeclaredField("name");
      id = secretClass.getDeclaredField("id");
      executionSlotList = secretClass.getDeclaredField("executionSlotList");
      active = secretClass.getDeclaredField("active");
     
      name.setAccessible(true);
      id.setAccessible(true);
      executionSlotList.setAccessible(true);
      active.setAccessible(true);
      
      activeThread = EngineThread.class.getDeclaredField("active");
      activeThread.setAccessible(true);
      
      engineThreadWorkerField = EngineThread.class.getDeclaredField("worker");
      engineThreadWorkerField.setAccessible(true);
      
      //    ExecutionSlot
      runningSet = secretClass2.getDeclaredField("runningSet");
      
      runningSet.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Before
  public void setup(){
	  mockedEngineThread = context.mock(EngineThread.class);
  }
  
  @Test
  public void testActivate() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    
    esp.activate();
    try {
      Boolean b = (Boolean) active.get(esp);
      assertTrue(b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testActivate() failed");
    }
  }
 
  @Test
  public void testExecutionSlotProfile_max_memory_usage_mbDoesntExist(){
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    assertNotNull(esp);    
  }

 
  @Test
  public void testExecutionSlotProfile_max_memory_usage_mbSetToSomeValue(){
    Share share = Share.instance();
    share.add("execution_profile_max_memory_usage_mb", 100);
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    assertNotNull(esp);    
  }

  @Test
  public void testAddExecutionSlot() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    
    esp.addExecutionSlot(es);
    
    try {
      Vector v = (Vector) executionSlotList.get(esp);
      assertTrue(v.contains(es));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAddExecutionSlot() failed");
    }
  }

  @Test
  public void testRemoveExecutionSlotExecutionSlot() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    
    try {
      executionSlotList.set(esp, v);
      esp.removeExecutionSlot(es);
      
      Vector rv = (Vector) executionSlotList.get(esp);
      
      assertFalse(rv.contains(es));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveExecutionSlotExecutionSlot() failed");
    }
  }

  @Test
  public void testGetRunningSet() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
//    Logger l = Logger.getLogger("Log");;
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    EngineThread et = new EngineThread("name", "setType1", 10L, to, l);
    et.start();
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetRunningSet() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    EngineThread ret = esp.getRunningSet("name", -1L);
    
    assertEquals("name", ret.getSetName());
  }
  
  @Test
  public void testGetRunningSet2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
//    Logger l = Logger.getLogger("Log");;
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    EngineThread et = new EngineThread("name", "setType1", 10L, to, l);
    et.start();
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetRunningSet2() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    EngineThread ret = esp.getRunningSet("notExist", 100L);
    
    assertNull(ret);
  }

  @Test
  public void testRemoveExecutionSlotInt() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    ExecutionSlot res = esp.removeExecutionSlot(0);
    
    try {
      Vector v = (Vector) executionSlotList.get(esp);
      assertFalse("False expected", v.contains(es));
      assertEquals("ESname1", res.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveExecutionSlotInt() failed");
    }
  }

  @Test
  public void testGetExecutionSlot() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    ExecutionSlot res = esp.getExecutionSlot(0);
    
    assertEquals("ESname1", res.getName());
    
  }

  @Test
  public void testGetAllExecutionSlots() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    Iterator it = esp.getAllExecutionSlots();
    
    String expected = "ESname1,ESname2";
    String actual = "";
    ExecutionSlot res = (ExecutionSlot) it.next();
    actual += res.getName();
    res = (ExecutionSlot) it.next();
    actual += "," + res.getName();
    
    assertEquals(expected, actual);
    
  }
  
  
/*
 * This test is brittle. It sometimes fails because the EngineThread is not started before the 
 * getAllFreeE...() method is called. This causes the isAlive to return false and in turn,
 * report back this this executionSlot is not active/in use. This is not a fault.
 */  
  @Test
  public void testGetAllFreeExecutionSlots() {
	  ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
	  ExecutionSlot es = new ExecutionSlot(0, "ESname1");
	  ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
	  ExecutionSlot es3 = new ExecutionSlot(0, "ESname3");

	  testObject to = new testObject();

	  Logger l = Logger.getLogger("Log");
	  EngineThread et = new EngineThread("type", "setType1", 10L, to, l);

	  try {
		  engineThreadWorkerField.set(et, true);
		  et.start(); // ExecutionSlot es2 is not free anymore
		  runningSet.set(es2, et);
	  } catch (Exception e) {
		  e.printStackTrace();
		  fail("testGetAllFreeExecutionSlots() failed");
	  }

	  esp.addExecutionSlot(es);
	  esp.addExecutionSlot(es2);
	  esp.addExecutionSlot(es3);

	  Iterator it = esp.getAllFreeExecutionSlots();

	  String expected = "ESname1ESname3";
	  String actual = "";

	  while (it.hasNext()) {
		  final ExecutionSlot ex = (ExecutionSlot) it.next();
		  actual += ex.getName();
	  }

	  assertEquals(expected, actual);
  }

  @Test
  public void testGetFirstFreeExecutionSlots() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    ExecutionSlot es3 = new ExecutionSlot(0, "ESname3");
    
//    Logger l = Logger.getLogger("Log");;
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
//    et.start();
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    et.start();
    
    try {
      runningSet.set(es, et);
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetFirstFreeExecutionSlots() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    esp.addExecutionSlot(es3); // only free ExecutionSlot
    
    ExecutionSlot res = esp.getFirstFreeExecutionSlots();
    
    assertEquals("ESname3", res.getName());
  }
  
  @Test
  public void testGetFirstFreeExecutionSlots2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
//    Logger l = Logger.getLogger("Log");;
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
//    et.start();
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    et.start();
    try {
      runningSet.set(es, et);
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetFirstFreeExecutionSlots2() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    ExecutionSlot res = esp.getFirstFreeExecutionSlots();
    
    assertNull("null expected", res);
  }

  @Test
  public void testGetNumberOfExecutionSlots() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    try {
      executionSlotList.set(esp, v);
      
      assertEquals(2, esp.getNumberOfExecutionSlots());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNumberOfExecutionSlots() failed");
    }
  }

  @Test
  public void testGetNumberOfFreeExecutionSlots() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    ExecutionSlot es3 = new ExecutionSlot(0, "ESname3");
    
//    Logger l = Logger.getLogger("Log");;
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
//    et.start();
  
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    et.start();
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNumberOfFreeExecutionSlots() failed");
    }
    
    esp.addExecutionSlot(es); // Free ExecutionSlot
    esp.addExecutionSlot(es2);
    esp.addExecutionSlot(es3); // Free ExecutionSlot
    
    assertEquals(2, esp.getNumberOfFreeExecutionSlots());
  }

  @Test
  public void testGetAllRunningExecutionSlots() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    ExecutionSlot es3 = new ExecutionSlot(0, "ESname3");
    
    //Logger l = Logger.getLogger("Log");;
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
//    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
//    et.start();
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    et.start();
    EngineThread et2 = new EngineThread("type", "setType2", 10L, to, l);
    et2.start();
    
    try {
      runningSet.set(es2, et);
      runningSet.set(es3, et2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetAllRunningExecutionSlots() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2); // Running ExecutionSlot
    esp.addExecutionSlot(es3); // Running ExecutionSlot
    
    Iterator it = esp.getAllRunningExecutionSlots();
    
    String expected = "ESname2,ESname3";
    String actual = "";
    ExecutionSlot res = (ExecutionSlot) it.next();
    actual += res.getName();
    res = (ExecutionSlot) it.next();
    actual += "," + res.getName();
    
    assertEquals(expected, actual);
  }

  /*
   * This is a brittle test.
   */
  @Test
  public void testGetAllRunningExecutionSlotSetTypes() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    ExecutionSlot es3 = new ExecutionSlot(0, "ESname3");
    
    Logger l = Logger.getLogger("Log");;
    testObject to = new testObject();
    
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    et.start();
    EngineThread et2 = new EngineThread("type", "setType2", 10L, to, l);
    et2.start();
    
    try {
      runningSet.set(es2, et);
      runningSet.set(es3, et2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetAllRunningExecutionSlotSetTypes() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2); // Running ExecutionSlot
    esp.addExecutionSlot(es3); // Running ExecutionSlot
    
    HashSet hs = (HashSet) esp.getAllRunningExecutionSlotSetTypes();
    
    assertTrue(hs.contains("setType1"));
    assertTrue(hs.contains("setType2"));
  }

  @Test
  public void testGetAllRunningExecutionSlotWorkers() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    es2.hold();
    
    testObject to = new testObject();
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("type", "setType1", 10L, to, l);
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetAllRunningExecutionSlotWorkers() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    HashSet hs = (HashSet) esp.getAllRunningExecutionSlotWorkers();
    assertTrue("True expected", hs.contains(to));
  }

  @Test
  public void testIDAndSetID() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    
    esp.setID("identification");
    
    assertEquals("identification", esp.ID());
  }

  @Test
  public void testNameAndSetName() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    
    esp.setName("settedName");
    
    assertEquals("settedName", esp.name());
  }

  @Test
  public void testDeactivate() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    
    try {
      active.set(esp, true);
      esp.deactivate();
      Boolean b = (Boolean) active.get(esp);
      
      assertFalse(b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDeactivate() failed");
    }
  }

  @Test
  public void testIsActivate() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    
    assertFalse(esp.IsActivate());
  }

  @Test
  public void testNotInExecution() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    EngineThread et2 = new EngineThread("name", 10L, l, new EngineCom());
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNotInExecution() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    assertFalse("False expected", esp.notInExecution(et2));
  }
  
  @Test
  public void testNotInExecution2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    EngineThread et2 = new EngineThread("name2", 20L, l, new EngineCom());
    
    try {
      runningSet.set(es2, et);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNotInExecution2() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    assertTrue("True expected", esp.notInExecution(et2));
  }

  @Test
  public void testCheckTable() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    EngineThread et2 = new EngineThread("name2", 20L, l, new EngineCom());
    EngineThread et3 = new EngineThread("name2", 20L, l, new EngineCom());
    
    Class secretClass = et.getClass();
    
    try {
      
      Field setTables = secretClass.getDeclaredField("setTables");
      setTables.setAccessible(true);
      
      ArrayList al = new ArrayList();
      al.add("foo");
      al.add("bar");
      ArrayList al2 = new ArrayList();
      al2.add("foo");
      al2.add("bar");
      
      setTables.set(et2, al2);
      setTables.set(et3, al);
      
      runningSet.set(es, et);
      runningSet.set(es2, et2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCheckTable() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    assertTrue("True expected", esp.checkTable(et3));
  }
  
  @Test
  public void testCheckTable2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("ESPname", "id");
    ExecutionSlot es = new ExecutionSlot(0, "ESname1");
    ExecutionSlot es2 = new ExecutionSlot(0, "ESname2");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    EngineThread et2 = new EngineThread("name2", 20L, l, new EngineCom());
    EngineThread et3 = new EngineThread("name2", 20L, l, new EngineCom());
    
    Class secretClass = et.getClass();
    
    try {
      
      Field setTables = secretClass.getDeclaredField("setTables");
      setTables.setAccessible(true);
      
      ArrayList al = new ArrayList();
      al.add("abc");
      al.add("def");
      ArrayList al2 = new ArrayList();
      al2.add("foo");
      al2.add("bar");
      
      setTables.set(et2, al2);
      setTables.set(et3, al);
      
      runningSet.set(es, et);
      runningSet.set(es2, et2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCheckTable2() failed");
    }
    
    esp.addExecutionSlot(es);
    esp.addExecutionSlot(es2);
    
    assertFalse("False expected", esp.checkTable(et3));
  }

  @Test
  public void testAreAllSlotsLockedOrFree() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    try {
      executionSlotList.set(esp, v);
      
      assertTrue("True expected", esp.areAllSlotsLockedOrFree());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAreAllSlotsLockedOrFree() failed");
    }
  }
  
  @Test
  public void testAreAllSlotsLockedOrFree2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("setType1", 10L, l, new EngineCom());
    et.start();
    
    try {
      runningSet.set(es2, et);
      
      executionSlotList.set(esp, v);
      
      assertFalse("False expected", esp.areAllSlotsLockedOrFree());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAreAllSlotsLockedOrFree2() failed");
    }
  }

  @Test
  public void testCleanProfile() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    es.removeAfterExecution(true);
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    
    try {
      executionSlotList.set(esp, v);
      
      esp.cleanProfile();
      
      Vector rv = (Vector) executionSlotList.get(esp);
      
      assertFalse("False expected", rv.contains(es));
      assertTrue("True expected", rv.contains(es2));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCleanProfile() failed");
    }
  }

  @Test
  public void testIsProfileClean() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    es.removeAfterExecution(true);
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    
    try {
      executionSlotList.set(esp, v);
      
      assertFalse("False expected", esp.isProfileClean());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsProfileClean() failed");
    }
  }
  
  @Test
  public void testIsProfileClean2() {
    ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
    ExecutionSlot es = new ExecutionSlot(0, "name");
    ExecutionSlot es2 = new ExecutionSlot(0, "name");
    
    Vector v = new Vector();
    v.add(es);
    v.add(es2);
    
    
    try {
      executionSlotList.set(esp, v);
      
      assertTrue("True expected", esp.isProfileClean());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsProfileClean2() failed");
    }
  }
  
  private class testObject implements Runnable {

    public void run() {
      System.out.println("testObject started");
      
    }
    
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ExecutionSlotProfileTest.class);
  }*/
}
