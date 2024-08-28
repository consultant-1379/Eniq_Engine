package com.distocraft.dc5000.releasetool;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;

/**
 * Test class for ReleasesubsystemFactory. Testing handling of all the objects
 * in Releasesubsystem table.
 */
public class ReleasesubsystemFactoryTest {

  private static ReleasesubsystemFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Releasesubsystem whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = ReleasesubsystemFactory.class.getDeclaredField("vec");
    vec.setAccessible(true);

    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt
        .execute("CREATE TABLE Releasesubsystem ( RELEASESUBSYSTEM VARCHAR(31)  ,VERSIONID VARCHAR(31) ,RELEASETYPE VARCHAR(31) ,RELEASEDATE TIMESTAMP  ,VERSION VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Releasesubsystem(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Releasesubsystem");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt
        .executeUpdate("INSERT INTO Releasesubsystem VALUES( 'testRELEASESUBSYSTEM3'  ,'testVERSIONID3'  ,'testRELEASETYPE3'  ,'2003-03-03 00:00:00.0'  ,'testVERSION3' )");
    stmt
        .executeUpdate("INSERT INTO Releasesubsystem VALUES( 'testRELEASESUBSYSTEM2'  ,'testVERSIONID2'  ,'testRELEASETYPE2'  ,'2002-02-02 00:00:00.0'  ,'testVERSION2' )");
    stmt
        .executeUpdate("INSERT INTO Releasesubsystem VALUES( 'testRELEASESUBSYSTEM1'  ,'testVERSIONID1'  ,'testRELEASETYPE1'  ,'2001-01-01 00:00:00.0'  ,'testVERSION1' )");

    /* Initializing tested object before each test */
    objUnderTest = new ReleasesubsystemFactory(rockFactory, whereObject);
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Releasesubsystem");
    objUnderTest = null;
  }

  /**
   * Testing ReleasesubsystemFactory constructor. All rows found from
   * Releasesubsystem table are put into vector.
   */
  @Test
  public void testReleasesubsystemFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ReleasesubsystemFactory(rockFactory, whereObject);

    /* Asserting all Releasesubsystems are found and put into vector */
    try {
      Vector<Releasesubsystem> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getReleasesubsystem() + ", "
          + actualVector.get(1).getReleasesubsystem() + ", " + actualVector.get(2).getReleasesubsystem();
      String expected = "3, testRELEASESUBSYSTEM3, testRELEASESUBSYSTEM2, testRELEASESUBSYSTEM1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Releasesubsystems was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testReleasesubsystemFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ReleasesubsystemFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing ReleasesubsystemFactory constructor. All rows found from
   * Releasesubsystem table are put into vector and data validation is on.
   */
  @Test
  public void testReleasesubsystemFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ReleasesubsystemFactory(rockFactory, whereObject, "ORDER BY RELEASESUBSYSTEM");

    /* Asserting all Releasesubsystems are found and put into vector */
    try {
      Vector<Releasesubsystem> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getReleasesubsystem() + ", "
          + actualVector.get(1).getReleasesubsystem() + ", " + actualVector.get(2).getReleasesubsystem();
      String expected = "3, testRELEASESUBSYSTEM1, testRELEASESUBSYSTEM2, testRELEASESUBSYSTEM3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Releasesubsystems was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testReleasesubsystemFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ReleasesubsystemFactory(null, whereObject, "ORDER BY RELEASESUBSYSTEM");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtWithGenericInput() throws Exception {

    assertEquals("testRELEASESUBSYSTEM2", objUnderTest.getElementAt(1).getReleasesubsystem().toString());
  }

  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtOutOfBounds() throws Exception {

    assertEquals(null, objUnderTest.getElementAt(5));
  }

  /**
   * Testing size retrieving of the vector containing Releasesubsystem objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }

  /**
   * Testing vector retrieving containing Releasesubsystem objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Releasesubsystem> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getReleasesubsystem() + ", "
          + actualVector.get(1).getReleasesubsystem() + ", " + actualVector.get(2).getReleasesubsystem();
      String expected = "3, testRELEASESUBSYSTEM3, testRELEASESUBSYSTEM2, testRELEASESUBSYSTEM1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Test comparing two Releasesubsystem objects. True is returned if the two
   * vectors containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithSameVector() throws Exception {

    /* Creating another vector with the same vector */
    Vector otherVector = (Vector) vec.get(objUnderTest);

    /* Asserting the two vectors are the same */
    assertEquals(true, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Releasesubsystem objects. True is returned if the two
   * vectors containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Releasesubsystem objects. True is returned if the two
   * vectors containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Releasesubsystem testObject = new Releasesubsystem(rockFactory, "testRELEASESUBSYSTEM1", "testVERSIONID1");
    otherVector.add(testObject);

    /* Asserting the two vectors are the same */
    assertEquals(false, objUnderTest.equals(otherVector));
  }

  /**
   * Test deleting objects from the database.
   */
  @Test
  public void testDeleteDB() throws Exception {

    /* Calling the tested object */
    String actual = objUnderTest.deleteDB() + ", ";

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Releasesubsystem");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Asserting object is deleted from the database */
    actual += rows;
    assertEquals(3 + ", " + 0, actual);
  }

  /**
   * Test object cloning.
   */
  @Test
  public void testClone() throws Exception {

    /* Asserting if cloning works */
    Object clonedObject = objUnderTest.clone();
    assertEquals(ReleasesubsystemFactory.class, clonedObject.getClass());
  }
}
