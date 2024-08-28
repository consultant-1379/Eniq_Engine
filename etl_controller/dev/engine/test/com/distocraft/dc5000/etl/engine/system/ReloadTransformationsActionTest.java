package com.distocraft.dc5000.etl.engine.system;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.main.TestITransferEngineRMI;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * 
 * @author ejarsok
 *
 */

public class ReloadTransformationsActionTest {

  private static ReloadTransformationsAction rta = null;
  
  private static Statement stm;
  
  private static Map<String, String> env = System.getenv();
  private static String userHome = env.get("WORKSPACE");
  
  @BeforeClass
  public static void init() {
	
    System.setProperty("dc5000.config.directory", userHome);
    File prop = new File(userHome, "ETLCServer.properties");
    prop.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(prop));
      pw.write("name=value");
      pw.close();
    } catch (IOException e3) {
      e3.printStackTrace();
      fail("Can't write in file");
    }
    Long collectionSetId = 1L;
    Long transferActionId = 1L;
    Long transferBatchId = 1L;
    Long connectId = 1L;
    RockFactory rockFact = null;
    Connection c;

    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("init() failed, ClassNotFoundException");
    }

    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(20), COLLECTION_SET_NAME VARCHAR(20),"
          + "DESCRIPTION VARCHAR(20),VERSION_NUMBER VARCHAR(20),ENABLED_FLAG VARCHAR(20),TYPE VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException e1");
    }
    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:.", "SA", "", "org.hsqldb.jdbcDriver", "conName", true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed, RockFactory Exception");
    }
    
    Meta_versions version = new Meta_versions(rockFact);
    Meta_collections collection = new Meta_collections(rockFact);
    Meta_transfer_actions trActions = new Meta_transfer_actions(rockFact);

    try {
      rta = new ReloadTransformationsAction(version, collectionSetId, collection, transferActionId, transferBatchId,
          connectId, rockFact, trActions);

    } catch (EngineMetaDataException e) {
      e.printStackTrace();
      fail("init() failed, EngineMetaDataException");
    }
  }

  @Test
  public void testExecute() {
    try {    
      TestITransferEngineRMI ttRMI = new TestITransferEngineRMI(false);
      rta.execute();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testExecute() failed, Exception");
    }
  }
  
  @Test
  public void testExecute2() {
    try {    
      TestITransferEngineRMI ttRMI = new TestITransferEngineRMI(true);
      rta.execute();
      fail("testExecute2() failed, should't execute this line");
    } catch (Exception e) {
      
    }
  }
  
  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_collection_sets");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ReloadTransformationsActionTest.class);
  }*/
}
