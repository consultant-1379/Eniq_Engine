package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import junit.framework.JUnit4TestAdapter;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
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
 * Test class for importAction class in com.distocraft.dc5000.etl.engine.sql.<br>
 * <br>
 * This class tests if tabledata in xml format imported from SetContext object
 * and inserted into database.
 * 
 * @author EJAAVAH
 * 
 */
public class importActionTest {

  private static importAction objUnderTest;

  private static Statement stmt;

  private static Meta_transfer_actions metaTransferActions;

  private static Meta_versions metaVersions;

  private static Meta_collections collection;

  private static RockFactory rockFactory;

  private static ConnectionPool connectionPool;

  private static Connection con = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

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

    stmt.execute("CREATE TABLE NEW_EXAMPLE_TABLE (ID BIGINT, VALUE VARCHAR(31), ANOTHERVALUE VARCHAR(31))");

    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    collection = new Meta_collections(rockFactory);

    connectionPool = new ConnectionPool(rockFactory);

    metaTransferActions = new Meta_transfer_actions(rockFactory);
    // Old tablename can be replaced with new one with this where clause
    metaTransferActions.setWhere_clause("replace.tablename.old = OLD_EXAMPLE_TABLE \n"
        + "replace.tablename.new = NEW_EXAMPLE_TABLE \n");

    metaVersions = new Meta_versions(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stmt.execute("DROP TABLE NEW_EXAMPLE_TABLE");
    stmt.execute("DROP TABLE Meta_databases");
    stmt.execute("DROP TABLE Meta_collection_sets");
    con = null;
    objUnderTest = null;
  }

  /**
   * Testing if the table data is uploaded from SetContext object to new table
   * and Checking if the actual table is the same as expected.
   */
  @Test
  public void testImportActionExecute() throws Exception {
    SetContext setContext = new SetContext();
    String XMLfile = "<dataset>\n  "
        + "<OLD_EXAMPLE_TABLE ID=\"1\" VALUE=\"testvalue\" ANOTHERVALUE=\"anothertestvalue\"/>\n  " + "</dataset>\n";
    setContext.put("exportData", XMLfile);

    try {
      objUnderTest = new importAction(metaVersions, 1L, collection, 1L, 1L, 1L, rockFactory, connectionPool,
          metaTransferActions, "batchColumnName", setContext);
      objUnderTest.execute();

      IDataSet actualDataSet = new DatabaseConnection(con).createDataSet();
      ITable actualTable = actualDataSet.getTable("NEW_EXAMPLE_TABLE");

      IDataSet expectedDataSet = new FlatXmlDataSet(new File(
          "test/XMLFiles/com.distocraft.dc5000.etl.engine.sql_importActionTest_importActionExecute/Expected.xml"));
      ITable expectedTable = expectedDataSet.getTable("EXAMPLETABLE1");

      Assertion.assertEquals(expectedTable, actualTable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Testing if another import will add new row to the table and add null row if
   * one of the columns is not defined.
   */
  @Test
  public void testAnotherImportActionExecute() throws Exception {
    SetContext setContext = new SetContext();
    String XMLfile = "<dataset>\n " + "<NEW_EXAMPLE_TABLE ID=\"1\" ANOTHERVALUE=\"differentTestValue\"/>\n "
        + "</dataset>\n";
    setContext.put("exportData", XMLfile);

    try {
      objUnderTest = new importAction(metaVersions, 1L, collection, 1L, 1L, 1L, rockFactory, connectionPool,
          metaTransferActions, "batchColumnName", setContext);
      objUnderTest.execute();

      IDataSet actualDataSet = new DatabaseConnection(con).createDataSet();
      ITable actualTable = actualDataSet.getTable("NEW_EXAMPLE_TABLE");

      IDataSet expectedDataSet = new FlatXmlDataSet(new File(
          "test/XMLFiles/com.distocraft.dc5000.etl.engine.sql_importActionTest_importActionExecute/Expected.xml"));
      ITable expectedTable = expectedDataSet.getTable("EXAMPLETABLE2");

      Assertion.assertEquals(expectedTable, actualTable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(importActionTest.class);
  }
}
