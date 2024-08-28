package com.distocraft.dc5000.etl.engine.executionslots;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;

import junit.framework.JUnit4TestAdapter;

/**
 * 
 * @author ejarsok
 *
 */

public class ExecutionSlotTest {

  private static Field hold;
  
  private static Field runningSet;
  
  private static Field removeAfterExecution;
  
  private static Field startTime;
  
  private static Field approvedSettypes;
  
  private static Statement stm;
  
  @BeforeClass
  public static void init() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("init() failed, ClassNotFoundException");
    }

    Connection c;
    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(20), COLLECTION_SET_NAME VARCHAR(20),"
          + "DESCRIPTION VARCHAR(20), VERSION_NUMBER VARCHAR(20), ENABLED_FLAG VARCHAR(20), TYPE VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");
      
      
      stm.execute("CREATE TABLE Meta_collections (COLLECTION_ID BIGINT, COLLECTION_NAME VARCHAR(20),"
          + "COLLECTION VARCHAR(20), MAIL_ERROR_ADDR VARCHAR(20), MAIL_FAIL_ADDR VARCHAR(20), MAIL_BUG_ADDR VARCHAR(20),"
          + "MAX_ERRORS BIGINT, MAX_FK_ERRORS BIGINT, MAX_COL_LIMIT_ERRORS BIGINT,"
          + "CHECK_FK_ERROR_FLAG VARCHAR(20), CHECK_COL_LIMITS_FLAG VARCHAR(20), LAST_TRANSFER_DATE TIMESTAMP,"
          + "VERSION_NUMBER VARCHAR(20), COLLECTION_SET_ID BIGINT, USE_BATCH_ID VARCHAR(20), PRIORITY BIGINT,"
          + "QUEUE_TIME_LIMIT BIGINT, ENABLED_FLAG VARCHAR(20), SETTYPE VARCHAR(20), FOLDABLE_FLAG VARCHAR(20),"
          + "MEASTYPE VARCHAR(20), HOLD_FLAG VARCHAR(20), SCHEDULING_INFO VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'col_name', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '1', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");
      
      
      // needed for execute
      /*stm.execute("CREATE TABLE Meta_versions (VERSION_NUMBER VARCHAR(20), DESCRIPTION VARCHAR(20),"
          + "CURRENT_FLAG VARCHAR(20), IS_PREDEFINED VARCHAR(20), ENGINE_SERVER VARCHAR(20), MAIL_SERVER VARCHAR(20), "
          + "SCHEDULER_SERVER VARCHAR(20), MAIL_SERVER_PORT BIGINT)");

      stm.executeUpdate("INSERT INTO Meta_versions VALUES('1', 'description', 'c_Flag', 'is_preD', 'eServer', 'mailS', 'schedS', 1212)");
      */
    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }
    
    
    ExecutionSlot es = new ExecutionSlot(0, null);
    Class secretClass = es.getClass();
    
    try {
      hold = secretClass.getDeclaredField("hold");
      runningSet = secretClass.getDeclaredField("runningSet");
      removeAfterExecution = secretClass.getDeclaredField("removeAfterExecution");
      startTime = secretClass.getDeclaredField("startTime");
      approvedSettypes = secretClass.getDeclaredField("approvedSettypes");
      
      hold.setAccessible(true);
      runningSet.setAccessible(true);
      removeAfterExecution.setAccessible(true);
      startTime.setAccessible(true);
      approvedSettypes.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    } 
  }
  
  @Test
  public void testToString() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    assertEquals("ExecutionSlot: ESLOT", es.toString());
  }
  
  @Test
  public void testIsAccepted() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    assertFalse(es.isAccepted(null));
  }
  
  @Test
  public void testIsAccepted2() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("ESLOT", 10L, l, new EngineCom());
    
    assertTrue("true expected", es.isAccepted(et));
  }
  
  @Test
  public void testIsAccepted3() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    
    assertFalse("false expected", es.isAccepted(et));
  }
  
  @Test
  public void testIsAccepted4() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT", "all");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    
    try {
      Field shutdownSet = EngineThread.class.getDeclaredField("shutdownSet");
      
      shutdownSet.setAccessible(true);
      
      shutdownSet.set(et, false);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsAccepted3() failed");
    }
    
    assertTrue("true expected", es.isAccepted(et));
  }
  
  @Test
  public void testIsAccepted5() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT", "settype");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("settype", 10L, l, new EngineCom());
    
    try {
      Field shutdownSet = EngineThread.class.getDeclaredField("shutdownSet");
      
      shutdownSet.setAccessible(true);
      
      shutdownSet.set(et, false);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsAccepted3() failed");
    }
    
    assertTrue("true expected", es.isAccepted(et));
  }
  
  @Test
  public void testIsAccepted6() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT", "foobar");
    
    Logger l = Logger.getLogger("Log");;
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    
    try {
      Field shutdownSet = EngineThread.class.getDeclaredField("shutdownSet");
      
      shutdownSet.setAccessible(true);
      
      shutdownSet.set(et, false);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsAccepted3() failed");
    }
    
    assertFalse("false expected", es.isAccepted(et));
  }
  
  @Test
  public void testIsFree() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");

    assertTrue("True expected", es.isFree());
  }
  
  @Test
  public void testIsFree2() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");;
    try {
      //EngineThread et = new EngineThread("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "set_name", "col_name", new PluginLoader("pluginPath"), l, new EngineCom());
      EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
      runningSet.set(es, et);
      
      assertTrue("True expected", es.isFree());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsFree() failed");
    }
  }
  
  @Test
  public void testIsFree3() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");;
    try {
      //EngineThread et = new EngineThread("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "set_name", "col_name", new PluginLoader("pluginPath"), l, new EngineCom());
      EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
      runningSet.set(es, et);
      
      es.isFree();
      assertNull(runningSet.get(es));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsFree() failed");
    }
  }
  
  @Test
  public void testIsFree4() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");;
    try {
      //EngineThread et = new EngineThread("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "set_name", "col_name", new PluginLoader("pluginPath"), l, new EngineCom());
      EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
      et.start();
      runningSet.set(es, et);
      
      assertFalse("False expected", es.isFree());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsFree() failed");
    }
  }
  
  @Test
  public void testIsFree5() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");

    try {
      hold.set(es, true);
      assertFalse("False expected", es.isFree());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsFree4() failed");
    }  
  }
  
  
  // TODO
  @Ignore
  public void testExecute() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    Logger l = Logger.getLogger("Log");
    
    try {
      EngineThread et = new EngineThread("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "set_name", "col_name", new PluginLoader("pluginPath"), l, new EngineCom());
      es.execute(et);
      EngineThread ret = (EngineThread) runningSet.get(es);
      
      assertTrue("True expected", ret.isActive());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testExecute() failed");
    }
  }
  
  @Test
  public void testStop() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    es.stop(); // doesn't do anything
  }
  
  @Test
  public void testRemoveAfterExecution() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    es.removeAfterExecution(true);
    
    try {
      Boolean b = (Boolean) removeAfterExecution.get(es);
      assertTrue("True expected", b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveAfterExecution() failed");
    }
  }
  
  @Test
  public void testIsRemovedAfterExecution() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    try {
      assertFalse("False expected", es.isRemovedAfterExecution());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsRemovedAfterExecution() failed");
    }
  }
  
  @Test
  public void testGetRunningSet() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Logger l = Logger.getLogger("Log");
    EngineThread et = new EngineThread("name", 10L, l, new EngineCom());
    EngineThread ret = null;
    
    try {
      runningSet.set(es, et);
      ret = es.getRunningSet();
      assertNotNull(ret);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetRunningSet() failed");
    }
  }
  
  @Test
  public void testHold() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    es.hold();
    try {
      Boolean b = (Boolean) hold.get(es);
      assertTrue("True expected", b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveAfterExecution() failed");
    }
  }
  
  @Test
  public void testRestart() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    try {
      hold.set(es, true);
      es.restart();
      Boolean b = (Boolean) hold.get(es);
      assertFalse("False expected", b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRestart() failed");
    }
  }
  
  @Test
  public void testIsOnHold() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    assertFalse("False expected", es.isOnHold());
  }
  
  @Test
  public void testIsLocked() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    assertFalse("False expected", es.islocked());
  }
  
  @Test
  public void testSetAndGetName() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    es.setName("settedName");
    assertEquals("settedName", es.getName());
  }
  
  @Test
  public void testGetStartTime() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT");
    
    Date d = new Date(10000000000L);
    try {
      startTime.set(es, d);
      Date r = es.getStartTime();
      assertEquals(10000000000L, r.getTime());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetStartTime() failed");
    }
  }
  
  @Test
  public void testSetAndGetApprovedSettypes() {
    ExecutionSlot es = new ExecutionSlot(0, "ESLOT", new Vector());
    Vector v = new Vector();
    ArrayList al = new ArrayList();
    v.add("foo");
    v.add("bar");
    
    al.add("foo");
    al.add("bar");
    
    es.setApprovedSettypes(v);
    Vector rv = es.getApprovedSettypes();
      
    assertTrue(rv.containsAll(al));

  }
  
  @Test
  public void testGetSlotId() {
    ExecutionSlot es = new ExecutionSlot(10, "ESLOT");
    
    assertEquals(10, es.getSlotId());
  }
  
  @Test
  public void testSetApprovedSettypes() {
    ExecutionSlot es = new ExecutionSlot(10, "ESLOT");
    
    es.setApprovedSettypes("foobar");
    
    try {
      Vector v = (Vector) approvedSettypes.get(es);
      assertTrue("True expected", v.contains("foobar"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetApprovedSettypes() failed");
    }
  }
  
  @Test
  public void testSetApprovedSettypes2() {
    ExecutionSlot es = new ExecutionSlot(10, "ESLOT");
    ArrayList al = new ArrayList();
    al.add("foo");
    al.add("bar");
    
    es.setApprovedSettypes("foo,bar");
    
    try {
      Vector v = (Vector) approvedSettypes.get(es);
      assertTrue("True expected", v.containsAll(al));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetApprovedSettypes() failed");
    }
  }
  
  @Test
  public void testSetApprovedSettypes3() {
    ExecutionSlot es = new ExecutionSlot(10, "ESLOT", "foo,bar");
    ArrayList al = new ArrayList();
    al.add("foo");
    al.add("bar");
    
    try {
      Vector v = (Vector) approvedSettypes.get(es);
      assertTrue("True expected", v.containsAll(al));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetApprovedSettypes() failed");
    }
  }
  
  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_collection_sets");
      stm.execute("DROP TABLE Meta_collections");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ExecutionSlotTest.class);
  }*/
}
