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
 * Test class for Meta_source_tables. Changes to Meta_source_tables table are made via
 * this class.
 */
public class Meta_source_tablesTest {

  private static Meta_source_tables objUnderTest;

  private static RockFactory rockFactory;

  private static Connection con = null;

  private static Statement stmt;
  
  private static Field newItem;
  
  private static Field LAST_TRANSFER_DATE;
  
  private static Field TRANSFER_ACTION_ID;
  
  private static Field TABLE_ID;
  
  private static Field USE_TR_DATE_IN_WHERE_FLAG;
  
  private static Field COLLECTION_SET_ID;
  
  private static Field COLLECTION_ID;
  
  private static Field CONNECTION_ID;
  
  private static Field DISTINCT_FLAG;
  
  private static Field AS_SELECT_OPTIONS;
  
  private static Field AS_SELECT_TABLESPACE;
  
  private static Field VERSION_NUMBER;
  
  private static Field TIMESTAMP_COLUMN_ID;
  
  private static Field timeStampName;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  
    /* Reflecting the private fields */
    newItem = Meta_source_tables.class.getDeclaredField("newItem");
		LAST_TRANSFER_DATE = Meta_source_tables.class.getDeclaredField("LAST_TRANSFER_DATE");
		TRANSFER_ACTION_ID = Meta_source_tables.class.getDeclaredField("TRANSFER_ACTION_ID");
		TABLE_ID = Meta_source_tables.class.getDeclaredField("TABLE_ID");
		USE_TR_DATE_IN_WHERE_FLAG = Meta_source_tables.class.getDeclaredField("USE_TR_DATE_IN_WHERE_FLAG");
		COLLECTION_SET_ID = Meta_source_tables.class.getDeclaredField("COLLECTION_SET_ID");
		COLLECTION_ID = Meta_source_tables.class.getDeclaredField("COLLECTION_ID");
		CONNECTION_ID = Meta_source_tables.class.getDeclaredField("CONNECTION_ID");
		DISTINCT_FLAG = Meta_source_tables.class.getDeclaredField("DISTINCT_FLAG");
		AS_SELECT_OPTIONS = Meta_source_tables.class.getDeclaredField("AS_SELECT_OPTIONS");
		AS_SELECT_TABLESPACE = Meta_source_tables.class.getDeclaredField("AS_SELECT_TABLESPACE");
		VERSION_NUMBER = Meta_source_tables.class.getDeclaredField("VERSION_NUMBER");
		TIMESTAMP_COLUMN_ID = Meta_source_tables.class.getDeclaredField("TIMESTAMP_COLUMN_ID");
		timeStampName = Meta_source_tables.class.getDeclaredField("timeStampName");
	newItem.setAccessible(true);
		LAST_TRANSFER_DATE.setAccessible(true);
		TRANSFER_ACTION_ID.setAccessible(true);
		TABLE_ID.setAccessible(true);
		USE_TR_DATE_IN_WHERE_FLAG.setAccessible(true);
		COLLECTION_SET_ID.setAccessible(true);
		COLLECTION_ID.setAccessible(true);
		CONNECTION_ID.setAccessible(true);
		DISTINCT_FLAG.setAccessible(true);
		AS_SELECT_OPTIONS.setAccessible(true);
		AS_SELECT_TABLESPACE.setAccessible(true);
		VERSION_NUMBER.setAccessible(true);
		TIMESTAMP_COLUMN_ID.setAccessible(true);
		timeStampName.setAccessible(true);
  
    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_source_tables ( LAST_TRANSFER_DATE TIMESTAMP  ,TRANSFER_ACTION_ID BIGINT  ,TABLE_ID BIGINT  ,USE_TR_DATE_IN_WHERE_FLAG VARCHAR(31) ,COLLECTION_SET_ID BIGINT  ,COLLECTION_ID BIGINT  ,CONNECTION_ID BIGINT  ,DISTINCT_FLAG VARCHAR(31) ,AS_SELECT_OPTIONS VARCHAR(31) ,AS_SELECT_TABLESPACE VARCHAR(31) ,VERSION_NUMBER VARCHAR(31) ,TIMESTAMP_COLUMN_ID BIGINT )");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Meta_source_tables");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
    stmt.executeUpdate("INSERT INTO Meta_source_tables VALUES( '2000-01-01 00:00:00.0'  ,1  ,1  ,'testUSE_TR_DATE_IN_WHERE_FLAG'  ,1  ,1  ,1  ,'testDISTINCT_FLAG'  ,'testAS_SELECT_OPTIONS'  ,'testAS_SELECT_TABLESPACE'  ,'testVERSION_NUMBER'  ,1 )");

    /* Initializing tested object where primary key is defined */
    objUnderTest = new Meta_source_tables(rockFactory ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Meta_source_tables");
    objUnderTest = null;
  }
  
  /**
   * Testing Meta_source_tables constructor variable initialization with null values.
   */
  @Test
  public void testMeta_source_tablesConstructorWithNullValues() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_source_tables(rockFactory, true);

    /* Asserting that variables are null initialized */
    String actual =  LAST_TRANSFER_DATE.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest)  + ", " + TABLE_ID.get(objUnderTest)  + ", " + USE_TR_DATE_IN_WHERE_FLAG.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + CONNECTION_ID.get(objUnderTest)  + ", " + DISTINCT_FLAG.get(objUnderTest)  + ", " + AS_SELECT_OPTIONS.get(objUnderTest)  + ", " + AS_SELECT_TABLESPACE.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TIMESTAMP_COLUMN_ID.get(objUnderTest) ;
    String expected =  null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null  + ", " + null ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing Meta_source_tables constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_source_tablesConstructorWithPrimaryKeyDefined() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new Meta_source_tables(rockFactory ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");

    /* Asserting that variables are initialized */
    String actual =  LAST_TRANSFER_DATE.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest)  + ", " + TABLE_ID.get(objUnderTest)  + ", " + USE_TR_DATE_IN_WHERE_FLAG.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + CONNECTION_ID.get(objUnderTest)  + ", " + DISTINCT_FLAG.get(objUnderTest)  + ", " + AS_SELECT_OPTIONS.get(objUnderTest)  + ", " + AS_SELECT_TABLESPACE.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TIMESTAMP_COLUMN_ID.get(objUnderTest) ;
    String expected =  "2000-01-01 00:00:00.0"  + ", 1"  + ", 1"  + ", testUSE_TR_DATE_IN_WHERE_FLAG"  + ", 1"  + ", 1"  + ", 1"  + ", testDISTINCT_FLAG"  + ", testAS_SELECT_OPTIONS"  + ", testAS_SELECT_TABLESPACE"  + ", testVERSION_NUMBER"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_source_tablesConstructorWithPrimaryKeyDefinedNullRockfactory() throws Exception {

    /* Checking that null pointer exception is thrown */
    try {
      objUnderTest = new Meta_source_tables(null ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Meta_source_tables constructor variable initialization with values taken
   * from database.
   */
  @Test
  public void testMeta_source_tablesConstructorWithPrimaryKeyUndefined() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_source_tables whereObject = new Meta_source_tables(rockFactory);

    /* Calling the tested constructor */
    objUnderTest = new Meta_source_tables(rockFactory, whereObject);

    /* Asserting that variables are initialized */
    String actual =  LAST_TRANSFER_DATE.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest)  + ", " + TABLE_ID.get(objUnderTest)  + ", " + USE_TR_DATE_IN_WHERE_FLAG.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + CONNECTION_ID.get(objUnderTest)  + ", " + DISTINCT_FLAG.get(objUnderTest)  + ", " + AS_SELECT_OPTIONS.get(objUnderTest)  + ", " + AS_SELECT_TABLESPACE.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TIMESTAMP_COLUMN_ID.get(objUnderTest) ;
    String expected =  "2000-01-01 00:00:00.0"  + ", 1"  + ", 1"  + ", testUSE_TR_DATE_IN_WHERE_FLAG"  + ", 1"  + ", 1"  + ", 1"  + ", testDISTINCT_FLAG"  + ", testAS_SELECT_OPTIONS"  + ", testAS_SELECT_TABLESPACE"  + ", testVERSION_NUMBER"  + ", 1" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testMeta_source_tablesConstructorWithPrimaryKeyUndefinedNullRockfactory() throws Exception {

    /* Creating where object which tells what sort of query is to be done */
    Meta_source_tables whereObject = new Meta_source_tables(rockFactory);

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new Meta_source_tables(null, whereObject);
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
    assertEquals("Meta_source_tables", objUnderTest.getTableName());
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
    Meta_source_tables whereObject = new Meta_source_tables(rockFactory);

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
    Meta_source_tables whereObject = new Meta_source_tables(rockFactory);

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
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Meta_source_tables");
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
  public void testSetAndGetLast_transfer_date() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setLast_transfer_date(new Timestamp(946677600000L));
    assertEquals(new Timestamp(946677600000L), LAST_TRANSFER_DATE.get(objUnderTest));
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
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTable_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTable_id(555L);
    assertEquals(555L, TABLE_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetUse_tr_date_in_where_flag() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setUse_tr_date_in_where_flag(Meta_source_tablesTest.testStringGenerator("anotherUSE_TR_DATE_IN_WHERE_FLAG", 1));
    assertEquals(Meta_source_tablesTest.testStringGenerator("anotherUSE_TR_DATE_IN_WHERE_FLAG", 1), USE_TR_DATE_IN_WHERE_FLAG.get(objUnderTest));
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
  public void testSetAndGetConnection_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setConnection_id(555L);
    assertEquals(555L, CONNECTION_ID.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetDistinct_flag() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setDistinct_flag(Meta_source_tablesTest.testStringGenerator("anotherDISTINCT_FLAG", 1));
    assertEquals(Meta_source_tablesTest.testStringGenerator("anotherDISTINCT_FLAG", 1), DISTINCT_FLAG.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetAs_select_options() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setAs_select_options(Meta_source_tablesTest.testStringGenerator("anotherAS_SELECT_OPTIONS", 200));
    assertEquals(Meta_source_tablesTest.testStringGenerator("anotherAS_SELECT_OPTIONS", 200), AS_SELECT_OPTIONS.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetAs_select_tablespace() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setAs_select_tablespace(Meta_source_tablesTest.testStringGenerator("anotherAS_SELECT_TABLESPACE", 30));
    assertEquals(Meta_source_tablesTest.testStringGenerator("anotherAS_SELECT_TABLESPACE", 30), AS_SELECT_TABLESPACE.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetVersion_number() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setVersion_number(Meta_source_tablesTest.testStringGenerator("anotherVERSION_NUMBER", 32));
    assertEquals(Meta_source_tablesTest.testStringGenerator("anotherVERSION_NUMBER", 32), VERSION_NUMBER.get(objUnderTest));
  }
    /**
   * Testing column data setting and retrieving via get and set methods.
   */
  @Test
  public void testSetAndGetTimestamp_column_id() throws Exception {

    /* Data validating on */
    objUnderTest.setValidateData(true);

    /* Setting column values and asserting correct value is returned */
    objUnderTest.setTimestamp_column_id(555L);
    assertEquals(555L, TIMESTAMP_COLUMN_ID.get(objUnderTest));
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
    String[] expectedKeys = { "TRANSFER_ACTION_ID","TABLE_ID","COLLECTION_SET_ID","COLLECTION_ID","CONNECTION_ID","VERSION_NUMBER"};

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
    objUnderTest = new Meta_source_tables(rockFactory, true);

    /* Calling the tested method and asserting nulls are removed */
    objUnderTest.removeNulls();
    String actual =  LAST_TRANSFER_DATE.get(objUnderTest)  + ", " + TRANSFER_ACTION_ID.get(objUnderTest)  + ", " + TABLE_ID.get(objUnderTest)  + ", " + USE_TR_DATE_IN_WHERE_FLAG.get(objUnderTest)  + ", " + COLLECTION_SET_ID.get(objUnderTest)  + ", " + COLLECTION_ID.get(objUnderTest)  + ", " + CONNECTION_ID.get(objUnderTest)  + ", " + DISTINCT_FLAG.get(objUnderTest)  + ", " + AS_SELECT_OPTIONS.get(objUnderTest)  + ", " + AS_SELECT_TABLESPACE.get(objUnderTest)  + ", " + VERSION_NUMBER.get(objUnderTest)  + ", " + TIMESTAMP_COLUMN_ID.get(objUnderTest) ;
    String expected =  new Timestamp(0)  + ", 0"  + ", 0"  + ", "  + ", 0"  + ", 0"  + ", 0"  + ", "  + ", "  + ", "  + ", "  + ", 0" ;
    assertEquals(expected, actual);
  }
  
  /**
   * Testing comparing another Meta_source_tables with our current one. If the two
   * Meta_source_tabless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithNullColumnMeta_source_tables() throws Exception {

    /* Creating another Meta_source_tables which will be compared to the tested one */
    Meta_source_tables comparedObj = new Meta_source_tables(rockFactory, true);

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_source_tables with our current one. If the two
   * Meta_source_tabless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentMeta_source_tables() throws Exception {

    /* Creating another Meta_source_tables which will be compared to the tested one */
    Meta_source_tables comparedObj = new Meta_source_tables(rockFactory ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");
    comparedObj.setLast_transfer_date( new Timestamp(100000L) );

    /* Asserting that false is returned */
    assertEquals(false, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_source_tables with our current one. If the two
   * Meta_source_tabless are the same true is returned, otherwise false.
   */
  @Test
  public void testEqualsWithSameMeta_source_tables() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_source_tables comparedObj = new Meta_source_tables(rockFactory ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");

    /* Asserting that true is returned */
    assertEquals(true, objUnderTest.equals(comparedObj));
  }
  
  /**
   * Testing comparing another Meta_source_tables with our current one using null value.
   */
  @Test
  public void testEqualsWithNullMeta_source_tables() throws Exception {

    /* Creating another aggregation which will be compared to the tested one */
    Meta_source_tables comparedObj = null;

    /* Asserting that exception is thrown */
    try {
      objUnderTest.equals(comparedObj);
      fail("Test Failed - Unexpected NullPointerException expected as compared Meta_source_tables was null \n");
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
    assertEquals(Meta_source_tables.class, actualObject.getClass());
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
    Meta_source_tables testAgg = new Meta_source_tables(rockFactory ,  1L ,  1L ,  1L ,  1L ,  1L ,  "testVERSION_NUMBER");
    LAST_TRANSFER_DATE.set(objUnderTest, new Timestamp(100000L));

    actual += objUnderTest.existsDB();
    assertEquals(true + ", " + false, actual);
  }
  
    /**
   * Testing columnsize retrieving for Last_transfer_date.
   */
  @Test
  public void testGetLast_transfer_dateColumnSize() throws Exception {
    
     assertEquals(23, objUnderTest.getLast_transfer_dateColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Last_transfer_date.
  */
  @Test
  public void testGetLast_transfer_dateDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getLast_transfer_dateDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Last_transfer_date.
  */
  @Test
  public void testGetLast_transfer_dateSQLType() throws Exception {
    
    assertEquals(93, objUnderTest.getLast_transfer_dateSQLType());    
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
   * Testing columnsize retrieving for Table_id.
   */
  @Test
  public void testGetTable_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getTable_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Table_id.
  */
  @Test
  public void testGetTable_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTable_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Table_id.
  */
  @Test
  public void testGetTable_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getTable_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Use_tr_date_in_where_flag.
   */
  @Test
  public void testGetUse_tr_date_in_where_flagColumnSize() throws Exception {
    
     assertEquals(1, objUnderTest.getUse_tr_date_in_where_flagColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Use_tr_date_in_where_flag.
  */
  @Test
  public void testGetUse_tr_date_in_where_flagDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getUse_tr_date_in_where_flagDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Use_tr_date_in_where_flag.
  */
  @Test
  public void testGetUse_tr_date_in_where_flagSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getUse_tr_date_in_where_flagSQLType());    
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
   * Testing columnsize retrieving for Connection_id.
   */
  @Test
  public void testGetConnection_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getConnection_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Connection_id.
  */
  @Test
  public void testGetConnection_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getConnection_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Connection_id.
  */
  @Test
  public void testGetConnection_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getConnection_idSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for Distinct_flag.
   */
  @Test
  public void testGetDistinct_flagColumnSize() throws Exception {
    
     assertEquals(1, objUnderTest.getDistinct_flagColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Distinct_flag.
  */
  @Test
  public void testGetDistinct_flagDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getDistinct_flagDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Distinct_flag.
  */
  @Test
  public void testGetDistinct_flagSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getDistinct_flagSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for As_select_options.
   */
  @Test
  public void testGetAs_select_optionsColumnSize() throws Exception {
    
     assertEquals(200, objUnderTest.getAs_select_optionsColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for As_select_options.
  */
  @Test
  public void testGetAs_select_optionsDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getAs_select_optionsDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for As_select_options.
  */
  @Test
  public void testGetAs_select_optionsSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getAs_select_optionsSQLType());    
  }
  
    /**
   * Testing columnsize retrieving for As_select_tablespace.
   */
  @Test
  public void testGetAs_select_tablespaceColumnSize() throws Exception {
    
     assertEquals(30, objUnderTest.getAs_select_tablespaceColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for As_select_tablespace.
  */
  @Test
  public void testGetAs_select_tablespaceDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getAs_select_tablespaceDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for As_select_tablespace.
  */
  @Test
  public void testGetAs_select_tablespaceSQLType() throws Exception {
    
    assertEquals(12, objUnderTest.getAs_select_tablespaceSQLType());    
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
   * Testing columnsize retrieving for Timestamp_column_id.
   */
  @Test
  public void testGetTimestamp_column_idColumnSize() throws Exception {
    
     assertEquals(38, objUnderTest.getTimestamp_column_idColumnSize());   
  }

 /**
  * Testing decimal digits retrieving for Timestamp_column_id.
  */
  @Test
  public void testGetTimestamp_column_idDecimalDigits() throws Exception {
    
     assertEquals(0, objUnderTest.getTimestamp_column_idDecimalDigits());     
  }
  
 /**
  * Testing columnsize retrieving for Timestamp_column_id.
  */
  @Test
  public void testGetTimestamp_column_idSQLType() throws Exception {
    
    assertEquals(2, objUnderTest.getTimestamp_column_idSQLType());    
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