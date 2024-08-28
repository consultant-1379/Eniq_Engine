package com.distocraft.dc5000.etl.engine.executionslots;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import junit.framework.JUnit4TestAdapter;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.distocraft.dc5000.etl.engine.common.Share;


public class ExecutionSlotProfileHandlerTest {
  
  // ExecutionSlotProfileHandler
  private static Field exeSlotsProfileMap;
  
  private static Field activeSlotProfile;
  
  private static Field locked;
  
  private static Field url;

  private static Field userName;

  private static Field password;

  private static Field dbDriverName;
  
  // ExecutionSlotProfile
  private static Field executionSlotList;
  
  private static Connection c;
  
  private static Statement stm;
  
  @BeforeClass
  public static void init() {
	   Share share = Share.instance();
	   share.add("execution_profile_max_memory_usage_mb", 512);
    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("execute() failed, ClassNotFoundException");
    }

    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_execution_slot_profile (PROFILE_NAME VARCHAR(20), PROFILE_ID VARCHAR(20), ACTIVE_FLAG VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_execution_slot_profile VALUES('Pname1', 'PID1', 'Y')");

      stm.executeUpdate("INSERT INTO Meta_execution_slot_profile VALUES('Pname2', 'PID2', 'N')");

      stm.execute("CREATE TABLE Meta_execution_slot (PROFILE_ID VARCHAR(20), SLOT_NAME VARCHAR(20), SLOT_ID VARCHAR(20),"
            + "ACCEPTED_SET_TYPES VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '1', 'sTypes')");
      
    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException e1");
    }
    
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      Class secretClass = esph.getClass();
      
      ExecutionSlotProfile esp = new ExecutionSlotProfile("name", "id");
      Class secretClass2 = esp.getClass();
      
      //    ExecutionSlotProfileHandler
      exeSlotsProfileMap = secretClass.getDeclaredField("exeSlotsProfileMap");
      activeSlotProfile = secretClass.getDeclaredField("activeSlotProfile");
      locked = secretClass.getDeclaredField("locked");
      url = secretClass.getDeclaredField("url");
      userName = secretClass.getDeclaredField("userName");
      password = secretClass.getDeclaredField("password");
      dbDriverName = secretClass.getDeclaredField("dbDriverName");
      
      exeSlotsProfileMap.setAccessible(true);
      activeSlotProfile.setAccessible(true);
      locked.setAccessible(true);
      url.setAccessible(true);
      userName.setAccessible(true);
      password.setAccessible(true);
      dbDriverName.setAccessible(true);
      
      //    ExecutionSlotProfile
      executionSlotList = secretClass2.getDeclaredField("executionSlotList");
      
