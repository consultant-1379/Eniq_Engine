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
 * Test class for Verificationobject. Changes to Verificationobject table are made via
 * this class.
 */
public class VerificationobjectTest {

  private static Verificationobject objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field VERSIONID;
  
  private static Field MEASTYPE;
  
  private static Field MEASLEVEL;
  
  private static Field OBJECTCLASS;
  
  private static Field OBJECTNAME;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Verificationobject.class.getDeclaredField("newItem");
		VERSIONID = Verificationobject.class.getDeclaredField("VERSIONID");
		MEASTYPE = Verificationobject.class.getDeclaredField("MEASTYPE");
		MEASLEVEL = Verificationobject.class.getDeclaredField("MEASLEVEL");
		OBJECTCLASS = Verificationobject.class.getDeclaredField("OBJECTCLASS");
		OBJECTNAME = Verificationobject.class.getDeclaredField("OBJECTNAME");
		newItem.setAccessible(true);
		VERSIONID.setAccessible(true);
		MEASTYPE.setAccessible(true);
		MEASLEVEL.setAccessible(true);
		OBJECTCLASS.setAccessible(true);
		OBJECTNAME.setAccessible(true);
	  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Verificationobject ( VERSIONID VARCHAR(31)  ,MEASTYPE VARCHAR(31) ,MEASLEVEL VARCHAR(31) ,OBJECTCLASS VARCHAR(31) ,OBJECTNAME VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Verificationobject");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
	  String className = Object.class.getName();

    stmt.executeUpdate("INSERT INTO Verificationobject VALUES( 'testVERSIONID'  ,'testMEASTYPE'  ,'testMEASLEVEL'  ,'testOBJECTCLASS'  ,'testOBJECTNAME' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Verificationobject");
    objUnderTest = null;
  }
  
