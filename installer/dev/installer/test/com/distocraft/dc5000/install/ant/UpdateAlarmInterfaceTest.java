package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.net.URL;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.Project;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

/**
 * 
 * @author ejarsok
 * 
 */

public class UpdateAlarmInterfaceTest {

  private UpdateAlarmInterface uai = new UpdateAlarmInterface();

  private static RockFactory rockFact;

  private static Statement stm;

  private static Connection c;

  @BeforeClass
  public static void init() {
    File prop = new File(System.getProperty("user.home"), "ETLCServer.properties");
    prop.deleteOnExit();

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

    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
          + "TYPE_NAME VARCHAR(31), CONNECTION_ID VARCHAR(31), CONNECTION_NAME VARCHAR(31), "
          + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
          + "DB_LINK_NAME VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Meta_databases VALUES('SA', '1', 'USER', '1', 'dwhrep', "
          + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

      stm.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(20), COLLECTION_SET_NAME VARCHAR(20),"
          + "DESCRIPTION VARCHAR(20),VERSION_NUMBER VARCHAR(20),ENABLED_FLAG VARCHAR(20),TYPE VARCHAR(20))");

      stm
          .executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'AlarmInterfaces', 'description', '1', 'Y', 'type')");

      stm
          .execute("CREATE TABLE Meta_collections (COLLECTION_ID BIGINT, COLLECTION_NAME VARCHAR(20),"
              + "COLLECTION VARCHAR(20), MAIL_ERROR_ADDR VARCHAR(20), MAIL_FAIL_ADDR VARCHAR(20), MAIL_BUG_ADDR VARCHAR(20),"
              + "MAX_ERRORS BIGINT, MAX_FK_ERRORS BIGINT, MAX_COL_LIMIT_ERRORS BIGINT,"
              + "CHECK_FK_ERROR_FLAG VARCHAR(20), CHECK_COL_LIMITS_FLAG VARCHAR(20), LAST_TRANSFER_DATE TIMESTAMP,"
              + "VERSION_NUMBER VARCHAR(20), COLLECTION_SET_ID BIGINT, USE_BATCH_ID VARCHAR(20), PRIORITY BIGINT,"
              + "QUEUE_TIME_LIMIT BIGINT, ENABLED_FLAG VARCHAR(20), SETTYPE VARCHAR(20), FOLDABLE_FLAG VARCHAR(20),"
              + "MEASTYPE VARCHAR(20), HOLD_FLAG VARCHAR(20), SCHEDULING_INFO VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'Adapter_1', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '10', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");
      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'Adapter_2', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '10', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");

      stm.execute("CREATE TABLE Alarminterface (INTERFACEID VARCHAR(20), DESCRIPTION VARCHAR(20),"
          + "STATUS VARCHAR(20), COLLECTION_SET_ID BIGINT, COLLECTION_ID BIGINT, QUEUE_NUMBER BIGINT)");

      stm.executeUpdate("INSERT INTO Alarminterface VALUES('1', 'description', 'status', 1, 2, 3)");
      stm.executeUpdate("INSERT INTO Alarminterface VALUES('2', 'description2', 'status2', 10, 20, 30)");

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
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stm.execute("DROP TABLE Meta_databases");
    stm.execute("DROP TABLE Meta_collection_sets");
    stm.execute("DROP TABLE Meta_collections");
    stm.execute("DROP TABLE Alarminterface");
  }

  @Test
  public void testCreateEtlrepRockFactory() {
    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");

    UpdateAlarmInterface instance = new UpdateAlarmInterface();
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
  public void testCreateDwhrepRockFactory() {
    UpdateAlarmInterface instance = new UpdateAlarmInterface();
    Class secretClass = instance.getClass();

    try {
      Method method = secretClass.getDeclaredMethod("createDwhrepRockFactory", null);
      method.setAccessible(true);

      instance.etlrepRockFactory = rockFact;
      method.invoke(instance, null);

      String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
      String actual = instance.dwhrepRockFactory.getDbURL() + "," + instance.dwhrepRockFactory.getUserName() + 
                      "," + instance.dwhrepRockFactory.getPassword() + "," + instance.dwhrepRockFactory.getDriverName();
      
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("CreateDwhrepRockFactory() failed, Exception");
    }
  }

  /**
   * Test method updates row values in Alarminterface table where interfaceId = 1
   *
   */
  
  @Test
  public void testUpdateInterface() {
    UpdateAlarmInterface instance = new UpdateAlarmInterface();
    Class secretClass = instance.getClass();

    try {
      Method method = secretClass.getDeclaredMethod("updateInterface", null);
      method.setAccessible(true);

      instance.dwhrepRockFactory = rockFact;
      instance.etlrepRockFactory = rockFact;
      instance.setInterfaceId("1");
      instance.setQueueNumber("1");
      instance.setDescription("descript");
      instance.setStatus("status_is_set");

      assertEquals(true, method.invoke(instance, null));

      ITable actualTable = new DatabaseConnection(c).createQueryTable("RESULT_NAME",
          "SELECT * FROM Alarminterface WHERE INTERFACEID = '1'");

		final URL url = ClassLoader.getSystemResource("XMLFiles");
		if(url == null){
			throw new FileNotFoundException("XMLFiles");
		}
		final File xmlBase = new File(url.toURI());
		final String xmlFile = xmlBase.getAbsolutePath() + "/com.distocraft.dc5000.install.ant_UpdateAlarmInterface_testUpdateInterface/Expected.xml";

      IDataSet expectedDataSet = new FlatXmlDataSet(new File(xmlFile));
      ITable expectedTable = expectedDataSet.getTable("Alarminterface");

      Assertion.assertEquals(expectedTable, actualTable);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateEtlrepRockFactory() failed, Exception");
    }
  }

  @Test
  public void testGetDatabaseConnectionDetails() {

    HashMap hm;

    UpdateAlarmInterface instance = new UpdateAlarmInterface();
    Class secretClass = instance.getClass();

    try {
      Field field = secretClass.getDeclaredField("propertiesFilepath");
      Method method = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      field.setAccessible(true);
      method.setAccessible(true);
      field.set(instance, System.getProperty("user.home") + File.separator + "ETLCServer.properties");

      Project proj = new Project();
      instance.setProject(proj);
      hm = (HashMap) method.invoke(instance, null);

      String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + "," + hm.get("etlrepDatabasePassword") + 
                    "," + hm.get("etlrepDatabaseDriver");
      
      assertEquals(expected, actual);


      proj = instance.getProject();
      Hashtable ht = proj.getProperties();
      assertEquals("jdbc:hsqldb:mem:testdb", ht.get("etlrepDatabaseUrl"));
      assertEquals("SA", ht.get("etlrepDatabaseUsername"));
      assertEquals("", ht.get("etlrepDatabasePassword"));
      assertEquals("org.hsqldb.jdbcDriver", ht.get("etlrepDatabaseDriver"));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetDatabaseConnectionDetails() failed, Exception");
    }
  }

  @Test
  public void testSetAndGetDescriptionString() {
    uai.setDescription("Descript");
    assertEquals("Descript", uai.getDescription());
  }

  @Test
  public void testSetAndGetInterfaceId() {
    uai.setInterfaceId("Interface_id");
    assertEquals("Interface_id", uai.getInterfaceId());
  }

  @Test
  public void testSetAndGetQueueNumber() {
    uai.setQueueNumber("QNumber");
    assertEquals("QNumber", uai.getQueueNumber());
  }

  @Test
  public void testSetAndGetStatus() {
    uai.setStatus("Status");
    assertEquals("Status", uai.getStatus());
  }

  @Test
  public void testSetAndGetConfigurationDirectory() {
    uai.setConfigurationDirectory("conf_dir");
    assertEquals("conf_dir", uai.getConfigurationDirectory());
  }

  @Test
  public void testExecute() {

    UpdateAlarmInterface instance = new UpdateAlarmInterface();
    Class secretClass = instance.getClass();

    try {
      Field field = secretClass.getDeclaredField("configurationDirectory");
      Field field2 = secretClass.getDeclaredField("etlrepRockFactory");
      field.setAccessible(true);
      field2.setAccessible(true);
      field.set(instance, System.getProperty("user.home"));

      Project proj = new Project();
      instance.setProject(proj);
      instance.setInterfaceId("2");
      instance.setQueueNumber("1");
      instance.setDescription("descript2");
      instance.setStatus("status_is_set2");
      instance.execute();

      ITable actualTable = new DatabaseConnection(c).createQueryTable("RESULT_NAME",
          "SELECT * FROM Alarminterface WHERE INTERFACEID = '2'");

			final URL url = ClassLoader.getSystemResource("XMLFiles");
		if(url == null){
			throw new FileNotFoundException("XMLFiles");
		}
		final File xmlBase = new File(url.toURI());
		final String xmlFile = xmlBase.getAbsolutePath() + "/com.distocraft.dc5000.install.ant_UpdateAlarmInterface_testExecute/Expected.xml";


      IDataSet expectedDataSet = new FlatXmlDataSet(new File(xmlFile));
      ITable expectedTable = expectedDataSet.getTable("Alarminterface");

      Assertion.assertEquals(expectedTable, actualTable);

      RockFactory rf = (RockFactory) field2.get(instance);

      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetDatabaseConnectionDetails() failed, Exception");
    }
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(UpdateAlarmInterfaceTest.class);
  }*/
}
