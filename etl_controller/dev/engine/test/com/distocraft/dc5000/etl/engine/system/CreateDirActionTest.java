package com.distocraft.dc5000.etl.engine.system;

import junit.framework.JUnit4TestAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.hsqldb.jdbcDriver;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

/**
 * 
 * @author ejarsok
 * 
 */

public class CreateDirActionTest {

  private static CreateDirAction cdr = null;

  private static RockFactory rockFact = null;

  private static Long collectionSetId = 1L;

  private static Long transferActionId = 1L;

  private static Long transferBatchId = 1L;

  private static Long connectId = 1L;

  private static Meta_versions version;

  private static Meta_collections collection;

  private static Meta_transfer_actions trActions;

  private Method method;

  private static File file;
  
  private static Statement stm;
  
  private static Map<String, String> env = System.getenv();

  @BeforeClass
  public static void init() {

    File homeDir = new File(env.get("WORKSPACE"));
    file = new File(homeDir, "file");
    file.deleteOnExit();

    try {
      PrintWriter pw = new PrintWriter(new FileWriter(file));
      pw.print("foobar");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Can´t write in file!");
    }

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

      /*
       * ResultSet rs = stm.executeQuery("SELECT * FROM Meta_collection_sets");
       * ResultSet rs = stm.executeQuery("SELECT
       * COLLECTION_SET_ID,COLLECTION_SET_NAME,DESCRIPTION,VERSION_NUMBER,ENABLED_FLAG,TYPE
       * FROM Meta_collection_sets"); while (rs.next()) {
       * System.out.println(rs.getString(1) + " " + rs.getString(2) + " " +
       * rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " +
       * rs.getString(6)); }
       */

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
    version = new Meta_versions(rockFact);
    collection = new Meta_collections(rockFact);
    trActions = new Meta_transfer_actions(rockFact);
    cdr = new CreateDirAction(version, collectionSetId, collection, transferActionId, transferBatchId, connectId,
        rockFact, trActions);
  }

  // @Test
  @Ignore //can't run this method in windows
  public void testChmod() {
    try {
      CreateDirAction instance = new CreateDirAction(version, collectionSetId, collection, transferActionId,
          transferBatchId, connectId, rockFact, trActions);
      Class secretClass = instance.getClass();

      try {
        method = secretClass.getDeclaredMethod("chmod", new Class[] { String.class, File.class });
      } catch (NoSuchMethodException nE) {
        nE.printStackTrace();
        fail("testChmod() secretClass.getDeclaredMethod failed");
      }
      method.setAccessible(true);

      method.invoke(instance, new Object[] { "600", file });
      instance = null;
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChmod() failed");
    }
  }

  // @Test
  @Ignore //can't run this method in windows
  public void testChown() {
    try {
      CreateDirAction instance = new CreateDirAction(version, collectionSetId, collection, transferActionId,
          transferBatchId, connectId, rockFact, trActions);
      Class secretClass = instance.getClass();

      try {
        method = secretClass.getDeclaredMethod("chown", new Class[] { String.class, File.class });
      } catch (NoSuchMethodException nE) {
        nE.printStackTrace();
        fail("testChown() secretClass.getDeclaredMethod failed");
      }
      method.setAccessible(true);

      method.invoke(instance, new Object[] { "testOwner", file });
      instance = null;
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChown() failed");
    }
  }

  // @Test
  @Ignore //can't run this method in windows
  public void testChgrp() {
    try {
      CreateDirAction instance = new CreateDirAction(version, collectionSetId, collection, transferActionId,
          transferBatchId, connectId, rockFact, trActions);
      Class secretClass = instance.getClass();

      try {
        method = secretClass.getDeclaredMethod("chgrp", new Class[] { String.class, File.class });
      } catch (NoSuchMethodException nE) {
        nE.printStackTrace();
        fail("testChgrp() secretClass.getDeclaredMethod failed");
      }
      method.setAccessible(true);

      method.invoke(instance, new Object[] { "testGroup", file });
      instance = null;
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChgrp() failed");
    }
  }

  /**
   * Test method use stringToProperty method to convert String to property
   * Key-Value pair.<br />
   * Assert checks that returned property object is not empty and it contains
   * Key-Value pair
   * 
   */

  @Test
  public void testStringToProperty() {
    Properties prop;

    try {
      prop = cdr.stringToProperty("Key=Value");
      assertEquals(false, prop.isEmpty());
      assertEquals("Value", prop.getProperty("Key"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStringToProperty() failed");
    }
  }

  /**
   * Test method tries to convert one property key-value pair to String.
   * PropertyToString method returns String formatted like,<br />
   * <br /> #<br />
   * #Fri Jun 06 09:00:28 EEST 2008<br/> Key=Value<br />
   * <br />
   * Test method splits String around matches of the given regular expression
   * "\n". assert checks if third splitted word equals Key=Value
   * 
   */

  @Test
  public void testPropertyToString() {
    Properties prop = new Properties();
    prop.setProperty("Key", "Value");

    try {
      String s = cdr.propertyToString(prop);
      String[] splitted = s.split("\n");
      assertEquals(0, splitted[2].trim().compareTo("Key=Value"));
    } catch (Exception e) {
      // e.printStackTrace();
      fail("testPropertyToString() failed");
    }
  }

  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_collection_sets");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(CreateDirActionTest.class);
  }*/
}