  /**
   * Testing Verificationobject constructor variable initialization with null values.
   */
  @Test
  public void testVerificationobjectConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Verificationobject(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + MEASTYPE.get(objUnderTest)  + ", " + MEASLEVEL.get(objUnderTest)  + ", " + OBJECTCLASS.get(objUnderTest)  + ", " + OBJECTNAME.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Verificationobject constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testVerificationobjectConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");

    /* Asserting that variables are initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + MEASTYPE.get(objUnderTest)  + ", " + MEASLEVEL.get(objUnderTest)  + ", " + OBJECTCLASS.get(objUnderTest)  + ", " + OBJECTNAME.get(objUnderTest) ;
    String expected =  "testVERSIONID"  + ", testMEASTYPE"  + ", testMEASLEVEL"  + ", testOBJECTCLASS"  + ", testOBJECTNAME" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testVerificationobjectConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Verificationobject(null ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Verificationobject constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testVerificationobjectConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Verificationobject whereObject = new Verificationobject(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Verificationobject(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  VERSIONID.get(objUnderTest)  + ", " + MEASTYPE.get(objUnderTest)  + ", " + MEASLEVEL.get(objUnderTest)  + ", " + OBJECTCLASS.get(objUnderTest)  + ", " + OBJECTNAME.get(objUnderTest) ;
    String expected =  "testVERSIONID"  + ", testMEASTYPE"  + ", testMEASLEVEL"  + ", testOBJECTCLASS"  + ", testOBJECTNAME" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testVerificationobjectConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Verificationobject whereObject = new Verificationobject(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Verificationobject(null, whereObject);
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
    assertEquals("Verificationobject", objUnderTest.getTableName());
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
    Verificationobject whereObject = new Verificationobject(rockFactory);

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
    Verificationobject whereObject = new Verificationobject(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Verificationobject");
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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Verificationobject");
    while (res.next()) {
      rows = res.getInt(1);
    }

    /* Getting the VERSIONID column and see if it has changed */
    String queryResult = "";
    res = stmt.executeQuery("SELECT VERSIONID FROM Verificationobject");
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
    
    String expected = "<Verificationobject VERSIONID=\"'testVERSIONID'\" MEASTYPE=\"'testMEASTYPE'\" MEASLEVEL=\"'testMEASLEVEL'\" OBJECTCLASS=\"'testOBJECTCLASS'\" OBJECTNAME=\"'testOBJECTNAME'\" DiffStatus=\"\" />\n";
    assertEquals(expected, objUnderTest.toXML_tag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_startTag() throws Exception {
    
    String expected = "<Verificationobject VERSIONID=\"'testVERSIONID'\" MEASTYPE=\"'testMEASTYPE'\" MEASLEVEL=\"'testMEASLEVEL'\" OBJECTCLASS=\"'testOBJECTCLASS'\" OBJECTNAME=\"'testOBJECTNAME'\" DiffStatus=\"\" >\n";
    assertEquals(expected, objUnderTest.toXML_startTag());
  }
  
  /**
   * Testing column data formatting into xml.
   */
  @Test
  public void testToXML_endTag() throws Exception {
    
    assertEquals("</Verificationobject>\n", objUnderTest.toXML_endTag());
  }
  
  /**
   * Testing column data formatting into sql insert clause.
   */
  @Test
  public void testToSQLInsert() throws Exception {
    
    String expected = "insert into Verificationobject ( VERSIONID, MEASTYPE, MEASLEVEL, OBJECTCLASS, OBJECTNAME ) values "
      + "( 'testVERSIONID', 'testMEASTYPE', 'testMEASLEVEL', 'testOBJECTCLASS', 'testOBJECTNAME' );\n";
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
    objUnderTest.setVersionid(VerificationobjectTest.testStringGenerator("anotherVERSIONID", 128));
    assertEquals(VerificationobjectTest.testStringGenerator("anotherVERSIONID", 128), VERSIONID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetMeastype() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setMeastype(VerificationobjectTest.testStringGenerator("anotherMEASTYPE", 128));
    assertEquals(VerificationobjectTest.testStringGenerator("anotherMEASTYPE", 128), MEASTYPE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetMeaslevel() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setMeaslevel(VerificationobjectTest.testStringGenerator("anotherMEASLEVEL", 32));
    assertEquals(VerificationobjectTest.testStringGenerator("anotherMEASLEVEL", 32), MEASLEVEL.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetObjectclass() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setObjectclass(VerificationobjectTest.testStringGenerator("anotherOBJECTCLASS", 32));
    assertEquals(VerificationobjectTest.testStringGenerator("anotherOBJECTCLASS", 32), OBJECTCLASS.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetObjectname() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setObjectname(VerificationobjectTest.testStringGenerator("anotherOBJECTNAME", 32));
    assertEquals(VerificationobjectTest.testStringGenerator("anotherOBJECTNAME", 32), OBJECTNAME.get(objUnderTest));
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
    String[] expectedKeys = { "VERSIONID","MEASLEVEL","OBJECTCLASS","OBJECTNAME"};

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
    objUnderTest = new Verificationobject(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  VERSIONID.get(objUnderTest)  + ", " + MEASTYPE.get(objUnderTest)  + ", " + MEASLEVEL.get(objUnderTest)  + ", " + OBJECTCLASS.get(objUnderTest)  + ", " + OBJECTNAME.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", "  + ", "  + ", " ;
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
    Verificationobject compareObj = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");

    /* Testing first with null primary key value */
    VERSIONID.set(objUnderTest, null);
  	MEASLEVEL.set(objUnderTest, null);
  	OBJECTCLASS.set(objUnderTest, null);
  	OBJECTNAME.set(objUnderTest, null);
  	String actual = objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Testing with different key value */
    VERSIONID.set(objUnderTest,  "different");
  	MEASLEVEL.set(objUnderTest,  "different");
  	OBJECTCLASS.set(objUnderTest,  "different");
  	OBJECTNAME.set(objUnderTest,  "different");
  	actual += objUnderTest.dbEquals(compareObj) + ", ";
    
    /* Finally test with same value and test assertion */
    VERSIONID.set(objUnderTest,  "testVERSIONID");
  	MEASLEVEL.set(objUnderTest,  "testMEASLEVEL");
  	OBJECTCLASS.set(objUnderTest,  "testOBJECTCLASS");
  	OBJECTNAME.set(objUnderTest,  "testOBJECTNAME");
  	actual += objUnderTest.dbEquals(compareObj);
    assertEquals(false + ", " + false + ", " + true, actual);
  }
  
  /**
   * Testing comparing another Verificationobject with our current one. If the two
   * Verificationobjects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnVerificationobject() throws Exception {

    /* Creating another Verificationobject which will be compared to the tested one */
    Verificationobject comparedObj = new Verificationobject(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Verificationobject with our current one. If the two
   * Verificationobjects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentVerificationobject() throws Exception {

    /* Creating another Verificationobject which will be compared to the tested one */
    Verificationobject comparedObj = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");
    comparedObj.setVersionid( "DifferentVERSIONID");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Verificationobject with our current one. If the two
   * Verificationobjects are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameVerificationobject() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Verificationobject comparedObj = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Verificationobject with our current one using null value.
   */
  @Test
  public void testEqualsWithNullVerificationobject() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Verificationobject comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Verificationobject was null \n");
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
    assertEquals(Verificationobject.class, actualObject.getClass());
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
    Verificationobject testAgg = new Verificationobject(rockFactory ,  "testVERSIONID",  "testMEASLEVEL",  "testOBJECTCLASS",  "testOBJECTNAME");
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
   * Testing columnsize retrieving for Meastype.
   */
  @Test
  public void testGetMeastypeColumnSize() throws Exception {
    
     assertEquals(128, objUnderTest.getMeastypeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Meastype.
  */
  @Test
  public void testGetMeastypeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getMeastypeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Meastype.
  */
  @Test
  public void testGetMeastypeSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getMeastypeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Measlevel.
   */
  @Test
  public void testGetMeaslevelColumnSize() throws Exception {
    
     assertEquals(32, objUnderTest.getMeaslevelColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Measlevel.
  */
  @Test
  public void testGetMeaslevelDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getMeaslevelDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Measlevel.
  */
  @Test
  public void testGetMeaslevelSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getMeaslevelSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Objectclass.
   */
  @Test
  public void testGetObjectclassColumnSize() throws Exception {
    
     assertEquals(32, objUnderTest.getObjectclassColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Objectclass.
  */
  @Test
  public void testGetObjectclassDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getObjectclassDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Objectclass.
  */
  @Test
  public void testGetObjectclassSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getObjectclassSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Objectname.
   */
  @Test
  public void testGetObjectnameColumnSize() throws Exception {
    
     assertEquals(32, objUnderTest.getObjectnameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Objectname.
  */
  @Test
  public void testGetObjectnameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getObjectnameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Objectname.
  */
  @Test
  public void testGetObjectnameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getObjectnameSQLType());    
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
   * Testing original Verificationobject object setting and retrieving.
   */
  @Test
  public void testSetAndGetOriginal() throws Exception {

    objUnderTest = new Verificationobject(rockFactory, false);
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
    Verificationobject changedOriginal = new Verificationobject(rockFactory, false);
    objUnderTest.setOriginal(changedOriginal);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object has been updated */
    modifiedColumns.add("updatedValue");
    objUnderTest.setModifiedColumns(modifiedColumns);
    actual += objUnderTest.isUpdated() + ", ";

    /* Tested object is null initialized */
    objUnderTest = new Verificationobject(rockFactory, false);
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
    Verificationobject changedOriginal = new Verificationobject(rockFactory, false);
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