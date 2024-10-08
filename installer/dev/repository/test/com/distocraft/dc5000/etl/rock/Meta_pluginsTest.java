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
 * Test class for Meta_plugins. Changes to Meta_plugins table are made via
 * this class.
 */
public class Meta_pluginsTest {

  private static Meta_plugins objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field PLUGIN_ID;
  
  private static Field PLUGIN_NAME;
  
  private static Field CONSTRUCTOR_PARAMETER;
  
  private static Field IS_SOURCE;
  
  private static Field COLLECTION_SET_ID;
  
  private static Field COLLECTION_ID;
  
  private static Field COMMIT_AFTER_N_ROWS;
  
  private static Field VERSION_NUMBER;
  
  private static Field TRANSFER_ACTION_ID;
  
  private static Field timeStampName;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Meta_plugins.class.getDeclaredField("newItem");
		PLUGIN_ID = Meta_plugins.class.getDeclaredField("PLUGIN_ID");
		PLUGIN_NAME = Meta_plugins.class.getDeclaredField("PLUGIN_NAME");
		CONSTRUCTOR_PARAMETER = Meta_plugins.class.getDeclaredField("CONSTRUCTOR_PARAMETER");
		IS_SOURCE = Meta_plugins.class.getDeclaredField("IS_SOURCE");
		COLLECTION_SET_ID = Meta_plugins.class.getDeclaredField("COLLECTION_SET_ID");
		COLLECTION_ID = Meta_plugins.class.getDeclaredField("COLLECTION_ID");
		COMMIT_AFTER_N_ROWS = Meta_plugins.class.getDeclaredField("COMMIT_AFTER_N_ROWS");
		VERSION_NUMBER = Meta_plugins.class.getDeclaredField("VERSION_NUMBER");
		TRANSFER_ACTION_ID = Meta_plugins.class.getDeclaredField("TRANSFER_ACTION_ID");
		timeStampName = Meta_plugins.class.getDeclaredField("timeStampName");
	newItem.setAccessible(true);
		PLUGIN_ID.setAccessible(true);
		PLUGIN_NAME.setAccessible(true);
		CONSTRUCTOR_PARAMETER.setAccessible(true);
		IS_SOURCE.setAccessible(true);
		COLLECTION_SET_ID.setAccessible(true);
		COLLECTION_ID.setAccessible(true);
		COMMIT_AFTER_N_ROWS.setAccessible(true);
		VERSION_NUMBER.setAccessible(true);
		TRANSFER_ACTION_ID.setAccessible(true);
		timeStampName.setAccessible(true);
  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_plugins ( PLUGIN_ID BIGINT  ,PLUGIN_NAME VARCHAR(31) ,CONSTRUCTOR_PARAMETER VARCHAR(31) ,IS_SOURCE VARCHAR(31) ,COLLECTION_SET_ID BIGINT  ,COLLECTION_ID BIGINT  ,COMMIT_AFTER_N_ROWS BIGINT  ,VERSION_NUMBER VARCHAR(31) ,TRANSFER_ACTION_ID BIGINT )");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_plugins");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Meta_plugins VALUES( 1  ,'testPLUGIN_NAME'  ,'testCONSTRUCTOR_PARAMETER'  ,'testIS_SOURCE'  ,1  ,1  ,1  ,'testVERSION_NUMBER'  ,1 )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Meta_plugins(rockFactory ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_plugins");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_plugins constructor variable initialization with null values.
   */
  @Test
  public void testMeta_pluginsConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_plugins(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  PLUGIN_ID.get(objUnderTest)  + ", " + PLUGIN_NAME.get(objUnderTest)  + ", " + CONSTRUCTOR_PARAMETER.get(objUnderTest)  + ", " + IS_SOURCE.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + COMMIT_AFTER_N_ROWS.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Meta_plugins constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_pluginsConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_plugins(rockFactory ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );

    /* Asserting that variables are initialized */
    String actual =  PLUGIN_ID.get(objUnderTest)  + ", " + PLUGIN_NAME.get(objUnderTest)  + ", " + CONSTRUCTOR_PARAMETER.get(objUnderTest)  + ", " + IS_SOURCE.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + COMMIT_AFTER_N_ROWS.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest) ;
    String expected =  "1"  + ", testPLUGIN_NAME"  + ", testCONSTRUCTOR_PARAMETER"  + ", testIS_SOURCE"  + ", 1"  + ", 1"  + ", 1"  + ", testVERSION_NUMBER"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_pluginsConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Meta_plugins(null ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_plugins constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_pluginsConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_plugins whereObject = new Meta_plugins(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Meta_plugins(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  PLUGIN_ID.get(objUnderTest)  + ", " + PLUGIN_NAME.get(objUnderTest)  + ", " + CONSTRUCTOR_PARAMETER.get(objUnderTest)  + ", " + IS_SOURCE.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + COMMIT_AFTER_N_ROWS.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest) ;
    String expected =  "1"  + ", testPLUGIN_NAME"  + ", testCONSTRUCTOR_PARAMETER"  + ", testIS_SOURCE"  + ", 1"  + ", 1"  + ", 1"  + ", testVERSION_NUMBER"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_pluginsConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_plugins whereObject = new Meta_plugins(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_plugins(null, whereObject);
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
    assertEquals("Meta_plugins", objUnderTest.getTableName());
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
    Meta_plugins whereObject = new Meta_plugins(rockFactory);

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
    Meta_plugins whereObject = new Meta_plugins(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_plugins");
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
  public void testSetAndGetPlugin_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setPlugin_id(555L);
    assertEquals(555L, PLUGIN_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetPlugin_name() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setPlugin_name(Meta_pluginsTest.testStringGenerator("anotherPLUGIN_NAME", 30));
    assertEquals(Meta_pluginsTest.testStringGenerator("anotherPLUGIN_NAME", 30), PLUGIN_NAME.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetConstructor_parameter() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setConstructor_parameter(Meta_pluginsTest.testStringGenerator("anotherCONSTRUCTOR_PARAMETER", 200));
    assertEquals(Meta_pluginsTest.testStringGenerator("anotherCONSTRUCTOR_PARAMETER", 200), CONSTRUCTOR_PARAMETER.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetIs_source() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setIs_source(Meta_pluginsTest.testStringGenerator("anotherIS_SOURCE", 1));
    assertEquals(Meta_pluginsTest.testStringGenerator("anotherIS_SOURCE", 1), IS_SOURCE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetCollection_set_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setCollection_set_id(555L);
    assertEquals(555L, COLLECTION_SET_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetCollection_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setCollection_id(555L);
    assertEquals(555L, COLLECTION_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetCommit_after_n_rows() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setCommit_after_n_rows(555L);
    assertEquals(555L, COMMIT_AFTER_N_ROWS.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetVersion_number() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setVersion_number(Meta_pluginsTest.testStringGenerator("anotherVERSION_NUMBER", 32));
    assertEquals(Meta_pluginsTest.testStringGenerator("anotherVERSION_NUMBER", 32), VERSION_NUMBER.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTransfer_action_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTransfer_action_id(555L);
    assertEquals(555L, TRANSFER_ACTION_ID.get(objUnderTest));
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
    String[] expectedKeys = { "PLUGIN_ID","COLLECTION_SET_ID","COLLECTION_ID","VERSION_NUMBER","TRANSFER_ACTION_ID"};

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
    objUnderTest = new Meta_plugins(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  PLUGIN_ID.get(objUnderTest)  + ", " + PLUGIN_NAME.get(objUnderTest)  + ", " + CONSTRUCTOR_PARAMETER.get(objUnderTest)  + ", " + IS_SOURCE.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + COMMIT_AFTER_N_ROWS.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest) ;
    String expected =  "0"  + ", "  + ", "  + ", "  + ", 0"  + ", 0"  + ", 0"  + ", "  + ", 0" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing comparing another Meta_plugins with our current one. If the two
   * Meta_pluginss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnMeta_plugins() throws Exception {

    /* Creating another Meta_plugins which will be compared to the tested one */
    Meta_plugins comparedObj = new Meta_plugins(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_plugins with our current one. If the two
   * Meta_pluginss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentMeta_plugins() throws Exception {

    /* Creating another Meta_plugins which will be compared to the tested one */
    Meta_plugins comparedObj = new Meta_plugins(rockFactory ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );
    comparedObj.setPlugin_id( 7L );

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_plugins with our current one. If the two
   * Meta_pluginss are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameMeta_plugins() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_plugins comparedObj = new Meta_plugins(rockFactory ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_plugins with our current one using null value.
   */
  @Test
  public void testEqualsWithNullMeta_plugins() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_plugins comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Meta_plugins was null \n");
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
    assertEquals(Meta_plugins.class, actualObject.getClass());
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
    Meta_plugins testAgg = new Meta_plugins(rockFactory ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER",  1L );
    PLUGIN_ID.set(objUnderTest, 7L);

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Plugin_id.
   */
  @Test
  public void testGetPlugin_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getPlugin_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Plugin_id.
  */
  @Test
  public void testGetPlugin_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getPlugin_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Plugin_id.
  */
  @Test
  public void testGetPlugin_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getPlugin_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Plugin_name.
   */
  @Test
  public void testGetPlugin_nameColumnSize() throws Exception {
    
     assertEquals(30, objUnderTest.getPlugin_nameColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Plugin_name.
  */
  @Test
  public void testGetPlugin_nameDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getPlugin_nameDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Plugin_name.
  */
  @Test
  public void testGetPlugin_nameSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getPlugin_nameSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Constructor_parameter.
   */
  @Test
  public void testGetConstructor_parameterColumnSize() throws Exception {
    
     assertEquals(200, objUnderTest.getConstructor_parameterColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Constructor_parameter.
  */
  @Test
  public void testGetConstructor_parameterDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getConstructor_parameterDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Constructor_parameter.
  */
  @Test
  public void testGetConstructor_parameterSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getConstructor_parameterSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Is_source.
   */
  @Test
  public void testGetIs_sourceColumnSize() throws Exception {
    
     assertEquals(1, objUnderTest.getIs_sourceColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Is_source.
  */
  @Test
  public void testGetIs_sourceDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getIs_sourceDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Is_source.
  */
  @Test
  public void testGetIs_sourceSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getIs_sourceSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Collection_set_id.
   */
  @Test
  public void testGetCollection_set_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getCollection_set_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Collection_set_id.
  */
  @Test
  public void testGetCollection_set_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getCollection_set_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Collection_set_id.
  */
  @Test
  public void testGetCollection_set_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getCollection_set_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Collection_id.
   */
  @Test
  public void testGetCollection_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getCollection_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Collection_id.
  */
  @Test
  public void testGetCollection_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getCollection_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Collection_id.
  */
  @Test
  public void testGetCollection_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getCollection_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Commit_after_n_rows.
   */
  @Test
  public void testGetCommit_after_n_rowsColumnSize() throws Exception {
    
     assertEquals(10, objUnderTest.getCommit_after_n_rowsColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Commit_after_n_rows.
  */
  @Test
  public void testGetCommit_after_n_rowsDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getCommit_after_n_rowsDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Commit_after_n_rows.
  */
  @Test
  public void testGetCommit_after_n_rowsSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getCommit_after_n_rowsSQLType());    
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
   * Testing columnsize retrieving for Transfer_action_id.
   */
  @Test
  public void testGetTransfer_action_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getTransfer_action_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Transfer_action_id.
  */
  @Test
  public void testGetTransfer_action_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTransfer_action_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Transfer_action_id.
  */
  @Test
  public void testGetTransfer_action_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getTransfer_action_idSQLType());    
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