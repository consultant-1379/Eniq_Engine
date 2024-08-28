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
 * Test class for Releasemodule. Changes to Releasemodule table are made via
 * this class.
 */
public class ReleasemoduleTest {

  private static Releasemodule objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;

  private static Field newItem;

  private static Field RELEASEMODULE;

  private static Field RELEASESUBSYSTEM;

  private static Field VERSIONID;

  private static Field BUILDNUMBER;

  private static Field RSTATE;

  private static Field DEV_VERSIONID;

  private static Field timeStampName;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    newItem = Releasemodule.class.getDeclaredField("newItem");
    RELEASEMODULE = Releasemodule.class.getDeclaredField("RELEASEMODULE");
    RELEASESUBSYSTEM = Releasemodule.class.getDeclaredField("RELEASESUBSYSTEM");
    VERSIONID = Releasemodule.class.getDeclaredField("VERSIONID");
    BUILDNUMBER = Releasemodule.class.getDeclaredField("BUILDNUMBER");
    RSTATE = Releasemodule.class.getDeclaredField("RSTATE");
    DEV_VERSIONID = Releasemodule.class.getDeclaredField("DEV_VERSIONID");
    timeStampName = Releasemodule.class.getDeclaredField("timeStampName");
    newItem.setAccessible(true);
    RELEASEMODULE.setAccessible(true);
    RELEASESUBSYSTEM.setAccessible(true);
    VERSIONID.setAccessible(true);
    BUILDNUMBER.setAccessible(true);
    RSTATE.setAccessible(true);
    DEV_VERSIONID.setAccessible(true);
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
        .execute("CREATE TABLE Releasemodule ( RELEASEMODULE VARCHAR(31)  ,RELEASESUBSYSTEM VARCHAR(31) ,VERSIONID VARCHAR(31) ,BUILDNUMBER BIGINT  ,RSTATE VARCHAR(31) ,DEV_VERSIONID VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Releasemodule");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt
        .executeUpdate("INSERT INTO Releasemodule VALUES( 'testRELEASEMODULE'  ,'testRELEASESUBSYSTEM'  ,'testVERSIONID'  ,1  ,'testRSTATE'  ,'testDEV_VERSIONID' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Releasemodule(rockFactory, "testRELEASEMODULE", "testVERSIONID");
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Releasemodule");
    objUnderTest = null;
  }

  /**
   * Testing Releasemodule constructor variable initialization with null values.
   */
  @Test
  public void testReleasemoduleConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Releasemodule(rockFactory);

