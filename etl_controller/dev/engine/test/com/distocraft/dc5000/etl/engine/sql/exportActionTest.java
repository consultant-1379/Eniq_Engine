package com.distocraft.dc5000.etl.engine.sql;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * Test class for exportAction class in com.distocraft.dc5000.etl.engine.sql.<br>
 * <br>
 * This class tests exporting tabledata into a SetContext object. The data will
 * be converted into xml format in the process.
 * 
 * @author EJAAVAH
 * 
 */
public class exportActionTest {

  private static exportAction objUnderTest;

  private static Statement stmt;

  private static Meta_versions metaVersions;

  private static Meta_collections collection;

  private static RockFactory rockFactory;

  private static ConnectionPool connectionPool;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    Connection con = null;
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(31),COLLECTION_SET_NAME VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31), VERSION_NUMBER VARCHAR(31), ENABLED_FLAG VARCHAR(31), TYPE VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");

    stmt.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
        + "TYPE_NAME VARCHAR(31), CONNECTION_ID VARCHAR(31), CONNECTION_NAME VARCHAR(31), "
        + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
        + "DB_LINK_NAME VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'typenames', '1', 'connectionname', "
        + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

    stmt.execute("CREATE TABLE EXAMPLE_TABLE (ID BIGINT, VALUE VARCHAR(31), ANOTHERVALUE VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO EXAMPLE_TABLE VALUES(1, 'testvalue', 'anothertestvalue')");
    stmt.executeUpdate("INSERT INTO EXAMPLE_TABLE VALUES(1, 'secondtestvalue', 'secondanothertestvalue')");
    stmt.executeUpdate("INSERT INTO EXAMPLE_TABLE VALUES(2, 'thirdtestvalue', 'thirdanothertestvalue')");

    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    collection = new Meta_collections(rockFactory);

    connectionPool = new ConnectionPool(rockFactory);

    metaVersions = new Meta_versions(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stmt.execute("DROP TABLE EXAMPLE_TABLE");
    stmt.execute("DROP TABLE Meta_databases");
    stmt.execute("DROP TABLE Meta_collection_sets");
    objUnderTest = null;
  }

  /**
   * Test if the execute() method in exportAction class returns the database
   * data (in table "EXAMPLE_TABLE") in correct XML format.
   */
  @Test
  public void testExportActionExecute() throws Exception {
    SetContext actual = new SetContext();
    Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
    mta.setWhere_clause("tables = EXAMPLE_TABLE \n" + "EXAMPLE_TABLE.sqlSelect = select * from EXAMPLE_TABLE \n"
        + "EXAMPLE_TABLE.sqlClause = where ID = 1");
    try {
      objUnderTest = new exportAction(metaVersions, 1L, collection, 1L, 1L, 1L, rockFactory, connectionPool, mta,
          "batchColumnName", actual);
      objUnderTest.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    String expected = "{exportData=<dataset>\n  "
        + "<EXAMPLE_TABLE ID=\"1\" VALUE=\"testvalue\" ANOTHERVALUE=\"anothertestvalue\"/>\n  "
        + "<EXAMPLE_TABLE ID=\"1\" VALUE=\"secondtestvalue\" ANOTHERVALUE=\"secondanothertestvalue\"/>\n"
        + "</dataset>\n}";

    assertEquals(expected, actual.toString());
  }

  /**
   * Test execute() with different sql clause. Earlier data in setContext object
   * should be overwritten.
   */
  @Test
  public void testAnotherExportActionExecute() throws Exception {
    SetContext actual = new SetContext();
    Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
    mta
        .setWhere_clause("tables = EXAMPLE_TABLE \n"
            + "EXAMPLE_TABLE.sqlSelect = select VALUE, ID from EXAMPLE_TABLE \n"
            + "EXAMPLE_TABLE.sqlClause = where ID = 2");
    try {
      objUnderTest = new exportAction(metaVersions, 1L, collection, 1L, 1L, 1L, rockFactory, connectionPool, mta,
          "batchColumnName", actual);
      objUnderTest.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    String expected = "{exportData=<dataset>\n " + " <EXAMPLE_TABLE VALUE=\"thirdtestvalue\" ID=\"2\"/>\n</dataset>\n}";

    assertEquals(expected, actual.toString());
  }

  /**
   * Test execute() with null initilized SetContext object -
   * NullPointerException expected
   */
  @Test
  public void testExportActionNullException() throws Exception {
    SetContext actual = null;
    Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
    mta.setWhere_clause("tables = EXAMPLE_TABLE \n" + "EXAMPLE_TABLE.sqlSelect = select ID from EXAMPLE_TABLE \n"
        + "EXAMPLE_TABLE.sqlClause = where VALUE = 'testvalue'");
    try {
      objUnderTest = new exportAction(metaVersions, 1L, collection, 1L, 1L, 1L, rockFactory, connectionPool, mta,
          "batchColumnName", actual);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      objUnderTest.execute();
      String expected = "{exportData=<dataset>\n " + " <EXAMPLE_TABLE ID=\"1\"/>\n</dataset>\n}";
      assertEquals(expected, actual.toString());
      fail("Test failed - NullPointerException expected as SetContext object was initialized with null");
    } catch (Exception e) {
      // Test passed, exception catched
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(exportActionTest.class);
  }
}
