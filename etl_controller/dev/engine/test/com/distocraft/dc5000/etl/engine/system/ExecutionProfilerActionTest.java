package com.distocraft.dc5000.etl.engine.system;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * 
 * @author ejarsok
 * 
 */

public class ExecutionProfilerActionTest {

  private static ExecutionProfilerAction epa;
  
  private static Statement stm;
  
  private static Map<String, String> env = System.getenv();
  private static String homeDir = env.get("WORKSPACE");
  
  @BeforeClass
  public static void init() {
    System.setProperty("CONF_DIR", homeDir);
    File ini = new File(homeDir, "niq.ini");
    ini.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(ini));
      pw.write("[ENIQ_HW_INFO]\n");
      pw.write("Eniq_Proc_Core=2\n");
      pw.close();
    } catch (IOException e3) {
      e3.printStackTrace();
      fail("Can't write in file");
    }
    
    Long collectionSetId = 1L;
    Long transferActionId = 1L;
    Long transferBatchId = 1L;
    Long connectId = 1L;
    RockFactory rockFact = null;
    Logger log = Logger.getLogger("Logger");

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
          + "DESCRIPTION VARCHAR(20),VERSION_NUMBER VARCHAR(20),ENABLED_FLAG VARCHAR(20),TYPE VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");
      
      
      stm.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
          + "TYPE_NAME VARCHAR(31), CONNECTION_ID VARCHAR(31), CONNECTION_NAME VARCHAR(31), "
          + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
          + "DB_LINK_NAME VARCHAR(31))");
      
      stm.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'USER', '1', 'dwhrep', "
          + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");
      
      
      stm.execute("CREATE TABLE Meta_execution_slot (PROFILE_ID VARCHAR(31), SLOT_NAME VARCHAR(31),"
          + "SLOT_ID VARCHAR(31), ACCEPTED_SET_TYPES VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Meta_execution_slot VALUES('1', 'name', '1', 'ast')");
      
      
      stm.execute("CREATE TABLE Configuration (PARAMNAME VARCHAR(31), PARAMVALUE VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Configuration VALUES('executionProfile.%.1', '1')");
      stm.executeUpdate("INSERT INTO Configuration VALUES('executionProfile.%.1.1.formula', '2')");
      stm.executeUpdate("INSERT INTO Configuration VALUES('executionProfile.%.1.1.formula', '3')");

      
      stm.execute("CREATE TABLE Meta_execution_slot_profile (PROFILE_NAME VARCHAR(31), PROFILE_ID VARCHAR(31),"
          + "ACTIVE_FLAG VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Meta_execution_slot_profile VALUES('profile_name', '1', 'y')");
      
    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }

    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);

    } catch (SQLException e) {
      e.printStackTrace();
      fail("init() failed, SQLException");
    } catch (RockException e) {
      e.printStackTrace();
      fail("init() failed, RockException");
    }
    Meta_versions version = new Meta_versions(rockFact);
    Meta_collections collection = new Meta_collections(rockFact);
    Meta_transfer_actions trActions = new Meta_transfer_actions(rockFact);

    try {
      epa = new ExecutionProfilerAction(version, collectionSetId, collection, transferActionId,
          transferBatchId, connectId, rockFact, trActions, log);
 
    } catch (Exception e) {
      e.printStackTrace();
      fail("ExecutionProfilerActionTest() failed, Exception");
    }

  }
  
  @Test
  public void testExecute() {
    try {
      epa.execute();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testExecute() failed, Exeption");
    }
  }
  
  @Test
  public void testGetEniqINIFilePath() {
    Class secretClass = epa.getClass();
    
    try {
      Method method = secretClass.getDeclaredMethod("getEniqINIFilePath", null);
      method.setAccessible(true);
      // 20110407, eanguan, To set the generic path separator so that can work across different OS archs.
      final String sep = System.getProperty("file.separator");
      assertEquals(homeDir + sep + "niq.ini", method.invoke(epa, null));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetCPUCoreAmountFromINI() failed");
    }
  }
  
  @Test
  public void testGetCPUCoreAmountFromINI() {
    Class secretClass = epa.getClass();
    
    try {
      Method method = secretClass.getDeclaredMethod("getCPUCoreAmountFromINI", null);
      method.setAccessible(true);
      
      assertEquals(2, method.invoke(epa, null));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetCPUCoreAmountFromINI() failed");
    }
  }
  
  @Test
  public void testGetAmountOfSlots() {
    Class secretClass = epa.getClass();
    
    try {
      Method method = secretClass.getDeclaredMethod("getAmountOfSlots", new Class[] {int.class, String.class});
      method.setAccessible(true);
      
      assertEquals(30, method.invoke(epa, new Object[] {2, "15n"}));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetCPUCoreAmountFromINI() failed");
    }
  }

  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_collection_sets");
      stm.execute("DROP TABLE Meta_databases");
      stm.execute("DROP TABLE Meta_execution_slot");
      stm.execute("DROP TABLE Configuration");
      stm.execute("DROP TABLE Meta_execution_slot_profile");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ExecutionProfilerActionTest.class);
  }*/
}
