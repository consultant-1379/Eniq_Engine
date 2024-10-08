package com.distocraft.dc5000.etl.rock;

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
 * Test class for Meta_transformation_rules. Changes to Meta_transformation_rules table are made via
 * this class.
 */
public class Meta_transformation_rulesTest {

  private static Meta_transformation_rules objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field TRANSFORMATION_ID;
  
  private static Field TRANSFORMATION_NAME;
  
  private static Field CODE;
  
  private static Field DESCRIPTION;
  
  private static Field VERSION_NUMBER;
  
  private static Field timeStampName;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Meta_transformation_rules.class.getDeclaredField("newItem");
		TRANSFORMATION_ID = Meta_transformation_rules.class.getDeclaredField("TRANSFORMATION_ID");
		TRANSFORMATION_NAME = Meta_transformation_rules.class.getDeclaredField("TRANSFORMATION_NAME");
		CODE = Meta_transformation_rules.class.getDeclaredField("CODE");
		DESCRIPTION = Meta_transformation_rules.class.getDeclaredField("DESCRIPTION");
		VERSION_NUMBER = Meta_transformation_rules.class.getDeclaredField("VERSION_NUMBER");
		timeStampName = Meta_transformation_rules.class.getDeclaredField("timeStampName");
	newItem.setAccessible(true);
		TRANSFORMATION_ID.setAccessible(true);
		TRANSFORMATION_NAME.setAccessible(true);
		CODE.setAccessible(true);
		DESCRIPTION.setAccessible(true);
		VERSION_NUMBER.setAccessible(true);
		timeStampName.setAccessible(true);
  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_transformation_rules ( TRANSFORMATION_ID BIGINT  ,TRANSFORMATION_NAME VARCHAR(31) ,CODE VARCHAR(31) ,DESCRIPTION VARCHAR(31) ,VERSION_NUMBER VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_transformation_rules");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Meta_transformation_rules VALUES( 1  ,'testTRANSFORMATION_NAME'  ,'testCODE'  ,'testDESCRIPTION'  ,'testVERSION_NUMBER' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Meta_transformation_rules(rockFactory ,  1L ,  "testVERSION_NUMBER");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_transformation_rules");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_transformation_rules constructor variable initialization with null values.
   */
  @Test
  public void testMeta_transformation_rulesConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_transformation_rules(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  TRANSFORMATION_ID.get(objUnderTest)  + ", " + TRANSFORMATION_NAME.get(objUnderTest)  + ", " + CODE.get(objUnderTest)  + ", " + DESCRIPTION.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Meta_transformation_rules constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_transformation_rulesConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_transformation_rules(rockFactory ,  1L ,  "testVERSION_NUMBER");

    /* Asserting that variables are initialized */
    String actual =  TRANSFORMATION_ID.get(objUnderTest)  + ", " + TRANSFORMATION_NAME.get(objUnderTest)  + ", " + CODE.get(objUnderTest)  + ", " + DESCRIPTION.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest) ;
    String expected =  "1"  + ", testTRANSFORMATION_NAME"  + ", testCODE"  + ", testDESCRIPTION"  + ", testVERSION_NUMBER" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_transformation_rulesConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Meta_transformation_rules(null ,  1L ,  "testVERSION_NUMBER");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_transformation_rules constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_transformation_rulesConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_transformation_rules whereObject = new Meta_transformation_rules(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Meta_transformation_rules(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  TRANSFORMATION_ID.get(objUnderTest)  + ", " + TRANSFORMATION_NAME.get(objUnderTest)  + ", " + CODE.get(objUnderTest)  + ", " + DESCRIPTION.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest) ;
    String expected =  "1"  + ", testTRANSFORMATION_NAME"  + ", testCODE"  + ", testDESCRIPTION"  + ", testVERSION_NUMBER" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_transformation_rulesConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_transformation_rules whereObject = new Meta_transformation_rules(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_transformation_rules(null, whereObject);
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
    assertEquals("Meta_transformation_rules", objUnderTest.getTableName());
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
    Meta_transformation_rules whereObject = new Meta_transformation_rules(rockFactory);

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
    Meta_transformation_rules whereObject = new Meta_transformation_rules(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_transformation_rules");
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
  public void testSetAndGetTransformation_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTransformation_id(555L);
    assertEquals(555L, TRANSFORMATION_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTransformation_name() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTransformation_name(Meta_transformation_rulesTest.testStringGenerator("anotherTRANSFORMATION_NAME", 10));
    assertEquals(Meta_transformation_rulesTest.testStringGenerator("anotherTRANSFORMATION_NAME", 10), TRANSFORMATION_NAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetCode() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setCode(Meta_transformation_rulesTest.testStringGenerator("anotherCODE", 2000));
    assertEquals(Meta_transformation_rulesTest.testStringGenerator("anotherCODE", 2000), CODE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetDescription() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setDescription(Meta_transformation_rulesTest.testStringGenerator("anotherDESCRIPTION", 32000));
    assertEquals(Meta_transformation_rulesTest.testStringGenerator("anotherDESCRIPTION", 32000), DESCRIPTION.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetVersion_number() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setVersion_number(Meta_transformation_rulesTest.testStringGenerator("anotherVERSION_NUMBER", 32));
    assertEquals(Meta_transformation_rulesTest.testStringGenerator("anotherVERSION_NUMBER", 32), VERSION_NUMBER.get(objUnderTest));
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
    String[] expectedKeys = { "TRANSFORMATION_ID","VERSION_NUMBER"};

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
    objUnderTest = new Meta_transformation_rules(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  TRANSFORMATION_ID.get(objUnderTest)  + ", " + TRANSFORMATION_NAME.get(objUnderTest)  + ", " + CODE.get(objUnderTest)  + ", " + DESCRIPTION.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest) ;
    String expected =  "0"  + ", "  + ", "  + ", "  + ", " ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing comparing another Meta_transformation_rules with our current one. If the two
   * Meta_transformation_ruless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnMeta_transformation_rules() throws Exception {

    /* Creating another Meta_transformation_rules which will be compared to the tested one */
    Meta_transformation_rules comparedObj = new Meta_transformation_rules(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_transformation_rules with our current one. If the two
   * Meta_transformation_ruless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentMeta_transformation_rules() throws Exception {

    /* Creating another Meta_transformation_rules which will be compared to the tested one */
    Meta_transformation_rules comparedObj = new Meta_transformation_rules(rockFactory ,  1L ,  "testVERSION_NUMBER");
    comparedObj.setTransformation_id( 7L );

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_transformation_rules with our current one. If the two
   * Meta_transformation_ruless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameMeta_transformation_rules() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_transformation_rules comparedObj = new Meta_transformation_rules(rockFactory ,  1L ,  "testVERSION_NUMBER");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_transformation_rules with our current one using null value.
   */
  @Test
  public void testEqualsWithNullMeta_transformation_rules() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_transformation_rules comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Meta_transformation_rules was null \n");
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
    assertEquals(Meta_transformation_rules.class, actualObject.getClass());
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
    Meta_transformation_rules testAgg = new Meta_transformation_rules(rockFactory ,  1L ,  "testVERSION_NUMBER");
    TRANSFORMATION_ID.set(objUnderTest, 7L);

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Transformation_id.
   */
  @Test
  public void testGetTransformation_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getTransformation_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Transformation_id.
  */
  @Test
  public void testGetTransformation_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTransformation_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Transformation_id.
  */
  @Test
  public void testGetTransformation_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getTransformation_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Transformation_name.
   */
  @Test
  public void testGetTransformation_nameColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getTransformation_nameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Transformation_name.
  */
  @Test
  public void testGetTransformation_nameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTransformation_nameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Transformation_name.
  */
  @Test
  public void testGetTransformation_nameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getTransformation_nameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Code.
   */
  @Test
  public void testGetCodeColumnSize() throws Exception {
    
     assertEquals(2000, objUnderTest.getCodeColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Code.
  */
  @Test
  public void testGetCodeDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getCodeDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Code.
  */
  @Test
  public void testGetCodeSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getCodeSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Description.
   */
  @Test
  public void testGetDescriptionColumnSize() throws Exception {
    
     assertEquals(32000, objUnderTest.getDescriptionColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Description.
  */
  @Test
  public void testGetDescriptionDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getDescriptionDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Description.
  */
  @Test
  public void testGetDescriptionSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getDescriptionSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Version_number.
   */
  @Test
  public void testGetVersion_numberColumnSize() throws Exception {
    
     assertEquals(32, objUnderTest.getVersion_numberColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Version_number.
  */
  @Test
  public void testGetVersion_numberDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getVersion_numberDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Version_number.
  */
  @Test
  public void testGetVersion_numberSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getVersion_numberSQLType());    
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