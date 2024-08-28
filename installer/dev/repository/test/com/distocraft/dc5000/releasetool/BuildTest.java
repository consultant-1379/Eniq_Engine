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
 * Test class for Build. Changes to Build table are made via this class.
 */
public class BuildTest {

  private static Build objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;

  private static Field newItem;

  private static Field BUILDNUMBER;

  private static Field MODULE;

  private static Field AUTHOR;

  private static Field BUILDDATE;

  private static Field BUILDTAG;

  private static Field MODULETESTER;

  private static Field MODULETESTDATE;

  private static Field TESTRESULT;

  private static Field timeStampName;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    newItem = Build.class.getDeclaredField("newItem");
    BUILDNUMBER = Build.class.getDeclaredField("BUILDNUMBER");
    MODULE = Build.class.getDeclaredField("MODULE");
    AUTHOR = Build.class.getDeclaredField("AUTHOR");
    BUILDDATE = Build.class.getDeclaredField("BUILDDATE");
    BUILDTAG = Build.class.getDeclaredField("BUILDTAG");
    MODULETESTER = Build.class.getDeclaredField("MODULETESTER");
    MODULETESTDATE = Build.class.getDeclaredField("MODULETESTDATE");
    TESTRESULT = Build.class.getDeclaredField("TESTRESULT");
    timeStampName = Build.class.getDeclaredField("timeStampName");
    newItem.setAccessible(true);
    BUILDNUMBER.setAccessible(true);
    MODULE.setAccessible(true);
    AUTHOR.setAccessible(true);
    BUILDDATE.setAccessible(true);
    BUILDTAG.setAccessible(true);
    MODULETESTER.setAccessible(true);
    MODULETESTDATE.setAccessible(true);
    TESTRESULT.setAccessible(true);
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
        .execute("CREATE TABLE Build ( BUILDNUMBER BIGINT  ,MODULE VARCHAR(31) ,AUTHOR VARCHAR(31) ,BUILDDATE TIMESTAMP  ,BUILDTAG VARCHAR(31) ,MODULETESTER VARCHAR(31) ,MODULETESTDATE TIMESTAMP  ,TESTRESULT VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Build");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt
        .executeUpdate("INSERT INTO Build VALUES( 1  ,'testMODULE'  ,'testAUTHOR'  ,'2000-01-01 00:00:00.0'  ,'testBUILDTAG'  ,'testMODULETESTER'  ,'2000-01-01 00:00:00.0'  ,'testTESTRESULT' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Build(rockFactory, 1L, "testMODULE");
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Build");
    objUnderTest = null;
  }

  /**
   * Testing Build constructor variable initialization with null values.
   */
  @Test
  public void testBuildConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Build(rockFactory);

    /* Asserting that variables are null initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", " + AUTHOR.get(objUnderTest)
        + ", " + BUILDDATE.get(objUnderTest) + ", " + BUILDTAG.get(objUnderTest) + ", "
        + MODULETESTER.get(objUnderTest) + ", " + MODULETESTDATE.get(objUnderTest) + ", "
        + TESTRESULT.get(objUnderTest);
    String expected = null + ", " + null + ", " + null + ", " + null + ", " + null + ", " + null + ", " + null + ", "
        + null;
    assertEquals(expected, actual);
  }

  /**
   * Testing Build constructor variable initialization with values taken from
   * database.
   */
  @Test
  public void testBuildConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Build(rockFactory, 1L, "testMODULE");

