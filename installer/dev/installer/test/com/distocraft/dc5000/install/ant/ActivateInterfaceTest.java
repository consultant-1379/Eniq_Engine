package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.net.URL;

import org.apache.tools.ant.Project;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlWriter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.ericsson.junit.HelpClass;
import ssc.rockfactory.RockFactory;

/**
 * @author ejarsok
 */
public class ActivateInterfaceTest {

  private static RockFactory rockFact;

  private static Method getDatabaseConnectionDetails;

  private static Method createDwhrepRockFactory;

  private static Method copyInterfaceSet;

  private static Method activateInterface;

  private static Method getNewCollectionSetId;

  private static Method getNewCollectionId;

  private static Method getNewTransferActionId;

  private static Method getNewMetaSchedulingsId;

  private static Method interfaceAlreadyActivated;

  private static Method removeIntfSets;

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
      stm.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(20), COLLECTION_SET_NAME VARCHAR(20),"
          + "DESCRIPTION VARCHAR(20),VERSION_NUMBER VARCHAR(20),ENABLED_FLAG VARCHAR(20),TYPE VARCHAR(20))");
      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'aiName-ossName', 'description', '1', "
          + "'Y', 'type')");
      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('2', 'copyaiName', 'description', '1', 'Y', 'type')");
      stm.execute("CREATE TABLE Meta_schedulings (VERSION_NUMBER VARCHAR(20), ID BIGINT, "
          + "EXECUTION_TYPE VARCHAR(20), OS_COMMAND VARCHAR(20), SCHEDULING_MONTH BIGINT, SCHEDULING_DAY BIGINT, "
          + "SCHEDULING_HOUR BIGINT, SCHEDULING_MIN BIGINT, COLLECTION_SET_ID BIGINT, COLLECTION_ID BIGINT, "
          + "MON_FLAG VARCHAR(20), TUE_FLAG VARCHAR(20), WED_FLAG VARCHAR(20), THU_FLAG VARCHAR(20), "
          + "FRI_FLAG VARCHAR(20), SAT_FLAG VARCHAR(20), SUN_FLAG VARCHAR(20), STATUS VARCHAR(20), "
          + "LAST_EXECUTION_TIME TIMESTAMP, INTERVAL_HOUR BIGINT, INTERVAL_MIN BIGINT, NAME VARCHAR(20),"
          + "HOLD_FLAG VARCHAR(20), PRIORITY BIGINT, SCHEDULING_YEAR BIGINT, TRIGGER_COMMAND VARCHAR(20))");
      stm.executeUpdate("INSERT INTO Meta_schedulings VALUES('1', 1, 'wait', 'os_c', 1, 2, 3, 4, 1, 1, 'y', 'y', 'y',"
          + "'y', 'y', 'y', 'y', 'ok', '2006-10-10', 10, 10, 'Meta_schedulings', 'y', 1, 2009, 't_co')");
      stm.executeUpdate("INSERT INTO Meta_schedulings VALUES('1', 1, 'wait', 'os_c', 1, 2, 3, 4, 2, 2, 'y', 'y', 'y',"
          + "'y', 'y', 'y', 'y', 'ok', '2006-10-10', 10, 10, 'Meta_schedulings', 'y', 1, 2009, 't_co')");
      stm.execute("CREATE TABLE Meta_collections (COLLECTION_ID BIGINT, "
          + "COLLECTION_NAME VARCHAR(20), COLLECTION VARCHAR(20), MAIL_ERROR_ADDR VARCHAR(20), "
          + "MAIL_FAIL_ADDR VARCHAR(20), MAIL_BUG_ADDR VARCHAR(20), MAX_ERRORS BIGINT, "
          + "MAX_FK_ERRORS BIGINT, MAX_COL_LIMIT_ERRORS BIGINT,CHECK_FK_ERROR_FLAG VARCHAR(20), "
          + "CHECK_COL_LIMITS_FLAG VARCHAR(20), LAST_TRANSFER_DATE TIMESTAMP, VERSION_NUMBER VARCHAR(20), "
          + "COLLECTION_SET_ID BIGINT, USE_BATCH_ID VARCHAR(20), PRIORITY BIGINT,QUEUE_TIME_LIMIT BIGINT, "
          + "ENABLED_FLAG VARCHAR(20), SETTYPE VARCHAR(20), FOLDABLE_FLAG VARCHAR(20), MEASTYPE VARCHAR(20), "
          + "HOLD_FLAG VARCHAR(20), SCHEDULING_INFO VARCHAR(20))");
      stm.executeUpdate("INSERT INTO Meta_collections VALUES('1', 'Directory_Checker_aiName', 'collection', "
          + "'me', 'mf', 'mb' , 5, 5, 5, 'y', 'y', 2006-10-10, '10', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', "
          + " 'y', 'info')");
      stm.executeUpdate("INSERT INTO Meta_collections VALUES('2', 'copyaiName${OSS}', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '10', 2, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");
      stm.execute("CREATE TABLE Meta_transfer_actions (VERSION_NUMBER VARCHAR(20), TRANSFER_ACTION_ID BIGINT, "
          + "COLLECTION_ID BIGINT, COLLECTION_SET_ID BIGINT, ACTION_TYPE VARCHAR(20), "
          + "TRANSFER_ACTION_NAME VARCHAR(20), ORDER_BY_NO BIGINT,DESCRIPTION VARCHAR(20), "
          + "ENABLED_FLAG VARCHAR(20), CONNECTION_ID BIGINT, WHERE_CLAUSE_02 VARCHAR(20),"
          + "WHERE_CLAUSE_03 VARCHAR(20), ACTION_CONTENTS_03 VARCHAR(20), ACTION_CONTENTS_02 VARCHAR(20), "
          + "ACTION_CONTENTS_01 VARCHAR(20), WHERE_CLAUSE_01 VARCHAR(20))");
      stm.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('1', 1, 1, 1, 'action_type', 'TA_name', 0, "
          + "'description', 'Y', 1, 'w_clause2', 'w_clause3', 'ac3', 'ac2', 'ac1', 'w_clause1')");
      stm.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('1', 1, 2, 2, 'action_type', 'TA_name${OSS}', 0, "
          + "'description', 'Y', 1, '', '', '', '', '', '')");
      stm.execute("CREATE TABLE Datainterface (INTERFACENAME VARCHAR(31), STATUS BIGINT, INTERFACETYPE VARCHAR(31),"
          + "DESCRIPTION VARCHAR(31), DATAFORMATTYPE VARCHAR(31), INTERFACEVERSION VARCHAR(31), LOCKEDBY VARCHAR(31),"
          + "LOCKDATE TIMESTAMP, PRODUCTNUMBER VARCHAR(31), ENIQ_LEVEL VARCHAR(31), RSTATE VARCHAR(31))");
      stm.executeUpdate("INSERT INTO Datainterface VALUES('if_name', 0, 'if_type', 'Description', 'dataType', "
          + "'ifVersion', 'user', 2008-07-10, 'pNumber', 'eniq_level', 'rstate')");
      stm.execute("CREATE TABLE Interfacetechpacks (INTERFACENAME VARCHAR(31), TECHPACKNAME VARCHAR(31), "
          + "TECHPACKVERSION VARCHAR(31), INTERFACEVERSION VARCHAR(31))");
      stm.executeUpdate("INSERT INTO Interfacetechpacks VALUES('if_name', 'tpName', 'tpVersion', 'ifVersion')");
      stm.execute("CREATE TABLE Tpactivation (TECHPACK_NAME VARCHAR(31), STATUS VARCHAR(31),"
          + "VERSIONID VARCHAR(31), TYPE VARCHAR(31), MODIFIED BIGINT)");
      stm.executeUpdate("INSERT INTO Tpactivation VALUES('tpName', 'ACTIVE', 'vID', 'type', '0')");
      stm.execute("CREATE TABLE Typeactivation (TECHPACK_NAME VARCHAR(31), STATUS VARCHAR(31), TYPENAME VARCHAR(31),"
          + "TABLELEVEL VARCHAR(31), STORAGETIME BIGINT, TYPE VARCHAR(31), PARTITIONPLAN VARCHAR(31))");
      stm.executeUpdate("INSERT INTO Typeactivation VALUES('tpName', '2', 'typeName', '4', 5, '6', '7')");
      stm.execute("CREATE TABLE Dataformat (DATAFORMATID VARCHAR(31), TYPEID VARCHAR(31), VERSIONID VARCHAR(31), "
          + "OBJECTTYPE VARCHAR(31), FOLDERNAME VARCHAR(31), DATAFORMATTYPE VARCHAR(31))");
      stm.executeUpdate("INSERT INTO Dataformat VALUES('vID:typeName:dataType', 'typeID', 'versionID', 'objType', "
          + "'folderName', 'vID:typeName:dataType')");
      stm.execute("CREATE TABLE Defaulttags (TAGID VARCHAR(31), DATAFORMATID VARCHAR(31), DESCRIPTION VARCHAR(31))");
      stm.executeUpdate("INSERT INTO Defaulttags VALUES('tagID', 'vID:typeName:dataType', 'description')");
      stm.execute("CREATE TABLE Interfacemeasurement (TAGID VARCHAR(31), DATAFORMATID VARCHAR(31), "
          + "INTERFACENAME VARCHAR(31), TRANSFORMERID VARCHAR(31), STATUS BIGINT, MODIFTIME TIMESTAMP, "
          + "DESCRIPTION VARCHAR(31), TECHPACKVERSION VARCHAR(31), INTERFACEVERSION VARCHAR(31))");
      stm.execute("CREATE TABLE Transformer (TRANSFORMERID VARCHAR(31), VERSIONID VARCHAR(31), "
          + "DESCRIPTION VARCHAR(31), TYPE VARCHAR(31))");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }

    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
      ActivateInterface ai = new ActivateInterface();
      Class secretClass = ai.getClass();
      getDatabaseConnectionDetails = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      createDwhrepRockFactory = secretClass.getDeclaredMethod("createDwhrepRockFactory", null);
      copyInterfaceSet = secretClass.getDeclaredMethod("copyInterfaceSet", null);
      activateInterface = secretClass.getDeclaredMethod("activateInterface", null);
      getNewCollectionSetId = secretClass.getDeclaredMethod("getNewCollectionSetId", null);
      getNewCollectionId = secretClass.getDeclaredMethod("getNewCollectionId", null);
      getNewTransferActionId = secretClass.getDeclaredMethod("getNewTransferActionId", null);
      getNewMetaSchedulingsId = secretClass.getDeclaredMethod("getNewMetaSchedulingsId", null);
      interfaceAlreadyActivated = secretClass.getDeclaredMethod("interfaceAlreadyActivated", null);
      removeIntfSets = secretClass.getDeclaredMethod("removeIntfSets", null);
      etlrepRockFactory = secretClass.getDeclaredField("etlrepRockFactory");
      dwhrepRockFactory = secretClass.getDeclaredField("dwhrepRockFactory");
      propertiesFilepath = secretClass.getDeclaredField("propertiesFilepath");
      getDatabaseConnectionDetails.setAccessible(true);
      createDwhrepRockFactory.setAccessible(true);
      copyInterfaceSet.setAccessible(true);
      activateInterface.setAccessible(true);
      getNewCollectionSetId.setAccessible(true);
      getNewCollectionId.setAccessible(true);
      getNewTransferActionId.setAccessible(true);
      getNewMetaSchedulingsId.setAccessible(true);
      interfaceAlreadyActivated.setAccessible(true);
      removeIntfSets.setAccessible(true);
      etlrepRockFactory.setAccessible(true);
      dwhrepRockFactory.setAccessible(true);
      propertiesFilepath.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    stm.execute("DROP TABLE Meta_databases");
    stm.execute("DROP TABLE Meta_collection_sets");
    stm.execute("DROP TABLE Meta_schedulings");
    stm.execute("DROP TABLE Meta_collections");
    stm.execute("DROP TABLE Meta_transfer_actions");
    stm.execute("DROP TABLE Datainterface");
    stm.execute("DROP TABLE Interfacetechpacks");
    stm.execute("DROP TABLE Tpactivation");
    stm.execute("DROP TABLE Typeactivation");
    stm.execute("DROP TABLE Dataformat");
    stm.execute("DROP TABLE Defaulttags");
    stm.execute("DROP TABLE Interfacemeasurement");
    stm.execute("DROP TABLE Transformer");
  }

  @Ignore
  public void testExecute() {

    /* TODO runCommand method problem!! */
  }

  @Test
  public void testSetAndGetActivatedInterfaceName() {

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("aiName");
    assertEquals("aiName", ai.getActivatedInterfaceName());
  }

  @Test
  public void testSetAndGetOssName() {

    ActivateInterface ai = new ActivateInterface();
    ai.setOssName("ossName");
    assertEquals("ossName", ai.getOssName());
  }

  @Test
  public void testSetAndGetConfigurationDirectory() {

    ActivateInterface ai = new ActivateInterface();
    ai.setConfigurationDirectory("confDir");
    assertEquals("confDir", ai.getConfigurationDirectory());
  }

  @Test
  public void testSetAndGetBinDirectory() {

    ActivateInterface ai = new ActivateInterface();
    ai.setBinDirectory("binDir");
    assertEquals("binDir", ai.getBinDirectory());
  }

  @Test
  public void testSetAndGetOnlyActivateInterface() {

    ActivateInterface ai = new ActivateInterface();
    ai.setOnlyActivateInterface("only_ai");
    assertEquals("only_ai", ai.getOnlyActivateInterface());
  }

  @Test
  public void testSetAndGetExitValue() {

    ActivateInterface ai = new ActivateInterface();
    ai.setExitValue(10);
    assertEquals((Integer) 10, ai.getExitValue());
  }

  @Test
  public void testGetDatabaseConnectionDetails() {

    ActivateInterface ai = new ActivateInterface();
    Project proj = new Project();
    ai.setProject(proj);
    String propPath = System.getProperty("user.home") + File.separator + "DatabaseConnectionDetails";
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "DatabaseConnectionDetails",
        "ENGINE_DB_URL=url;ENGINE_DB_USERNAME=uName;ENGINE_DB_PASSWORD=passwd;ENGINE_DB_DRIVERNAME=driver");

    try {

      propertiesFilepath.set(ai, propPath);
      HashMap hm = (HashMap) getDatabaseConnectionDetails.invoke(ai, null);
      String expected = "url,uName,passwd,driver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + ","
          + hm.get("etlrepDatabasePassword") + "," + hm.get("etlrepDatabaseDriver");
      assertEquals(expected, actual);

      proj = ai.getProject();
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

    ActivateInterface ai = new ActivateInterface();
    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");
    Class secretClass = ai.getClass();

    try {

      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);
      RockFactory rf = (RockFactory) method.invoke(ai, new Object[] { hm });
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

    ActivateInterface ai = new ActivateInterface();

    try {

      etlrepRockFactory.set(ai, rockFact);
      assertNull(dwhrepRockFactory.get(ai));
      createDwhrepRockFactory.invoke(ai, null);
      assertNotNull(dwhrepRockFactory.get(ai));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateDwhrepRockFactory() failed");
    }
  }

  @Test
  public void testActivateInterface() {

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("if_name");

    try {

      dwhrepRockFactory.set(ai, rockFact);
      Boolean b = (Boolean) activateInterface.invoke(ai, null);
      assertEquals(true, b);

      IDataSet actualDataSet = new DatabaseConnection(c).createDataSet();
      ITable actualTable = actualDataSet.getTable("Interfacemeasurement");
			final URL url = ClassLoader.getSystemResource("XMLFiles");
		if(url == null){
			throw new FileNotFoundException("XMLFiles");
		}
		final File xmlBase = new File(url.toURI());
		final String xmlFile = xmlBase.getAbsolutePath() + "/com.distocraft.dc5000.install.ant_ActivateInterface_testActivateInterface/Expected.xml";
      IDataSet expectedDataSet = new FlatXmlDataSet(new File(xmlFile));
      ITable expectedTable = expectedDataSet.getTable("Interfacemeasurement");
      ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable.getTableMetaData()
          .getColumns());
      Assertion.assertEquals(expectedTable, filteredTable);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testActivateInterface() failed");
    }
  }

  @Ignore
  public void testCopyInterfaceSet() {

    /* TODO clean files and assertion */

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("copyaiName");
    ai.setOssName("ossi");

    try {

      Statement stm = c.createStatement();
      etlrepRockFactory.set(ai, rockFact);
      copyInterfaceSet.invoke(ai, null);
      IDataSet actualDataSet = new DatabaseConnection(c).createDataSet();
      ITable actualTable = actualDataSet.getTable("Meta_collection_sets");

      File f = new File(System.getProperty("user.home"), "ActivateInterfaceBEFORE.xml");
      FlatXmlWriter fw = new FlatXmlWriter(new FileOutputStream(f));
      fw.write(actualDataSet);
      stm.execute("DELETE FROM META_COLLECTION_SETS WHERE COLLECTION_SET_NAME='copyaiName-ossi'");
      stm.execute("DELETE FROM META_TRANSFER_ACTIONS WHERE TRANSFER_ACTION_NAME='TA_nameossi'");
      stm.execute("DELETE FROM META_COLLECTIONS WHERE COLLECTION_NAME='copyaiNameossi'");
      stm.execute("DELETE FROM META_SCHEDULINGS  WHERE LAST_EXECUTION_TIME='1970-01-01 02:00:00.0'");

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCopyInterfaceSet() failed");
    }
  }

  @Test
  public void testGetNewCollectionSetId() {

    ActivateInterface ai = new ActivateInterface();

    try {

      etlrepRockFactory.set(ai, rockFact);
      Long l = (Long) getNewCollectionSetId.invoke(ai, null);
      assertEquals("CollectionSetId", (Long) 3l, l);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNewCollectionSetId() failed");
    }
  }

  @Test
  public void testGetNewCollectionId() {

    ActivateInterface ai = new ActivateInterface();

    try {

      etlrepRockFactory.set(ai, rockFact);
      Long l = (Long) getNewCollectionId.invoke(ai, null);
      assertEquals("CollectionId", (Long) 3l, (Long) l);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNewCollectionId() failed");
    }
  }

  @Test
  public void testGetNewTransferActionId() {

    ActivateInterface ai = new ActivateInterface();

    try {

      etlrepRockFactory.set(ai, rockFact);
      Long l = (Long) getNewTransferActionId.invoke(ai, null);
      assertEquals("TransferActionId", (Long) 2l, (Long) l);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNewTransferActionId() failed");
    }
  }

  @Test
  public void testDirectoryCheckerSetExists() {

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("aiName");
    ai.setOssName("ossName");

    try {

      etlrepRockFactory.set(ai, rockFact);
      Boolean b = ai.directoryCheckerSetExists();
      assertTrue("true expected", b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testDirectoryCheckerSetExists() failed");
    }
  }

  @Test
  public void testGetNewMetaSchedulingsId() {

    ActivateInterface ai = new ActivateInterface();

    try {

      etlrepRockFactory.set(ai, rockFact);
      Long l = (Long) getNewMetaSchedulingsId.invoke(ai, null);
      assertEquals("SchedulingsId", (Long) 2l, l);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetNewMetaSchedulingsId() failed");
    }
  }

  @Test
  public void testInterfaceAlreadyActivated() {

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("aiName");
    ai.setOssName("ossName");

    try {

      etlrepRockFactory.set(ai, rockFact);
      Boolean b = (Boolean) interfaceAlreadyActivated.invoke(ai, null);
      assertTrue("true expected", b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testInterfaceAlreadyActivated() failed");
    }
  }

  @Test
  public void testInterfaceAlreadyActivated2() {

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("foo");
    ai.setOssName("bar");

    try {

      etlrepRockFactory.set(ai, rockFact);
      Boolean b = (Boolean) interfaceAlreadyActivated.invoke(ai, null);
      assertFalse("false expected", b);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testInterfaceAlreadyActivated() failed");
    }
  }

  @Ignore
  public void testRemoveIntfSets() {

    /* TODO clean files and fix assertions */

    ActivateInterface ai = new ActivateInterface();
    ai.setActivatedInterfaceName("aiName");
    ai.setOssName("ossName");

    try {

      etlrepRockFactory.set(ai, rockFact);
      removeIntfSets.invoke(ai, null);
      IDataSet actualDataSet = new DatabaseConnection(c).createDataSet();
      ITable actualTable = actualDataSet.getTable("Meta_collection_sets");
      assertEquals("Meta_collection_sets", 1, actualTable.getRowCount());
      actualTable = actualDataSet.getTable("Meta_collections");
      assertEquals("Meta_collections", 1, actualTable.getRowCount());
      actualTable = actualDataSet.getTable("Meta_transfer_actions");
      assertEquals("Meta_transfer_actions", 1, actualTable.getRowCount());
      actualTable = actualDataSet.getTable("Meta_schedulings");
      assertEquals("Meta_schedulings", 0, actualTable.getRowCount());

    } catch (Exception e) {
      e.printStackTrace();
      fail("testRemoveIntfSets() failed");
    }
  }
}
