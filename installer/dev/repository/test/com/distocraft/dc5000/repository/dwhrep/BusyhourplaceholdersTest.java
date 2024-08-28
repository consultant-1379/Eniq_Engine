package com.distocraft.dc5000.repository.dwhrep;

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
 * Test class for Busyhourplaceholders. Changes to Busyhourplaceholders table are made via
 * this class.
 */
public class BusyhourplaceholdersTest {

  private static Busyhourplaceholders objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field VERSIONID;
  
  private static Field BHLEVEL;
  
  private static Field PRODUCTPLACEHOLDERS;
  
  private static Field CUSTOMPLACEHOLDERS;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Busyhourplaceholders.class.getDeclaredField("newItem");
		VERSIONID = Busyhourplaceholders.class.getDeclaredField("VERSIONID");
		BHLEVEL = Busyhourplaceholders.class.getDeclaredField("BHLEVEL");
		PRODUCTPLACEHOLDERS = Busyhourplaceholders.class.getDeclaredField("PRODUCTPLACEHOLDERS");
		CUSTOMPLACEHOLDERS = Busyhourplaceholders.class.getDeclaredField("CUSTOMPLACEHOLDERS");
		newItem.setAccessible(true);
		VERSIONID.setAccessible(true);
		BHLEVEL.setAccessible(true);
		PRODUCTPLACEHOLDERS.setAccessible(true);
		CUSTOMPLACEHOLDERS.setAccessible(true);
	  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Busyhourplaceholders ( VERSIONID VARCHAR(31)  ,BHLEVEL VARCHAR(255) ,PRODUCTPLACEHOLDERS INTEGER  ,CUSTOMPLACEHOLDERS INTEGER )");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Busyhourplaceholders");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Busyhourplaceholders VALUES( 'testVERSIONID'  ,'testBHLEVEL'  ,1  ,1 )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Busyhourplaceholders");
    objUnderTest = null;
  }
  
  /**
   * Testing Busyhourplaceholders constructor variable initialization with null values.
   */
  @Test
  public void testBusyhourplaceholdersConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Busyhourplaceholders(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + BHLEVEL.get(objUnderTest)  + ", " + PRODUCTPLACEHOLDERS.get(objUnderTest)  + ", " + CUSTOMPLACEHOLDERS.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Busyhourplaceholders constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testBusyhourplaceholdersConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");

    /* Asserting that variables are initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + BHLEVEL.get(objUnderTest)  + ", " + PRODUCTPLACEHOLDERS.get(objUnderTest)  + ", " + CUSTOMPLACEHOLDERS.get(objUnderTest) ;
    String expected =  "testVERSIONID"  + ", testBHLEVEL"  + ", 1"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testBusyhourplaceholdersConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Busyhourplaceholders(null ,  "testVERSIONID",  "testBHLEVEL");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Busyhourplaceholders constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testBusyhourplaceholdersConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Busyhourplaceholders whereObject = new Busyhourplaceholders(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Busyhourplaceholders(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + BHLEVEL.get(objUnderTest)  + ", " + PRODUCTPLACEHOLDERS.get(objUnderTest)  + ", " + CUSTOMPLACEHOLDERS.get(objUnderTest) ;
    String expected =  "testVERSIONID"  + ", testBHLEVEL"  + ", 1"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testBusyhourplaceholdersConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Busyhourplaceholders whereObject = new Busyhourplaceholders(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Busyhourplaceholders(null, whereObject);
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
    assertEquals("Busyhourplaceholders", objUnderTest.getTableName());
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDB() throws Exception {

    /* Invoking tested method and asserting the update has been made */
    String actual = objUnderTest.updateDB() + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDBwithTimestamp() throws Exception {

    /* Invoking tested method and asserting the update has been made */
    String actual = objUnderTest.updateDB(true) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + false, actual);
  }
  
  /**
   * Testing database updating. Affected row count is returned.
   */
  @Test
  public void testUpdateDBWithConstructedWhereClause() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Busyhourplaceholders whereObject = new Busyhourplaceholders(rockFactory);

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
    Busyhourplaceholders whereObject = new Busyhourplaceholders(rockFactory);

    /* Invoking tested method and asserting the delete has been made */
    String actual = objUnderTest.deleteDB(whereObject) + ", " + newItem.get(objUnderTest);
    assertEquals("1, " + true, actual);
  }
  
  /**
   * Testing data saving to the database.
   */
  @Test
  public void testSaveDB() throws Exception {

    /* Calling the tested method twice with different setting */
    objUnderTest.saveDB();
    newItem.set(objUnderTest, true);
    objUnderTest.saveDB();

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Busyhourplaceholders");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Invoking tested method and asserting the data has been saved */
    String actual = rows + ", " + newItem.get(objUnderTest);
    assertEquals("2, " + false, actual);
  }
  
  /**
   * Testing data saving to the database.
   */
  @Test
  public void testSaveToDB() throws Exception {

    /* Calling the tested method twice, first insert, next update */
    newItem.set(objUnderTest, true);
    objUnderTest.saveToDB();
    VERSIONID.set(objUnderTest, "changed");
    HashSet testSet = new HashSet();
    testSet.add("VERSIONID");
    objUnderTest.setModifiedColumns(testSet);
    objUnderTest.saveToDB();

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Busyhourplaceholders");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Getting the VERSIONID column and see if it has changed */
    String queryResult = "";
    res = stmt.executeQuery("SELECT VERSIONID FROM Busyhourplaceholders");
    while (res.next()) {
      queryResult = res.getString(1);
    }

    /* Invoking tested method and asserting the data has been saved */
    String actual = rows + ", " + queryResult + ", " + newItem.get(objUnderTest);
    assertEquals("2, changed, " + false, actual);
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_tag() throws Exception {
    
    String expected = "<Busyhourplaceholders VERSIONID=\"'testVERSIONID'\" BHLEVEL=\"'testBHLEVEL'\" PRODUCTPLACEHOLDERS=\"1\" CUSTOMPLACEHOLDERS=\"1\" DiffStatus=\"\" />\n";
    assertEquals(expected, objUnderTest.toXML_tag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_startTag() throws Exception {
    
    String expected = "<Busyhourplaceholders VERSIONID=\"'testVERSIONID'\" BHLEVEL=\"'testBHLEVEL'\" PRODUCTPLACEHOLDERS=\"1\" CUSTOMPLACEHOLDERS=\"1\" DiffStatus=\"\" >\n";
    assertEquals(expected, objUnderTest.toXML_startTag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_endTag() throws Exception {
    
    assertEquals("</Busyhourplaceholders>\n", objUnderTest.toXML_endTag());
  }
  
  /**
   * Testing column data formatting into sql insert clause.
   */
  @Test
  public void testToSQLInsert() throws Exception {
    
    String expected = "insert into Busyhourplaceholders ( VERSIONID, BHLEVEL, PRODUCTPLACEHOLDERS, CUSTOMPLACEHOLDERS ) values "
      + "( 'testVERSIONID', 'testBHLEVEL', 1, 1 );\n";
    assertEquals(expected, objUnderTest.toSQLInsert());
  }
  
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetVersionid() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setVersionid(BusyhourplaceholdersTest.testStringGenerator("anotherVERSIONID", 128));
    assertEquals(BusyhourplaceholdersTest.testStringGenerator("anotherVERSIONID", 128), VERSIONID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetBhlevel() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setBhlevel(BusyhourplaceholdersTest.testStringGenerator("anotherBHLEVEL", 255));
    assertEquals(BusyhourplaceholdersTest.testStringGenerator("anotherBHLEVEL", 255), BHLEVEL.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetProductplaceholders() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setProductplaceholders(555);
    assertEquals(555, PRODUCTPLACEHOLDERS.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetCustomplaceholders() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setCustomplaceholders(555);
    assertEquals(555, CUSTOMPLACEHOLDERS.get(objUnderTest));
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
    String[] expectedKeys = { "VERSIONID","BHLEVEL"};

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
    objUnderTest = new Busyhourplaceholders(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  VERSIONID.get(objUnderTest)  + ", " + BHLEVEL.get(objUnderTest)  + ", " + PRODUCTPLACEHOLDERS.get(objUnderTest)  + ", " + CUSTOMPLACEHOLDERS.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", 0"  + ", 0" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing if the current primary key(s) equals with the parameter given
   * $testedClass object. If the primary keys are the same, true is returned,
   * otherwise false.
   */
  @Test
  public void testDbEquals() throws Exception {
    
    /* Creating a $testedClass object to which tested one is compared */
    Busyhourplaceholders compareObj = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");

    /* Testing first with null primary key value */
    VERSIONID.set(objUnderTest, null);
  	BHLEVEL.set(objUnderTest, null);
  	String actual = objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Testing with different key value */
    VERSIONID.set(objUnderTest,  "different");
  	BHLEVEL.set(objUnderTest,  "different");
  	actual += objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Finally test with same value and test assertion */
    VERSIONID.set(objUnderTest,  "testVERSIONID");
  	BHLEVEL.set(objUnderTest,  "testBHLEVEL");
  	actual += objUnderTest.dbEquals(compareObj);
    assertEquals(false + ", " + false + ", " + true, actual);
  }
  
  /**
   * Testing comparing another Busyhourplaceholders with our current one. If the two
   * Busyhourplaceholderss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnBusyhourplaceholders() throws Exception {

    /* Creating another Busyhourplaceholders which will be compared to the tested one */
    Busyhourplaceholders comparedObj = new Busyhourplaceholders(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Busyhourplaceholders with our current one. If the two
   * Busyhourplaceholderss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentBusyhourplaceholders() throws Exception {

    /* Creating another Busyhourplaceholders which will be compared to the tested one */
    Busyhourplaceholders comparedObj = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");
    comparedObj.setVersionid( "DifferentVERSIONID");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Busyhourplaceholders with our current one. If the two
   * Busyhourplaceholderss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameBusyhourplaceholders() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Busyhourplaceholders comparedObj = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Busyhourplaceholders with our current one using null value.
   */
  @Test
  public void testEqualsWithNullBusyhourplaceholders() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Busyhourplaceholders comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Busyhourplaceholders was null \n");
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
    assertEquals(Busyhourplaceholders.class, actualObject.getClass());
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
    Busyhourplaceholders testAgg = new Busyhourplaceholders(rockFactory ,  "testVERSIONID",  "testBHLEVEL");
    VERSIONID.set(objUnderTest, "changed");

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Versionid.
   */
  @Test
  public void testGetVersionidColumnSize() throws Exception {
    
     assertEquals(128, objUnderTest.getVersionidColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Versionid.
  */
  @Test
  public void testGetVersionidDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getVersionidDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Versionid.
  */
  @Test
  public void testGetVersionidSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getVersionidSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Bhlevel.
   */
  @Test
  public void testGetBhlevelColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getBhlevelColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Bhlevel.
  */
  @Test
  public void testGetBhlevelDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getBhlevelDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Bhlevel.
  */
  @Test
  public void testGetBhlevelSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getBhlevelSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Productplaceholders.
   */
  @Test
  public void testGetProductplaceholdersColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getProductplaceholdersColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Productplaceholders.
  */
  @Test
  public void testGetProductplaceholdersDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getProductplaceholdersDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Productplaceholders.
  */
  @Test
  public void testGetProductplaceholdersSQLType() throws Exception {
    
    assertEquals(4, objUnderTest.getProductplaceholdersSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Customplaceholders.
   */
  @Test
  public void testGetCustomplaceholdersColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getCustomplaceholdersColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Customplaceholders.
  */
  @Test
  public void testGetCustomplaceholdersDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getCustomplaceholdersDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Customplaceholders.
  */
  @Test
  public void testGetCustomplaceholdersSQLType() throws Exception {
    
    assertEquals(4, objUnderTest.getCustomplaceholdersSQLType());    
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
   * Testing original Busyhourplaceholders object setting and retrieving.
   */
  @Test
  public void testSetAndGetOriginal() throws Exception {

    objUnderTest = new Busyhourplaceholders(rockFactory, false);
    objUnderTest.setOriginal(objUnderTest);
    assertNotNull(objUnderTest.getOriginal());
  }

  /**
   * Testing checking of rock object. If it is new or updated true is returned,
   * otherwise false.
   */
  @Test
  public void testIsUpdated() throws Exception {

    /* No changes made to tested object */
    HashSet modifiedColumns = new HashSet();
    objUnderTest.setModifiedColumns(modifiedColumns);
    String actual = objUnderTest.isUpdated() + ", ";
    
    /* Rock object has been changed */
    Busyhourplaceholders changedOriginal = new Busyhourplaceholders(rockFactory, false);
    objUnderTest.setOriginal(changedOriginal);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object has been updated */
    modifiedColumns.add("updatedValue");
    objUnderTest.setModifiedColumns(modifiedColumns);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object is null initialized */
    objUnderTest = new Busyhourplaceholders(rockFactory, false);
    actual += objUnderTest.isUpdated();
    assertEquals(false + ", " + true + ", " + true + ", " + true, actual);
  }
  
  /**
   * Testing checking if rock object is changed.
   */
  @Test
  public void testIsChanged() throws Exception {
    
    /* Testing rock object checking with original object */
    String actual = objUnderTest.isChanged() + ", ";
    
    /* Changing the original object */
    Busyhourplaceholders changedOriginal = new Busyhourplaceholders(rockFactory, false);
    objUnderTest.setOriginal(changedOriginal);
    actual += objUnderTest.isChanged();
    assertEquals(false + ", " + true, actual);
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