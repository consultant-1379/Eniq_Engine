package com.distocraft.dc5000.etl.engine.structure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.common.LoaderLog;
import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.system.AlarmConfigCacheWrapper;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;


/**
 * Test class for TrCollection class.
 * 
 * @author EJAAVAH
 * 
 */
public class TrCollectionTest{

  private static TrCollection objUnderTest;

  private static Statement stmt;

  private static RockFactory rockFactory;

  private static Meta_versions metaVersions;

  private static Meta_collections metaCollection;

  private static PluginLoader pLoader;

  private static EngineCom eCom;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    objUnderTest = new TrCollection();

    Connection con = null;
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(31), COLLECTION_SET_NAME VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31), VERSION_NUMBER VARCHAR(31), ENABLED_FLAG VARCHAR(31), TYPE VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");

    stmt.execute("CREATE TABLE Meta_collections (COLLECTION_ID BIGINT, COLLECTION_NAME VARCHAR(31), "
        + "COLLECTION VARCHAR(31), MAIL_ERROR_ADDR VARCHAR(31), MAIL_FAIL_ADDR VARCHAR(31), "
        + "MAIL_BUG_ADDR VARCHAR(31), MAX_ERRORS BIGINT, MAX_FK_ERRORS BIGINT, MAX_COL_LIMIT_ERRORS BIGINT, "
        + "CHECK_FK_ERROR_FLAG VARCHAR(31), CHECK_COL_LIMITS_FLAG VARCHAR(31), LAST_TRANSFER_DATE TIMESTAMP, "
        + "VERSION_NUMBER VARCHAR(31), COLLECTION_SET_ID BIGINT, USE_BATCH_ID VARCHAR(31), PRIORITY BIGINT, "
        + "QUEUE_TIME_LIMIT BIGINT, ENABLED_FLAG VARCHAR(31), SETTYPE VARCHAR(31), FOLDABLE_FLAG VARCHAR(31), "
        + "MEASTYPE VARCHAR(31), HOLD_FLAG VARCHAR(31), SCHEDULING_INFO VARCHAR(31))");
    
    stmt.executeUpdate("INSERT INTO Meta_collections VALUES(2, 'testCOLLECTION_NAME', 'testCOLLECTION', "
        + "'testMAIL_ERROR_ADDR', 'testMAIL_FAIL_ADDR', 'testMAIL_BUG_ADDR', 1, 1, 1, 'testCHECK_FK_ERROR_FLAG', "
        + "'testCHECK_COL_LIMITS_FLAG', '2000-01-01 00:00:00.0', 'versionnumber', 3, 'testUSE_BATCH_ID', 1, 1, "
        + "'testENABLED_FLAG', 'testSETTYPE', 'testFOLDABLE_FLAG', 'testMEASTYPE', 'testHOLD_FLAG', "
        + "'testSCHEDULING_INFO' )");
    
    stmt.executeUpdate("INSERT INTO Meta_collections VALUES(3, 'Loader_DC_E_TEST_MEASUREMENT_RAW', 'testCOLLECTION', "
            + "'testMAIL_ERROR_ADDR', 'testMAIL_FAIL_ADDR', 'testMAIL_BUG_ADDR', 1, 1, 1, 'testCHECK_FK_ERROR_FLAG', "
            + "'testCHECK_COL_LIMITS_FLAG', '2000-01-01 00:00:00.0', '((15))', 1, 'testUSE_BATCH_ID', 1, 1, "
            + "'testENABLED_FLAG', 'Loader', 'Y', 'testMEASTYPE', 'N', "
            + "'testSCHEDULING_INFO' )");
    stmt.executeUpdate("INSERT INTO Meta_collections VALUES(4, 'Aggregator_DC_E_TEST_MEASUREMENT_DAY', 'testCOLLECTION', "
            + "'testMAIL_ERROR_ADDR', 'testMAIL_FAIL_ADDR', 'testMAIL_BUG_ADDR', 1, 1, 1, 'testCHECK_FK_ERROR_FLAG', "
            + "'testCHECK_COL_LIMITS_FLAG', '2000-01-01 00:00:00.0', '((15))', 1, 'testUSE_BATCH_ID', 1, 1, "
            + "'testENABLED_FLAG', 'Aggregator', 'Y', 'testMEASTYPE', 'N', "
            + "'testSCHEDULING_INFO' )");

    stmt.execute("CREATE TABLE Meta_transfer_actions (VERSION_NUMBER VARCHAR(31), TRANSFER_ACTION_ID BIGINT, "
        + "COLLECTION_ID BIGINT, COLLECTION_SET_ID BIGINT, ACTION_TYPE VARCHAR(31), TRANSFER_ACTION_NAME VARCHAR(31), "
        + "ORDER_BY_NO BIGINT, DESCRIPTION VARCHAR(31), ENABLED_FLAG VARCHAR(31), CONNECTION_ID BIGINT, "
        + "WHERE_CLAUSE_02 VARCHAR(31), WHERE_CLAUSE_03 VARCHAR(31), ACTION_CONTENTS_03 VARCHAR(31), "
        + "ACTION_CONTENTS_02 VARCHAR(31), ACTION_CONTENTS_01 VARCHAR(31), WHERE_CLAUSE_01 VARCHAR(31))");
    
    stmt.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('versionnumber', 1, 2, '3', 'Loader', "
        + "'transferactionname', 1, 'description', 'enabledflag', 1 ,'', '', "
        + "'actioncontents03', 'actioncontents02', 'actioncontents01', 'tablename=DC_E_RAN_UCELL')");
    
    stmt.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('versionnumber', 2, 2, '3', 'UpdateDimSession', "
        + "'transferactionname', 1, 'description', 'enabledflag', 1 ,'', '', "
        + "'actioncontents03', 'actioncontents02', 'actioncontents01',  "
        + "'##Thu Apr 24 15:30:51 EEST 2008\nuseRAWSTATUS=true\nelement=RAN')");
    
    stmt.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('((15))', 3, 4, '1', 'GateKeeper', "
            + "'GateKeeper', 0, 'description', 'enabledflag', 1 ,'', '', "
            + "'actioncontents03', 'actioncontents02', 'actioncontents01',  "
            + "'##Thu Apr 24 15:30:51 EEST 2008\nuseRAWSTATUS=true\nelement=RAN')");
    stmt.executeUpdate("INSERT INTO Meta_transfer_actions VALUES('((15))', 4, 4, '1', 'Aggregation', "
            + "'Aggregator_DC_E_TEST_MEASUREMENT_DAY', 0, 'description', 'enabledflag', 1 ,'', '', "
            + "'actioncontents03', 'actioncontents02', 'actioncontents01',  "
            + "'##Thu Apr 24 15:30:51 EEST 2008\nuseRAWSTATUS=true\nelement=RAN')");
    
    
    stmt.execute("CREATE TABLE META_TRANSFER_BATCHES (ID BIGINT, START_DATE TIMESTAMP, "
        + "END_DATE TIMESTAMP, FAIL_FLAG VARCHAR(31), STATUS VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
        + "COLLECTION_SET_ID BIGINT, COLLECTION_ID BIGINT, META_COLLECTION_NAME VARCHAR(31), "
        + "META_COLLECTION_SET_NAME VARCHAR(31), SETTYPE VARCHAR(31), SLOT_ID INTEGER )");
    
    stmt.executeUpdate("INSERT INTO META_TRANSFER_BATCHES VALUES(1, '2000-01-01 00:00:00.0', "
        + "'2000-01-01 00:00:00.0', 'testFAIL_FLAG', 'testSTATUS', 'testVERSION_NUMBER', 1, 1, "
        + "'testMETA_COLLECTION_NAME', 'testMETA_COLLECTION_SET_NAME', 'testSETTYPE', 1 )");
    stmt.executeUpdate("INSERT INTO META_TRANSFER_BATCHES VALUES(3, '2000-01-01 00:00:00.0', "
        + "'2000-01-01 00:00:00.0', 'testFAIL_FLAG', 'testSTATUS', 'testVERSION_NUMBER', 1, 1, "
        + "'testMETA_COLLECTION_NAME', 'testMETA_COLLECTION_SET_NAME', 'testSETTYPE', 1 )");
    stmt.executeUpdate("INSERT INTO META_TRANSFER_BATCHES VALUES(8239, '2000-01-01 00:00:00.0', "
        + "'2000-01-01 00:00:00.0', 'testFAIL_FLAG', 'testSTATUS', 'testVERSION_NUMBER', 1, 1, "
        + "'testMETA_COLLECTION_NAME', 'testMETA_COLLECTION_SET_NAME', 'testSETTYPE', 1 )");
    stmt.executeUpdate("INSERT INTO META_TRANSFER_BATCHES VALUES(12345, '2000-01-01 00:00:00.0', "
        + "'2000-01-01 00:00:00.0', 'testFAIL_FLAG', 'testSTATUS', 'testVERSION_NUMBER', 1, 1, "
        + "'testMETA_COLLECTION_NAME', 'testMETA_COLLECTION_SET_NAME', 'testSETTYPE', 1 )");    
    stmt.executeUpdate("INSERT INTO META_TRANSFER_BATCHES VALUES(12346, '2000-01-01 00:00:00.0', "
            + "'2000-01-01 00:00:00.0', 'testFAIL_FLAG', 'testSTATUS', 'testVERSION_NUMBER', 1, 1, "
            + "'testMETA_COLLECTION_NAME', 'testMETA_COLLECTION_SET_NAME', 'testSETTYPE', 1 )");  
    
    stmt.execute("create table Log_AggregationRules (Aggregation varchar(255), Target_Table varchar(50))");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_MGW_REMOTEMSC_DAY', 'DC_E_MGW_REMOTEMSC_DAY')");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_MGW_REMOTEMSC_COUNT', 'DC_E_MGW_REMOTEMSC_COUNT')");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_MGW_REMOTEMSC_RAW', 'DC_E_MGW_REMOTEMSC_RAW')");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_MGW_REMOTEMSC_DAYBH', 'DC_E_MGW_REMOTEMSC_DAYBH')");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_TEST_MEASUREMENT_RAW', 'DC_E_TEST_MEASUREMENT_RAW')");
    stmt.executeUpdate("INSERT INTO Log_AggregationRules values ('DC_E_TEST_MEASUREMENT_DAY', 'DC_E_TEST_MEASUREMENT_DAY')");
    
    stmt.execute("create table AlarmInterface (INTERFACEID varchar (50), DESCRIPTION varchar (200), STATUS varchar (20), " +
    		"COLLECTION_SET_ID numeric (31), COLLECTION_ID numeric (31), QUEUE_NUMBER numeric (31))");
    
    stmt.executeUpdate("INSERT INTO AlarmInterface values ('AlarmInterface_15min', 'Alarm interface for 15 minutes interval.', 'active', 265, 5851, 3)");
    stmt.executeUpdate("INSERT INTO AlarmInterface values ('AlarmInterface_RD', 'Alarm interface for reduced delay alarms.', 'active', 270, 5892, 0)");
    
    
    stmt.execute("create table AlarmReport (INTERFACEID varchar (50), REPORTID varchar (255), REPORTNAME varchar (255), URL	varchar	(32000), STATUS varchar (10),  SIMULTANEOUS int)");
    stmt.executeUpdate("INSERT INTO AlarmReport values ('AlarmInterface_RD', '2f110b53-d266-47a7-b19d-23e02490c196', 'AM_DC_E_TEST_MEASUREMENT_RAW_pmMaxDelayVariation', 'reportname=AM_DC_E_TEST_MEASUREMENT_RAW_pmMaxDelayVariation&reportid=2f110b53-d266-47a7-b19d-23e02490c196&promptValue_Number of Days backwards:=1', 'ACTIVE', 1)");
    stmt.executeUpdate("INSERT INTO AlarmReport values ('AlarmInterface_RD', '4fa2b396-0221-4bf1-8eca-b9b266dd4e8f', 'AM_DC_E_TEST_MEASUREMENT_DAY', 'reportname=AM_DC_E_TEST_MEASUREMENT_DAY&reportid=4fa2b396-0221-4bf1-8eca-b9b266dd4e8f&promptValue_Number of Days backwards:=1', 'ACTIVE', 1)");
    stmt.executeUpdate("INSERT INTO AlarmReport values ('AlarmInterface_15min', '6542b66d-5f7f-49d4-bc05-713340e3a094', 'AM_RAN_CCDEVICE_pmSumCcSpMeasLoad', 'reportname=AM_RAN_CCDEVICE_pmSumCcSpMeasLoad&reportid=6542b66d-5f7f-49d4-bc05-713340e3a094', 'ACTIVE', 0)");
   
    stmt.execute("create table AlarmReportParameter (REPORTID varchar (255), NAME varchar (255), VALUE varchar (255))");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('2f110b53-d266-47a7-b19d-23e02490c196', 'eniqBasetableName', 'DC_E_TEST_MEASUREMENT_RAW')");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('2f110b53-d266-47a7-b19d-23e02490c196', 'Number of Days backwards:', 1)");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('4fa2b396-0221-4bf1-8eca-b9b266dd4e8f', 'eniqBasetableName', 'DC_E_TEST_MEASUREMENT_DAY')");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('4fa2b396-0221-4bf1-8eca-b9b266dd4e8f', 'Number of Days backwards:', 1)");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('6542b66d-5f7f-49d4-bc05-713340e3a094', 'eniqBasetableName', 'DC_E_RAN_CCDEVICE_RAW')");
    stmt.executeUpdate("INSERT INTO AlarmReportParameter values ('6542b66d-5f7f-49d4-bc05-713340e3a094', 'Row Status:', '')");
    
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    metaVersions = new Meta_versions(rockFactory);

    metaCollection = new Meta_collections(rockFactory, 2L, "versionnumber", 3L);

    pLoader = new PluginLoader("");

    eCom = new EngineCom();

    /* Home directory path */
    File homeDir = new File(System.getProperty("user.dir"));

    /* Parsing file directorypaths for property directories */
    String projectPath = homeDir.getPath();
    String[] pathToBeParsed = projectPath.split("\\\\");
    String parsedPath = "";
    for (int i = 0; i < pathToBeParsed.length; i++) {
      parsedPath += pathToBeParsed[i] + "/";
    }

    /* Creating static property file */
    File sp = new File(homeDir, "static.properties");
    sp.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(sp));
      pw.write("maxLoadClauseLength=1024\n");
      pw.print("SessionHandling.storageFile=" + homeDir + "storage.txt\n");
      pw.print("SessionHandling.log.types=LOADER\n");
      pw.print("SessionHandling.log.LOADER.class=" + LoaderLog.class.getName() + "\n");
      pw.print("SessionHandling.log.LOADER.inputTableDir=" + parsedPath + "\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Setting the system property for different directory paths */
    System.setProperty("dc5000.config.directory", homeDir.getPath());
    System.setProperty("ETLDATA_DIR", homeDir.getPath());

    /* Initializing Static Properties in order to initialize SessionHandler */
    new StaticProperties().reload();

    /* Initializing SessionHandler */
    SessionHandler.init();
    
    /* Creating ETLC property file */
	File ETLCConfFile = new File(System.getProperty("user.home") + File.separator + "ETLCServer.properties");
	ETLCConfFile.deleteOnExit();
	try {
		PrintWriter pw = new PrintWriter(new FileWriter(ETLCConfFile));
		pw.print("ENGINE_DB_URL=jdbc:hsqldb:mem:testdb\n");
		pw.print("ENGINE_DB_USERNAME=sa\n");
		pw.print("ENGINE_DB_PASSWORD=\n");
		pw.print("ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver\n");
		pw.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    stmt.execute("DROP TABLE META_TRANSFER_BATCHES");
    objUnderTest = null;
  }

  /**
   * Testing that correct ID is returned from META_TRANSFER_BATCHES table.
   */
  @Test
  public void testMaxIDCheck() throws Exception {

    /* Initializing test object */
    objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);

    /* Reflecting the tested method */
    Method maxIDCheck = TrCollection.class.getDeclaredMethod("maxIDCheck", new Class[] {});
    maxIDCheck.setAccessible(true);

    /* Asserting that the maximum value has been returned */
    assertEquals((long) 12346, maxIDCheck.invoke(objUnderTest, null));
  }  
  
  
  @Test
  public void createTriggerAlarmActionTestAggregation() throws Exception{
	  metaCollection.setSettype("Aggregator");
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  
	  Meta_transfer_actions aggregationAction = new Meta_transfer_actions(rockFactory);
	  aggregationAction.setConnection_id(1L);
	  aggregationAction.setOrder_by_no(1L);
	  
	  String seperator = ", ";
	  String transferActionID = "1234567899";
	  String setActionType = "TriggerScheduledSet";
	  String transferActionName = "TriggerAlarmsAction_" + metaCollection.getCollection_name();
	  String orderByNo = "2";
	  String actionContents = "Scheduling_AlarmInterface_RD";
	  StringBuilder expected = new StringBuilder();
	  
	  expected.append(transferActionID);expected.append(seperator);
	  expected.append(setActionType);expected.append(seperator);
	  expected.append(transferActionName);expected.append(seperator);
	  expected.append(orderByNo);expected.append(seperator);
	  expected.append(actionContents);
	  
	  
	  final Method createTriggerAlarmAction = TrCollection.class.getDeclaredMethod("createTriggerAlarmAction", new Class[] {Meta_transfer_actions.class});
	  createTriggerAlarmAction.setAccessible(true);
	  final Meta_transfer_actions actualAction = (Meta_transfer_actions)createTriggerAlarmAction.invoke(objUnderTest, aggregationAction);
	  
	  StringBuilder actual = new StringBuilder();
	  actual.append(actualAction.getTransfer_action_id());actual.append(seperator);
	  actual.append(actualAction.getAction_type());actual.append(seperator);
	  actual.append(actualAction.getTransfer_action_name());actual.append(seperator);
	  actual.append(actualAction.getOrder_by_no());actual.append(seperator);
	  actual.append(actionContents);
	  
	  assertEquals(expected.toString(), actual.toString());	  	  
  }
  
  @Test
  public void createTriggerAlarmActionTestLoader() throws Exception{
	  metaCollection.setSettype("Loader");
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  
	  Meta_transfer_actions aggregationAction = new Meta_transfer_actions(rockFactory);
	  aggregationAction.setConnection_id(1L);
	  aggregationAction.setOrder_by_no(1L);
	  
	  String seperator = ", ";
	  String transferActionID = "1234567890";
	  String setActionType = "TriggerScheduledSet";
	  String transferActionName = "TriggerAlarmsAction_" + metaCollection.getCollection_name();
	  String orderByNo = "2";
	  String actionContents = "Scheduling_AlarmInterface_RD";
	  StringBuilder expected = new StringBuilder();
	  
	  expected.append(transferActionID);expected.append(seperator);
	  expected.append(setActionType);expected.append(seperator);
	  expected.append(transferActionName);expected.append(seperator);
	  expected.append(orderByNo);expected.append(seperator);
	  expected.append(actionContents);
	  
	  
	  final Method createTriggerAlarmAction = TrCollection.class.getDeclaredMethod("createTriggerAlarmAction", new Class[] {Meta_transfer_actions.class});
	  createTriggerAlarmAction.setAccessible(true);
	  final Meta_transfer_actions actualAction = (Meta_transfer_actions)createTriggerAlarmAction.invoke(objUnderTest, aggregationAction);
	  
	  StringBuilder actual = new StringBuilder();
	  actual.append(actualAction.getTransfer_action_id());actual.append(seperator);
	  actual.append(actualAction.getAction_type());actual.append(seperator);
	  actual.append(actualAction.getTransfer_action_name());actual.append(seperator);
	  actual.append(actualAction.getOrder_by_no());actual.append(seperator);
	  actual.append(actionContents);
	  
	  assertEquals(expected.toString(), actual.toString());	  	  
  }
  
  @Test  
  @Ignore //Ignored because it can't pass without access to dwhrep, and DatabaseConnections.getDwhDBConnection() is static so can't be mocked out.
  public void getAggregationBaseTableTestSuc() throws Exception{	
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);	  
	  final String aggregation = "Aggregator_DC_E_MGW_REMOTEMSC_DAY";	  
	  final String expected = "DC_E_MGW_REMOTEMSC_DAY";	  	  
	  final Method getAggBaseTable = TrCollection.class.getDeclaredMethod("getAggregationBaseTable", new Class[] {String.class});
	  getAggBaseTable.setAccessible(true);
	  final String actual = (String)getAggBaseTable.invoke(objUnderTest, aggregation);
	  assertEquals(expected, actual);
  }    
  
  @Test  
  public void getAggregationBaseTableTestInvalidAgg() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);	 
	  final String aggregation = "Aggregator_DC_E_TEST";	  
	  final String expected = "";	  	  
	  final Method getAggBaseTable = TrCollection.class.getDeclaredMethod("getAggregationBaseTable", new Class[] {String.class});	  
	  getAggBaseTable.setAccessible(true);	    	  
	  final String actual = (String)getAggBaseTable.invoke(objUnderTest, aggregation);	  	  
	  assertEquals(expected, actual);
  }  
  
  @Test
  public void hasSimultaneousReportTestNull() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  String baseTable = null;
	  
	  final Method hasSimultaneousReport = TrCollection.class.getDeclaredMethod("hasSimultaneousReport", new Class[] {String.class});	  
	  hasSimultaneousReport.setAccessible(true);	    	  
	  final Boolean actual = (Boolean)hasSimultaneousReport.invoke(objUnderTest, baseTable);	
	  
	  assertFalse(actual);
  }
  
  @Test
  public void hasSimultaneousReportTestEmptyString() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  String baseTable = "";
	  
	  final Method hasSimultaneousReport = TrCollection.class.getDeclaredMethod("hasSimultaneousReport", new Class[] {String.class});	  
	  hasSimultaneousReport.setAccessible(true);	    	  
	  final Boolean actual = (Boolean)hasSimultaneousReport.invoke(objUnderTest, baseTable);	
	  
	  assertFalse(actual);
  }
  
  @Test
  public void hasSimultaneousReportTestFalse() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  String baseTable = "DC_E_TEST_MEASUREMENT_RAW_Fail";
	  AlarmConfigCacheWrapper.revalidate(rockFactory);
	  
	  final Method hasSimultaneousReport = TrCollection.class.getDeclaredMethod("hasSimultaneousReport", new Class[] {String.class});	  
	  hasSimultaneousReport.setAccessible(true);	    	  
	  final Boolean actual = (Boolean)hasSimultaneousReport.invoke(objUnderTest, baseTable);	
	  
	  assertFalse(actual);
  }
  
  @Test
  public void hasSimultaneousReportTestTrue() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  String baseTable = "DC_E_TEST_MEASUREMENT_RAW";
	  
	  final Method hasSimultaneousReport = TrCollection.class.getDeclaredMethod("hasSimultaneousReport", new Class[] {String.class});	  
	  hasSimultaneousReport.setAccessible(true);	    	  
	  final Boolean actual = (Boolean)hasSimultaneousReport.invoke(objUnderTest, baseTable);	
	  
	  assertTrue(actual);
  }
  
  @Test
  public void getTransferActionsNoDynamicAction() throws Exception{
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  
	  final Method getTransferActions = TrCollection.class.getDeclaredMethod("getTransferActions", new Class[] {});	  
	  getTransferActions.setAccessible(true);	    	  
	  final Vector<TransferAction> vecActual = (Vector<TransferAction>)getTransferActions.invoke(objUnderTest);	
	  int expected = 0;
	  int actual = vecActual.size();
	  assertEquals(expected, actual);
	  
  }
  
  /*@Test
  public void getTransferActions() throws Exception{
	  metaCollection = new Meta_collections(rockFactory, 4L, "((15))", 1L);
	  objUnderTest = new TrCollection(rockFactory, metaVersions, 1L, metaCollection, pLoader, eCom);
	  
	  final Method getTransferActions = TrCollection.class.getDeclaredMethod("getTransferActions", new Class[] {});	  
	  getTransferActions.setAccessible(true);	    	  
	  final Vector<TransferAction> vecActual = (Vector<TransferAction>)getTransferActions.invoke(objUnderTest);	
	  int expected = 3;
	  int actual = vecActual.size();
	  assertEquals(expected, actual);
  }
  */
  
}
