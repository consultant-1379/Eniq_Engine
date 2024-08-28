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
 * Test class for Subsystem. Changes to Subsystem table are made via this class.
 */
public class SubsystemTest {

  private static Subsystem objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;

  private static Field newItem;

  private static Field SUBSYSTEM;

  private static Field timeStampName;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    newItem = Subsystem.class.getDeclaredField("newItem");
    SUBSYSTEM = Subsystem.class.getDeclaredField("SUBSYSTEM");
    timeStampName = Subsystem.class.getDeclaredField("timeStampName");
    newItem.setAccessible(true);
    SUBSYSTEM.setAccessible(true);
    timeStampName.setAccessible(true);

    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Subsystem ( SUBSYSTEM VARCHAR(31) )");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Subsystem");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Subsystem VALUES( 'testSUBSYSTEM' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Subsystem(rockFactory, "testSUBSYSTEM");
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Subsystem");
    objUnderTest = null;
  }

  /**
   * Testing Subsystem constructor variable initialization with null values.
   */
  @Test
  public void testSubsystemConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Subsystem(rockFactory);

    /* Asserting that variables are null initialized */
    String actual = (String) SUBSYSTEM.get(objUnderTest);
    String expected = null;
    assertEquals(expected, actual);
  }

  /**
   * Testing Subsystem constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testSubsystemConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Subsystem(rockFactory, "testSUBSYSTEM");

    /* Asserting that variables are initialized */
    String actual = (String) SUBSYSTEM.get(objUnderTest);
    String expected = "testSUBSYSTEM";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testSubsystemConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Subsystem(null, "testSUBSYSTEM");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing Subsystem constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testSubsystemConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Subsystem whereObject = new Subsystem(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Subsystem(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual = (String) SUBSYSTEM.get(objUnderTest);
    String expected = "testSUBSYSTEM";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testSubsystemConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Subsystem whereObject = new Subsystem(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Subsystem(null, whereObject);
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
    assertEquals("Subsystem", objUnderTest.getTableName());
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
    Subsystem whereObject = new Subsystem(rockFactory);

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
    Subsystem whereObject = new Subsystem(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Subsystem");
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
  public void testSetAndGetSubsystem() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setSubsystem(SubsystemTest.testStringGenerator("anotherSUBSYSTEM", 255));
    assertEquals(SubsystemTest.testStringGenerator("anotherSUBSYSTEM", 255), SUBSYSTEM.get(objUnderTest));
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
    String[] expectedKeys = { "SUBSYSTEM" };

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
    objUnderTest = new Subsystem(rockFactory);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual = (String) SUBSYSTEM.get(objUnderTest);
    String expected = "";
    assertEquals(expected, actual);
  }

  /**
   * Testing comparing another Subsystem with our current one. If the two
   * Subsystems are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnSubsystem() throws Exception {

    /* Creating another Subsystem which will be compared to the tested one */
    Subsystem comparedObj = new Subsystem(rockFactory);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Subsystem with our current one. If the two
   * Subsystems are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentSubsystem() throws Exception {

    /* Creating another Subsystem which will be compared to the tested one */
    Subsystem comparedObj = new Subsystem(rockFactory, "testSUBSYSTEM");
    comparedObj.setSubsystem("DifferentSUBSYSTEM");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Subsystem with our current one. If the two
   * Subsystems are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameSubsystem() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Subsystem comparedObj = new Subsystem(rockFactory, "testSUBSYSTEM");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Subsystem with our current one using null value.
   */
  @Test
  public void testEqualsWithNullSubsystem() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Subsystem comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Subsystem was null \n");
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
    assertEquals(Subsystem.class, actualObject.getClass());
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
    Subsystem testAgg = new Subsystem(rockFactory, "testSUBSYSTEM");
    SUBSYSTEM.set(objUnderTest, "changed");

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
