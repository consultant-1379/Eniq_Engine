package com.distocraft.dc5000.releasetool;

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
 * Test class for Moduledefect. Changes to Moduledefect table are made via this
 * class.
 */
public class ModuledefectTest {

  private static Moduledefect objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;

  private static Field newItem;

  private static Field BUILDNUMBER;

  private static Field MODULE;

  private static Field TRACKERPROJECT;

  private static Field TRACKERID;

  private static Field timeStampName;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    newItem = Moduledefect.class.getDeclaredField("newItem");
    BUILDNUMBER = Moduledefect.class.getDeclaredField("BUILDNUMBER");
    MODULE = Moduledefect.class.getDeclaredField("MODULE");
    TRACKERPROJECT = Moduledefect.class.getDeclaredField("TRACKERPROJECT");
    TRACKERID = Moduledefect.class.getDeclaredField("TRACKERID");
    timeStampName = Moduledefect.class.getDeclaredField("timeStampName");
    newItem.setAccessible(true);
    BUILDNUMBER.setAccessible(true);
    MODULE.setAccessible(true);
    TRACKERPROJECT.setAccessible(true);
    TRACKERID.setAccessible(true);
    timeStampName.setAccessible(true);

    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt
        .execute("CREATE TABLE Moduledefect ( BUILDNUMBER BIGINT  ,MODULE VARCHAR(31) ,TRACKERPROJECT VARCHAR(31) ,TRACKERID VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Moduledefect");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Moduledefect VALUES( 1  ,'testMODULE'  ,'testTRACKERPROJECT'  ,'testTRACKERID' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Moduledefect(rockFactory, 1L, "testMODULE", "testTRACKERID");
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Moduledefect");
    objUnderTest = null;
  }

  /**
   * Testing Moduledefect constructor variable initialization with null values.
   */
  @Test
  public void testModuledefectConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Moduledefect(rockFactory);

    /* Asserting that variables are null initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", "
        + TRACKERPROJECT.get(objUnderTest) + ", " + TRACKERID.get(objUnderTest);
    String expected = null + ", " + null + ", " + null + ", " + null;
    assertEquals(expected, actual);
  }

  /**
   * Testing Moduledefect constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testModuledefectConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Moduledefect(rockFactory, 1L, "testMODULE", "testTRACKERID");

    /* Asserting that variables are initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", "
        + TRACKERPROJECT.get(objUnderTest) + ", " + TRACKERID.get(objUnderTest);
    String expected = "1" + ", testMODULE" + ", testTRACKERPROJECT" + ", testTRACKERID";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuledefectConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Moduledefect(null, 1L, "testMODULE", "testTRACKERID");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing Moduledefect constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testModuledefectConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Moduledefect whereObject = new Moduledefect(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Moduledefect(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", "
        + TRACKERPROJECT.get(objUnderTest) + ", " + TRACKERID.get(objUnderTest);
    String expected = "1" + ", testMODULE" + ", testTRACKERPROJECT" + ", testTRACKERID";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuledefectConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Moduledefect whereObject = new Moduledefect(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Moduledefect(null, whereObject);
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
    Field modifiedColumns = objUnderTest.getClass().getDeclaredField("modifiedColumns");
    modifiedColumns.setAccessible(true);
    modifiedColumns.set(objUnderTest, testSet);
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
    assertEquals("Moduledefect", objUnderTest.getTableName());
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
    Moduledefect whereObject = new Moduledefect(rockFactory);

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
    Moduledefect whereObject = new Moduledefect(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Moduledefect");
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
  public void testSetAndGetBuildnumber() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setBuildnumber(555L);
    assertEquals(555L, BUILDNUMBER.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetModule() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setModule(ModuledefectTest.testStringGenerator("anotherMODULE", 255));
    assertEquals(ModuledefectTest.testStringGenerator("anotherMODULE", 255), MODULE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTrackerproject() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTrackerproject(ModuledefectTest.testStringGenerator("anotherTRACKERPROJECT", 255));
    assertEquals(ModuledefectTest.testStringGenerator("anotherTRACKERPROJECT", 255), TRACKERPROJECT.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTrackerid() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTrackerid(ModuledefectTest.testStringGenerator("anotherTRACKERID", 255));
    assertEquals(ModuledefectTest.testStringGenerator("anotherTRACKERID", 255), TRACKERID.get(objUnderTest));
  }

  /**
   * Testing timestamp retrieving.
   */
  @Test
  public void testGetTimestamp() throws Exception {

    /* Setting column values and asserting correct value is returned */
    assertEquals("LAST_UPDATED", objUnderTest.gettimeStampName());
  }

  /**
   * Testing column and sequence setting and retrieving via get method.
   */
  @Test
  public void testGetcolumnsAndSequences() throws Exception {

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
    String[] expectedKeys = { "BUILDNUMBER", "MODULE", "TRACKERID" };

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
    objUnderTest = new Moduledefect(rockFactory);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", "
        + TRACKERPROJECT.get(objUnderTest) + ", " + TRACKERID.get(objUnderTest);
    String expected = "0" + ", " + ", " + ", ";
    assertEquals(expected, actual);
  }

  /**
   * Testing comparing another Moduledefect with our current one. If the two
   * Moduledefects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnModuledefect() throws Exception {

    /* Creating another Moduledefect which will be compared to the tested one */
    Moduledefect comparedObj = new Moduledefect(rockFactory);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Moduledefect with our current one. If the two
   * Moduledefects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentModuledefect() throws Exception {

    /* Creating another Moduledefect which will be compared to the tested one */
    Moduledefect comparedObj = new Moduledefect(rockFactory, 1L, "testMODULE", "testTRACKERID");
    comparedObj.setBuildnumber(7L);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Moduledefect with our current one. If the two
   * Moduledefects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameModuledefect() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Moduledefect comparedObj = new Moduledefect(rockFactory, 1L, "testMODULE", "testTRACKERID");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Moduledefect with our current one using null
   * value.
   */
  @Test
  public void testEqualsWithNullModuledefect() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Moduledefect comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Moduledefect was null \n");
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
    assertEquals(Moduledefect.class, actualObject.getClass());
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
    Moduledefect testAgg = new Moduledefect(rockFactory, 1L, "testMODULE", "testTRACKERID");
    BUILDNUMBER.set(objUnderTest, 7L);

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }

  /**
   * Method checking the maximum length of a string used to test column setting.
   * If test string is too long, it will be cut to be within size limit.
   */
  private static String testStringGenerator(String testString, int maxSize) throws Exception {

    /* Checking if the test string is too large */
    if (testString.length() > maxSize) {
      testString = testString.substring(0, maxSize);
    }

    return testString;
  }
}
