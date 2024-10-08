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
 * Test class for Dwhpartition. Changes to Dwhpartition table are made via
 * this class.
 */
public class DwhpartitionTest {

  private static Dwhpartition objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field STORAGEID;
  
  private static Field TABLENAME;
  
  private static Field STARTTIME;
  
  private static Field ENDTIME;
  
  private static Field STATUS;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Dwhpartition.class.getDeclaredField("newItem");
		STORAGEID = Dwhpartition.class.getDeclaredField("STORAGEID");
		TABLENAME = Dwhpartition.class.getDeclaredField("TABLENAME");
		STARTTIME = Dwhpartition.class.getDeclaredField("STARTTIME");
		ENDTIME = Dwhpartition.class.getDeclaredField("ENDTIME");
		STATUS = Dwhpartition.class.getDeclaredField("STATUS");
		newItem.setAccessible(true);
		STORAGEID.setAccessible(true);
		TABLENAME.setAccessible(true);
		STARTTIME.setAccessible(true);
		ENDTIME.setAccessible(true);
		STATUS.setAccessible(true);
	  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Dwhpartition ( STORAGEID VARCHAR(31)  ,TABLENAME VARCHAR(31) ,STARTTIME TIMESTAMP  ,ENDTIME TIMESTAMP  ,STATUS VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Dwhpartition");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Dwhpartition VALUES( 'testSTORAGEID'  ,'testTABLENAME'  ,'2000-01-01 00:00:00.0'  ,'2000-01-01 00:00:00.0'  ,'testSTATUS' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Dwhpartition(rockFactory ,  "testTABLENAME");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Dwhpartition");
    objUnderTest = null;
  }
  
  /**
   * Testing Dwhpartition constructor variable initialization with null values.
   */
  @Test
  public void testDwhpartitionConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Dwhpartition(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  STORAGEID.get(objUnderTest)  + ", " + TABLENAME.get(objUnderTest)  + ", " + STARTTIME.get(objUnderTest)  + ", " + ENDTIME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Dwhpartition constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testDwhpartitionConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Dwhpartition(rockFactory ,  "testTABLENAME");

    /* Asserting that variables are initialized */
    String actual =  STORAGEID.get(objUnderTest)  + ", " + TABLENAME.get(objUnderTest)  + ", " + STARTTIME.get(objUnderTest)  + ", " + ENDTIME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  "testSTORAGEID"  + ", testTABLENAME"  + ", 2000-01-01 00:00:00.0"  + ", 2000-01-01 00:00:00.0"  + ", testSTATUS" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testDwhpartitionConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Dwhpartition(null ,  "testTABLENAME");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Dwhpartition constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testDwhpartitionConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Dwhpartition whereObject = new Dwhpartition(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Dwhpartition(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  STORAGEID.get(objUnderTest)  + ", " + TABLENAME.get(objUnderTest)  + ", " + STARTTIME.get(objUnderTest)  + ", " + ENDTIME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  "testSTORAGEID"  + ", testTABLENAME"  + ", 2000-01-01 00:00:00.0"  + ", 2000-01-01 00:00:00.0"  + ", testSTATUS" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testDwhpartitionConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Dwhpartition whereObject = new Dwhpartition(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Dwhpartition(null, whereObject);
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
    assertEquals("Dwhpartition", objUnderTest.getTableName());
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
    Dwhpartition whereObject = new Dwhpartition(rockFactory);

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
    Dwhpartition whereObject = new Dwhpartition(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Dwhpartition");
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
    STORAGEID.set(objUnderTest, "changed");
    HashSet testSet = new HashSet();
    testSet.add("STORAGEID");
    objUnderTest.setModifiedColumns(testSet);
    objUnderTest.saveToDB();

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Dwhpartition");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Getting the STORAGEID column and see if it has changed */
    String queryResult = "";
    res = stmt.executeQuery("SELECT STORAGEID FROM Dwhpartition");
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
    
    String expected = "<Dwhpartition STORAGEID=\"'testSTORAGEID'\" TABLENAME=\"'testTABLENAME'\" STARTTIME=\"'2000-01-01 00:00:00.0'\" ENDTIME=\"'2000-01-01 00:00:00.0'\" STATUS=\"'testSTATUS'\" DiffStatus=\"\" />\n";
    assertEquals(expected, objUnderTest.toXML_tag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_startTag() throws Exception {
    
    String expected = "<Dwhpartition STORAGEID=\"'testSTORAGEID'\" TABLENAME=\"'testTABLENAME'\" STARTTIME=\"'2000-01-01 00:00:00.0'\" ENDTIME=\"'2000-01-01 00:00:00.0'\" STATUS=\"'testSTATUS'\" DiffStatus=\"\" >\n";
    assertEquals(expected, objUnderTest.toXML_startTag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_endTag() throws Exception {
    
    assertEquals("</Dwhpartition>\n", objUnderTest.toXML_endTag());
  }
  
  /**
   * Testing column data formatting into sql insert clause.
   */
  @Test
  public void testToSQLInsert() throws Exception {
    
    String expected = "insert into Dwhpartition ( STORAGEID, TABLENAME, STARTTIME, ENDTIME, STATUS ) values "
      + "( 'testSTORAGEID', 'testTABLENAME', '2000-01-01 00:00:00.0', '2000-01-01 00:00:00.0', 'testSTATUS' );\n";
    assertEquals(expected, objUnderTest.toSQLInsert());
  }
  
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStorageid() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStorageid(DwhpartitionTest.testStringGenerator("anotherSTORAGEID", 255));
    assertEquals(DwhpartitionTest.testStringGenerator("anotherSTORAGEID", 255), STORAGEID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTablename() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTablename(DwhpartitionTest.testStringGenerator("anotherTABLENAME", 255));
    assertEquals(DwhpartitionTest.testStringGenerator("anotherTABLENAME", 255), TABLENAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStarttime() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStarttime(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), STARTTIME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetEndtime() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setEndtime(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), ENDTIME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStatus() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStatus(DwhpartitionTest.testStringGenerator("anotherSTATUS", 10));
    assertEquals(DwhpartitionTest.testStringGenerator("anotherSTATUS", 10), STATUS.get(objUnderTest));
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
    String[] expectedKeys = { "TABLENAME"};

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
    objUnderTest = new Dwhpartition(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  STORAGEID.get(objUnderTest)  + ", " + TABLENAME.get(objUnderTest)  + ", " + STARTTIME.get(objUnderTest)  + ", " + ENDTIME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", " + new Timestamp(0)  + ", " + new Timestamp(0)  + ", " ;
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
    Dwhpartition compareObj = new Dwhpartition(rockFactory ,  "testTABLENAME");

    /* Testing first with null primary key value */
    TABLENAME.set(objUnderTest, null);
  	String actual = objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Testing with different key value */
    TABLENAME.set(objUnderTest,  "different");
  	actual += objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Finally test with same value and test assertion */
    TABLENAME.set(objUnderTest,  "testTABLENAME");
  	actual += objUnderTest.dbEquals(compareObj);
    assertEquals(false + ", " + false + ", " + true, actual);
  }
  
  /**
   * Testing comparing another Dwhpartition with our current one. If the two
   * Dwhpartitions are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnDwhpartition() throws Exception {

    /* Creating another Dwhpartition which will be compared to the tested one */
    Dwhpartition comparedObj = new Dwhpartition(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Dwhpartition with our current one. If the two
   * Dwhpartitions are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentDwhpartition() throws Exception {

    /* Creating another Dwhpartition which will be compared to the tested one */
    Dwhpartition comparedObj = new Dwhpartition(rockFactory ,  "testTABLENAME");
    comparedObj.setStorageid( "DifferentSTORAGEID");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Dwhpartition with our current one. If the two
   * Dwhpartitions are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameDwhpartition() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Dwhpartition comparedObj = new Dwhpartition(rockFactory ,  "testTABLENAME");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Dwhpartition with our current one using null value.
   */
  @Test
  public void testEqualsWithNullDwhpartition() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Dwhpartition comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Dwhpartition was null \n");
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
    assertEquals(Dwhpartition.class, actualObject.getClass());
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
    Dwhpartition testAgg = new Dwhpartition(rockFactory ,  "testTABLENAME");
    STORAGEID.set(objUnderTest, "changed");

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Storageid.
   */
  @Test
  public void testGetStorageidColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getStorageidColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Storageid.
  */
  @Test
  public void testGetStorageidDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getStorageidDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Storageid.
  */
  @Test
  public void testGetStorageidSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getStorageidSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Tablename.
   */
  @Test
  public void testGetTablenameColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getTablenameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Tablename.
  */
  @Test
  public void testGetTablenameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTablenameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Tablename.
  */
  @Test
  public void testGetTablenameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTablenameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Starttime.
   */
  @Test
  public void testGetStarttimeColumnSize() throws Exception {
    
     assertEquals(23, objUnderTest.getStarttimeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Starttime.
  */
  @Test
  public void testGetStarttimeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getStarttimeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Starttime.
  */
  @Test
  public void testGetStarttimeSQLType() throws Exception {
    
    assertEquals(93, objUnderTest.getStarttimeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Endtime.
   */
  @Test
  public void testGetEndtimeColumnSize() throws Exception {
    
     assertEquals(23, objUnderTest.getEndtimeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Endtime.
  */
  @Test
  public void testGetEndtimeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getEndtimeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Endtime.
  */
  @Test
  public void testGetEndtimeSQLType() throws Exception {
    
    assertEquals(93, objUnderTest.getEndtimeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Status.
   */
  @Test
  public void testGetStatusColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getStatusColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Status.
  */
  @Test
  public void testGetStatusDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getStatusDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Status.
  */
  @Test
  public void testGetStatusSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getStatusSQLType());    
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
   * Testing original Dwhpartition object setting and retrieving.
   */
  @Test
  public void testSetAndGetOriginal() throws Exception {

    objUnderTest = new Dwhpartition(rockFactory, false);
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
    Dwhpartition changedOriginal = new Dwhpartition(rockFactory, false);
    objUnderTest.setOriginal(changedOriginal);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object has been updated */
    modifiedColumns.add("updatedValue");
    objUnderTest.setModifiedColumns(modifiedColumns);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object is null initialized */
    objUnderTest = new Dwhpartition(rockFactory, false);
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
    Dwhpartition changedOriginal = new Dwhpartition(rockFactory, false);
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