    /* Asserting that variables are null initialized */
    String actual = RELEASEMODULE.get(objUnderTest) + ", " + RELEASESUBSYSTEM.get(objUnderTest) + ", "
        + VERSIONID.get(objUnderTest) + ", " + BUILDNUMBER.get(objUnderTest) + ", " + RSTATE.get(objUnderTest) + ", "
        + DEV_VERSIONID.get(objUnderTest);
    String expected = null + ", " + null + ", " + null + ", " + null + ", " + null + ", " + null;
    assertEquals(expected, actual);
  }

  /**
   * Testing Releasemodule constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testReleasemoduleConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Releasemodule(rockFactory, "testRELEASEMODULE", "testVERSIONID");

    /* Asserting that variables are initialized */
    String actual = RELEASEMODULE.get(objUnderTest) + ", " + RELEASESUBSYSTEM.get(objUnderTest) + ", "
        + VERSIONID.get(objUnderTest) + ", " + BUILDNUMBER.get(objUnderTest) + ", " + RSTATE.get(objUnderTest) + ", "
        + DEV_VERSIONID.get(objUnderTest);
    String expected = "testRELEASEMODULE" + ", testRELEASESUBSYSTEM" + ", testVERSIONID" + ", 1" + ", testRSTATE"
        + ", testDEV_VERSIONID";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testReleasemoduleConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Releasemodule(null, "testRELEASEMODULE", "testVERSIONID");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing Releasemodule constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testReleasemoduleConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Releasemodule whereObject = new Releasemodule(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Releasemodule(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual = RELEASEMODULE.get(objUnderTest) + ", " + RELEASESUBSYSTEM.get(objUnderTest) + ", "
        + VERSIONID.get(objUnderTest) + ", " + BUILDNUMBER.get(objUnderTest) + ", " + RSTATE.get(objUnderTest) + ", "
        + DEV_VERSIONID.get(objUnderTest);
    String expected = "testRELEASEMODULE" + ", testRELEASESUBSYSTEM" + ", testVERSIONID" + ", 1" + ", testRSTATE"
        + ", testDEV_VERSIONID";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testReleasemoduleConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Releasemodule whereObject = new Releasemodule(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Releasemodule(null, whereObject);
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
    assertEquals("Releasemodule", objUnderTest.getTableName());
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
    Releasemodule whereObject = new Releasemodule(rockFactory);

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
    Releasemodule whereObject = new Releasemodule(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Releasemodule");
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
  public void testSetAndGetReleasemodule() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setReleasemodule(ReleasemoduleTest.testStringGenerator("anotherRELEASEMODULE", 255));
    assertEquals(ReleasemoduleTest.testStringGenerator("anotherRELEASEMODULE", 255), RELEASEMODULE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetReleasesubsystem() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setReleasesubsystem(ReleasemoduleTest.testStringGenerator("anotherRELEASESUBSYSTEM", 255));
    assertEquals(ReleasemoduleTest.testStringGenerator("anotherRELEASESUBSYSTEM", 255), RELEASESUBSYSTEM
        .get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetVersionid() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setVersionid(ReleasemoduleTest.testStringGenerator("anotherVERSIONID", 255));
    assertEquals(ReleasemoduleTest.testStringGenerator("anotherVERSIONID", 255), VERSIONID.get(objUnderTest));
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
  public void testSetAndGetRstate() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setRstate(ReleasemoduleTest.testStringGenerator("anotherRSTATE", 20));
    assertEquals(ReleasemoduleTest.testStringGenerator("anotherRSTATE", 20), RSTATE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetDev_versionid() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setDev_versionid(ReleasemoduleTest.testStringGenerator("anotherDEV_VERSIONID", 255));
    assertEquals(ReleasemoduleTest.testStringGenerator("anotherDEV_VERSIONID", 255), DEV_VERSIONID.get(objUnderTest));
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
    String[] expectedKeys = { "RELEASEMODULE", "VERSIONID" };

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
    objUnderTest = new Releasemodule(rockFactory);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual = RELEASEMODULE.get(objUnderTest) + ", " + RELEASESUBSYSTEM.get(objUnderTest) + ", "
        + VERSIONID.get(objUnderTest) + ", " + BUILDNUMBER.get(objUnderTest) + ", " + RSTATE.get(objUnderTest) + ", "
        + DEV_VERSIONID.get(objUnderTest);
    String expected = "" + ", " + ", " + ", 0" + ", " + ", ";
    assertEquals(expected, actual);
  }

  /**
   * Testing comparing another Releasemodule with our current one. If the two
   * Releasemodules are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnReleasemodule() throws Exception {

    /* Creating another Releasemodule which will be compared to the tested one */
    Releasemodule comparedObj = new Releasemodule(rockFactory);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Releasemodule with our current one. If the two
   * Releasemodules are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentReleasemodule() throws Exception {

    /* Creating another Releasemodule which will be compared to the tested one */
    Releasemodule comparedObj = new Releasemodule(rockFactory, "testRELEASEMODULE", "testVERSIONID");
    comparedObj.setReleasemodule("DifferentRELEASEMODULE");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Releasemodule with our current one. If the two
   * Releasemodules are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameReleasemodule() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Releasemodule comparedObj = new Releasemodule(rockFactory, "testRELEASEMODULE", "testVERSIONID");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Releasemodule with our current one using null
   * value.
   */
  @Test
  public void testEqualsWithNullReleasemodule() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Releasemodule comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Releasemodule was null \n");
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
    assertEquals(Releasemodule.class, actualObject.getClass());
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
    Releasemodule testAgg = new Releasemodule(rockFactory, "testRELEASEMODULE", "testVERSIONID");
    RELEASEMODULE.set(objUnderTest, "changed");

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