      executionSlotList.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }

  @Test
  public void testResetProfiles() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      ExecutionSlotProfile esp = null;
      
      url.set(esph, "jdbc:hsqldb:mem:.");
      userName.set(esph, "SA");
      password.set(esph, "");
      dbDriverName.set(esph, "org.hsqldb.jdbcDriver");
      
      esph.resetProfiles();
      
      HashMap hm = (HashMap) exeSlotsProfileMap.get(esph);
      
      esp = (ExecutionSlotProfile) hm.get("Pname1");
      
      assertNotNull(esp);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testResetProfiles() failed");
    }
  }

  @Test
  public void testGetNumberOfAdapterSets() {
      try {
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '10', 'adapter,Adapter,Alarm,Install,Mediation')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '11', 'adapter,Adapter,Alarm,Install,Mediation')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '12', 'adapter,Adapter,Alarm,Install,Mediation')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '13', 'Support,Install')");
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      ExecutionSlotProfile esp = null;
      
      url.set(esph, "jdbc:hsqldb:mem:.");
      userName.set(esph, "SA");
      password.set(esph, "");
      dbDriverName.set(esph, "org.hsqldb.jdbcDriver");
      
      esph.resetProfiles();
      esph.resetProfiles();
      
      int numberOfSets = esph.getNumberOfAdapterSlots();
      assertEquals(3, numberOfSets);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testResetProfiles() failed");
    }
  }

  @Test
  public void testGetNumberOfAggregatorSets() {
      try {
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '10', 'adapter,Adapter,Alarm,Install,Mediation')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '11', 'Loader,Topology,Aggregator')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '12', 'Service,Support')");
		stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('PID1', 'Sname', '13', 'Loader,Topology,Aggregator')");
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      ExecutionSlotProfile esp = null;
      
      url.set(esph, "jdbc:hsqldb:mem:.");
      userName.set(esph, "SA");
      password.set(esph, "");
      dbDriverName.set(esph, "org.hsqldb.jdbcDriver");
      
      esph.resetProfiles();
      esph.resetProfiles();
      
      int numberOfSets = esph.getNumberOfAggregatorSlots();
      assertEquals(2, numberOfSets);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testResetProfiles() failed");
    }
  }

  @Test
  public void testCreateActiveProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      ExecutionSlotProfile esp = esph.createActiveProfile(1);
      Vector v = (Vector) executionSlotList.get(esp);
      
      ExecutionSlot ex = (ExecutionSlot) v.get(0);
      
      String expected = "0,Default0";
      String actual = ex.getSlotId() + "," + ex.getName();
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateActiveProfile() failed");
    }
  }

  @Test
  public void testWriteProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler("jdbc:hsqldb:mem:.", "SA", "", "org.hsqldb.jdbcDriver");
      
      ExecutionSlotProfile esp = new ExecutionSlotProfile("Pname2", "PID2");
      
      activeSlotProfile.set(esph, esp);
      
      // Changes Pname2 executionslotprofile active flag to y
      esph.writeProfile();
      
      IDataSet databaseDataSet = new DatabaseConnection(c).createDataSet();
      ITable actualTable = databaseDataSet.getTable("Meta_execution_slot_profile");
  
      IDataSet expectedDataSet = new FlatXmlDataSet(new File(
      "test/XMLFiles/com.distocraft.dc5000.etl.engine.executionslots_ExecutionSlotProfileHandlerTest_testWriteProfile/Expected.xml"));
      ITable expectedTable = expectedDataSet.getTable("Meta_execution_slot_profile");

      Assertion.assertEquals(expectedTable, actualTable);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testWriteProfile() failed");
    }
  }

  @Test
  public void testGetExecutionProfileNames() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertNull("null expected", esph.getExecutionProfileNames());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetExecutionProfileNames() failed");
    }
  }

  @Test
  public void testGetExecutionProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertNull("null expected", esph.getExecutionProfile("foobar"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetExecutionProfile() failed");
    }
  }

  @Test
  public void testGetAllExecutionProfiles() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertNull("null expected", esph.getAllExecutionProfiles());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetAllExecutionProfiles() failed");
    }
  }

  @Test
  public void testSetActiveProfileString() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler("jdbc:hsqldb:mem:.", "SA", "", "org.hsqldb.jdbcDriver");
      
      Boolean b = esph.setActiveProfile("Pname1");
      assertTrue("true expected", b);
        
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetActiveProfileString() failed");
    }
  }
  
  @Test
  public void testSetActiveProfileString2() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      Boolean b = esph.setActiveProfile("foobar");
      assertFalse("false expected", b);
        
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetActiveProfileString() failed");
    }
  }

  @Test
  public void testAddSlot() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      ExecutionSlot es = new ExecutionSlot(0, "name");
      
      assertTrue("True expected", esph.addSlot(es));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAddSlot() failed");
    }
  }

  @Test
  public void testRemoveSlot() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertNull("null expected", esph.removeSlot("foobar"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveSlot() failed");
    }
  }

  @Test
  public void testGetActiveExecutionProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertNotNull("Not null expected", esph.getActiveExecutionProfile());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveSlot() failed");
    }
  }

  @Test
  public void testLockProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      esph.lockProfile();
      
      assertTrue("True expected", esph.isProfileLocked());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testLockProfile() failed");
    }
  }

  @Test
  public void testUnLockProfile() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      locked.set(esph, true);
      esph.unLockProfile();
      
      assertFalse("False expected", esph.isProfileLocked());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testUnLockProfile() failed");
    }
  }

  @Test
  public void testIsProfileLocked() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(0);
      
      assertFalse("False expected", esph.isProfileLocked());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsProfileLocked() failed");
    }
  }

  
  /**
   * Cheks that the profile does not contain any slots marked for removal
   *
   */
  @Test
  public void testIsProfileClean() {
    try {
      ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler(5);
      
      esph.cleanActiveProfile();
      
      assertTrue(esph.isProfileClean());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetExecutionProfileNames() failed");
    }
  }
  
  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_execution_slot_profile");
      stm.execute("DROP TABLE Meta_execution_slot");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ExecutionSlotProfileHandlerTest.class);
  }*/
}