    /* Asserting that variables are initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", " + AUTHOR.get(objUnderTest)
        + ", " + BUILDDATE.get(objUnderTest) + ", " + BUILDTAG.get(objUnderTest) + ", "
        + MODULETESTER.get(objUnderTest) + ", " + MODULETESTDATE.get(objUnderTest) + ", "
        + TESTRESULT.get(objUnderTest);
    String expected = "1" + ", testMODULE" + ", testAUTHOR" + ", 2000-01-01 00:00:00.0" + ", testBUILDTAG"
        + ", testMODULETESTER" + ", 2000-01-01 00:00:00.0" + ", testTESTRESULT";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testBuildConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Build(null, 1L, "testMODULE");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing Build constructor variable initialization with values taken from
   * database.
   */
  @Test
  public void testBuildConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Build whereObject = new Build(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Build(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", " + AUTHOR.get(objUnderTest)
        + ", " + BUILDDATE.get(objUnderTest) + ", " + BUILDTAG.get(objUnderTest) + ", "
        + MODULETESTER.get(objUnderTest) + ", " + MODULETESTDATE.get(objUnderTest) + ", "
        + TESTRESULT.get(objUnderTest);
    String expected = "1" + ", testMODULE" + ", testAUTHOR" + ", 2000-01-01 00:00:00.0" + ", testBUILDTAG"
        + ", testMODULETESTER" + ", 2000-01-01 00:00:00.0" + ", testTESTRESULT";
    assertEquals(expected, actual);
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testBuildConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Build whereObject = new Build(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Build(null, whereObject);
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
    assertEquals("Build", objUnderTest.getTableName());
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
    Build whereObject = new Build(rockFactory);

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
    Build whereObject = new Build(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Build");
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
    objUnderTest.setModule(BuildTest.testStringGenerator("anotherMODULE", 255));
    assertEquals(BuildTest.testStringGenerator("anotherMODULE", 255), MODULE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetAuthor() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setAuthor(BuildTest.testStringGenerator("anotherAUTHOR", 255));
    assertEquals(BuildTest.testStringGenerator("anotherAUTHOR", 255), AUTHOR.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetBuilddate() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setBuilddate(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), BUILDDATE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetBuildtag() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setBuildtag(BuildTest.testStringGenerator("anotherBUILDTAG", 255));
    assertEquals(BuildTest.testStringGenerator("anotherBUILDTAG", 255), BUILDTAG.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetModuletester() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setModuletester(BuildTest.testStringGenerator("anotherMODULETESTER", 255));
    assertEquals(BuildTest.testStringGenerator("anotherMODULETESTER", 255), MODULETESTER.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetModuletestdate() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setModuletestdate(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), MODULETESTDATE.get(objUnderTest));
  }

  /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTestresult() throws Exception {

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTestresult(BuildTest.testStringGenerator("anotherTESTRESULT", 255));
    assertEquals(BuildTest.testStringGenerator("anotherTESTRESULT", 255), TESTRESULT.get(objUnderTest));
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
    String[] expectedKeys = { "BUILDNUMBER", "MODULE" };

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
    objUnderTest = new Build(rockFactory);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual = BUILDNUMBER.get(objUnderTest) + ", " + MODULE.get(objUnderTest) + ", " + AUTHOR.get(objUnderTest)
        + ", " + BUILDDATE.get(objUnderTest) + ", " + BUILDTAG.get(objUnderTest) + ", "
        + MODULETESTER.get(objUnderTest) + ", " + MODULETESTDATE.get(objUnderTest) + ", "
        + TESTRESULT.get(objUnderTest);
    String expected = "0" + ", " + ", " + ", " + new Timestamp(0) + ", " + ", " + ", " + new Timestamp(0) + ", ";
    assertEquals(expected, actual);
  }

  /**
   * Testing comparing another Build with our current one. If the two Builds are
   * the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnBuild() throws Exception {

    /* Creating another Build which will be compared to the tested one */
    Build comparedObj = new Build(rockFactory);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Build with our current one. If the two Builds are
   * the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentBuild() throws Exception {

    /* Creating another Build which will be compared to the tested one */
    Build comparedObj = new Build(rockFactory, 1L, "testMODULE");
    comparedObj.setBuildnumber(7L);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Build with our current one. If the two Builds are
   * the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameBuild() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Build comparedObj = new Build(rockFactory, 1L, "testMODULE");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }

  /**
   * Testing comparing another Build with our current one using null value.
   */
  @Test
  public void testEqualsWithNullBuild() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Build comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Build was null \n");
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
    assertEquals(Build.class, actualObject.getClass());
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
    Build testAgg = new Build(rockFactory, 1L, "testMODULE");
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
