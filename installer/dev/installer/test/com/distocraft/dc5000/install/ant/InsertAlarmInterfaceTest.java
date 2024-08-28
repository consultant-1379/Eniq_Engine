package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.net.URL;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Ignore;

import ssc.rockfactory.RockFactory;

import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class InsertAlarmInterfaceTest {

  private static RockFactory rockFact;

  private static Method getDatabaseConnectionDetails;

  private static Method createDwhrepRockFactory;

  private static Method insertInterface;

  private static Field etlrepRockFactory;

  private static Field dwhrepRockFactory;

  private static Field propertiesFilepath;

  private static Connection c;
  
  private static Statement stm;

  @BeforeClass
  public static void init() {
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

      stm.execute("CREATE TABLE Alarminterface (INTERFACEID VARCHAR(20), DESCRIPTION VARCHAR(20),"
          + "STATUS VARCHAR(20), COLLECTION_SET_ID BIGINT, COLLECTION_ID BIGINT, QUEUE_NUMBER BIGINT)");

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

      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'Adapter_ID', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '10', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");
      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'Adapter_id', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '10', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }

    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed, Exception");
    }

    InsertAlarmInterface ia = new InsertAlarmInterface();
    Class secretClass = ia.getClass();

    try {
      getDatabaseConnectionDetails = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      createDwhrepRockFactory = secretClass.getDeclaredMethod("createDwhrepRockFactory", null);
      insertInterface = secretClass.getDeclaredMethod("insertInterface", null);
      etlrepRockFactory = secretClass.getDeclaredField("etlrepRockFactory");
      dwhrepRockFactory = secretClass.getDeclaredField("dwhrepRockFactory");
      propertiesFilepath = secretClass.getDeclaredField("propertiesFilepath");
      getDatabaseConnectionDetails.setAccessible(true);
      createDwhrepRockFactory.setAccessible(true);
      insertInterface.setAccessible(true);
      etlrepRockFactory.setAccessible(true);
      dwhrepRockFactory.setAccessible(true);
      propertiesFilepath.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stm.execute("DROP TABLE Meta_databases");
    stm.execute("DROP TABLE Alarminterface");
    stm.execute("DROP TABLE Meta_collection_sets");
    stm.execute("DROP TABLE Meta_collections");
  }

  @Test
  public void testSetAndGetDescriptionString() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    ia.setDescription("Descript");
    assertEquals("Descript", ia.getDescription());
  }

  @Test
  public void testSetAndGetInterfaceId() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    ia.setInterfaceId("iID");
    assertEquals("iID", ia.getInterfaceId());
  }

  @Test
  public void testSetAndGetQueueNumber() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    ia.setQueueNumber("qnumber");
    assertEquals("qnumber", ia.getQueueNumber());
  }

  @Test
  public void testSetAndGetStatus() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    ia.setStatus("status");
    assertEquals("status", ia.getStatus());
  }

  @Test
  public void testSetAndGetConfigurationDirectory() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    ia.setConfigurationDirectory("confDir");
    assertEquals("confDir", ia.getConfigurationDirectory());
  }

  @Test
  public void testGetDatabaseConnectionDetails() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    Project proj = new Project();
    ia.setProject(proj);
    String propPath = System.getProperty("user.home") + File.separator + "DatabaseConnectionDetails";
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "DatabaseConnectionDetails",
        "ENGINE_DB_URL=url;ENGINE_DB_USERNAME=uName;ENGINE_DB_PASSWORD=passwd;ENGINE_DB_DRIVERNAME=driver");

    try {
      propertiesFilepath.set(ia, propPath);

      HashMap hm = (HashMap) getDatabaseConnectionDetails.invoke(ia, null);

      String expected = "url,uName,passwd,driver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + "," + hm.get("etlrepDatabasePassword") + 
                    "," + hm.get("etlrepDatabaseDriver");
      
      assertEquals(expected, actual);


      proj = ia.getProject();
      assertEquals("url", proj.getProperty("etlrepDatabaseUrl"));
      assertEquals("uName", proj.getProperty("etlrepDatabaseUsername"));
      assertEquals("passwd", proj.getProperty("etlrepDatabasePassword"));
      assertEquals("driver", proj.getProperty("etlrepDatabaseDriver"));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetEtlrepDatabaseConnectionDetails() failed");
    }

    f.delete();
  }

  @Test
  public void testCreateEtlrepRockFactory() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");

    Class secretClass = ia.getClass();
    try {
      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);

      RockFactory rf = (RockFactory) method.invoke(ia, new Object[] { hm });

      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateEtlrepRockFactory() failed, Exception");
    }
  }
  
  @Test
  public void testCreateEtlrepRockFactory2() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    HashMap hm = new HashMap();

    Class secretClass = ia.getClass();
    try {
      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);

      //ivoke with empty HashMap
      RockFactory rf = (RockFactory) method.invoke(ia, new Object[] { hm });
      fail("Should't execute this line");
      
    } catch (Exception e) {

    }
  }

  @Test
  public void testCreateDwhrepRockFactory() {
    InsertAlarmInterface ia = new InsertAlarmInterface();

    try {
      etlrepRockFactory.set(ia, rockFact);
      assertNull(dwhrepRockFactory.get(ia));

      createDwhrepRockFactory.invoke(ia, null);

      assertNotNull(dwhrepRockFactory.get(ia));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateDwhrepRockFactory()");
    }
  }

  @Test
  public void testInsertInterface() {
    InsertAlarmInterface ia = new InsertAlarmInterface();

    try {
      dwhrepRockFactory.set(ia, rockFact);
      etlrepRockFactory.set(ia, rockFact);
      ia.setInterfaceId("ID");
      ia.setDescription("DESCRIPTION");
      ia.setStatus("STATUS");
      ia.setQueueNumber("100");

      insertInterface.invoke(ia, null);

      ITable actualTable = new DatabaseConnection(c).createQueryTable("RESULT_NAME",
      "SELECT * FROM Alarminterface WHERE INTERFACEID = 'ID'");

      IDataSet expectedDataSet = new FlatXmlDataSet(
				 getFile("com.distocraft.dc5000.install.ant_InsertAlarmInterface_testInsertInterface/Expected.xml"));
      ITable expectedTable = expectedDataSet.getTable("Alarminterface");

      Assertion.assertEquals(expectedTable, actualTable);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testInsertInterface() failed");
    }
  }

	private File getFile(final String name) throws Exception {
		final URL url = ClassLoader.getSystemResource("XMLFiles");
		if(url == null){
			throw new FileNotFoundException("XMLFiles");
		}
		final File xmlBase = new File(url.toURI());
		final String xmlFile = xmlBase.getAbsolutePath() + "/"+name;
		return new File(xmlFile);
	}
  @Test
  public void testExecute() {
    InsertAlarmInterface ia = new InsertAlarmInterface();
    Project proj = new Project();
    ia.setProject(proj);
    ia.setConfigurationDirectory(System.getProperty("user.home"));
    ia.setInterfaceId("id");
    ia.setDescription("descript");
    ia.setStatus("stat");
    ia.setQueueNumber("10");
    
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "ETLCServer.properties",
    "ENGINE_DB_URL=jdbc:hsqldb:mem:testdb;ENGINE_DB_USERNAME=SA;ENGINE_DB_PASSWORD= ;ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver");
    
    ia.execute();
    
    try {
      ITable actualTable = new DatabaseConnection(c).createQueryTable("RESULT_NAME",
      "SELECT * FROM Alarminterface WHERE INTERFACEID = 'id'");


      IDataSet expectedDataSet = new FlatXmlDataSet(
				 getFile("com.distocraft.dc5000.install.ant_InsertAlarmInterface_testExecute/Expected.xml"));
      ITable expectedTable = expectedDataSet.getTable("Alarminterface");

      Assertion.assertEquals(expectedTable, actualTable);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testExecute() failed");
    }
    
    f.delete();
  }
}
