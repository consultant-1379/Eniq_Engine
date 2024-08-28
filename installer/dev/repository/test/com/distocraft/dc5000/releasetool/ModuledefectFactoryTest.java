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
 * Test class for ModuledefectFactory. Testing handling of all the objects in
 * Moduledefect table.
 */
public class ModuledefectFactoryTest {

  private static ModuledefectFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Moduledefect whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = ModuledefectFactory.class.getDeclaredField("vec");
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
        .execute("CREATE TABLE Moduledefect ( BUILDNUMBER BIGINT  ,MODULE VARCHAR(31) ,TRACKERPROJECT VARCHAR(31) ,TRACKERID VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Moduledefect(rockFactory);
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
    stmt
        .executeUpdate("INSERT INTO Moduledefect VALUES( 3  ,'testMODULE3'  ,'testTRACKERPROJECT3'  ,'testTRACKERID3' )");
    stmt
        .executeUpdate("INSERT INTO Moduledefect VALUES( 2  ,'testMODULE2'  ,'testTRACKERPROJECT2'  ,'testTRACKERID2' )");
    stmt
        .executeUpdate("INSERT INTO Moduledefect VALUES( 1  ,'testMODULE1'  ,'testTRACKERPROJECT1'  ,'testTRACKERID1' )");

    /* Initializing tested object before each test */
    objUnderTest = new ModuledefectFactory(rockFactory, whereObject);
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Moduledefect");
    objUnderTest = null;
  }

  /**
   * Testing ModuledefectFactory constructor. All rows found from Moduledefect
   * table are put into vector.
   */
  @Test
  public void testModuledefectFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ModuledefectFactory(rockFactory, whereObject);

    /* Asserting all Moduledefects are found and put into vector */
    try {
      Vector<Moduledefect> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 3, 2, 1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Moduledefects was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuledefectFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ModuledefectFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }

  /**
   * Testing ModuledefectFactory constructor. All rows found from Moduledefect
   * table are put into vector and data validation is on.
   */
  @Test
  public void testModuledefectFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new ModuledefectFactory(rockFactory, whereObject, "ORDER BY BUILDNUMBER");

    /* Asserting all Moduledefects are found and put into vector */
    try {
      Vector<Moduledefect> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 1, 2, 3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Moduledefects was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testModuledefectFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new ModuledefectFactory(null, whereObject, "ORDER BY BUILDNUMBER");
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
   * Testing size retrieving of the vector containing Moduledefect objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }

  /**
   * Testing vector retrieving containing Moduledefect objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Moduledefect> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getBuildnumber() + ", "
          + actualVector.get(1).getBuildnumber() + ", " + actualVector.get(2).getBuildnumber();
      String expected = "3, 3, 2, 1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }

  /**
   * Test comparing two Moduledefect objects. True is returned if the two
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
   * Test comparing two Moduledefect objects. True is returned if the two
   * vectors containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }

  /**
   * Test comparing two Moduledefect objects. True is returned if the two
   * vectors containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Moduledefect testObject = new Moduledefect(rockFactory, 1L, "testMODULE1", "testTRACKERID1");
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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Moduledefect");
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
    assertEquals(ModuledefectFactory.class, clonedObject.getClass());
  }
}
