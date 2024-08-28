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
 * Test class for SubsystemFactory. Testing handling of all the objects in
 * Subsystem table.
 */
public class SubsystemFactoryTest {

  private static SubsystemFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Subsystem whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = SubsystemFactory.class.getDeclaredField("vec");
    vec.setAccessible(true);

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

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Subsystem(rockFactory);
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
    stmt.executeUpdate("INSERT INTO Subsystem VALUES( 'testSUBSYSTEM3' )");
    stmt.executeUpdate("INSERT INTO Subsystem VALUES( 'testSUBSYSTEM2' )");
    stmt.executeUpdate("INSERT INTO Subsystem VALUES( 'testSUBSYSTEM1' )");

    /* Initializing tested object before each test */
    objUnderTest = new SubsystemFactory(rockFactory, whereObject);
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Subsystem");
    objUnderTest = null;
  }

  /**
   * Testing SubsystemFactory constructor. All rows found from Subsystem table
   * are put into vector.
   */
  @Test
  public void testSubsystemFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new SubsystemFactory(rockFactory, whereObject);

    /* Asserting all Subsystems are found and put into vector */
    try {
      Vector<Subsystem> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getSubsystem() + ", "
          + actualVector.get(1).getSubsystem() + ", " + actualVector.get(2).getSubsystem();
      String expected = "3, testSUBSYSTEM3, testSUBSYSTEM2, testSUBSYSTEM1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Subsystems was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testSubsystemFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new SubsystemFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing SubsystemFactory constructor. All rows found from Subsystem table
   * are put into vector and data validation is on.
   */
  @Test
  public void testSubsystemFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new SubsystemFactory(rockFactory, whereObject, "ORDER BY SUBSYSTEM");

    /* Asserting all Subsystems are found and put into vector */
    try {
      Vector<Subsystem> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getSubsystem() + ", "
          + actualVector.get(1).getSubsystem() + ", " + actualVector.get(2).getSubsystem();
      String expected = "3, testSUBSYSTEM1, testSUBSYSTEM2, testSUBSYSTEM3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Subsystems was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testSubsystemFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new SubsystemFactory(null, whereObject, "ORDER BY SUBSYSTEM");
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

    assertEquals("testSUBSYSTEM2", objUnderTest.getElementAt(1).getSubsystem().toString());
  }

  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtOutOfBounds() throws Exception {

    assertEquals(null, objUnderTest.getElementAt(5));
  }

  /**
   * Testing size retrieving of the vector containing Subsystem objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }

  /**
   * Testing vector retrieving containing Subsystem objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Subsystem> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getSubsystem() + ", "
          + actualVector.get(1).getSubsystem() + ", " + actualVector.get(2).getSubsystem();
      String expected = "3, testSUBSYSTEM3, testSUBSYSTEM2, testSUBSYSTEM1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Test comparing two Subsystem objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithSameVector() throws Exception {

    /* Creating another vector with the same vector */
    Vector otherVector = (Vector) vec.get(objUnderTest);

    /* Asserting the two vectors are the same */
    assertEquals(true, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Subsystem objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Subsystem objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Subsystem testObject = new Subsystem(rockFactory, "testSUBSYSTEM1");
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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Subsystem");
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
    assertEquals(SubsystemFactory.class, clonedObject.getClass());
  }
}
