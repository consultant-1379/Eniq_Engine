package com.distocraft.dc5000.etl.engine.sql;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
 * Test for DuplicateCheckAction class in com.distocraft.dc5000.etl.engine.sql.<br>
 * <br>
 * This class tests if duplicate table is found from DWHType table and marks
 * this. SetContext object is initilized with a tablelist including the
 * tablenames to be checked in case of duplicates. If the <i>basetablename</i>
 * column in DWHType table matches one of the names in the tablelist,
 * markDuplicates() method is called and SQL clause stated in
 * metatransferactions is executed.
 * 
 * @author EJAAVAH
 */
public class DuplicateCheckActionTest {

  private static DuplicateCheckAction objUnderTest;

  private static Statement stmt;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    Meta_versions metaVersions;
    Long collectionSetId = 1L;
    Meta_collections collection;
    Long transferActionId = 1L;
    Long transferBatchId = 1L;
    Long connectId = 1L;
    RockFactory rockFactory;
    ConnectionPool connectionPool;
    Meta_transfer_actions metaTransferActions;
    SetContext setContext;

    // hsql database with the tables needed in testing
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
    } catch (ClassNotFoundException cnfe) {
      System.out.println(cnfe);
      System.exit(1);
    } catch (InstantiationException ie) {
      System.out.println(ie);
      System.exit(1);
    } catch (IllegalAccessException iae) {
      System.out.println(iae);
      System.exit(1);
    }
    Connection con = null;
    try {
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (SQLException sqle) {
      System.out.println(sqle);
      System.exit(1);
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID BIGINT,COLLECTION_SET_NAME VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31), VERSION_NUMBER VARCHAR(31), ENABLED_FLAG VARCHAR(31), TYPE VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_collection_sets VALUES(1, 'set_name', 'description', '1', 'Y', 'type')");

    stmt.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
        + "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
        + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
        + "DB_LINK_NAME VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'typenames', 1, 'connectionname', "
        + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

    stmt.execute("CREATE TABLE DWHType (TECHPACK_NAME VARCHAR(31), TYPENAME VARCHAR(31), TABLELEVEL VARCHAR(31), "
        + "STORAGEID VARCHAR(31), PARTITIONSIZE BIGINT, PARTITIONCOUNT BIGINT, STATUS VARCHAR(31), "
        + "TYPE VARCHAR(31), OWNER VARCHAR(31), VIEWTEMPLATE VARCHAR(31), CREATETEMPLATE VARCHAR(31), "
        + "NEXTPARTITIONTIME TIMESTAMP, BASETABLENAME VARCHAR(31), DATADATECOLUMN VARCHAR(31), "
        + "PUBLICVIEWTEMPLATE VARCHAR(31), PARTITIONPLAN VarChar(31))");
    stmt.executeUpdate("INSERT INTO DWHType VALUES('techpakname', 'typename', 'tablelevel', 'storageid', "
        + "1, 1, 'status', 'type', 'owner', 'viewtemplate', 'createtemplate', "
        + "2008-06-23, 'EXAMPLE_TABLE', 'datadatecolumn', 'publicviewtemplate', 'partitionplan')");

    stmt.execute("CREATE TABLE Dwhcolumn (STORAGEID VARCHAR(31), DATANAME VARCHAR(31), COLNUMBER BIGINT, "
        + "DATATYPE VARCHAR(31), DATASIZE INTEGER, DATASCALE INTEGER, UNIQUEVALUE BIGINT, "
        + "NULLABLE INTEGER, INDEXES VARCHAR(31), UNIQUEKEY INTEGER, STATUS VARCHAR(31), INCLUDESQL INTEGER)");

    stmt.execute("CREATE TABLE EXAMPLE_TABLE_01 (DUPLICATE VARCHAR(31))");

    // Initializing rockFactory
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    // Initializing meta collections
    collection = new Meta_collections(rockFactory);

    // Initializing connection pool
    connectionPool = new ConnectionPool(rockFactory);

    // Initializing metaTransferActions
    metaTransferActions = new Meta_transfer_actions(rockFactory);
    // This clause is executed if duplicates are found
    metaTransferActions.setAction_contents("INSERT INTO EXAMPLE_TABLE_01 VALUES ('test')");

    // Initializing metaVersions
    metaVersions = new Meta_versions(rockFactory);

    // Initializing SetContext with list of tablenames to be checked - Number
    // index at the end of table name is parsed out to get the basetablename
    // which is then matched to the column with the same name in DWHType
    setContext = new SetContext();
    List tables = new ArrayList();
    tables.add("EXAMPLE_TABLE_01");
    setContext.put("tableList", tables);

    try {
      objUnderTest = new DuplicateCheckAction(metaVersions, collectionSetId, collection, transferActionId,
          transferBatchId, connectId, rockFactory, connectionPool, metaTransferActions, setContext);
      objUnderTest.dwhRockFactory = rockFactory;
      objUnderTest.dwhrepRockFactory = rockFactory;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stmt.execute("DROP TABLE Dwhcolumn");
    stmt.execute("DROP TABLE DWHType");
    stmt.execute("DROP TABLE Meta_databases");
    stmt.execute("DROP TABLE Meta_collection_sets");
    objUnderTest = null;
  }

  /**
   * Execute() method in DuplicateCheckAction checks for duplicates and marks
   * them to a table according to Action_contents in metaTransferActions.
   * Execute parses out the basetablenames from given SetContext object and
   * compares them to the <i>basetablename</i> column in DWHType. In this case
   * EXAMPLE_TABLE basetablename is found to be the same in SetContext as well
   * as in DWHTypes basetablename column, so the markDuplicates() method is
   * called. This will mark "test" value into "Duplicate" column in table
   * EXAMPLE_TABLE_01.
   */
  @Test
  public void testExecuteAndMarkDuplicates() throws Exception {
    objUnderTest.execute();
    ResultSet result = stmt.executeQuery("SELECT * FROM EXAMPLE_TABLE_01");
    if (result.next()) {
      String value = result.getString("DUPLICATE");
      assertEquals("test", value);
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(DuplicateCheckActionTest.class);
  }
}
