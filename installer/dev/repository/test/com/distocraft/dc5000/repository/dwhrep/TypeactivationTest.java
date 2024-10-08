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
 * Test class for Typeactivation. Changes to Typeactivation table are made via
 * this class.
 */
public class TypeactivationTest {

  private static Typeactivation objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field TECHPACK_NAME;
  
  private static Field STATUS;
  
  private static Field TYPENAME;
  
  private static Field TABLELEVEL;
  
  private static Field STORAGETIME;
  
  private static Field TYPE;
  
  private static Field PARTITIONPLAN;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Typeactivation.class.getDeclaredField("newItem");
		TECHPACK_NAME = Typeactivation.class.getDeclaredField("TECHPACK_NAME");
		STATUS = Typeactivation.class.getDeclaredField("STATUS");
		TYPENAME = Typeactivation.class.getDeclaredField("TYPENAME");
		TABLELEVEL = Typeactivation.class.getDeclaredField("TABLELEVEL");
		STORAGETIME = Typeactivation.class.getDeclaredField("STORAGETIME");
		TYPE = Typeactivation.class.getDeclaredField("TYPE");
		PARTITIONPLAN = Typeactivation.class.getDeclaredField("PARTITIONPLAN");
		newItem.setAccessible(true);
		TECHPACK_NAME.setAccessible(true);
		STATUS.setAccessible(true);
		TYPENAME.setAccessible(true);
		TABLELEVEL.setAccessible(true);
		STORAGETIME.setAccessible(true);
		TYPE.setAccessible(true);
		PARTITIONPLAN.setAccessible(true);
	  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Typeactivation ( TECHPACK_NAME VARCHAR(31)  ,STATUS VARCHAR(31) ,TYPENAME VARCHAR(31) ,TABLELEVEL VARCHAR(31) ,STORAGETIME BIGINT  ,TYPE VARCHAR(31) ,PARTITIONPLAN VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Typeactivation");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Typeactivation VALUES( 'testTECHPACK_NAME'  ,'testSTATUS'  ,'testTYPENAME'  ,'testTABLELEVEL'  ,1  ,'testTYPE'  ,'testPARTITIONPLAN' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Typeactivation");
    objUnderTest = null;
  }
  
  /**
   * Testing Typeactivation constructor variable initialization with null values.
   */
  @Test
  public void testTypeactivationConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Typeactivation(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  TECHPACK_NAME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest)  + ", " + TYPENAME.get(objUnderTest)  + ", " + TABLELEVEL.get(objUnderTest)  + ", " + STORAGETIME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + PARTITIONPLAN.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Typeactivation constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testTypeactivationConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");

    /* Asserting that variables are initialized */
    String actual =  TECHPACK_NAME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest)  + ", " + TYPENAME.get(objUnderTest)  + ", " + TABLELEVEL.get(objUnderTest)  + ", " + STORAGETIME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + PARTITIONPLAN.get(objUnderTest) ;
    String expected =  "testTECHPACK_NAME"  + ", testSTATUS"  + ", testTYPENAME"  + ", testTABLELEVEL"  + ", 1"  + ", testTYPE"  + ", testPARTITIONPLAN" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testTypeactivationConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Typeactivation(null ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Typeactivation constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testTypeactivationConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Typeactivation whereObject = new Typeactivation(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Typeactivation(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  TECHPACK_NAME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest)  + ", " + TYPENAME.get(objUnderTest)  + ", " + TABLELEVEL.get(objUnderTest)  + ", " + STORAGETIME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + PARTITIONPLAN.get(objUnderTest) ;
    String expected =  "testTECHPACK_NAME"  + ", testSTATUS"  + ", testTYPENAME"  + ", testTABLELEVEL"  + ", 1"  + ", testTYPE"  + ", testPARTITIONPLAN" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testTypeactivationConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Typeactivation whereObject = new Typeactivation(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Typeactivation(null, whereObject);
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
    assertEquals("Typeactivation", objUnderTest.getTableName());
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
    Typeactivation whereObject = new Typeactivation(rockFactory);

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
    Typeactivation whereObject = new Typeactivation(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Typeactivation");
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
    TECHPACK_NAME.set(objUnderTest, "changed");
    HashSet testSet = new HashSet();
    testSet.add("TECHPACK_NAME");
    objUnderTest.setModifiedColumns(testSet);
    objUnderTest.saveToDB();

    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Typeactivation");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Getting the TECHPACK_NAME column and see if it has changed */
    String queryResult = "";
    res = stmt.executeQuery("SELECT TECHPACK_NAME FROM Typeactivation");
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
    
    String expected = "<Typeactivation TECHPACK_NAME=\"'testTECHPACK_NAME'\" STATUS=\"'testSTATUS'\" TYPENAME=\"'testTYPENAME'\" TABLELEVEL=\"'testTABLELEVEL'\" STORAGETIME=\"1\" TYPE=\"'testTYPE'\" PARTITIONPLAN=\"'testPARTITIONPLAN'\" DiffStatus=\"\" />\n";
    assertEquals(expected, objUnderTest.toXML_tag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_startTag() throws Exception {
    
    String expected = "<Typeactivation TECHPACK_NAME=\"'testTECHPACK_NAME'\" STATUS=\"'testSTATUS'\" TYPENAME=\"'testTYPENAME'\" TABLELEVEL=\"'testTABLELEVEL'\" STORAGETIME=\"1\" TYPE=\"'testTYPE'\" PARTITIONPLAN=\"'testPARTITIONPLAN'\" DiffStatus=\"\" >\n";
    assertEquals(expected, objUnderTest.toXML_startTag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_endTag() throws Exception {
    
    assertEquals("</Typeactivation>\n", objUnderTest.toXML_endTag());
  }
  
  /**
   * Testing column data formatting into sql insert clause.
   */
  @Test
  public void testToSQLInsert() throws Exception {
    
    String expected = "insert into Typeactivation ( TECHPACK_NAME, STATUS, TYPENAME, TABLELEVEL, STORAGETIME, TYPE, PARTITIONPLAN ) values "
      + "( 'testTECHPACK_NAME', 'testSTATUS', 'testTYPENAME', 'testTABLELEVEL', 1, 'testTYPE', 'testPARTITIONPLAN' );\n";
    assertEquals(expected, objUnderTest.toSQLInsert());
  }
  
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTechpack_name() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTechpack_name(TypeactivationTest.testStringGenerator("anotherTECHPACK_NAME", 30));
    assertEquals(TypeactivationTest.testStringGenerator("anotherTECHPACK_NAME", 30), TECHPACK_NAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStatus() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStatus(TypeactivationTest.testStringGenerator("anotherSTATUS", 10));
    assertEquals(TypeactivationTest.testStringGenerator("anotherSTATUS", 10), STATUS.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTypename() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTypename(TypeactivationTest.testStringGenerator("anotherTYPENAME", 255));
    assertEquals(TypeactivationTest.testStringGenerator("anotherTYPENAME", 255), TYPENAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTablelevel() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTablelevel(TypeactivationTest.testStringGenerator("anotherTABLELEVEL", 50));
    assertEquals(TypeactivationTest.testStringGenerator("anotherTABLELEVEL", 50), TABLELEVEL.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetStoragetime() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setStoragetime(555L);
    assertEquals(555L, STORAGETIME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetType() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setType(TypeactivationTest.testStringGenerator("anotherTYPE", 12));
    assertEquals(TypeactivationTest.testStringGenerator("anotherTYPE", 12), TYPE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetPartitionplan() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setPartitionplan(TypeactivationTest.testStringGenerator("anotherPARTITIONPLAN", 128));
    assertEquals(TypeactivationTest.testStringGenerator("anotherPARTITIONPLAN", 128), PARTITIONPLAN.get(objUnderTest));
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
    String[] expectedKeys = { "TECHPACK_NAME","TYPENAME","TABLELEVEL"};

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
    objUnderTest = new Typeactivation(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  TECHPACK_NAME.get(objUnderTest)  + ", " + STATUS.get(objUnderTest)  + ", " + TYPENAME.get(objUnderTest)  + ", " + TABLELEVEL.get(objUnderTest)  + ", " + STORAGETIME.get(objUnderTest)  + ", " + TYPE.get(objUnderTest)  + ", " + PARTITIONPLAN.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", "  + ", "  + ", 0"  + ", "  + ", " ;
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
    Typeactivation compareObj = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");

    /* Testing first with null primary key value */
    TECHPACK_NAME.set(objUnderTest, null);
  	TYPENAME.set(objUnderTest, null);
  	TABLELEVEL.set(objUnderTest, null);
  	String actual = objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Testing with different key value */
    TECHPACK_NAME.set(objUnderTest,  "different");
  	TYPENAME.set(objUnderTest,  "different");
  	TABLELEVEL.set(objUnderTest,  "different");
  	actual += objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Finally test with same value and test assertion */
    TECHPACK_NAME.set(objUnderTest,  "testTECHPACK_NAME");
  	TYPENAME.set(objUnderTest,  "testTYPENAME");
  	TABLELEVEL.set(objUnderTest,  "testTABLELEVEL");
  	actual += objUnderTest.dbEquals(compareObj);
    assertEquals(false + ", " + false + ", " + true, actual);
  }
  
  /**
   * Testing comparing another Typeactivation with our current one. If the two
   * Typeactivations are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnTypeactivation() throws Exception {

    /* Creating another Typeactivation which will be compared to the tested one */
    Typeactivation comparedObj = new Typeactivation(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Typeactivation with our current one. If the two
   * Typeactivations are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentTypeactivation() throws Exception {

    /* Creating another Typeactivation which will be compared to the tested one */
    Typeactivation comparedObj = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");
    comparedObj.setTechpack_name( "DifferentTECHPACK_NAME");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Typeactivation with our current one. If the two
   * Typeactivations are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameTypeactivation() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Typeactivation comparedObj = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Typeactivation with our current one using null value.
   */
  @Test
  public void testEqualsWithNullTypeactivation() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Typeactivation comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Typeactivation was null \n");
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
    assertEquals(Typeactivation.class, actualObject.getClass());
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
    Typeactivation testAgg = new Typeactivation(rockFactory ,  "testTECHPACK_NAME",  "testTYPENAME",  "testTABLELEVEL");
    TECHPACK_NAME.set(objUnderTest, "changed");

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Techpack_name.
   */
  @Test
  public void testGetTechpack_nameColumnSize() throws Exception {
    
     assertEquals(30, objUnderTest.getTechpack_nameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Techpack_name.
  */
  @Test
  public void testGetTechpack_nameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTechpack_nameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Techpack_name.
  */
  @Test
  public void testGetTechpack_nameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTechpack_nameSQLType());    
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
   * Testing columnsize retrieving for Typename.
   */
  @Test
  public void testGetTypenameColumnSize() throws Exception {
    
     assertEquals(255, objUnderTest.getTypenameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Typename.
  */
  @Test
  public void testGetTypenameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTypenameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Typename.
  */
  @Test
  public void testGetTypenameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTypenameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Tablelevel.
   */
  @Test
  public void testGetTablelevelColumnSize() throws Exception {
    
     assertEquals(50, objUnderTest.getTablelevelColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Tablelevel.
  */
  @Test
  public void testGetTablelevelDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTablelevelDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Tablelevel.
  */
  @Test
  public void testGetTablelevelSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTablelevelSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Storagetime.
   */
  @Test
  public void testGetStoragetimeColumnSize() throws Exception {
    
     assertEquals(9, objUnderTest.getStoragetimeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Storagetime.
  */
  @Test
  public void testGetStoragetimeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getStoragetimeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Storagetime.
  */
  @Test
  public void testGetStoragetimeSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getStoragetimeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Type.
   */
  @Test
  public void testGetTypeColumnSize() throws Exception {
    
     assertEquals(12, objUnderTest.getTypeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Type.
  */
  @Test
  public void testGetTypeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTypeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Type.
  */
  @Test
  public void testGetTypeSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTypeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Partitionplan.
   */
  @Test
  public void testGetPartitionplanColumnSize() throws Exception {
    
     assertEquals(128, objUnderTest.getPartitionplanColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Partitionplan.
  */
  @Test
  public void testGetPartitionplanDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getPartitionplanDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Partitionplan.
  */
  @Test
  public void testGetPartitionplanSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getPartitionplanSQLType());    
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
   * Testing original Typeactivation object setting and retrieving.
   */
  @Test
  public void testSetAndGetOriginal() throws Exception {

    objUnderTest = new Typeactivation(rockFactory, false);
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
    Typeactivation changedOriginal = new Typeactivation(rockFactory, false);
    objUnderTest.setOriginal(changedOriginal);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object has been updated */
    modifiedColumns.add("updatedValue");
    objUnderTest.setModifiedColumns(modifiedColumns);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object is null initialized */
    objUnderTest = new Typeactivation(rockFactory, false);
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
    Typeactivation changedOriginal = new Typeactivation(rockFactory, false);
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