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
 * Test class for Meta_execution_slot. Changes to Meta_execution_slot table are made via
 * this class.
 */
public class Meta_execution_slotTest {

  private static Meta_execution_slot objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field PROFILE_ID;
  
  private static Field SLOT_NAME;
  
  private static Field SLOT_ID;
  
  private static Field ACCEPTED_SET_TYPES;
  
  private static Field timeStampName;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Meta_execution_slot.class.getDeclaredField("newItem");
		PROFILE_ID = Meta_execution_slot.class.getDeclaredField("PROFILE_ID");
		SLOT_NAME = Meta_execution_slot.class.getDeclaredField("SLOT_NAME");
		SLOT_ID = Meta_execution_slot.class.getDeclaredField("SLOT_ID");
		ACCEPTED_SET_TYPES = Meta_execution_slot.class.getDeclaredField("ACCEPTED_SET_TYPES");
		timeStampName = Meta_execution_slot.class.getDeclaredField("timeStampName");
	newItem.setAccessible(true);
		PROFILE_ID.setAccessible(true);
		SLOT_NAME.setAccessible(true);
		SLOT_ID.setAccessible(true);
		ACCEPTED_SET_TYPES.setAccessible(true);
		timeStampName.setAccessible(true);
  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_execution_slot ( PROFILE_ID VARCHAR(31)  ,SLOT_NAME VARCHAR(31) ,SLOT_ID VARCHAR(31) ,ACCEPTED_SET_TYPES VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_execution_slot");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Meta_execution_slot VALUES( 'testPROFILE_ID'  ,'testSLOT_NAME'  ,'testSLOT_ID'  ,'testACCEPTED_SET_TYPES' )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Meta_execution_slot(rockFactory ,  "testSLOT_ID");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_execution_slot");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_execution_slot constructor variable initialization with null values.
   */
  @Test
  public void testMeta_execution_slotConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_execution_slot(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  PROFILE_ID.get(objUnderTest)  + ", " + SLOT_NAME.get(objUnderTest)  + ", " + SLOT_ID.get(objUnderTest)  + ", " + ACCEPTED_SET_TYPES.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Meta_execution_slot constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_execution_slotConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_execution_slot(rockFactory ,  "testSLOT_ID");

    /* Asserting that variables are initialized */
    String actual =  PROFILE_ID.get(objUnderTest)  + ", " + SLOT_NAME.get(objUnderTest)  + ", " + SLOT_ID.get(objUnderTest)  + ", " + ACCEPTED_SET_TYPES.get(objUnderTest) ;
    String expected =  "testPROFILE_ID"  + ", testSLOT_NAME"  + ", testSLOT_ID"  + ", testACCEPTED_SET_TYPES" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_execution_slotConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Meta_execution_slot(null ,  "testSLOT_ID");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_execution_slot constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_execution_slotConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_execution_slot whereObject = new Meta_execution_slot(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Meta_execution_slot(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  PROFILE_ID.get(objUnderTest)  + ", " + SLOT_NAME.get(objUnderTest)  + ", " + SLOT_ID.get(objUnderTest)  + ", " + ACCEPTED_SET_TYPES.get(objUnderTest) ;
    String expected =  "testPROFILE_ID"  + ", testSLOT_NAME"  + ", testSLOT_ID"  + ", testACCEPTED_SET_TYPES" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_execution_slotConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_execution_slot whereObject = new Meta_execution_slot(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_execution_slot(null, whereObject);
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
    assertEquals("Meta_execution_slot", objUnderTest.getTableName());
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
    Meta_execution_slot whereObject = new Meta_execution_slot(rockFactory);

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
    Meta_execution_slot whereObject = new Meta_execution_slot(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_execution_slot");
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
  public void testSetAndGetProfile_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setProfile_id(Meta_execution_slotTest.testStringGenerator("anotherPROFILE_ID", 38));
    assertEquals(Meta_execution_slotTest.testStringGenerator("anotherPROFILE_ID", 38), PROFILE_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetSlot_name() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setSlot_name(Meta_execution_slotTest.testStringGenerator("anotherSLOT_NAME", 15));
    assertEquals(Meta_execution_slotTest.testStringGenerator("anotherSLOT_NAME", 15), SLOT_NAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetSlot_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setSlot_id(Meta_execution_slotTest.testStringGenerator("anotherSLOT_ID", 38));
    assertEquals(Meta_execution_slotTest.testStringGenerator("anotherSLOT_ID", 38), SLOT_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetAccepted_set_types() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setAccepted_set_types(Meta_execution_slotTest.testStringGenerator("anotherACCEPTED_SET_TYPES", 2000));
    assertEquals(Meta_execution_slotTest.testStringGenerator("anotherACCEPTED_SET_TYPES", 2000), ACCEPTED_SET_TYPES.get(objUnderTest));
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
    String[] expectedKeys = { "SLOT_ID"};

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
    objUnderTest = new Meta_execution_slot(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  PROFILE_ID.get(objUnderTest)  + ", " + SLOT_NAME.get(objUnderTest)  + ", " + SLOT_ID.get(objUnderTest)  + ", " + ACCEPTED_SET_TYPES.get(objUnderTest) ;
    String expected =  ""  + ", "  + ", "  + ", " ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing comparing another Meta_execution_slot with our current one. If the two
   * Meta_execution_slots are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnMeta_execution_slot() throws Exception {

    /* Creating another Meta_execution_slot which will be compared to the tested one */
    Meta_execution_slot comparedObj = new Meta_execution_slot(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_execution_slot with our current one. If the two
   * Meta_execution_slots are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentMeta_execution_slot() throws Exception {

    /* Creating another Meta_execution_slot which will be compared to the tested one */
    Meta_execution_slot comparedObj = new Meta_execution_slot(rockFactory ,  "testSLOT_ID");
    comparedObj.setProfile_id( "DifferentPROFILE_ID");

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_execution_slot with our current one. If the two
   * Meta_execution_slots are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameMeta_execution_slot() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_execution_slot comparedObj = new Meta_execution_slot(rockFactory ,  "testSLOT_ID");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_execution_slot with our current one using null value.
   */
  @Test
  public void testEqualsWithNullMeta_execution_slot() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_execution_slot comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Meta_execution_slot was null \n");
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
    assertEquals(Meta_execution_slot.class, actualObject.getClass());
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
    Meta_execution_slot testAgg = new Meta_execution_slot(rockFactory ,  "testSLOT_ID");
    PROFILE_ID.set(objUnderTest, "changed");

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Profile_id.
   */
  @Test
  public void testGetProfile_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getProfile_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Profile_id.
  */
  @Test
  public void testGetProfile_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getProfile_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Profile_id.
  */
  @Test
  public void testGetProfile_idSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getProfile_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Slot_name.
   */
  @Test
  public void testGetSlot_nameColumnSize() throws Exception {
    
     assertEquals(15, objUnderTest.getSlot_nameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Slot_name.
  */
  @Test
  public void testGetSlot_nameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getSlot_nameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Slot_name.
  */
  @Test
  public void testGetSlot_nameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getSlot_nameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Slot_id.
   */
  @Test
  public void testGetSlot_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getSlot_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Slot_id.
  */
  @Test
  public void testGetSlot_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getSlot_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Slot_id.
  */
  @Test
  public void testGetSlot_idSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getSlot_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Accepted_set_types.
   */
  @Test
  public void testGetAccepted_set_typesColumnSize() throws Exception {
    
     assertEquals(2000, objUnderTest.getAccepted_set_typesColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Accepted_set_types.
  */
  @Test
  public void testGetAccepted_set_typesDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getAccepted_set_typesDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Accepted_set_types.
  */
  @Test
  public void testGetAccepted_set_typesSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getAccepted_set_typesSQLType());    
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