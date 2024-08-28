package com.distocraft.dc5000.etl.rock;

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
 * Test class for Meta_transf_table_valuesFactory. Testing handling of all the objects in
 * Meta_transf_table_values table.
 */
public class Meta_transf_table_valuesFactoryTest {

  private static Meta_transf_table_valuesFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Meta_transf_table_values whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = Meta_transf_table_valuesFactory.class.getDeclaredField("vec");
    vec.setAccessible(true);

    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_transf_table_values ( OLD_VALUE VARCHAR(31)  ,NEW_VALUE VARCHAR(31) ,VERSION_NUMBER VARCHAR(31) ,TRANSF_TABLE_ID BIGINT )");
    
    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Meta_transf_table_values(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_transf_table_values");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
	  stmt.executeUpdate("INSERT INTO Meta_transf_table_values VALUES( 'testOLD_VALUE3'  ,'testNEW_VALUE3'  ,'testVERSION_NUMBER3'  ,3 )");
	  stmt.executeUpdate("INSERT INTO Meta_transf_table_values VALUES( 'testOLD_VALUE2'  ,'testNEW_VALUE2'  ,'testVERSION_NUMBER2'  ,2 )");
	  stmt.executeUpdate("INSERT INTO Meta_transf_table_values VALUES( 'testOLD_VALUE1'  ,'testNEW_VALUE1'  ,'testVERSION_NUMBER1'  ,1 )");

    /* Initializing tested object before each test */
    objUnderTest = new Meta_transf_table_valuesFactory(rockFactory, whereObject);
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_transf_table_values");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_transf_table_valuesFactory constructor. All rows found from Meta_transf_table_values
   * table are put into vector.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_transf_table_valuesFactory(rockFactory, whereObject);

    /* Asserting all Meta_transf_table_valuess are found and put into vector */
    try {
      Vector<Meta_transf_table_values> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getOld_value() + ", " +  actualVector.get(1).getOld_value() + ", " +  actualVector.get(2).getOld_value();
      String expected = "3, testOLD_VALUE3, testOLD_VALUE2, testOLD_VALUE1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Meta_transf_table_valuess was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_transf_table_valuesFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_transf_table_valuesFactory constructor. All rows found from Meta_transf_table_values
   * table are put into vector and data validation is on.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithValidate() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_transf_table_valuesFactory(rockFactory, whereObject, true);

    /* Asserting all Meta_transf_table_valuess are found and put into vector */
    try {     
      Vector<Meta_transf_table_values> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).isValidateData() + ", " +  actualVector.get(1).isValidateData() + ", " +  actualVector.get(2).isValidateData();
      String expected = 3 + ", " + true + ", " + true + ", " + true;
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithValidateNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_transf_table_valuesFactory(null, whereObject, true);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_transf_table_valuesFactory constructor. All rows found from Meta_transf_table_values
   * table are put into vector and data validation is on.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_transf_table_valuesFactory(rockFactory, whereObject, "ORDER BY OLD_VALUE");

    /* Asserting all Meta_transf_table_valuess are found and put into vector */
    try {
      Vector<Meta_transf_table_values> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getOld_value() + ", " +  actualVector.get(1).getOld_value() + ", " +  actualVector.get(2).getOld_value();
      String expected = "3, testOLD_VALUE1, testOLD_VALUE2, testOLD_VALUE3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Meta_transf_table_valuess was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_transf_table_valuesFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_transf_table_valuesFactory(null, whereObject, "ORDER BY OLD_VALUE");
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

    assertEquals( "testOLD_VALUE2" , objUnderTest.getElementAt(1).getOld_value().toString());
  }
  
  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtOutOfBounds() throws Exception {

    assertEquals(null, objUnderTest.getElementAt(5));
  }
  
  /**
   * Testing size retrieving of the vector containing Meta_transf_table_values objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }
  
  /**
   * Testing vector retrieving containing Meta_transf_table_values objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Meta_transf_table_values> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getOld_value() + ", " +  actualVector.get(1).getOld_value() + ", " +  actualVector.get(2).getOld_value();
      String expected = "3, testOLD_VALUE3, testOLD_VALUE2, testOLD_VALUE1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Test comparing two Meta_transf_table_values objects. True is returned if the two vectors
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
   * Test comparing two Meta_transf_table_values objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test comparing two Meta_transf_table_values objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Meta_transf_table_values testObject = new Meta_transf_table_values(rockFactory, "testOLD_VALUE1", "testVERSION_NUMBER1", 1L);
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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_transf_table_values");
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
    assertEquals(Meta_transf_table_valuesFactory.class, clonedObject.getClass());
  }
}