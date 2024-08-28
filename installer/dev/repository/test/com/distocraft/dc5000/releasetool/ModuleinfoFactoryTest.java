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
 * Test class for ModuleinfoFactory. Testing handling of all the objects in
 * Moduleinfo table.
 */
public class ModuleinfoFactoryTest {

  private static ModuleinfoFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Moduleinfo whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = ModuleinfoFactory.class.getDeclaredField("vec");
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
        .execute("CREATE TABLE Moduleinfo ( BUILDNUMBER BIGINT  ,MODULE VARCHAR(31) ,TYPE VARCHAR(31) ,SUMMARY VARCHAR(31) ,DESCRIPTION VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Moduleinfo(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Moduleinfo");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt
        .executeUpdate("INSERT INTO Moduleinfo VALUES( 3  ,'testMODULE3'  ,'testTYPE3'  ,'testSUMMARY3'  ,'testDESCRIPTION3' )");
    stmt
        .executeUpdate("INSERT INTO Moduleinfo VALUES( 2  ,'testMODULE2'  ,'testTYPE2'  ,'testSUMMARY2'  ,'testDESCRIPTION2' )");
    stmt
        .executeUpdate("INSERT INTO Moduleinfo VALUES( 1  ,'testMODULE1'  ,'testTYPE1'  ,'testSUMMARY1'  ,'testDESCRIPTION1' )");

    /* Initializing tested object before each test */
    objUnderTest = new ModuleinfoFactory(rockFactory, whereObject);
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Moduleinfo");
    objUnderTest = null;
  }

  /**
   * Testing ModuleinfoFactory constructor. All rows found from Moduleinfo table
   * are put into vector.
   */
  @Test
  public void testModuleinfoFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ModuleinfoFactory(rockFactory, whereObject);

    /* Asserting all Moduleinfos are found and put into vector */
    try {
      Vector<Moduleinfo> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 3, 2, 1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Moduleinfos was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuleinfoFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ModuleinfoFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing ModuleinfoFactory constructor. All rows found from Moduleinfo table
   * are put into vector and data validation is on.
   */
  @Test
  public void testModuleinfoFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ModuleinfoFactory(rockFactory, whereObject, "ORDER BY BUILDNUMBER");

    /* Asserting all Moduleinfos are found and put into vector */
    try {
      Vector<Moduleinfo> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 1, 2, 3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Moduleinfos was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuleinfoFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ModuleinfoFactory(null, whereObject, "ORDER BY BUILDNUMBER");
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

    assertEquals("2", objUnderTest.getElementAt(1).getBuildnumber().toString());
  }

  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtOutOfBounds() throws Exception {

    assertEquals(null, objUnderTest.getElementAt(5));
  }

  /**
   * Testing size retrieving of the vector containing Moduleinfo objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }

  /**
   * Testing vector retrieving containing Moduleinfo objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Moduleinfo> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 3, 2, 1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Test comparing two Moduleinfo objects. True is returned if the two vectors
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
   * Test comparing two Moduleinfo objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Moduleinfo objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Moduleinfo testObject = new Moduleinfo(rockFactory, 1L, "testMODULE1", "testSUMMARY1");
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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Moduleinfo");
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
    assertEquals(ModuleinfoFactory.class, clonedObject.getClass());
  }
}
