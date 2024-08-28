package com.distocraft.dc5000.etl.rock;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;

/**
 * Test class for Meta_system_monitors. Changes to Meta_system_monitors table are made via
 * this class.
 */
public class Meta_system_monitorsTest {

  private static Meta_system_monitors objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field MONITOR;
  
  private static Field HOSTNAME;
  
  private static Field TYPE;
  
  private static Field CONFIGURATION;
  
  private static Field EXECUTED;
  
  private static Field STATUS;
  
  private static Field timeStampName;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Meta_system_monitors.class.getDeclaredField("newItem");
		MONITOR = Meta_system_monitors.class.getDeclaredField("MONITOR");
		HOSTNAME = Meta_system_monitors.class.getDeclaredField("HOSTNAME");
		TYPE = Meta_system_monitors.class.getDeclaredField("TYPE");
		CONFIGURATION = Meta_system_monitors.class.getDeclaredField("CONFIGURATION");
		EXECUTED = Meta_system_monitors.class.getDeclaredField("EXECUTED");
		STATUS = Meta_system_monitors.class.getDeclaredField("STATUS");
		timeStampName = Meta_system_monitors.class.getDeclaredField("timeStampName");
	newItem.setAccessible(true);
		MONITOR.setAccessible(true);
		HOSTNAME.setAccessible(true);
		TYPE.setAccessible(true);
		CONFIGURATION.setAccessible(true);
		EXECUTED.setAccessible(true);
		STATUS.setAccessible(true);
		timeStampName.setAccessible(true);
  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_system_monitors ( MONITOR VARCHAR(31)  ,HOSTNAME VARCHAR(31) ,TYPE VARCHAR(31) ,CONFIGURATION VARCHAR(31) ,EXECUTED TIMESTAMP  ,STATUS VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_system_monitors");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Meta_system_monitors VALUES( 'testMONITOR'  ,'testHOSTNAME'  ,'testTYPE'  ,'testCONFIGURATION'  ,'2000-01-01 00:00:00.0'  ,'testSTATUS' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Meta_system_monitors(rockFactory ,  "testMONITOR",  "testHOSTNAME");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_system_monitors");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_system_monitors constructor variable initialization with null values.
   */
  @Test
  public void testMeta_system_monitorsConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_system_monitors(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  MONITOR.get(objUnderTest)  + ", " + HOSTNAME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + CONFIGURATION.get(objUnderTest)  + ", " + EXECUTED.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Meta_system_monitors constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_system_monitorsConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_system_monitors(rockFactory ,  "testMONITOR",  "testHOSTNAME");

    /* Asserting that variables are initialized */
    String actual =  MONITOR.get(objUnderTest)  + ", " + HOSTNAME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + CONFIGURATION.get(objUnderTest)  + ", " + EXECUTED.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  "testMONITOR"  + ", testHOSTNAME"  + ", testTYPE"  + ", testCONFIGURATION"  + ", 2000-01-01 00:00:00.0"  + ", testSTATUS" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_system_monitorsConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Meta_system_monitors(null ,  "testMONITOR",  "testHOSTNAME");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_system_monitors constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_system_monitorsConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_system_monitors whereObject = new Meta_system_monitors(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Meta_system_monitors(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  MONITOR.get(objUnderTest)  + ", " + HOSTNAME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + CONFIGURATION.get(objUnderTest)  + ", " + EXECUTED.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  "testMONITOR"  + ", testHOSTNAME"  + ", testTYPE"  + ", testCONFIGURATION"  + ", 2000-01-01 00:00:00.0"  + ", testSTATUS" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_system_monitorsConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_system_monitors whereObject = new Meta_system_monitors(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_system_monitors(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing modified columns set, get and clean methods. A set object is
   * initialized using set method, which is then retrieved using get method and
   * finally the set is cleared using clean method.
   */
  @Test
  public void testGetSetAndClearModifiedColumns() throws Exception {

    /* Creating a set for testing */
    HashSet testSet = new HashSet();
    testSet.add("testClause");

    /* calling the set, get and clean methods */
    objUnderTest.setModifiedColumns(testSet);
    HashSet actualSet = (HashSet) objUnderTest.gimmeModifiedColumns();
    String actualSetToString = actualSet.toString();
    objUnderTest.cleanModifiedColumns();

    /* Asserting that the field has been set and cleared */
    String actual = actualSetToString + ", " + testSet.toString();
    assertEquals("[testClause], []", actual);
  }
  
  /**
   * Testing table name retrieving. Returned value is static.
   */
  @Test
  public void testGetTableName() throws Exception {

    /* Invoking tested method and asserting that correct value is returned */
    assertEquals("Meta_system_monitors", objUnderTest.getTableName());
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDB() throws Exception {
  
    /**/
  	timeStampName.set(objUnderTest, "");

    /* Invoking tested method and asserting the update has been made */
    String actual = objUnderTest.updateDB() + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDBwithTimestamp() throws Exception {
  
    /**/
  	timeStampName.set(objUnderTest, "");

    /* Invoking tested method and asserting the update has been made */
    String actual = objUnderTest.updateDB(true) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDBWithConstructedWhereClause() throws Exception {
  
    /**/
  	timeStampName.set(objUnderTest, "");

    /* Creating where object which tells what sort of query is to be done */
    Meta_system_monitors whereObject = new Meta_system_monitors(rockFactory);

    /* Invoking tested method and asserting the update has been made */
    String actual = objUnderTest.updateDB(true, whereObject) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing inserting into database. Affected row count is returned.
   */
  @Test
  public void testInsertDB() throws Exception {

    /* Invoking tested method and asserting the insert has been made */
    String actual = objUnderTest.insertDB() + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing inserting into database. Affected row count is returned.
   */
  @Test
  public void testInsertDBwithTimestamp() throws Exception {

    /* Invoking tested method and asserting the insert has been made */
    String actual = objUnderTest.insertDB(true, true) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing deleting from database. Affected row count is returned.
   */
  @Test
  public void testDeleteDB() throws Exception {

    /* Invoking tested method and asserting the delete has been made */
    String actual = objUnderTest.deleteDB() + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + true, actual);
  }
  
  /**
   * Testing deleting from database. Affected row count is returned.
   */
  @Test
  public void testDeleteDBWithConstructedWhereClause() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_system_monitors whereObject = new Meta_system_monitors(rockFactory);

    /* Invoking tested method and asserting the delete has been made */
    String actual = objUnderTest.deleteDB(whereObject) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + true, actual);
  }
  
  /**
   * Testing data saving to the database.
   */
  @Test
  public void testSaveDB() throws Exception {
  
    /**/
  	timeStampName.set(objUnderTest, "");

    /* Calling the tested method twice with different setting */
    objUnderTest.saveDB();
    newItem.set(objUnderTest, true);
    objUnderTest.saveDB();

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_system_monitors");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Invoking tested method and asserting the data has been saved */
    String actual = rows + ", " + newItem.get(objUnderTest);
    assertEquals("2, " + false, actual);
  }
  
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetMonitor() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setMonitor(Meta_system_monitorsTest.testStringGenerator("anotherMONITOR", 255));
    assertEquals(Meta_system_monitorsTest.testStringGenerator("anotherMONITOR", 255), MONITOR.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetHostname() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setHostname(Meta_system_monitorsTest.testStringGenerator("anotherHOSTNAME", 255));
    assertEquals(Meta_system_monitorsTest.testStringGenerator("anotherHOSTNAME", 255), HOSTNAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetType() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setType(Meta_system_monitorsTest.testStringGenerator("anotherTYPE", 32));
    assertEquals(Meta_system_monitorsTest.testStringGenerator("anotherTYPE", 32), TYPE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetConfiguration() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setConfiguration(Meta_system_monitorsTest.testStringGenerator("anotherCONFIGURATION", 32000));
    assertEquals(Meta_system_monitorsTest.testStringGenerator("anotherCONFIGURATION", 32000), CONFIGURATION.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetExecuted() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setExecuted(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), EXECUTED.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStatus() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStatus(Meta_system_monitorsTest.testStringGenerator("anotherSTATUS", 10));
    assertEquals(Meta_system_monitorsTest.testStringGenerator("anotherSTATUS", 10), STATUS.get(objUnderTest));
  }
    
  /**
   * Testing timestamp retrieving.
   */
  @Test
  public void testGetTimestamp() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    assertEquals("LAST_UPDATED", objUnderTest.gettimeStampName());
  }
  
  /**
   * Testing column and sequence setting and retrieving via get method.
   */
  @Test
  public void testGetcolumnsAndSequences() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column and sequences */
    String[] expectedKeys = { "testColumn", "testSequence" };
    objUnderTest.setcolumnsAndSequences(expectedKeys);

    /* Asserting that */
    String[] actualKeys = objUnderTest.getcolumnsAndSequences();
    try {
      for (int i = 0; i < actualKeys.length; i++) {
        assertEquals(expectedKeys[i], actualKeys[i]);
      }
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test failed - There were different amount of actual keys than expected! \n" + aioobe);
    } catch (Exception e) {
      fail("Test Failed - Unexpected error occurred: \n" + e);
    }
  }
  
  /**
   * Testing primary key retrieving via get method.
   */
  @Test
  public void testGetPrimaryKeys() throws Exception {

    String[] actualKeys = objUnderTest.getprimaryKeyNames();
    String[] expectedKeys = { "MONITOR","HOSTNAME"};

    try {
      for (int i = 0; i < actualKeys.length; i++) {
        assertEquals(expectedKeys[i], actualKeys[i]);
      }
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test failed - There were different amount of actual keys than expected! \n" + aioobe);
    } catch (Exception e) {
      fail("Test Failed - Unexpected error occurred: \n" + e);
    }
  }
  
  /**
   * Testing rockfactory object retrieving via get method.
   */
  @Test
  public void testGetRockFactory() throws Exception {

    RockFactory actualRock = objUnderTest.getRockFactory();
    String actual = actualRock.getDbURL() + ", " + actualRock.getUserName() + ", " + actualRock.getPassword() + ", "
        + actualRock.getDriverName();
    assertEquals("jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver", actual);
  }
  
  /**
   * Testing null removing from column values.
   */
  @Test
  public void testRemoveNulls() throws Exception {

    /* Initializing tested object with nulls */
    objUnderTest = new Meta_system_monitors(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  MONITOR.get(objUnderTest)  + ", " + HOSTNAME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + CONFIGURATION.get(objUnderTest)  + ", " + EXECUTED.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", "  + ", "  + ", " + new Timestamp(0)  + ", " ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing comparing another Meta_system_monitors with our current one. If the two
   * Meta_system_monitorss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnMeta_system_monitors() throws Exception {

    /* Creating another Meta_system_monitors which will be compared to the tested one */
    Meta_system_monitors comparedObj = new Meta_system_monitors(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_system_monitors with our current one. If the two
   * Meta_system_monitorss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentMeta_system_monitors() throws Exception {

    /* Creating another Meta_system_monitors which will be compared to the tested one */
    Meta_system_monitors comparedObj = new Meta_system_monitors(rockFactory ,  "testMONITOR",  "testHOSTNAME");
    comparedObj.setMonitor( "DifferentMONITOR");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_system_monitors with our current one. If the two
   * Meta_system_monitorss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameMeta_system_monitors() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_system_monitors comparedObj = new Meta_system_monitors(rockFactory ,  "testMONITOR",  "testHOSTNAME");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_system_monitors with our current one using null value.
   */
  @Test
  public void testEqualsWithNullMeta_system_monitors() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_system_monitors comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Meta_system_monitors was null \n");
    } catch (NullPointerException npe) {
      // Test passed
    } catch (Exception e) {
      fail("Test Failed - Unexpected exception thrown! \n" + e);
    }
  }
  
  /**
   * Testing cloning of tested class.
   */
  @Test
  public void testClone() throws Exception {

    Object actualObject = objUnderTest.clone();
    assertEquals(Meta_system_monitors.class, actualObject.getClass());
  }
  
  /**
   * Testing checking the primary key definitions. If no primary keys are
   * defined false is returned, otherwise true.
   */
  @Test
  public void testIsPrimaryDefined() throws Exception {

    String actual = objUnderTest.isPrimaryDefined() + ", ";
    Field primaryKeyNames = objUnderTest.getClass().getDeclaredField("primaryKeyNames");
    primaryKeyNames.setAccessible(true);
    String[] emptyPrimaries = {};
    primaryKeyNames.set(objUnderTest, emptyPrimaries);
    actual += objUnderTest.isPrimaryDefined();
    assertEquals(true + ", " + false, actual);
  }
  
  /**
   * Not implemented.
   */
  @Test
  public void testSetDefaults() throws Exception {
  }
  
  /**
   * Testing if tested object exists in the database. If object exists true is
   * returned, otherwise false.
   */
  @Test
  public void testExistsDB() throws Exception {

    String actual = objUnderTest.existsDB() + ", ";
    Meta_system_monitors testAgg = new Meta_system_monitors(rockFactory ,  "testMONITOR",  "testHOSTNAME");
    MONITOR.set(objUnderTest, "changed");

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Monitor.
   */
  @Test
  public void testGetMonitorColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getMonitorColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Monitor.
  */
  @Test
  public void testGetMonitorDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getMonitorDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Monitor.
  */
  @Test
  public void testGetMonitorSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getMonitorSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Hostname.
   */
  @Test
  public void testGetHostnameColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getHostnameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Hostname.
  */
  @Test
  public void testGetHostnameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getHostnameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Hostname.
  */
  @Test
  public void testGetHostnameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getHostnameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Type.
   */
  @Test
  public void testGetTypeColumnSize() throws Exception {
    
     assertEquals(32, objUnderTest.getTypeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Type.
  */
  @Test
  public void testGetTypeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTypeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Type.
  */
  @Test
  public void testGetTypeSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTypeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Configuration.
   */
  @Test
  public void testGetConfigurationColumnSize() throws Exception {
    
     assertEquals(32000, objUnderTest.getConfigurationColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Configuration.
  */
  @Test
  public void testGetConfigurationDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getConfigurationDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Configuration.
  */
  @Test
  public void testGetConfigurationSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getConfigurationSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Executed.
   */
  @Test
  public void testGetExecutedColumnSize() throws Exception {
    
     assertEquals(23, objUnderTest.getExecutedColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Executed.
  */
  @Test
  public void testGetExecutedDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getExecutedDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Executed.
  */
  @Test
  public void testGetExecutedSQLType() throws Exception {
    
    assertEquals(93, objUnderTest.getExecutedSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Status.
   */
  @Test
  public void testGetStatusColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getStatusColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Status.
  */
  @Test
  public void testGetStatusDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getStatusDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Status.
  */
  @Test
  public void testGetStatusSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getStatusSQLType());    
  }
  
    /**
   * Testing flag variable setting and retrieving.
   */
  @Test
  public void testSetAndGetflagVariables() throws Exception {

    objUnderTest.setNewItem(true);
    objUnderTest.setValidateData(true);
    assertEquals(true + ", " + true, objUnderTest.isNewItem() + ", " + objUnderTest.isValidateData());
  }
  
  /**
   * Method checking the maximum length of a string used to test column setting.
   * If test string is too long, it will be cut to be within size limit.
   */
  private static String testStringGenerator(String testString, int maxSize) throws Exception {
  
    /* Checking if the test string is too large */
    if(testString.length() > maxSize) {
      testString = testString.substring(0, maxSize);
    }
    
    return testString;
  }
}