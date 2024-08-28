package com.distocraft.dc5000.install.ant;

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
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;

/**
 * @author ejarsok
 */
public class IntervalDSTModifierTest {

  private static Statement stm;

  @BeforeClass
  public static void init() {

    File prop = new File(System.getProperty("user.home"), "ETLCServer.properties");
    try {

      PrintWriter pw = new PrintWriter(new FileWriter(prop));
      pw.write("ENGINE_DB_URL=jdbc:hsqldb:mem:testdb\n");
      pw.write("ENGINE_DB_USERNAME=SA\n");
      pw.write("ENGINE_DB_PASSWORD= \n");
      pw.write("ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver\n");
      pw.close();

    } catch (IOException e1) {
      e1.printStackTrace();
      fail("Failed, can't write in file.");
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
      stm.execute("CREATE TABLE Meta_schedulings (VERSION_NUMBER VARCHAR(20), ID BIGINT, "
          + "EXECUTION_TYPE VARCHAR(20), OS_COMMAND VARCHAR(20), SCHEDULING_MONTH BIGINT, "
          + "SCHEDULING_DAY BIGINT, SCHEDULING_HOUR BIGINT, SCHEDULING_MIN BIGINT, "
          + "COLLECTION_SET_ID BIGINT, COLLECTION_ID BIGINT, MON_FLAG VARCHAR(20), "
          + "TUE_FLAG VARCHAR(20), WED_FLAG VARCHAR(20), THU_FLAG VARCHAR(20), "
          + "FRI_FLAG VARCHAR(20), SAT_FLAG VARCHAR(20), SUN_FLAG VARCHAR(20), "
          + "STATUS VARCHAR(20), LAST_EXECUTION_TIME TIMESTAMP, INTERVAL_HOUR BIGINT, "
          + "INTERVAL_MIN BIGINT, NAME VARCHAR(20),HOLD_FLAG VARCHAR(20), PRIORITY BIGINT, "
          + "SCHEDULING_YEAR BIGINT, TRIGGER_COMMAND VARCHAR(20), LAST_EXEC_TIME_MS BIGINT)");
      stm.executeUpdate("INSERT INTO Meta_schedulings VALUES('1', 1, 'interval', 'os_c', 1, 1, "
          + "1, 1, 1, 1, 'y', 'y', 'y','y', 'y', 'y', 'y', 'ok', '2008-09-10 01:01:01.0', 10, 10, "
          + "'Meta_schedulings', 'y', 1, 2008, 't_co', 1)");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }
  }

  @Test
  public void testGetDatabaseConnectionDetails() {

    HashMap hm;
    IntervalDSTModifier instance = new IntervalDSTModifier();
    Class secretClass = instance.getClass();

    try {

      System.setProperty("CONF_DIR", System.getProperty("user.home"));
      Method method = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      method.setAccessible(true);
      hm = (HashMap) method.invoke(instance, null);
      String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + ","
          + hm.get("etlrepDatabasePassword") + "," + hm.get("etlrepDatabaseDriver");
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetDatabaseConnectionDetails() failed, Exception");
    }
  }

  @Test
  public void testCreateEtlrepRockFactory() {

    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");
    IntervalDSTModifier instance = new IntervalDSTModifier();
    Class secretClass = instance.getClass();

    try {

      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);
      RockFactory rf = (RockFactory) method.invoke(instance, new Object[] { hm });
      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateEtlrepRockFactory() failed, Exception");
    }
  }

  @Test
  public void testExecute() {

    IntervalDSTModifier instance = new IntervalDSTModifier();
    System.setProperty("CONF_DIR", System.getProperty("user.home"));
    instance.execute();
  }

  @AfterClass
  public static void clean() throws Exception {

    File prop = new File(System.getProperty("user.home"), "ETLCServer.properties");
    prop.delete();
    stm.execute("DROP TABLE Meta_schedulings");
  }
}
